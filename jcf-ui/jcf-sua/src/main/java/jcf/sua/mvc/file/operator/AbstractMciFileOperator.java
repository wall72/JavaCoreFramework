package jcf.sua.mvc.file.operator;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.exception.MciException;
import jcf.upload.FileInfo;
import jcf.upload.FileSender;
import jcf.upload.MultiPartInfo;
import jcf.upload.MultiPartReceiver;
import jcf.upload.handler.DownloadEventHandler;
import jcf.upload.handler.UploadEventHandler;
import jcf.upload.persistence.PersistenceManager;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

/**
*
* {@link FileOperator} 의 구현체
*
* @author nolang
*
*/
public abstract class AbstractMciFileOperator implements FileOperator {

	private static Logger logger = LoggerFactory.getLogger(AbstractMciFileOperator.class);

	protected Map<String, UploadEventHandler> uploadEventHandlers;
	protected Map<String, DownloadEventHandler> downloadEventHandlers;
	protected PathMatcher pathMatcher = new AntPathMatcher();
	protected Map<String, MultiPartReceiver> receivers;
	protected Map<String, FileSender> senders;
	protected String tempDir = "tempdir";

	/**
	 * DefaultUploadEventHandler는 서버의 임의의 위치에 임시 디렉토리를 생성하여 이곳에 파일을 저장한다.
	 *
	 * 여기서 저장된 임시 파일은 MciRequest.handleIfMultipart 메소드를 통해 실제 저장될 위치로 이동된다.
	 */
	protected UploadEventHandler defaultUploadEventHandler = new UploadEventHandler() {

		public void prepareStorage(PersistenceManager persistenceManager, String folder) {
		}

		public void postprocess(String folder, MultiPartInfo info, PersistenceManager persistenceManager) {
		}

		public long getMaxUploadSize() {
			return 0;
		}

		public String getFolder(HttpServletRequest request) {
			return tempDir;
		}

		public String createFileNameIfAccepted(String folder, FileInfo fileInfo) {
			return StringUtils.getFilename(StringUtils.cleanPath(fileInfo.getName()));
		}
	};

	protected DownloadEventHandler defaultDownloadEventHandler = new DownloadEventHandler() {

		public String createFileName(FileInfo fileInfo) {
			return fileInfo.getName();
		}

		public void preprocess(FileInfo fileInfo) {

		}
	};

	/**
	 * {@inheritDoc}
	 */
	public abstract boolean isMultiPartRequest(HttpServletRequest request);

	/**
	 * {@inheritDoc}
	 */
	public MultiPartInfo handleMultiPartRequest(HttpServletRequest request) {
		try {
			/*
			 * 임시 디렉토리 생성 및 저장, 임시 디렉토리 위치 및 파일 정보 반환.
			 */
			UploadEventHandler eventHandler = null;

			if (uploadEventHandlers != null) {
				for (Map.Entry<String, UploadEventHandler> e : uploadEventHandlers.entrySet()) {
					if(pathMatcher.match(e.getKey(), request.getRequestURI()))	{
						eventHandler = e.getValue();
						break;
					}
				}
			}

			if(eventHandler == null)	{
				eventHandler = defaultUploadEventHandler;
			}

			MultiPartInfo multiInfo = null;

			if (!receivers.isEmpty()) {
				for (Map.Entry<String, MultiPartReceiver> receiverEntry : receivers.entrySet()) {
					if (pathMatcher.match(receiverEntry.getKey(), request.getRequestURI())) {
						MultiPartReceiver multiPartReceiver = receiverEntry.getValue();
						multiInfo = multiPartReceiver.receive(request, eventHandler);
						break;
					}
				}
			} else {
				throw new MciException("[JCF-SUA] FileOperator - MultiPartReceiver가 정의되어 있지 않습니다.");
			}

			Map<String, Object> params = new HashMap<String, Object>();

			handlerQueryString(params, request.getQueryString());

			params.putAll(multiInfo.getAttributes());

// ksahn@e-int.co.kr
			Map<String, Object> parameterMap = request.getParameterMap();
			Enumeration paramNames = request.getParameterNames();

			while(paramNames.hasMoreElements()) {
				String key = paramNames.nextElement().toString();
				Object value = parameterMap.get(key);

				if(String[].class.isAssignableFrom(value.getClass())){
					String[] paramValue = (String[])value;

					if(paramValue.length == 1){
						params.put(key, paramValue[0]);
					}else{
						params.put(key, paramValue);
					}
				}
			}

			return new MultiPartInfo(params, multiInfo.getFileInfos());

		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				e.printStackTrace();
			}

			throw new MciException("[JCF-SUA] FileOperator - File Upload 처리중 에러 발생", e);
		}
	}

	protected void handlerQueryString(Map<String, Object> attributes, String queryString) throws IOException {

		if (!StringUtils.hasText(queryString)) {
			return;
		}

		queryString = URLDecoder.decode(queryString, "utf-8");
		String[] paramValues = queryString.split("&");

		for (int i = 0; i < paramValues.length; ++i) {
			String[] paramValue = paramValues[i].split("=", 2);
			Object currentValue = null;

			if (attributes.containsKey(paramValue[0])) {
				if (String[].class.isAssignableFrom(attributes.get(
						paramValue[0]).getClass())) {
					currentValue = (String[]) attributes.get(paramValue[0]);

				} else {
					currentValue = (String[]) ArrayUtils.add(
							(String[]) currentValue,
							attributes.get(paramValue[0]));
				}

				attributes.put(paramValue[0], (String[]) ArrayUtils.add(
						(String[]) currentValue, paramValue[1]));

			} else {
				attributes.put(paramValue[0], paramValue[1]);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void sendFileStream(HttpServletRequest request, HttpServletResponse response, FileInfo fileInfo) {
		try {
			DownloadEventHandler eventHandler = null;

			if (downloadEventHandlers != null) {
				for (Map.Entry<String, DownloadEventHandler> e : downloadEventHandlers.entrySet()) {
					if(pathMatcher.match(e.getKey(), request.getRequestURI()))	{
						eventHandler = e.getValue();
						break;
					}
				}
			}

			if(eventHandler == null)	{
				eventHandler = defaultDownloadEventHandler;
			}

			FileSender sender = null;

			if (!senders.isEmpty()) {
				for (Map.Entry<String, FileSender> senderEntry : senders.entrySet()) {
					if (pathMatcher.match(senderEntry.getKey(), request.getRequestURI())) {
						sender = senderEntry.getValue();
						break;
					}
				}
			} else {
				throw new MciException("[JCF-SUA] FileOperator - FieSender가 정의되어 있지 않습니다.");
			}

			sender.sendOctetStream(request, response, eventHandler, fileInfo.getFolder(), fileInfo.getName());

		} catch (IOException e) {
			if(logger.isErrorEnabled()){
				e.printStackTrace();
			}

			throw new MciException("[JCF-SUA] FileOperator - File Download 처리중 에러 발생", e);
		}
	}

	/**
	 *
	 * 멀티파트 요청을 처리할 UploadEventHandler의 의존성 주입
	 *
	 * @param uploadEventHandlers
	 */
	public void setUploadEventHandlers(Map<String, UploadEventHandler> uploadEventHandlers) {
		this.uploadEventHandlers = uploadEventHandlers;
	}

	/**
	 * 멀티파트 요청을 처리할 DownloadEventHandler의 의존성 주입
	 *
	 * @param downloadEventHandlers
	 */
	public void setDownloadEventHandlers(Map<String, DownloadEventHandler> downloadEventHandlers) {
		this.downloadEventHandlers = downloadEventHandlers;
	}

	/**
	 *
	 * 멀티파트 파일 업로드 요청 처리를 위한 receivers의 의존성 주입
	 *
	 * @param receivers
	 */
	public void setReceivers(Map<String, MultiPartReceiver> receivers) {
		this.receivers = receivers;
	}

	/**
	 *
	 * 멀티파트 파일 다운로드 요청 처리를 위한 sender의 의존성 주입
	 *
	 * @param senders
	 */
	public void setSenders(Map<String, FileSender> senders) {
		this.senders = senders;
	}

	/**
	 *
	 * 요청 처리에 적합한 EventHandler 선택을 위한 patchMatcher 의존성 주입
	 *
	 * @param pathMatcher
	 */
	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	/**
	 *
	 * 임시파일을 저장할 임시디렉토리를 지정
	 *
	 * @param tempDir
	 */
	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

	/**
	 *
	 * 요청 패턴에 적합한 UploadEventHandler가 없을 경우 동작할 기본 핸들러의 의존성 주입
	 *
	 * @param defaultUploadEventHandler
	 */
	public void setDefaultUploadEventHandler(UploadEventHandler defaultUploadEventHandler) {
		this.defaultUploadEventHandler = defaultUploadEventHandler;
	}

	/**
	 *
	 * 요청 패턴에 적합한 DownloadEventHandler가 없을 경우 동작할 기본 핸들러의 의존성 주입
	 *
	 * @param defaultDownloadEventHandler
	 */
	public void setDefaultDownloadEventHandler(DownloadEventHandler defaultDownloadEventHandler) {
		this.defaultDownloadEventHandler = defaultDownloadEventHandler;
	}
}
