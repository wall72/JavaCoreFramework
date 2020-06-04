package jcf.iam.core.authentication.event;

import org.springframework.security.core.Authentication;

/**
 *
 * 로그인 성공시 동작하는 이벤트 프로세서를 구현하기 위한 인터페이스
 *
 * @author nolang
 *
 */
public interface AuthenticationSuccessEventProcessor {

	/**
	 *
	 * 로그인 성공시 동작하는 이벤트 처리기
	 *
	 * @param authentication
	 * @param timestamp
	 */
	void execute(Authentication authentication, long timestamp);

}
