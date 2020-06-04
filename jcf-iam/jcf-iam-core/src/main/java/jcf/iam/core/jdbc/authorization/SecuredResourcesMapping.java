package jcf.iam.core.jdbc.authorization;

import jcf.iam.core.jdbc.SecurityStatement;

/**
 *
 * <pre>
 * 권한/사용자별 인가 자원 처리 모델 객체를 정의하기 위해 구현한다.
 *
 *  ex)
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
 *        add constraint JCFIAM_VIEW_RESRC_ROLES_FK01 foreign key (VIEW_RESOURCE_ID)
 *        references JCFIAM_VIEW_RESOURCES (VIEW_RESOURCE_ID);
 *      ...
 *
 *    - 위의 테이블과 매핑되는 권한별 인가 자원 관리 클래스는 아래와 같이 정의한다.
 *
 *      &#64;TableDef(tableName = "jcfiam_view_resources_roles", alias = "v1")
 *      public class SecuredResourcesRoles implements SecuredResourcesMapping {
 *
 *          &#64;ReferenceKey(targetObject = "securedResourcesPermissions", targetField = "viewResourceId")
 *          &#64;ColumnDef(columnName = "VIEW_RESOURCE_ID")
 *          private String viewResourceId;
 *
 *          ...
 *
 *          private RoleSecuredViewResource securedResourcesPermissions;
 *
 *          &#64;TableDef(tableName = "jcfiam_view_resources", alias = "v2")
 *          public static class RoleSecuredViewResource implements SecurityStatement {
 *              &#64;ColumnDef(columnName = "VIEW_RESOURCE_ID")
 *              private String viewResourceId;
 *              ...
 *          }
 *
 *          public String getResourcePattern() {
 *              return securedResourcesPermissions.viewResourcePattern;
 *          }
 *
 *          public String getResourceId() {
 *              return this.viewResourceId;
 *          }
 *
 *          public String getResourcePermission() {
 *              return this.permissionId;
 *          }
 *
 *          public String getOrderSeq() {
 *              return securedResourcesPermissions.viewResourceSeq;
 *          }
 *      }
 *
 * <pre>
 *
 * @see jcf.iam.core.jdbc.acl.ViewResourcesRoles
 * @see jcf.iam.core.jdbc.acl.ViewResourcesUsers
 * @see jcf.iam.core.authorization.service.SecuredResourcesService
 * @see jcf.iam.core.jdbc.builder.annotation.ReferenceKey
 *
 * @author nolang
 *
 */
public interface SecuredResourcesMapping extends SecurityStatement {

	/**
	 *
	 * 자원의 패턴 정의
	 *
	 * @return
	 */
	String getResourcePattern();

	/**
	 *
	 * 자원을 구분하는 ID
	 *
	 * @return
	 */
	String getResourceId();

	/**
	 *
	 * 자원에 허용된 접근 세부 권한
	 *
	 * @return
	 */
	String getResourcePermission();

	/**
	 *
	 * Filtering 될 순서를 정의
	 *
	 * @return
	 */
	String getOrderSeq();

}
