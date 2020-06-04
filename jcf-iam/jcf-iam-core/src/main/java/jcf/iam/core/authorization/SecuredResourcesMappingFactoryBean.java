package jcf.iam.core.authorization;

import java.util.Collection;
import java.util.LinkedHashMap;

import jcf.iam.core.authorization.service.SecuredResourcesService;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.access.intercept.RequestKey;

/**
 *
 * 인가 대상 자원을 조회하여 시스템에 등록한다.
 *
 * @see jcf.iam.core.authorization.SecuredResourcesMetadataSource
 * @see jcf.iam.core.authorization.service.SecuredResourcesService
 *
 * @author nolang
 *
 * @param <Map>
 */
public class SecuredResourcesMappingFactoryBean<Map> implements FactoryBean<Map> {

	private LinkedHashMap<RequestKey, Collection<ConfigAttribute>> resources = null;

	private SecuredResourcesService securedResourcesService;

	public void setSecuredResourcesService(SecuredResourcesService securedResourcesService) {
		this.securedResourcesService = securedResourcesService;
	}

	/**
	 * FactoryBean init-method
	 */
	public void init()	{
		resources = securedResourcesService.loadSecuredResourceMapping();
	}

	@SuppressWarnings("unchecked")
	public Map getObject() throws Exception {
		if(resources == null){
			synchronized (resources) {
				resources = securedResourcesService.loadSecuredResourceMapping();
			}
		}

		return (Map) resources;
	}

	public Class<?> getObjectType() {
		return LinkedHashMap.class;
	}

	public boolean isSingleton() {
		return true;
	}

	/**
	 * 인가 대상 자원 갱신
	 */
	public void reloadSecuredResourceMapping()	{
		resources = securedResourcesService.loadSecuredResourceMapping();
	}
}
