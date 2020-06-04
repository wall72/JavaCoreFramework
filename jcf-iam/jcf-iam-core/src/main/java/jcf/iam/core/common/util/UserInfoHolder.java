package jcf.iam.core.common.util;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <pre>
 * 인증된 사용자 정보에 접근하기 위한 Helper 클래스
 * <pre>
 *
 * @see    jcf.iam.core.Customizer
 * @see    jcf.iam.core.DefaultCustomizer
 * @see    jcf.iam.core.jdbc.authentication.UserMapping
 *
 * @author nolang
 *
 */
public class UserInfoHolder {

	/**
	 * <pre>
	 * 로그인한 사용자 정보 조회
	 * <pre>
	 *
	 * @param <T>
	 * @param clazz - 로그인을 위해 사용된 Query 클래스
	 * @return
	 */
	public static <T> T getUserInfo(Class<? extends UserDetails> clazz)	{
		return (T) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	/**
	 * 로그인한 사용자 권한 조회
	 *
	 * @return
	 */
	public static Collection<GrantedAuthority> getUserAuthorities()	{
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
	}


}
