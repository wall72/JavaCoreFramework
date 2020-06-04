package jcf.iam.core.filter.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.iam.core.common.exception.IamException;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.StringUtils;

/**
 *
 * @author nolang
 *
 */
public class ZooKeeperSecurityContextRepository implements SecurityContextRepository, InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(ZooKeeperSecurityContextRepository.class);

	private Map<String, SecurityContext> contexts = new HashMap<String, SecurityContext>();

	private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

    /** SecurityContext instance used to check for equality with default (unauthenticated) content */
    private boolean disableUrlRewriting = false;

    private String sessionTrackerString = "jcfiam_session_tracker";
	private String connectString = "127.0.0.1";
	private int sessionTimeout = Integer.MAX_VALUE;
	private Watcher watcher = new Watcher() {
		public void process(WatchedEvent event) {
			if (event.getType() == Event.EventType.None) {
				/**
				 * TODO 접속성공이벤트 찾기..
				 */
				switch (event.getState()) {
					case SyncConnected :
					case Disconnected :
						monitor.notifyAll();
						break;
				}
			}
		}
	};

	private Object monitor = new Object();
	private ZooKeeper zooKeeper;

	private SecurityContextRepository sessionContextRepository = new HttpSessionSecurityContextRepository();

	public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
		SecurityContext context = null;

		String sessionTracker = getSessionTracker(requestResponseHolder.getRequest());

		if (StringUtils.hasText(sessionTracker)	&& contexts.containsKey(sessionTracker)) {
			context = contexts.get(sessionTracker);
		} else {
			try {
				context = readSecurityContextFromZooKeeper(sessionTracker);

				if (context == null) {
					/*
					 * ZooKeeper에 인증정보가 없는 경우 세션에서 조회함. (Zooper 다운 시점에서 세션기반으로 로그인 한 경우..)
					 */
					context = readSecurityContextFromHttpSession(requestResponseHolder.getRequest());
				}

			} catch (Exception e) {
				/*
				 * ZooKeeper 서버가 다운된 경우 WAS세션 기반으로 동작한다.
				 */

				logger.warn("ZooKeeper로의 접속에 실패하였습니다. WAS세션 기반으로 동작합니다. - Error={}", e.getMessage());

				return sessionContextRepository.loadContext(requestResponseHolder);
			}
		}

		requestResponseHolder.getResponse().addCookie(generateCookieForSessionTracker(sessionTracker));
		requestResponseHolder.setResponse(new SaveToZooKeeperResponseWrapper(requestResponseHolder.getRequest(), requestResponseHolder.getResponse(), sessionTracker, context.hashCode()));

		return context;
	}

	public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
		if(response instanceof SaveToZooKeeperResponseWrapper){
			SaveToZooKeeperResponseWrapper responseWrapper = (SaveToZooKeeperResponseWrapper) response;

			if (!responseWrapper.isContextSaved()) {
				responseWrapper.saveContext(context);
			}
		} else {
			sessionContextRepository.saveContext(context, request, response);
		}
	}

	public boolean containsContext(HttpServletRequest request) {
		return contexts.containsKey(getSessionTracker(request)) || sessionContextRepository.containsContext(request);
	}

	private String getSessionTracker(HttpServletRequest request) {
		String sessionTracker = "";

		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(sessionTrackerString)) {
					sessionTracker = cookie.getValue();
					logger.debug("Browser로 부터 전송된 SessionTracker가 검색되었습니다. - SessionTracker={}", sessionTracker);
					break;
				}
			}
		}

		if(!StringUtils.hasText(sessionTracker))	{
			sessionTracker = generateSessionTracker();
		}

		return sessionTracker;
	}

	private Cookie generateCookieForSessionTracker(String sessionTracker) {
		Cookie cookie = new Cookie(sessionTrackerString, sessionTracker);
		cookie.setMaxAge(-1);
		return cookie;
	}

	/*
	 * TODO 사용자 ID를 조합해서 SessionTracker를 만들고 로그인된 사용자의 ID와 세션관리서버에 저장된 컨텍스트의 사용자 ID를 비교하여
	 * 다른 경우 예외처리한다.
	 */
	private String generateSessionTracker()	{
		String sessionTracker = UUID.randomUUID().toString();

		logger.debug("신규 SessionTracker를 생성합니다. - New SessionTracker={}", sessionTracker);

		return sessionTracker;
	}

	private SecurityContext readSecurityContextFromHttpSession(HttpServletRequest request) {
		SecurityContext context = (SecurityContext) request.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

		if(context == null)	{
			context = SecurityContextHolder.createEmptyContext();
		}

		return context;
	}

	private SecurityContext readSecurityContextFromZooKeeper(String sessionTracker) throws Exception {
		String location = "/" + sessionTracker;

		if (zooKeeper.exists(location, false) == null) {
			return null;
		}

		SecurityContext context = null;

		byte[] contextStream = zooKeeper.getData(location, false, null);

		if (contextStream != null && contextStream.length > 0) {
			ByteArrayInputStream bis = new ByteArrayInputStream(contextStream);
			ObjectInputStream ois = new ObjectInputStream(bis);

			try {
				context = (SecurityContext) ois.readObject();
			} finally {
				ois.close();
				bis.close();
			}
		}

		if(context != null){
			logger.debug("ZooKeeper로부터 사용자 인증정보가 전송되었습니다. - 사용자ID={}", context.getAuthentication().getName());
		}

		return context;
	}

	private void removeContextFromZooKeeper(final String sessionTracker) {
		String location = "/" + sessionTracker;

		try {
			connectToZooKeeper();

			if (zooKeeper.exists(location, false) != null) {
				zooKeeper.delete(location, -1, new VoidCallback() {

					public void processResult(int rc, String path, Object ctx) {
						logger.debug("사용자 인증정보 삭제작업 종료 - ResultCode={}, Path={}", rc, path);
					}
				}, null);
			}
		} catch (Exception e) {
			logger.debug("사용자 인증정보 삭제실패 - {}" , e.getMessage());
		}
	}

//	private String readSessionTrackerFromZooKeeper(String userId) throws Exception {
//		String location = "/" + userId;
//
//		if (zooKeeper.exists(location, false) == null) {
//			return null;
//		}
//
//		String sessionTracker = new String(zooKeeper.getData(location, false, null));
//
//		if(StringUtils.hasText(sessionTracker)){
//			logger.debug("ZooKeeper로부터 사용자 SessionTracker가 전송되었습니다. - 사용자ID={} SessionTracker={}", userId, sessionTracker);
//		}
//
//		return sessionTracker;
//	}

	private void saveSessionTrackerToZooKeeper(final String userId, final String sessionTracker) throws Exception {
		final String location = "/" + userId;

		connectToZooKeeper();

		if (zooKeeper.exists(location, false) == null) {
			zooKeeper.create(location, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}

		zooKeeper.setData(location, sessionTracker.getBytes(), -1, new StatCallback() {
			public void processResult(int rc, String path, Object ctx, Stat stat) {
				logger.debug("ZooKeeper로 SessionTracker를 전송하였습니다. - 사용자ID={} SessionTracker={}", userId, sessionTracker);
			}
		}, null);
	}

	/**
	 *
	 * @author nolang
	 *
	 */
	final class SaveToZooKeeperResponseWrapper extends SaveContextOnUpdateOrErrorResponseWrapper	{

		private HttpServletRequest request;
		private int contextHashBeforeChainExecution;
		private String sessionTracker;

		public SaveToZooKeeperResponseWrapper(HttpServletRequest request, HttpServletResponse response, String sessionTracker, int contextHashBeforeChainExecution) {
			super(response, disableUrlRewriting);
			this.request = request;
			this.sessionTracker = sessionTracker;
			this.contextHashBeforeChainExecution = contextHashBeforeChainExecution;
		}

		@Override
		protected void saveContext(SecurityContext context) {
			final Authentication authentication = context.getAuthentication();

            // See SEC-776
            if (authentication == null || authenticationTrustResolver.isAnonymous(authentication)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("비인증사용자 또는 익명사용자의 경우 인증정보를 저장하지 않습니다.");
                }

				removeContextFromZooKeeper(sessionTracker);
				contexts.remove(sessionTracker);

                return;
            }

            if(contexts.containsKey(sessionTracker) && contexts.get(sessionTracker).hashCode() == contextHashBeforeChainExecution) {
        		return;
            }

			try {
				saveSessionTrackerToZooKeeper(context.getAuthentication().getName(), sessionTracker);
				saveContextToZooKeeper(context);
			} catch (Exception e) {
				throw new IamException(e);
			} finally	{
				saveContextToHttpSession(context);
			}

			contexts.put(sessionTracker, context);
		}

		private void saveContextToHttpSession(SecurityContext context) {
			request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
		}

		private void saveContextToZooKeeper(SecurityContext context) throws Exception {
			final String location = "/" + sessionTracker;

			connectToZooKeeper();

			if (zooKeeper.exists(location, false) == null) {
				zooKeeper.create(location, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);

			try {
				oos.writeObject(context);
			} finally {
				oos.close();
				bos.close();
			}

			zooKeeper.setData(location, bos.toByteArray(), -1, new StatCallback() {
				public void processResult(int rc, String path, Object ctx, Stat stat) {
					logger.debug("ZooKeeper로 사용자 인증정보를 전송하였습니다. - 사용자ID={} SessionTracker={}", ((Authentication) ctx).getName(), sessionTracker);
				}
			}, context.getAuthentication());
		}
	}

	private void connectToZooKeeper() throws IOException {
		if(zooKeeper == null || !zooKeeper.getState().isAlive())	{
			zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);
			logger.debug("ZooKeeper[IP:{} SessionTimeOut={}] 가 연결되었습니다.", connectString, sessionTimeout);
		}
	}

	/**
	 *
	 * @param disableUrlRewriting
	 */
	public void setDisableUrlRewriting(boolean disableUrlRewriting) {
		this.disableUrlRewriting = disableUrlRewriting;
	}

	/**
	 *
	 * @param connectString
	 */
	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	/**
	 *
	 * @param sessionTimeout
	 */
	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	/**
	 *
	 * @param sessionTrackerString
	 */
	public void setSessionTrackerString(String sessionTrackerString) {
		this.sessionTrackerString = sessionTrackerString;
	}

	public void afterPropertiesSet() throws Exception {
		connectToZooKeeper();

		synchronized (monitor) {
//			monitor.wait();
		}
	}

	public void destroy() throws Exception {
		zooKeeper.close();

		synchronized (monitor) {
//			monitor.wait();
		}
	}
}
