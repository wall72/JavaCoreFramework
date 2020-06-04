package jcf.iam.core.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import jcf.iam.core.authentication.pki.PkiProcessingHandler;
import jcf.iam.core.authentication.ria.RiaParameterProcessingHandler;
import jcf.iam.core.authentication.sso.SsoProcessingHandler;
import jcf.iam.core.filter.request.ExternalAuthenticationToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 *
 * SSO / PKI 인증 처리를 위한 확장점을 제공한다.
 *
 * @author nolang
 *
 */
public class ExternalAuthenticationProcessingFilter extends GenericFilterBean {

	@Autowired (required = false)
	private UsernamePasswordAuthenticationFilter authenticationFilter;

	private SsoProcessingHandler ssoProcessingHandler;

	private PkiProcessingHandler pkiProcessingHandler;

	private RiaParameterProcessingHandler riaParameterProcessingHandler;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if (authenticationFilter != null && requiresAuthentication((HttpServletRequest) request, (HttpServletResponse) response, authenticationFilter.getFilterProcessesUrl())) {
			String usernameParameter = authenticationFilter.getUsernameParameter();
			String passwordParameter = authenticationFilter.getPasswordParameter();

			ExternalAuthenticationToken token = getExternalAuthenticationToken((HttpServletRequest) request, (HttpServletResponse) response);

			if (token != null) {
				ExternalHttpServletRequestWrapper requestWrapper = new ExternalHttpServletRequestWrapper((HttpServletRequest) request);

				requestWrapper.addParameter(usernameParameter, token.getUsername());
				requestWrapper.addParameter(passwordParameter, token.getPassword());

				chain.doFilter(requestWrapper, response);

				return;
			}
		}

		chain.doFilter(request, response);
	}

	private ExternalAuthenticationToken getExternalAuthenticationToken(HttpServletRequest request, HttpServletResponse response) {
		ExternalAuthenticationToken token = null;

		if (ssoProcessingHandler != null && ssoProcessingHandler.isSsoAuthRequest(request)) {
			token = ssoProcessingHandler.authenticate(request, response);
		}

		if (token == null && pkiProcessingHandler != null && pkiProcessingHandler.isPkiAuthRequest(request)) {
			token = pkiProcessingHandler.authenticate(request, response);
		}

		if (token == null && riaParameterProcessingHandler != null) {
			token = riaParameterProcessingHandler.getAuthenticationToken(request);
		}

		return token;
	}

	private boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response, String filterProcessesUrl) {
        String uri = request.getRequestURI();
        int pathParamIndex = uri.indexOf(';');

        if (pathParamIndex > 0) {
            // strip everything after the first semi-colon
            uri = uri.substring(0, pathParamIndex);
        }

        if ("".equals(request.getContextPath())) {
            return uri.endsWith(filterProcessesUrl);
        }

        return uri.endsWith(request.getContextPath() + filterProcessesUrl);
    }

    private class ExternalHttpServletRequestWrapper extends HttpServletRequestWrapper	{

    	private Map<String, String> params = new HashMap<String, String>();

    	public ExternalHttpServletRequestWrapper(HttpServletRequest request) {
    		super(request);
    	}

    	public void addParameter(String name, String value) {
    		params.put(name, value);
    	}

    	public String getParameter(String name) {
    		String paramValue = params.get(name);

    		if(StringUtils.hasText(paramValue)){
    			return paramValue;
    		}

    		return super.getParameter(name);
    	}
    }

	public void setAuthenticationFilter(UsernamePasswordAuthenticationFilter authenticationFilter) {
		this.authenticationFilter = authenticationFilter;
	}

    /**
     *
     * SSO 요청 처리를 위한 Handler
     *
     * @param authenticationFilter
     */
	public void setSsoProcessingHandler(SsoProcessingHandler ssoProcessingHandler) {
		this.ssoProcessingHandler = ssoProcessingHandler;
	}

	/**
	 *
	 * PKI 요청 처리를 위한 Handler
	 *
	 * @param pkiProcessingHandler
	 */
	public void setPkiProcessingHandler(PkiProcessingHandler pkiProcessingHandler) {
		this.pkiProcessingHandler = pkiProcessingHandler;
	}

	/**
	 *
	 * RIA 인증을 처리하기 위한 Handler
	 *
	 * @param riaParameterProcessingHandler
	 */
	public void setRiaParameterProcessingHandler(
			RiaParameterProcessingHandler riaParameterProcessingHandler) {
		this.riaParameterProcessingHandler = riaParameterProcessingHandler;
	}
}
