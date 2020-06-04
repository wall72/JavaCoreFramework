package jcf.iam.config;

import jcf.iam.core.jdbc.authentication.UserMapping;
import jcf.query.core.annotation.orm.ColumnDef;
import jcf.query.core.annotation.orm.PrimaryKey;
import jcf.query.core.annotation.orm.TableDef;
import jcf.query.core.annotation.orm.Updatable;
import jcf.query.core.evaluator.definition.KeyType;

@TableDef(tableName = "test_table")
public class ConfigTestUser extends UserMapping {

	@PrimaryKey(keyType = KeyType.DYNAMIC)
	@ColumnDef(columnName = "user_id")
	private String username;

	@Updatable
	@ColumnDef(columnName = "password")
	private String password;

	@Updatable
	@ColumnDef(columnName = "user_name")
	private String name;

	@Updatable
	@ColumnDef(columnName = "enabled")
	private String enabled;

	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUsername(String username) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

}
