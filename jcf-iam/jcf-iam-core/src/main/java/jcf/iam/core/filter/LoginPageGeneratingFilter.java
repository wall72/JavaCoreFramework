package jcf.iam.core.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.web.filter.GenericFilterBean;

/**
 *
 * Default 로그인 페이지 생성 필터
 *
 * @author nolang
 *
 */
public class LoginPageGeneratingFilter extends GenericFilterBean {

	public static final String DEFAULT_LOGIN_PAGE_URL = "/spring_security_login";
    public static final String ERROR_PARAMETER_NAME = "login_error";

    boolean formLoginEnabled;
    boolean openIdEnabled;
    private String authenticationUrl;
    private String usernameParameter;
    private String passwordParameter;
    private String rememberMeParameter;
    private String openIDauthenticationUrl;
    private String openIDusernameParameter;
    private String openIDrememberMeParameter;

    public LoginPageGeneratingFilter() {
	}

    public LoginPageGeneratingFilter(AbstractAuthenticationProcessingFilter filter) {
        if (filter instanceof UsernamePasswordAuthenticationFilter) {
            init((UsernamePasswordAuthenticationFilter)filter, null);
        } else {
            init(null, filter);
        }
    }

    public LoginPageGeneratingFilter(UsernamePasswordAuthenticationFilter authFilter, AbstractAuthenticationProcessingFilter openIDFilter) {
        init(authFilter, openIDFilter);
    }

    private void init(UsernamePasswordAuthenticationFilter authFilter, AbstractAuthenticationProcessingFilter openIDFilter) {
        if (authFilter != null) {
            formLoginEnabled = true;
            authenticationUrl = authFilter.getFilterProcessesUrl();
            usernameParameter = authFilter.getUsernameParameter();
            passwordParameter = authFilter.getPasswordParameter();

            if (authFilter.getRememberMeServices() instanceof AbstractRememberMeServices) {
                rememberMeParameter = ((AbstractRememberMeServices)authFilter.getRememberMeServices()).getParameter();
            }
        }

        if (openIDFilter != null) {
            openIdEnabled = true;
            openIDauthenticationUrl = openIDFilter.getFilterProcessesUrl();
            openIDusernameParameter = "openid_identifier";

            if (openIDFilter.getRememberMeServices() instanceof AbstractRememberMeServices) {
                openIDrememberMeParameter = ((AbstractRememberMeServices)openIDFilter.getRememberMeServices()).getParameter();
            }
        }
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (isLoginUrlRequest(request)) {
            String loginPageHtml = generateLoginPageHtml(request);
            response.setContentType("text/html;charset=UTF-8");
            response.setContentLength(loginPageHtml.length());
            response.getWriter().write(loginPageHtml);

            return;
        }

        chain.doFilter(request, response);
    }

    private String generateLoginPageHtml(HttpServletRequest request) {
        boolean loginError = request.getParameter(ERROR_PARAMETER_NAME) != null;
        String errorMsg = "none";
        String lastUser = "";

        if (loginError) {
            HttpSession session = request.getSession(false);

            if(session != null) {
                lastUser = (String) session.getAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_LAST_USERNAME_KEY);
                AuthenticationException ex = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
                errorMsg = ex != null ? ex.getMessage() : "none";
                if (lastUser == null) {
                    lastUser = "";
                }
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append("<html><head><title>JCF IAM Default Login Page</title></head>");

        if (formLoginEnabled) {
            sb.append("<body onload='document.f.").append(usernameParameter).append(".focus();'>\n");
        }

        if (loginError) {
            sb.append("<p><font color='red'>Your login attempt was not successful, try again.<br/><br/>Reason: ");
            sb.append(errorMsg);
            sb.append("</font></p>");
        }

        if (formLoginEnabled) {
            sb.append("<h3>Login with Username and Password</h3>");
            sb.append("<form name='f' action='").append(request.getContextPath()).append(authenticationUrl).append("' method='POST'>\n");
            sb.append(" <table>\n");
            sb.append("    <tr><td>User:</td><td><input type='text' name='");
            sb.append(usernameParameter).append("' value='").append(lastUser).append("'></td></tr>\n");
            sb.append("    <tr><td>Password:</td><td><input type='password' name='").append(passwordParameter).append("'/></td></tr>\n");

            if (rememberMeParameter != null) {
                sb.append("    <tr><td><input type='checkbox' name='").append(rememberMeParameter).append("'/></td><td>Remember me on this computer.</td></tr>\n");
            }

            sb.append("    <tr><td colspan='2'><input name=\"submit\" type=\"submit\"/></td></tr>\n");
            sb.append("    <tr><td colspan='2'><input name=\"reset\" type=\"reset\"/></td></tr>\n");
            sb.append("  </table>\n");
            sb.append("</form>");
        }

        if(openIdEnabled) {
            sb.append("<h3>Login with OpenID Identity</h3>");
            sb.append("<form name='oidf' action='").append(request.getContextPath()).append(openIDauthenticationUrl).append("' method='POST'>\n");
            sb.append(" <table>\n");
            sb.append("    <tr><td>Identity:</td><td><input type='text' name='");
            sb.append(openIDusernameParameter).append("'/></td></tr>\n");

            if (openIDrememberMeParameter != null) {
                sb.append("    <tr><td><input type='checkbox' name='").append(openIDrememberMeParameter).append("'></td><td>Remember me on this computer.</td></tr>\n");
            }

            sb.append("    <tr><td colspan='2'><input name=\"submit\" type=\"submit\"/></td></tr>\n");
            sb.append("    <tr><td colspan='2'><input name=\"reset\" type=\"reset\"/></td></tr>\n");
            sb.append("  </table>\n");
            sb.append("</form>");
        }

        sb.append("</body></html>");

        return sb.toString();
    }

    private boolean isLoginUrlRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int pathParamIndex = uri.indexOf(';');

        if (pathParamIndex > 0) {
            // strip everything after the first semi-colon
            uri = uri.substring(0, pathParamIndex);
        }

        if ("".equals(request.getContextPath())) {
            return uri.endsWith(DEFAULT_LOGIN_PAGE_URL);
        }

        return uri.endsWith(request.getContextPath() + DEFAULT_LOGIN_PAGE_URL);
    }

}
