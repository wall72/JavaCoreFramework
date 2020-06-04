package jcf.iam.core.authorization.expression;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

/**
 *
 * URL 접근제어를 위한 Expression 정의
 *
 * @see jcf.iam.core.authorization.expression.UrlSecurityExpressionHandler
 *
 * @author nolang
 *
 */
public class UrlSecurityExpressionRoot extends
		WebSecurityExpressionRoot {

	public UrlSecurityExpressionRoot(Authentication a,
			FilterInvocation fi) {
		super(a, fi);
	}

	/**
	 *
	 * URL 단위의 접근제어를 수행을 위한 체크메소드
	 *
	 * @param resourceId
	 * @param permission
	 * @return
	 */
	public boolean hasViewRole(String resourceId, String permission) {
		return hasRole(String.format("%s(%s)", resourceId, permission));
	}
}
