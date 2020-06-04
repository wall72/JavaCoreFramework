package jcf.iam.core.jdbc.authentication;

import jcf.iam.core.jdbc.SecurityStatement;

import org.springframework.security.core.GrantedAuthority;

/**
 *
 * <pre>
 * 사용자가 가지는 권한을 처리하는 모델 클래스를 정의하기 위해서 구현한다.
 *
 *  ex)
 *    - 사용자 권한 매핑관리 테이블의 정의가 아래와 같다고 가정할 때..
 *      create table JCFIAM_AUTHORITIES
 *      (
 *		  ROLE_ID             VARCHAR2(16) not null,
 *		  USER_ID             VARCHAR2(64) not null,
 *		  CREATE_DATE         DATE,
 *		  CREATE_USER_ID      VARCHAR2(16),
 *		  LAST_MODIFY_DATE    DATE,
 *		  LAST_MODIFY_USER_ID VARCHAR2(16)
 *		);
 *		alter table JCFIAM_AUTHORITIES
 *		  add constraint JCFIAM_AUTHORITIES_PK primary key (ROLE_ID, USER_ID);
 *
 *    - 위의 테이블과 매핑되는 사용자 권한 매핑관리 클래스는 아래와 같이 정의한다.
 *      &#64;TableDef(tableName = "jcfiam_authorities", alias = "r")
 *		public class UserAuthority extends AuthorityMapping {
 *
 *		    &#64;PrimaryKey(keyType = KeyType.DYNAMIC)
 *		    &#64;ColumnDef(columnName = "user_id")
 *		    private String userId;
 *
 *		    &#64;PrimaryKey(keyType = KeyType.DYNAMIC)
 *		    &#64;ColumnDef(columnName = "role_id")
 *		    private String roleId;
 *
 *		    &#64;ColumnDef(columnName = "create_date", columnType = ColumnType.DATE)
 *		    private String createDate;
 *
 *		    &#64;ColumnDef(columnName = "create_user_id")
 *		    private String createUserId;
 *
 *		    &#64;ColumnDef(columnName = "last_modify_date", columnType = ColumnType.DATE)
 *		    private String lastModifyDate;
 *
 *		    &#64;ColumnDef(columnName = "last_modify_user_id")
 *		    private String lastModifyUserId;
 *
 *		    public UserAuthority() {
 *		    }
 *
 *		    public UserAuthority(String roleId) {
 *		        this.roleId = roleId;
 *		    }
 *
 *		    public String getAuthority() {
 *		        return roleId;
 *		    }
 *
 *		    public void setAuthority(String roleId) {
 *		        this.roleId = roleId;
 *		    }
 *
 *		    public void setUsername(String username) {
 *		        this.userId = username;
 *		    }
 *		}
 *
 * <pre>
 *
 * @see jcf.iam.core.jdbc.authentication.UserAuthority
 *
 * @author nolang
 *
 */
public abstract class AuthorityMapping implements GrantedAuthority, SecurityStatement {

	/**
	 *
	 */
	private static final long serialVersionUID = 6074263028332689162L;

	/**
	 * 사용자의 이름을 저장한다.
	 *
	 * @param username
	 */
	public abstract void setUsername(String username);

	/**
	 * 사용자가 가지는 특정 권한 정보를 저장한다.
	 *
	 * @param authority
	 */
	public abstract void setAuthority(String authority);
}
