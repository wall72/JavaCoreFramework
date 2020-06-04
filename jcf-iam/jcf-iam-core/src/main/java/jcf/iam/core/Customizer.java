package jcf.iam.core;

import java.util.List;

import jcf.iam.core.jdbc.authentication.AuthorityMapping;
import jcf.iam.core.jdbc.authentication.RoleMapping;
import jcf.iam.core.jdbc.authentication.UserMapping;
import jcf.iam.core.jdbc.authorization.PermissionMapping;
import jcf.iam.core.jdbc.authorization.SecuredResourcesMapping;
import jcf.iam.core.jdbc.authorization.ViewResourcesMapping;
import jcf.iam.core.jdbc.authorization.ViewResourcesRoleMapping;

/**
 * <pre>
 * 사용자 정의 커스터마이저를 구현하기 위한 인터페이스
 * <pre>
 *
 * @see    jcf.iam.core.IamCustomizerFactory
 * @see    jcf.iam.core.DefaultCustomizer
 * @see    jcf.iam.core.jdbc.authentication.UserMapping
 * @see    jcf.iam.core.jdbc.authentication.AuthorityMapping
 * @see    jcf.iam.core.jdbc.authentication.RoleMapping
 * @see    jcf.iam.core.jdbc.authorization.SecuredResourcesMapping
 * @see    jcf.iam.core.jdbc.authorization.ViewResourcesRoleMapping
 * @see    jcf.iam.core.jdbc.authorization.ViewResourcesMapping
 * @see    jcf.iam.core.jdbc.authorization.PermissionMapping
 *
 * @author nolang
 *
 */
public interface Customizer {

	/**
	 * <pre>
	 * JCF-IAM 의 ACL 기능을 사용할지 여부를 결정한다.
	 *
	 *  - true : 사용
	 *  - false : 사용하지 않음
	 * <pre>
	 *
	 * @return
	 */
	boolean isUseJcfIamAcl();

	/**
	 *
	 * 인증의 필요로 인해 별도로 처리할  부가 정보(회사/부서 코드 등등)를 지칭하는 RequestParameter의 이름을 반환한다.
	 * 여기서 정의된 값들은 SessionScope에서 별도 관리된다.
	 *
	 * @return
	 */
	List<String> getCustomParameterList();

	/**
	 *
	 * 사용자 인증처리 모델 클래스를 반환한다. (사용자 조회 쿼리)
	 *
	 * @return
	 */
	Class<? extends UserMapping> getUserClass();

	/**
	 * 사용자별 권한 매핑 처리 모델 클래스를 반환한다. (사용자 - 권한 매핑 쿼리)
	 *
	 * @return
	 */
	Class<? extends AuthorityMapping> getAuthorityClass();

	/**
	 *
	 * 시스템 권한 정의 처리 모델 클래스를 반환한다.
	 *
	 * @return
	 */
	Class<? extends RoleMapping> getRoleClass();

	/**
	 *
	 * 권한별 인가 자원 처리 모델 클래스를 반환한다. (Optional : useJcfIam = 'true' 일때만 사용됨)
	 *
	 * @return
	 */
	Class<? extends SecuredResourcesMapping> getSecuredRoleMappingClass();

	/**
	 *
	 * 사용자별 인가 자원 처리 모델 클래스를 반환한다. (Optional : useJcfIam = 'true' 일때만 사용됨)
	 *
	 * @return
	 */
	Class<? extends SecuredResourcesMapping> getSecuredUserMappingClass();

	/**
	 *
	 * 리소스 세부 접근 권한 처리 모델 클래스를 반환한다.
	 *
	 * @return
	 */
	Class<? extends PermissionMapping> getPermissionClass();

	/**
	 *
	 * 사용자별 인가 자원 접근 권한 처리 모델 클래스를 반환한다. (Optional : useJcfIam = 'true' 일때만 사용됨)
	 *
	 * @return
	 */
	Class<? extends ViewResourcesRoleMapping> getPermissionByUserClass();

	/**
	 *
	 * 권한별 인가 자원 접근 권한 처리 모델 클래스를 반환한다. (Optional : useJcfIam = 'true' 일때만 사용됨)
	 *
	 * @return
	 */
	Class<? extends ViewResourcesRoleMapping> getPermissionByRoleClass();

	/**
	 *
	 * ViewResource (화면) 처리 모델 클래스를 반환한다. - JCF-IAM Admin 작업 수행시 사용됨
	 *
	 * @return
	 */
	Class<? extends ViewResourcesMapping> getViewResourceClass();
}