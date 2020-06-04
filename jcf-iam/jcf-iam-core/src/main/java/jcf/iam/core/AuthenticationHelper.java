package jcf.iam.core;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

/**
 *
 * @author nolang
 *
 */
public class AuthenticationHelper implements ApplicationContextAware {

	private static final String SPRING_SECURITY_FILTERCHAIN_NAME = "springSecurityFilterChain";

	private FilterChainProxy securityFilter;

	@Autowired
	private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;

	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		securityFilter = ctx.getBean(SPRING_SECURITY_FILTERCHAIN_NAME, FilterChainProxy.class);
	}

	/**
	 *
	 * 인증수행
	 *
	 * @param request
	 * @param response
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public SecurityContext authenticate(HttpServletRequest request, HttpServletResponse response, String username, String password) throws Exception {
		SecurityRequestWrapper requestWrapper = new SecurityRequestWrapper(request);

		requestWrapper.addParameter(usernamePasswordAuthenticationFilter.getUsernameParameter(), username);
		requestWrapper.addParameter(usernamePasswordAuthenticationFilter.getPasswordParameter(), password);

		securityFilter.doFilter(requestWrapper, response, null);

		return (SecurityContext) request.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
	}

	class SecurityRequestWrapper extends HttpServletRequestWrapper {

		private Map<String, String> params = new HashMap<String, String>();

		public SecurityRequestWrapper(HttpServletRequest request){
			super(request);
		}

		@Override
		public String getRequestURI() {
			return usernamePasswordAuthenticationFilter.getFilterProcessesUrl();
		}

		public void addParameter(String name, String value){
			params.put(name, value);
		}

		@Override
		public String getParameter(String name) {
			if(params.containsKey(name)){
				return params.get(name);
			}

			return super.getParameter(name);
		}
	}
}
