package jcf.iam.core.authentication.userdetails.model;

import jcf.iam.core.jdbc.IamDefaultTableNames;
import jcf.iam.core.jdbc.authentication.AuthorityMapping;
import jcf.query.core.annotation.orm.ColumnDef;
import jcf.query.core.annotation.orm.PrimaryKey;
import jcf.query.core.annotation.orm.TableDef;
import jcf.query.core.evaluator.definition.ColumnType;
import jcf.query.core.evaluator.definition.KeyType;

/**
 * <pre>
 * JCF-IAM Default 사용자 권한 매핑 처리 모델
 * <pre>
 *
 * @see jcf.iam.core.jdbc.authentication.AuthorityMapping
 *
 * @author nolang
 *
 */
@TableDef(tableName = IamDefaultTableNames.AUTHORITIES, alias = "r")
public class UserAuthority extends AuthorityMapping {

	@PrimaryKey(keyType = KeyType.DYNAMIC)
	@ColumnDef(columnName = "user_id")
	private String userId;

	@PrimaryKey(keyType = KeyType.DYNAMIC)
	@ColumnDef(columnName = "role_id")
	private String roleId;

	@ColumnDef(columnName = "create_date", columnType = ColumnType.DATE)
	private String createDate;

	@ColumnDef(columnName = "create_user_id")
	private String createUserId;

	@ColumnDef(columnName = "last_modify_date", columnType = ColumnType.DATE)
	private String lastModifyDate;

	@ColumnDef(columnName = "last_modify_user_id")
	private String lastModifyUserId;

	public UserAuthority() {
	}

	public UserAuthority(String roleId) {
		this.roleId = roleId;
	}

	public String getAuthority() {
		return roleId;
	}

	public void setAuthority(String roleId) {
		this.roleId = roleId;
	}

	public void setUsername(String username) {
		this.userId = username;
	}
}
