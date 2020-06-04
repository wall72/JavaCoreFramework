package jcf.iam.core.jdbc.authorization;

import jcf.iam.core.jdbc.IamDefaultTableNames;
import jcf.iam.core.jdbc.SecurityStatement;
import jcf.query.core.annotation.orm.ColumnDef;
import jcf.query.core.annotation.orm.OrderBy;
import jcf.query.core.annotation.orm.PrimaryKey;
import jcf.query.core.annotation.orm.TableDef;
import jcf.query.core.annotation.orm.Updatable;
import jcf.query.core.evaluator.definition.ColumnType;
import jcf.query.core.evaluator.definition.KeyType;

/**
 * <pre>
 *
 * 화면접근 권한 정의를 처리하는 모델 객체
 *
 * - 아래의 테이블 정의와 매핑된다.
 *
 *  create table JCFIAM_PERMISSIONS
 *  (
 *    PERMISSION_ID       VARCHAR2(16),
 *    DESCRIPTION         VARCHAR2(128),
 *    MASK                NUMBER(8),
 *    CREATE_DATE         DATE,
 *    CREATE_USER_ID      VARCHAR2(16),
 *    LAST_MODIFY_DATE    DATE,
 *    LAST_MODIFY_USER_ID VARCHAR2(16)
 *  )
 *
 * <pre>
 *
 * @see jcf.iam.core.jdbc.builder.QueryBuilder
 * @see jcf.iam.core.jdbc.acl.ViewResourcesRoles
 * @see jcf.iam.core.jdbc.acl.ViewResourcesUsers
 *
 * @author nolang
 *
 */
@TableDef(tableName = IamDefaultTableNames.PERMISSIONS, alias = "p")
public class PermissionMapping implements SecurityStatement {

	@PrimaryKey(keyType = KeyType.DYNAMIC)
	@ColumnDef(columnName = "permission_id")
	private String permissionId;

	@Updatable
	@OrderBy(sortOrder = 2)
	@ColumnDef(columnName = "description")
	private String description;

	@ColumnDef(columnName = "create_date", columnType = ColumnType.DATE)
	private String createDate;

	@ColumnDef(columnName = "create_user_id")
	private String createUserId;

	@Updatable
	@ColumnDef(columnName = "last_modify_date", columnType = ColumnType.DATE)
	private String lastModifyDate;

	@Updatable
	@ColumnDef(columnName = "last_modify_user_id")
	private String lastModifyUserId;

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

	public String getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
