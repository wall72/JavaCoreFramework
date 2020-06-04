package jcf.iam.core.authentication.userdetails.model;

import jcf.iam.core.jdbc.IamDefaultTableNames;
import jcf.iam.core.jdbc.authentication.RoleMapping;
import jcf.query.core.annotation.orm.ColumnDef;
import jcf.query.core.annotation.orm.PrimaryKey;
import jcf.query.core.annotation.orm.TableDef;
import jcf.query.core.annotation.orm.Updatable;
import jcf.query.core.evaluator.definition.ColumnType;
import jcf.query.core.evaluator.definition.KeyType;

/**
 *
 * <pre>
 * JCF-IAM Default 시스템 정의 권한 매핑 처리 모델
 * <pre>
 *
 * @see jcf.iam.core.jdbc.authentication.RoleMapping
 *
 * @author nolang
 *
 */
@TableDef(tableName = IamDefaultTableNames.ROLES)
public class Role implements RoleMapping {

	@PrimaryKey(keyType = KeyType.DYNAMIC)
	@ColumnDef(columnName = "ROLE_ID")
	private String roleId;

	@Updatable
	@ColumnDef(columnName = "ROLE_NAME")
	private String roleName;

	@Updatable
	@ColumnDef(columnName = "DESCRIPTION")
	private String description;

	@Updatable
	@ColumnDef(columnName = "ENABLED")
	private String enabled;

	@ColumnDef(columnName = "CREATE_DATE", columnType = ColumnType.DATE)
	private String createDate;

	@ColumnDef(columnName = "CREATE_USER_ID")
	private String createUserId;

	@Updatable
	@ColumnDef(columnName = "LAST_MODIFY_DATE", columnType = ColumnType.DATE)
	private String lastModifyDate;

	@Updatable
	@ColumnDef(columnName = "LAST_MODIFY_USER_ID")
	private String lastModifyUserId;

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
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

}
