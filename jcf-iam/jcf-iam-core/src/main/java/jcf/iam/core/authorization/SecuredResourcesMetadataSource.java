package jcf.iam.core.authorization;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.RequestKey;
import org.springframework.security.web.util.UrlMatcher;

/**
 *
 * 인가 대상 자원 저장소
 *
 * @see jcf.iam.core.authorization.SecuredResourcesMappingFactoryBean
 * @see jcf.iam.core.authorization.service.SecuredResourcesService
 *
 * @author nolang
 *
 */
public class SecuredResourcesMetadataSource extends
		DefaultFilterInvocationSecurityMetadataSource {

	public SecuredResourcesMetadataSource(UrlMatcher urlPathMatcher,
			LinkedHashMap<RequestKey, Collection<ConfigAttribute>> resources) {
		super(urlPathMatcher, resources);
	}
}
