package jcf.iam.core.jdbc.authentication;

import jcf.iam.core.jdbc.SecurityStatement;

/**
 * <pre>
 * 시스템에서 사용하는 권한을 처리하는 모델 객체를 정의하기 위해서 구현한다.
 *
 *  ex)
 *    - 시스템 정의 권한 관리 테이블의 정의가 아래와 같다고 가정할 때..
 *      create table JCFIAM_ROLES
 *      (
 *        ROLE_ID             VARCHAR2(16) not null,
 *        ROLE_NAME           VARCHAR2(64) not null,
 *        DESCRIPTION         VARCHAR2(512),
 *        ENABLED             VARCHAR2(1),
 *        PARENT_ROLE_ID      VARCHAR2(16),
 *        CREATE_DATE         DATE,
 *        CREATE_USER_ID      VARCHAR2(16),
 *        LAST_MODIFY_DATE    DATE,
 *        LAST_MODIFY_USER_ID VARCHAR2(16)
 *      );
 *      alter table JCFIAM_ROLES
 *        add constraint JCFIAM_ROLES_PK primary key (ROLE_ID);
 *
 *    - 위의 테이블과 매핑되는 시스템 정의 권한 관리 클래스는 아래와 같이 정의한다.
 *      &#64;TableDef(tableName = "jcfiam_roles")
 *      public class Role implements RoleMapping {
 *
 *          &#64;PrimaryKey(keyType = KeyType.DYNAMIC)
 *          &#64;ColumnDef(columnName = "ROLE_ID")
 *          private String roleId;
 *
 *          &#64;Updatable
 *          &#64;ColumnDef(columnName = "ROLE_NAME")
 *          private String roleName;
 *
 *          &#64;Updatable
 *          &#64;ColumnDef(columnName = "DESCRIPTION")
 *          private String description;
 *
 *          &#64;Updatable
 *          &#64;ColumnDef(columnName = "ENABLED")
 *          private String enabled;
 *
 *          &#64;ColumnDef(columnName = "CREATE_DATE", columnType = ColumnType.DATE, prefix = "to_char(", suffix = ", 'yyyy-mm-dd hh24:mi:ss')")
 *          private String createDate;
 *
 *          &#64;ColumnDef(columnName = "CREATE_USER_ID")
 *          private String createUserId;
 *
 *          &#64;Updatable
 *          &#64;ColumnDef(columnName = "LAST_MODIFY_DATE", columnType = ColumnType.DATE, prefix = "to_char(", suffix = ", 'yyyy-mm-dd hh24:mi:ss')")
 *          private String lastModifyDate;
 *
 *          &#64;Updatable
 *          &#64;ColumnDef(columnName = "LAST_MODIFY_USER_ID")
 *          private String lastModifyUserId;
 *
 *          public String getRoleId() {
 *              return roleId;
 *          }
 *
 *          ...
 *      }
 * <pre>
 *
 * @see jcf.iam.core.jdbc.builder.QueryBuilder
 * @see jcf.iam.core.authorization.service.SecuredResourcesService
 *
 * {@link jcf.iam.admin.authentication.controller.RoleController)
 * {@link jcf.iam.admin.authentication.service.RoleService}
 *
 * @author nolang
 *
 */
public interface RoleMapping extends SecurityStatement {

	/**
	 *
	 * 정의된 Rold ID 를 반환한다.
	 *
	 * @return
	 */
	String getRoleId();

	/**
	 *
	 * Rold ID 를 저장한다.
	 *
	 * @return
	 */
	void setRoleId(String roleId);


	/**
	 * 정의된 권한이 사용가능한지의 여부를 반환한다.
	 *
	 * @return
	 */
	String getEnabled();
}
