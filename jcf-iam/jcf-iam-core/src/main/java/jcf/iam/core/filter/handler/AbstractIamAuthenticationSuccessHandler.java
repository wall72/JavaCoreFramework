package jcf.iam.core.filter.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 *
 * @author nolang
 *
 */
public abstract class AbstractIamAuthenticationSuccessHandler implements AuthenticationSuccessHandler, InitializingBean {

	private SimpleUrlAuthenticationSuccessHandler successHandler;
    private String targetUrlParameter = AbstractAuthenticationTargetUrlRequestHandler.DEFAULT_TARGET_PARAMETER;
    private String defaultTargetUrl = "/";
    private boolean alwaysUseDefaultTargetUrl = false;
    private boolean useReferer = false;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		if(isMciRequest(request)){
			onMciAuthenticationSuccess(request, response, authentication);
		} else {
			successHandler.onAuthenticationSuccess(request, response, authentication);
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
	 * @param authentication
	 */
	protected abstract void onMciAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

	/**
	 *
	 * @param alwaysUseDefaultTargetUrl
	 */
	public void setAlwaysUseDefaultTargetUrl(boolean alwaysUseDefaultTargetUrl) {
		this.alwaysUseDefaultTargetUrl = alwaysUseDefaultTargetUrl;
	}

	/**
	 *
	 * @param defaultTargetUrl
	 */
	public void setDefaultTargetUrl(String defaultTargetUrl) {
		this.defaultTargetUrl = defaultTargetUrl;
	}

	/**
	 *
	 * @param targetUrlParameter
	 */
	public void setTargetUrlParameter(String targetUrlParameter) {
		this.targetUrlParameter = targetUrlParameter;
	}

	/**
	 *
	 * @param useReferer
	 */
	public void setUseReferer(boolean useReferer) {
		this.useReferer = useReferer;
	}

	/**
	 *
	 * @param redirectStrategy
	 */
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	public void afterPropertiesSet() throws Exception {
		successHandler = new SimpleUrlAuthenticationSuccessHandler();

		successHandler.setAlwaysUseDefaultTargetUrl(alwaysUseDefaultTargetUrl);
		successHandler.setDefaultTargetUrl(defaultTargetUrl);
		successHandler.setRedirectStrategy(redirectStrategy);
		successHandler.setTargetUrlParameter(targetUrlParameter);
		successHandler.setUseReferer(useReferer);
	}
}
