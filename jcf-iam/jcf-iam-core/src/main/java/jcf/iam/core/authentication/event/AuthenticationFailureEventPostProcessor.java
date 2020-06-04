package jcf.iam.core.authentication.event;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 *
 * 로그인 실패시 동작하는 이벤트 프로세서를 구현하기 위한 인터페이스
 *
 * @see jcf.iam.core.authentication.event.AuthenticationFailureEventListener
 *
 * @author nolang
 *
 */
public interface AuthenticationFailureEventPostProcessor {

	/**
	 *
	 * 로그인 실패시 동작하는 이벤트 처리기
	 *
	 * @param authentication
	 * @param timestamp
	 * @param authenticationException
	 */
	void execute(Authentication authentication, long timestamp, AuthenticationException authenticationException);

}
