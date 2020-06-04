package jcf.iam.core.filter.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 *
 * @author nolang
 *
 */
public abstract class AbstractIamAuthenticationFailureHandler implements AuthenticationFailureHandler, InitializingBean {

	private SimpleUrlAuthenticationFailureHandler failureHandler;
	private String defaultFailureUrl;
	private boolean forwardToDestination = false;
	private boolean allowSessionCreation = true;
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		if(isMciRequest(request)){
			onMciAuthenticationFailure(request, response, exception);
		} else {
			failureHandler.onAuthenticationFailure(request, response, exception);
		}
	}

	/**
	 *
	 * @param request
	 * @return
	 */
	protected abstract boolean isMciRequest(HttpServletRequest request);

	/**
	 *
	 * @param request
	 * @param response
	 * @param exception
	 */
	protected abstract void onMciAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception);

	/**
	 *
	 * @param allowSessionCreation
	 */
	public void setAllowSessionCreation(boolean allowSessionCreation) {
		this.allowSessionCreation = allowSessionCreation;
	}

	/**
	 *
	 * @param defaultFailureUrl
	 */
	public void setDefaultFailureUrl(String defaultFailureUrl) {
		this.defaultFailureUrl = defaultFailureUrl;
	}

	/**
	 *
	 * @param forwardToDestination
	 */
	public void setForwardToDestination(boolean forwardToDestination) {
		this.forwardToDestination = forwardToDestination;
	}

	/**
	 *
	 * @param redirectStrategy
	 */
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	public void afterPropertiesSet() throws Exception {
		failureHandler = new SimpleUrlAuthenticationFailureHandler();

		failureHandler.setAllowSessionCreation(allowSessionCreation);
		failureHandler.setDefaultFailureUrl(defaultFailureUrl);
		failureHandler.setRedirectStrategy(redirectStrategy);
		failureHandler.setUseForward(forwardToDestination);

	}
}
