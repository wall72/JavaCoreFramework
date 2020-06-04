package jcf.iam.core.authorization.service.model;

import jcf.iam.core.jdbc.IamDefaultTableNames;
import jcf.iam.core.jdbc.authorization.PermissionMapping;
import jcf.iam.core.jdbc.authorization.ViewResourcesRoleMapping;
import jcf.query.core.annotation.orm.ColumnDef;
import jcf.query.core.annotation.orm.OrderBy;
import jcf.query.core.annotation.orm.PrimaryKey;
import jcf.query.core.annotation.orm.ReferenceKey;
import jcf.query.core.annotation.orm.TableDef;
import jcf.query.core.evaluator.definition.ColumnType;

/**
 * <pre>
 * JCF-IAM Default 화면접근권한 처리 모델
 * <pre>
 *
 * @see jcf.iam.core.jdbc.authorization.ViewResourcesRoleMapping
 *
 * @author nolang
 *
 */
@TableDef(tableName = IamDefaultTableNames.VIEW_RESOURCES_ROLES, alias = "vr")
public class ViewResourcesRoles implements ViewResourcesRoleMapping {

	@OrderBy(sortOrder = 1)
	@ColumnDef(columnName = "view_resource_id")
	private String viewResourceId;

	@PrimaryKey
	@ColumnDef(columnName = "role_id")
	private String roleId;

	@ReferenceKey(targetObject = "permission", targetField = "permissionId")
	@ColumnDef(columnName = "permission_id")
	private String permissionId;

	@ColumnDef(columnName = "CREATE_DATE", columnType = ColumnType.DATE)
	private String createDate;

	@ColumnDef(columnName = "CREATE_USER_ID")
	private String createUserId;

	@ColumnDef(columnName = "LAST_MODIFY_DATE", columnType = ColumnType.DATE)
	private String lastModifyDate;

	@ColumnDef(columnName = "LAST_MODIFY_USER_ID")
	private String lastModifyUserId;

	private PermissionMapping permission;

	public String getViewResourceId() {
		return viewResourceId;
	}

	public void setViewResourceId(String viewResourceId) {
		this.viewResourceId = viewResourceId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}

	public PermissionMapping getPermission() {
		return permission;
	}

	public void setPermission(PermissionMapping permission) {
		this.permission = permission;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getLastModifyDate() {
		return lastModifyDate;
	}

	public void setLastModifyDate(String lastModifyDate) {
		this.lastModifyDate = lastModifyDate;
	}

	public String getLastModifyUserId() {
		return lastModifyUserId;
	}

	public void setLastModifyUserId(String lastModifyUserId) {
		this.lastModifyUserId = lastModifyUserId;
	}

	public String getAuthority() {
		return this.roleId;
	}

	public void setAuthority(String roleOrUserId) {
		this.roleId = roleOrUserId;
	}
}
