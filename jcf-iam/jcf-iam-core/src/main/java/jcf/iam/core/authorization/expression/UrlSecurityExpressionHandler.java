package jcf.iam.core.authorization.expression;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebSecurityExpressionHandler;

/**
 *
 * JCF-IAM ACL (URL) 기능 지원을 위한 ExpressionHandler
 *
 * @see jcf.iam.core.authorization.expression.UrlSecurityExpressionRoot
 *
 * @author nolang
 *
 */
public class UrlSecurityExpressionHandler implements
		WebSecurityExpressionHandler {

	private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private ExpressionParser expressionParser = new SpelExpressionParser();
    private RoleHierarchy roleHierarchy;

    public ExpressionParser getExpressionParser() {
        return expressionParser;
    }

    public EvaluationContext createEvaluationContext(Authentication authentication, FilterInvocation fi) {
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        SecurityExpressionRoot root = new UrlSecurityExpressionRoot(authentication, fi);
        root.setTrustResolver(trustResolver);
        root.setRoleHierarchy(roleHierarchy);
        ctx.setRootObject(root);

        return ctx;
    }

    public void setRoleHierarchy(RoleHierarchy roleHierarchy) {
        this.roleHierarchy = roleHierarchy;
    }
}
