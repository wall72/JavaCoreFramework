package jcf.iam.core.authentication.userdetails.model;

import jcf.iam.core.jdbc.IamDefaultTableNames;
import jcf.iam.core.jdbc.authentication.UserMapping;
import jcf.query.core.annotation.orm.ColumnDef;
import jcf.query.core.annotation.orm.PrimaryKey;
import jcf.query.core.annotation.orm.TableDef;
import jcf.query.core.annotation.orm.Updatable;
import jcf.query.core.evaluator.definition.ColumnType;
import jcf.query.core.evaluator.definition.KeyType;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * <pre>
 * JCF-IAM Default 사용자 인증 처리 모델
 * <pre>
 *
 * @see jcf.iam.core.jdbc.authentication.UserMapping
 * @see jcf.iam.core.jdbc.builder.QueryBuilder
 *
 * @author nolang
 *
 */
@SuppressWarnings("serial")
@TableDef(tableName = IamDefaultTableNames.USERS, alias = "u")
public class User extends UserMapping {

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

//	@ColumnDef(columnName = "create_date", columnType = ColumnType.DATE, prefix = "to_char(", suffix = ", 'yyyy-mm-dd hh24:mi:ss')")
	@ColumnDef(columnName = "create_date", columnType = ColumnType.DATE)
	private String createDate;

	@ColumnDef(columnName = "create_user_id")
	private String createUserId;

	@Updatable
//	@ColumnDef(columnName = "last_modify_date", columnType = ColumnType.DATE, prefix = "to_char(", suffix = ", 'yyyy-mm-dd hh24:mi:ss')")
	@ColumnDef(columnName = "last_modify_date", columnType = ColumnType.DATE)
	private String lastModifyDate;

	@Updatable
	@ColumnDef(columnName = "last_modify_user_id")
	private String lastModifyUserId;

	public User() {
	}

	public User(String username, String password, String enabled) {
		super(true, true, true, AuthorityUtils.NO_AUTHORITIES);

		if (((username == null) || "".equals(username)) || (password == null)) {
			throw new IllegalArgumentException(
					"Cannot pass null or empty values to constructor");
		}

		this.username = username;
		this.password = password;
		this.enabled = enabled;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public boolean isEnabled() {
		return enabled != null && enabled.equals("Y");
	}

	/**
	 * Returns {@code true} if the supplied object is a {@code User} instance
	 * with the same {@code username} value.
	 * <p>
	 * In other words, the objects are equal if they have the same username,
	 * representing the same principal.
	 */
	@Override
	public boolean equals(Object rhs) {
		if (rhs instanceof User) {
			return username.equals(((User) rhs).username);
		}
		return false;
	}

	/**
	 * Returns the hashcode of the {@code username}.
	 */
//	@Override
//	public int hashCode() {
//		return username.hashCode();
//	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(": ");
		sb.append("Username: ").append(this.username).append("; ");
		sb.append("Password: [PROTECTED]; ");
		sb.append("Enabled: ").append(this.enabled).append("; ");
		sb.append("AccountNonExpired: ").append(this.accountNonExpired)
				.append("; ");
		sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired)
				.append("; ");
		sb.append("AccountNonLocked: ").append(this.accountNonLocked)
				.append("; ");

		if (!authorities.isEmpty()) {
			sb.append("Granted Authorities: ");

			boolean first = true;
			for (GrantedAuthority auth : authorities) {
				if (!first) {
					sb.append(",");
				}
				first = false;

				sb.append(auth);
			}
		} else {
			sb.append("Not granted any authorities");
		}

		return sb.toString();
	}

	/**
	 *
	 * 사용자 이름을 반환한다.
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * 사용자 이름을 설정한다.
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * 해당 사용자가 유효한 사용자인지 반환한다.
	 *
	 * @return
	 */
	public String getEnabled() {
		return enabled;
	}

	/**
	 *
	 * 해당 사용자의 유효성 여부를 설정한다.
	 *
	 * @param enabled
	 */
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	/**
	 *
	 * 해당 사용자를 추가한 사용자의 ID를 설정한다.
	 *
	 * @param createUserId
	 */
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	/**
	 *
	 * 해당 사용자를 수정한 사용자의 ID를 설정한다.
	 *
	 * @param createUserId
	 */
	public void setLastModifyUserId(String lastModifyUserId) {
		this.lastModifyUserId = lastModifyUserId;
	}

	/**
	 *
	 * 사용자의 password 를 설정한다.
	 *
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
