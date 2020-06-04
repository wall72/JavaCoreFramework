package jcf.iam.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcf.iam.core.authentication.userdetails.model.Role;
import jcf.iam.core.authentication.userdetails.model.User;
import jcf.iam.core.authentication.userdetails.model.UserAuthority;
import jcf.iam.core.authorization.service.model.SecuredResourcesRoles;
import jcf.iam.core.authorization.service.model.SecuredResourcesUsers;
import jcf.iam.core.authorization.service.model.ViewResources;
import jcf.iam.core.authorization.service.model.ViewResourcesRoles;
import jcf.iam.core.authorization.service.model.ViewResourcesUsers;
import jcf.iam.core.common.exception.IamException;
import jcf.iam.core.jdbc.authentication.AuthorityMapping;
import jcf.iam.core.jdbc.authentication.RoleMapping;
import jcf.iam.core.jdbc.authentication.UserMapping;
import jcf.iam.core.jdbc.authorization.PermissionMapping;
import jcf.iam.core.jdbc.authorization.SecuredResourcesMapping;
import jcf.iam.core.jdbc.authorization.ViewResourcesMapping;
import jcf.iam.core.jdbc.authorization.ViewResourcesRoleMapping;

import org.springframework.beans.factory.InitializingBean;

/**
 * <pre>
 * JCF-IAM Default Customizer
 * <pre>
 *
 * @see    jcf.iam.core.Customizer
 * @see    jcf.iam.core.IamCustomizerFactory
 * @see    jcf.iam.core.IamMappingClass
 *
 * @author nolang
 *
 */
public final class DefaultCustomizer implements Customizer, InitializingBean {

	private boolean useDefaultConfiguation = true;

	private boolean useJcfIamAcl = false;

	private List<String> customParameterList;

	private Map<IamMappingClass, String> mappingClasses = new HashMap<IamMappingClass, String>();

	public boolean isUseJcfIamAcl() {
		return useJcfIamAcl;
	}

	/**
	 * JCFIAM Default 설정을 사용할지 여부를 설정한다.
	 *
	 * @param userDefaultConfiguation
	 */
	public void setUseDefaultConfiguation(boolean useDefaultConfiguation) {
		this.useDefaultConfiguation = useDefaultConfiguation;
	}

	/**
	 * JCF IAM에서 제공하는 ACL 기능을 사용할지 여부를 저장한다.
	 *
	 * @param useJcfIamAcl
	 */
	public void setUseJcfIamAcl(boolean useJcfIamAcl) {
		this.useJcfIamAcl = useJcfIamAcl;
	}

	public List<String> getCustomParameterList() {
		return customParameterList;
	}

	/**
	 *
	 * CustomParameter 저장
	 *
	 * @param customParameterList
	 */
	public void setCustomParameterList(List<String> customParameterList) {
		this.customParameterList = customParameterList;
	}

	public Class<? extends UserMapping> getUserClass() {
		String className = mappingClasses.get(IamMappingClass.USER);

		if (className == null) {
			throw new IamException("[DefaultCustomizer] getUserClass - 사용자 인증처리 모델이 정의되지 않았습니다.");
		}

		return getClass(className).asSubclass(UserMapping.class);
	}

	/**
	 *
	 * 사용자 인증 처리 모델 설정
	 *
	 * @param userClass
	 */
	public void setUserClassName(String userClass) {
		synchronized (mappingClasses) {
			mappingClasses.put(IamMappingClass.USER, userClass);
		}
	}

	public Class<? extends AuthorityMapping> getAuthorityClass() {
		String className = mappingClasses.get(IamMappingClass.AUTHORITY);

		if (className == null) {
			throw new IamException("[DefaultCustomizer] getAuthorityClass - 사용자별 권한 매핑 처리 모델이 정의되지 않았습니다.");
		}

		return getClass(className).asSubclass(AuthorityMapping.class);
	}

	/**
	 *
	 * 사용자별 권한 매핑 처리 모델 설정
	 *
	 * @param className
	 */
	public void setAuthorityClassName(String className) {
		synchronized (mappingClasses) {
			mappingClasses.put(IamMappingClass.AUTHORITY, className);
		}
	}

	public Class<? extends RoleMapping> getRoleClass() {
		String className = mappingClasses.get(IamMappingClass.ROLE);

		if (className == null) {
			throw new IamException("[DefaultCustomizer] getRoleClass - 시스템 권한 정의 처리 모델이 정의되지 않았습니다.");
		}

		return getClass(className).asSubclass(RoleMapping.class);
	}

	/**
	 *
	 * 시스템 권한 정의 처리 모델 설정
	 *
	 * @param className
	 */
	public void setRoleClassName(String className) {
		synchronized (mappingClasses) {
			mappingClasses.put(IamMappingClass.ROLE, className);
		}
	}

	public Class<? extends SecuredResourcesMapping> getSecuredRoleMappingClass() {
		if (!useJcfIamAcl) {
			return null;
		}

		String className = mappingClasses.get(IamMappingClass.SECURED_RESOURCES_ROLES);

		if(!useDefaultConfiguation && className == null){
			return null;
		}

		if (className == null) {
			throw new IamException("[DefaultCustomizer] getSecuredRoleMappingClass - 권한별 인가 자원 처리 모델이 정의되지 않았습니다.");
		}

		return getClass(className).asSubclass(SecuredResourcesMapping.class);
	}

	/**
	 *
	 * 권한별 인가 자원 처리 모델 설정
	 *
	 * @param className
	 */
	public void setSecuredRoleMappingClassName(String className)	{
		synchronized (mappingClasses) {
			mappingClasses.put(IamMappingClass.SECURED_RESOURCES_ROLES, className);
		}
	}

	public Class<? extends SecuredResourcesMapping> getSecuredUserMappingClass() {
		if (!useJcfIamAcl) {
			return null;
		}

		String className = mappingClasses.get(IamMappingClass.SECURED_RESOURCES_USERS);

		if(!useDefaultConfiguation && className == null){
			return null;
		}

		if (className == null) {
			throw new IamException("[DefaultCustomizer] getSecuredUserMappingClass - 사용자별 인가 자원 처리 모델이 정의되지 않았습니다.");
		}

		return getClass(className).asSubclass(SecuredResourcesMapping.class);
	}

	/**
	 *
	 * 사용자별 인가 자원 처리 모델 설정
	 *
	 * @param className
	 */
	public void setSecuredUserMappingClassName(String className)	{
		synchronized (mappingClasses) {
			mappingClasses.put(IamMappingClass.SECURED_RESOURCES_USERS, className);
		}
	}

	public Class<? extends PermissionMapping> getPermissionClass() {
		if (!useJcfIamAcl) {
			return null;
		}

		String className = mappingClasses.get(IamMappingClass.PERMISSION);

		if(!useDefaultConfiguation && className == null){
			return null;
		}

		if (className == null) {
			throw new IamException("[DefaultCustomizer] getPermissionClass - 시스템 정의 리소스 세부 접근 권한  처리 모델이 정의되지 않았습니다.");
		}

		return getClass(className).asSubclass(PermissionMapping.class);
	}

	/**
	 *
	 * 시스템 정의 리소스 세부 접근 권한 처리 모델 설정
	 *
	 * @param className
	 */
	public void setPermissionClassName(String className)	{
		synchronized (mappingClasses) {
			mappingClasses.put(IamMappingClass.PERMISSION, className);
		}
	}

	public Class<? extends ViewResourcesRoleMapping> getPermissionByUserClass() {
		if (!useJcfIamAcl) {
			return null;
		}

		String className = mappingClasses.get(IamMappingClass.PERMISSION_USERS);

		if(!useDefaultConfiguation && className == null){
			return null;
		}

		if (className == null) {
			throw new IamException("[DefaultCustomizer] getPermissionByUserClass - 사용자별 인가 자원 접근 권한 처리 모델이 정의되지 않았습니다.");
		}

		return getClass(className).asSubclass(ViewResourcesRoleMapping.class);
	}

	/**
	 *
	 * 사용자별 인가 자원 접근 권한 처리 모델 설정
	 *
	 * @param className
	 */
	public void setPermissionByUserClassName(String className)	{
		synchronized (mappingClasses) {
			mappingClasses.put(IamMappingClass.PERMISSION_USERS, className);
		}
	}

	public Class<? extends ViewResourcesRoleMapping> getPermissionByRoleClass() {
		if (!useJcfIamAcl) {
			return null;
		}

		String className = mappingClasses.get(IamMappingClass.PERMISSION_ROLES);

		if(!useDefaultConfiguation && className == null){
			return null;
		}

		if (className == null) {
			throw new IamException("[DefaultCustomizer] getPermissionByRoleClass - 권한별 인가 자원 접근 권한 처리 모델이 정의되지 않았습니다.");
		}

		return getClass(className).asSubclass(ViewResourcesRoleMapping.class);
	}

	/**
	 *
	 * 권한별 인가 자원 접근 권한 처리 모델 설정
	 *
	 * @param className
	 */
	public void setPermissionByRoleClassName(String className)	{
		synchronized (mappingClasses) {
			mappingClasses.put(IamMappingClass.PERMISSION_ROLES, className);
		}
	}

	public Class<? extends ViewResourcesMapping> getViewResourceClass() {
		if (!useJcfIamAcl) {
			return null;
		}

		String className = mappingClasses.get(IamMappingClass.VIEW_RESOURCES);

		if(!useDefaultConfiguation && className == null){
			return null;
		}

		if (className == null) {
			throw new IamException("[DefaultCustomizer] getViewResourceClass - ViewResource (화면) 처리 모델이 정의되지 않았습니다.");
		}

		return getClass(className).asSubclass(ViewResourcesMapping.class);
	}

	/**
	 *
	 * ViewResource (화면) 처리 모델 설정
	 *
	 * @param className
	 */
	public void setViewResourceClassName(String className)	{
		synchronized (mappingClasses) {
			mappingClasses.put(IamMappingClass.VIEW_RESOURCES, className);
		}
	}

	private Class<?> getClass(String className)	{
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IamException("[DefaultCustomizer] ClassNotFound - " + className, e);
		}
	}

	public void afterPropertiesSet() throws Exception {
		synchronized (mappingClasses) {
			if (useDefaultConfiguation) {
				if (!mappingClasses.containsKey(IamMappingClass.USER)) {
					mappingClasses.put(IamMappingClass.USER, User.class.getName());
				}

				if (!mappingClasses.containsKey(IamMappingClass.ROLE)) {
					mappingClasses.put(IamMappingClass.ROLE, Role.class.getName());
				}

				if (!mappingClasses.containsKey(IamMappingClass.AUTHORITY)) {
					mappingClasses.put(IamMappingClass.AUTHORITY, UserAuthority.class.getName());
				}

				if (useJcfIamAcl) {
					if (!mappingClasses.containsKey(IamMappingClass.SECURED_RESOURCES_ROLES)) {
						mappingClasses.put(IamMappingClass.SECURED_RESOURCES_ROLES, SecuredResourcesRoles.class.getName());
					}

					if (!mappingClasses.containsKey(IamMappingClass.SECURED_RESOURCES_USERS)) {
						mappingClasses.put(IamMappingClass.SECURED_RESOURCES_USERS, SecuredResourcesUsers.class.getName());
					}

					if (!mappingClasses.containsKey(IamMappingClass.PERMISSION)) {
						mappingClasses.put(IamMappingClass.PERMISSION, PermissionMapping.class.getName());
					}

					if (!mappingClasses.containsKey(IamMappingClass.PERMISSION_ROLES)) {
						mappingClasses.put(IamMappingClass.PERMISSION_ROLES, ViewResourcesRoles.class.getName());
					}

					if (!mappingClasses.containsKey(IamMappingClass.PERMISSION_USERS)) {
						mappingClasses.put(IamMappingClass.PERMISSION_USERS, ViewResourcesUsers.class.getName());
					}

					if (!mappingClasses.containsKey(IamMappingClass.VIEW_RESOURCES)) {
						mappingClasses.put(IamMappingClass.VIEW_RESOURCES, ViewResources.class.getName());
					}

				}
			}
		}
	}
}
