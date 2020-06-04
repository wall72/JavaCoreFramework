package jcf.iam.core.authorization.expression;

import java.io.Serializable;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

/**
 *
 * JCF-IAM ACL (METHOD) 기능 지원을 위한 ExpressionHandler
 *
 * @see jcf.iam.core.authorization.expression.MethodSecurityExpressionRoot
 *
 * @author nolang
 *
 */
public class MethodSecurityExpressionHandler extends
		DefaultMethodSecurityExpressionHandler {

	private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
	private RoleHierarchy roleHierarchy;

	@Override
	public EvaluationContext createEvaluationContext(Authentication auth,
			MethodInvocation mi) {
		StandardEvaluationContext ctx = (StandardEvaluationContext) super
				.createEvaluationContext(auth, mi);

		MethodSecurityExpressionRoot root = new MethodSecurityExpressionRoot(
				auth);
		root.setTrustResolver(trustResolver);
		root.setRoleHierarchy(roleHierarchy);
		root.setPermissionEvaluator(new PermissionEvaluator() {

			public boolean hasPermission(Authentication authentication,
					Serializable targetId, String targetType, Object permission) {
				return false;
			}

			public boolean hasPermission(Authentication authentication,
					Object targetDomainObject, Object permission) {
				return false;
			}
		});

		ctx.setRootObject(root);

		return ctx;
	}

	public void setRoleHierarchy(RoleHierarchy roleHierarchy) {
		this.roleHierarchy = roleHierarchy;
	}
}
