package jcf.iam.core.authorization.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import jcf.iam.core.Customizer;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.authentication.userdetails.model.GrantedResourceAuthority;
import jcf.iam.core.jdbc.authentication.RoleMapping;
import jcf.iam.core.jdbc.authorization.SecuredResourcesMapping;
import jcf.query.core.QueryExecutorWrapper;
import jcf.query.core.evaluator.SimpleORMQueryType;
import jcf.query.core.mapper.ObjectRelationMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.access.intercept.RequestKey;
import org.springframework.util.StringUtils;

/**
 *
 * 시스템에 정의된 보호자원에 대한 접근 및 권한 정의와 관련한 연산을 수행한다.
 *
 * @see jcf.iam.core.authorization.SecuredResourcesMappingFactoryBean
 * @see jcf.iam.core.authorization.SecuredResourcesMetadataSource
 *
 * @author nolang
 *
 */
public class SecuredResourcesService {

	private static final Logger logger = LoggerFactory.getLogger(SecuredResourcesService.class);

	@Autowired
	private QueryExecutorWrapper queryExecutor;

	private ObjectRelationMapper resultMapper = new ObjectRelationMapper();

	@Autowired
	private IamCustomizerFactory customizerFactory;

	/**
	 *
	 * 시스템에 정의된 보호자원과 자원별 접근권한에 대한 매핑작업을 수행한다.
	 *
	 * @return
	 */
	public LinkedHashMap<RequestKey, Collection<ConfigAttribute>> loadSecuredResourceMapping() {
		final LinkedHashMap<RequestKey, Collection<ConfigAttribute>> resources = new LinkedHashMap<RequestKey, Collection<ConfigAttribute>>();

		final Customizer customizer = customizerFactory.getCustomizer();

		if(customizer == null)	{
			return resources;
		}

		/*
		 * 권한별 인가자원 목록 매핑
		 */
		final Class<? extends SecuredResourcesMapping> securedResourceRoleMapping = customizer.getSecuredRoleMappingClass();

		if(customizer.isUseJcfIamAcl() && securedResourceRoleMapping != null)	{
			queryExecutor.queryForList(SimpleORMQueryType.SELECT, BeanUtils.instantiate(securedResourceRoleMapping), new RowMapper<Object>() {
						public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

							SecuredResourcesMapping securedResources = resultMapper.mapper(rs, securedResourceRoleMapping);

							if (logger.isDebugEnabled()) {
								logger.debug(
										"[SecuredResourcesService] loadSecuredResourceMapping() - 권한별 인가자원 목록: resource id ={} pattenrn={} permission={} seq={}",
										new Object[] {
												securedResources.getResourceId(),
												securedResources.getResourcePattern(),
												securedResources.getResourcePermission(),
												securedResources.getOrderSeq() });
							}

							if(StringUtils.hasText(securedResources.getResourcePattern()))	{
								retrieveSecuredResources(resources, securedResources);
							}

							return null;
						}
					});
		}

		/*
		 * 사용자별 인가자원 목록 매핑
		 */
		final Class<? extends SecuredResourcesMapping> securedResourceUuserMapping = customizer.getSecuredUserMappingClass();

		if(customizer.isUseJcfIamAcl() && securedResourceUuserMapping != null)	{
			queryExecutor.queryForList(SimpleORMQueryType.SELECT, BeanUtils.instantiate(securedResourceUuserMapping), new RowMapper<Object>() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

					SecuredResourcesMapping securedResources = resultMapper.mapper(rs, securedResourceUuserMapping);

					if (logger.isDebugEnabled()) {
						logger.debug(
								"[SecuredResourcesService] loadSecuredResourceMapping() - 사용자별 인가자원 목록: resource id ={} pattenrn={} permission={} seq={}",
								new Object[] {
										securedResources.getResourceId(),
										securedResources.getResourcePattern(),
										securedResources.getResourcePermission(),
										securedResources.getOrderSeq() });
					}

					if(StringUtils.hasText(securedResources.getResourcePattern()))	{
						retrieveSecuredResources(resources, securedResources);
					}

					return null;
				}
			});
		}

		final Class<? extends RoleMapping> roleQuery = customizer.getRoleClass();

		if(roleQuery != null)	{
			queryExecutor.queryForList(SimpleORMQueryType.SELECT, BeanUtils.instantiate(roleQuery), new RowMapper<Object>() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

					final RoleMapping role = resultMapper.mapper(rs, roleQuery);

					if(!StringUtils.hasText(role.getEnabled()) || !role.getEnabled().equals("Y"))	{
						return null;
					}

					if (logger.isDebugEnabled()) {
						logger.debug(
								"[SecuredResourcesService] loadSecuredResourceMapping() - 권한별 기본 인가자원 목록: pattenrn={} role={}",
								new Object[] {
										"/**/*",
										role.getRoleId() });
					}

					retrieveAuthenticatedResources(resources, role);

					return null;
				}
			});
		} else {
			/**
			 * @TODO 시스템이 특별한 인증권한을 가지지 않을 경우 익명권한을 사용하도록 설정하는 부분을 추가한다.
			 */
		}

		return resources;
	}

	protected final void retrieveSecuredResources(final LinkedHashMap<RequestKey, Collection<ConfigAttribute>> resources, SecuredResourcesMapping securedResource)	{
		RequestKey requestKey = new RequestKey(securedResource.getResourcePattern());

		Collection<ConfigAttribute> attributes = resources.get(requestKey);

		if (attributes == null) {
			attributes = new ArrayList<ConfigAttribute>();
		}

		SecurityConfig securityConfig = new SecurityConfig(String.format(GrantedResourceAuthority.resourcePermissionStringFormat, securedResource.getResourceId(), securedResource.getResourcePermission()));

		if(!attributes.contains(securityConfig))	{
			attributes.add(securityConfig);
		}

		resources.put(requestKey, attributes);
	}

	protected final void retrieveAuthenticatedResources(final LinkedHashMap<RequestKey, Collection<ConfigAttribute>> resources, RoleMapping role)	{
		RequestKey requestKey = new RequestKey("/**/*");

		Collection<ConfigAttribute> attributes = resources.get(requestKey);

		if (attributes == null) {
			attributes = new ArrayList<ConfigAttribute>();
		}

		attributes.add(new SecurityConfig(role.getRoleId()));

		resources.put(requestKey, attributes);
	}

	public void setCustomizerFactory(IamCustomizerFactory customizerFactory) {
		this.customizerFactory = customizerFactory;
	}

	public void setQueryExecutor(QueryExecutorWrapper queryExecutor) {
		this.queryExecutor = queryExecutor;
	}
}
