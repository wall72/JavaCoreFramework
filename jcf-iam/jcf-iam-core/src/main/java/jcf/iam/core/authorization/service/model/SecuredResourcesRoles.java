package jcf.iam.core.authorization.service.model;

import jcf.iam.core.jdbc.IamDefaultTableNames;
import jcf.iam.core.jdbc.SecurityStatement;
import jcf.iam.core.jdbc.authorization.SecuredResourcesMapping;
import jcf.query.core.annotation.orm.ColumnDef;
import jcf.query.core.annotation.orm.OrderBy;
import jcf.query.core.annotation.orm.ReferenceKey;
import jcf.query.core.annotation.orm.TableDef;
import jcf.query.core.annotation.orm.Updatable;
import jcf.query.core.evaluator.definition.ColumnType;

/**
 * <pre>
 * JCF-IAM Default 권한별 인가 자원 처리 모델
 * <pre>
 *
 * @see jcf.iam.core.jdbc.authorization.SecuredResourcesMapping
 *
 * @author noalng
 *
 */
@TableDef(tableName = IamDefaultTableNames.VIEW_RESOURCES_ROLES, alias = "v1")
public class SecuredResourcesRoles implements SecuredResourcesMapping {

	@ReferenceKey(targetObject = "securedResourcesPermissions", targetField = "viewResourceId")
	@ColumnDef(columnName = "VIEW_RESOURCE_ID")
	private String viewResourceId;

	@ColumnDef(columnName = "ROLE_ID")
	private String roleId;

	@ColumnDef(columnName = "PERMISSION_ID")
	private String permissionId;

//	@ColumnDef(columnName = "CREATE_DATE", columnType = ColumnType.DATE, prefix = "to_char(", suffix = ", 'yyyy-mm-dd hh24:mi:ss')")
//	private String createDate;
//
//	@ColumnDef(columnName = "CREATE_USER_ID")
//	private String createUserId;
//
//	@Updatable
//	@ColumnDef(columnName = "LAST_MODIFY_DATE", columnType = ColumnType.DATE, prefix = "to_char(", suffix = ", 'yyyy-mm-dd hh24:mi:ss')")
//	private String lastModifyDate;
//
//	@Updatable
//	@ColumnDef(columnName = "LAST_MODIFY_USER_ID")
//	private String lastModifyUserId;

	private RoleSecuredViewResource securedResourcesPermissions;

	@TableDef(tableName = IamDefaultTableNames.VIEW_RESOURCES, alias = "v2")
	public static class RoleSecuredViewResource implements SecurityStatement {
		@ColumnDef(columnName = "VIEW_RESOURCE_ID")
		private String viewResourceId;

		@ColumnDef(columnName = "VIEW_RESOURCE_PATTERN")
		private String viewResourcePattern;

		@ColumnDef(columnName = "VIEW_RESOURCE_SEQ")
		@OrderBy(sortOrder = 0)
		private String viewResourceSeq;
	}

	public String getResourcePattern() {
		return securedResourcesPermissions.viewResourcePattern;
	}

	public String getResourceId() {
		return this.viewResourceId;
	}

	public String getResourcePermission() {
		return this.permissionId;
	}

	public String getOrderSeq() {
		return securedResourcesPermissions.viewResourceSeq;
	}
}
