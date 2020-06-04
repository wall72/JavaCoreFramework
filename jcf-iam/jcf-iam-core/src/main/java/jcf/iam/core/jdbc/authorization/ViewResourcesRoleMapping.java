package jcf.iam.core.jdbc.authorization;

import jcf.iam.core.jdbc.SecurityStatement;

/**
 * <pre>
 * 권한/사용자별 인가 자원 접근 권한 처리 모델 객체를 정의하기 위해 구현한다.
 *
 * ex)
 *    - 권한별 인가 자원 관리 테이블의 정의가 아래와 같다고 가정할 때..
 *
 *      create table JCFIAM_VIEW_RESOURCES_ROLES
 *      (
 *        VIEW_RESOURCE_ID    VARCHAR2(16) not null,
 *        ROLE_ID             VARCHAR2(16) not null,
 *        PERMISSION_ID       VARCHAR2(16) not null,
 *        CREATE_DATE         DATE,
 *        CREATE_USER_ID      VARCHAR2(16),
 *        LAST_MODIFY_DATE    DATE,
 *        LAST_MODIFY_USER_ID VARCHAR2(16)
 *      );
 *      alter table JCFIAM_VIEW_RESOURCES_ROLES
 *        add constraint JCFIAM_VIEW_RESRC_ROLES_FK02 foreign key (PERMISSION_ID)
 *        references JCFIAM_PERMISSIONS (PERMISSION_ID);
 *
 *      create table JCFIAM_PERMISSIONS
 *      (
 *        PERMISSION_ID       VARCHAR2(16),
 *        DESCRIPTION         VARCHAR2(128),
 *        MASK                NUMBER(8),
 *        CREATE_DATE         DATE,
 *        CREATE_USER_ID      VARCHAR2(16),
 *        LAST_MODIFY_DATE    DATE,
 *        LAST_MODIFY_USER_ID VARCHAR2(16)
 *      );
 *
 *
 *    - 위의 테이블과 매핑되는 권한별 인가 자원 관리 클래스는 아래와 같이 정의한다.
 *
 *      &#64;TableDef(tableName = "jcfiam_view_resources_roles", alias = "vr")
 *      public class ViewResourcesRoles implements ViewResourcesRoleMapping {
 *
 *          &#64;OrderBy(sortOrder = 1)
 *          &#64;ColumnDef(columnName = "view_resource_id")
 *          private String viewResourceId;
 *
 *          &#64;PrimaryKey
 *          &#64;ColumnDef(columnName = "role_id")
 *          private String roleId;
 *
 *          &#64;ReferenceKey(targetObject = "permission", targetField = "permissionId")
 *          &#64;ColumnDef(columnName = "permission_id")
 *          private String permissionId;
 *
 *          ....
 *
 *          private PermissionMapping permission;
 *
 *          ...
 *      }
 * <pre>
 *
 * @see jcf.iam.core.jdbc.acl.ViewResourcesRoles
 * @see jcf.iam.core.jdbc.acl.ViewResourcesUsers
 *
 * @author nolang
 *
 */
public interface ViewResourcesRoleMapping extends SecurityStatement {

	/**
	 *
	 * 화면ID를 반환한다.
	 *
	 * @return
	 */
	String getViewResourceId();

	/**
	 *
	 * 화면의 접근권한/사용자를 반환한다.
	 *
	 * @return
	 */
	String getAuthority();

	/**
	 *
	 * 화면의 접근 세부권한을 반환한다.
	 * @return
	 */
	String getPermissionId();

	/**
	 *
	 * 화면의 ID를 설정한다.
	 *
	 * @param viewResourceId
	 */
	void setViewResourceId(String viewResourceId);

	/**
	 *
	 * 화면의 접근 세부권한을 설정한다.
	 *
	 * @param permissionId
	 */
	void setPermissionId(String permissionId);

	/**
	 *
	 * 화면의 접근 권한/사용자를 설정한다.
	 *
	 * @param roleOrUserId
	 */
	void setAuthority(String roleOrUserId);
}
