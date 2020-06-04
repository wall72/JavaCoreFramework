package jcf.iam.core.authentication.openid;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAuthenticationStatus;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 *
 * OpenID 인증 사용시 동작하며, 인증 실패와 관련한 이벤트를 처리한다.
 *
 * @see jcf.iam.core.authentication.openid.OpenIdAuthenticationFailureProcessor
 *
 * @author nolang
 *
 */
public class OpenIdAuthenticationFailureHandler extends
		SimpleUrlAuthenticationFailureHandler {

	private OpenIdAuthenticationFailureProcessor authenticationFailureProcessor = new OpenIdAuthenticationFailureProcessor() {
		public void processor(HttpServletRequest request,
				HttpServletResponse response, AuthenticationException exception) {
			// dummy processor
		}
	};

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {

		if(support(exception)){
			/**
			 * 로그인한 openid와 매핑되는 로컬id가 존재하지 않을 경우 등록해야함..
			 */
			authenticationFailureProcessor.processor(request, response, exception);
		}

		super.onAuthenticationFailure(request, response, exception);
	}

	private boolean support(AuthenticationException exception)	{
		return exception instanceof UsernameNotFoundException && exception.getAuthentication() instanceof OpenIDAuthenticationToken
		&& ((OpenIDAuthenticationToken) exception.getAuthentication()).getStatus().equals(OpenIDAuthenticationStatus.SUCCESS);
	}

	public void setAuthenticationFailureProcessor(OpenIdAuthenticationFailureProcessor authenticationFailureProcessor) {
		this.authenticationFailureProcessor = authenticationFailureProcessor;
	}
}
