package jcf.iam.core.authentication.openid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;

/**
 *
 * OpenId 인증실패 관련 이벤트 처리기
 *
 * @see jcf.iam.core.authentication.openid.OpenIdAuthenticationFailureHandler
 *
 * @author
 *
 */
public interface OpenIdAuthenticationFailureProcessor {

	/**
	 *
	 * OpenId 인증실패 관련 이벤트 처리기
	 *
	 * @param request
	 * @param response
	 * @param exception
	 */
	void processor(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception);

}
