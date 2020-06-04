package jcf.iam.core.jdbc.authentication;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jcf.iam.core.jdbc.SecurityStatement;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * <pre>
 * 인증 사용자 정보를 처리하는 모델 객체를 정의하기 위해서 구현된 기반 클래스
 *
 *  - 구현 참조 사항
 *    1. 시스템에서 username (userid) 으로 사용되는 Field 는 getUsername/setUsername 의 접근자로 참조되어야 하며
 *    2. 1번 항목에서 정의된 Field는 반드시 [@PrimaryKey(keyType = KeyType.DYNAMIC)] 어노테이션이 정의되어야 한다.
 *
 *    ex)
 *
 *      - 사용자 관리 테이블의 정의가 아래와 같다고 가정할 때..
 *
 *          create table JCFIAM_USERS
 *          (
 *            USER_ID             VARCHAR2(64) not null,
 *            USER_NAME           VARCHAR2(32),
 *            PASSWORD            VARCHAR2(16),
 *            ENABLED             VARCHAR2(1),
 *            CREATE_DATE         DATE,
 *            CREATE_USER_ID      VARCHAR2(16),
 *            LAST_MODIFY_DATE    DATE,
 *            LAST_MODIFY_USER_ID VARCHAR2(16)
 *          );
 *
 *          alter table USERS
 *            add constraint USERS_PK primary key (USER_ID);
 *
 *
 *		- 위의 테이블과 매핑되는 사용자 관리 클래스는 아래와 같이 정의한다.
 *
 *    		&#64;TableDef(tableName = "JCFIAM_USERS", alias = "u")
 *    		public class User extedns UserMapping {
 *
 *              &#64;PrimaryKey(keyType = KeyType.DYNAMIC) // JCFIAM_USERS 테이블의 USER_ID 컬럼은 User 클래스의 username으로 매핑되며, 이는 PrimaryKey 로 사용된다.
 *              &#64;ColumnDef(columnName = "user_id")
 *              private String username;
 *
 *              &#64;Updatable
 *              &#64;ColumnDef(columnName = "user_name")
 *              private String name;
 *
 *              &#64;Updatable
 *              &#64;ColumnDef(columnName = "password")
 *              private String password;
 *
 *              ...
 *
 *              &#64;ColumnDef(columnName = "create_date", columnType = ColumnType.DATE, prefix = "to_char(", suffix = ", 'yyyy-mm-dd hh24:mi:ss')")
 *              private String createDate;
 *
 *              ...
 *
 *              public String getUsername()	{
 *                  this.username;
 *              }
 *
 *              public void setUsername(String username)	{
 *                  this.username = username;
 *              }
 *
 *              ...
 *    		}
 *
 * <pre>
 *
 * @see jcf.iam.core.jdbc.authentication.User
 *
 * @author nolang
 *
 */
public abstract class UserMapping implements UserDetails, SecurityStatement {

	/**
	 *
	 */
	private static final long serialVersionUID = 4093689072962180601L;

	protected Set<GrantedAuthority> authorities;

	protected String authorityString;

	protected boolean accountNonExpired;

	protected boolean accountNonLocked;

	protected boolean credentialsNonExpired;

	public UserMapping() {
		this.accountNonExpired = true;
		this.credentialsNonExpired = true;
		this.accountNonLocked = true;
		this.authorities = Collections.unmodifiableSet(sortAuthorities(AuthorityUtils.NO_AUTHORITIES));
	}

	public UserMapping(boolean accountNonExpired,	boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
		this.accountNonExpired = accountNonExpired;
		this.credentialsNonExpired = credentialsNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
	}

	/**
	 * 인증된 사용자의 ID를 반환한다.
	 *
	 */
	public abstract String getUsername();

	/**
	 * 사용자의 ID를 저장한다.
	 *
	 * @param username
	 */
	public abstract void setUsername(String username);

	/**
	 * 사용자의 Password를 반환한다.
	 */
	public abstract String getPassword();

	/**
	 * 사용자의 ID가 사용가능한지 여부를 반환한다.
	 */
	public abstract boolean isEnabled();

	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<GrantedAuthority> authorities) {
		this.authorities = Collections
				.unmodifiableSet(sortAuthorities(authorities));
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	/**
	 *
	 * 인증된 사용자의 권한을 문자열로 반환한다.
	 *
	 * @return
	 */
	public String getAuthorityString()	{
		if(!StringUtils.hasText(authorityString))	{
			Iterator<GrantedAuthority> it = authorities.iterator();

			while(it.hasNext()){
				GrantedAuthority grantedAuthority = it.next();

				if(StringUtils.hasText(authorityString)){
					authorityString += ",";
				}

				authorityString = authorityString + grantedAuthority.getAuthority();
			}
		}

		return authorityString;
	}

	private static SortedSet<GrantedAuthority> sortAuthorities(
			Collection<? extends GrantedAuthority> authorities) {
		Assert.notNull(authorities,
				"Cannot pass a null GrantedAuthority collection");

		SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<GrantedAuthority>(
				new AuthorityComparator());

		for (GrantedAuthority grantedAuthority : authorities) {
			Assert.notNull(grantedAuthority,
					"GrantedAuthority list cannot contain any null elements");
			sortedAuthorities.add(grantedAuthority);
		}

		return sortedAuthorities;
	}

	private static class AuthorityComparator implements
			Comparator<GrantedAuthority>, Serializable {
		public int compare(GrantedAuthority g1, GrantedAuthority g2) {

			if (g2.getAuthority() == null) {
				return -1;
			}

			if (g1.getAuthority() == null) {
				return 1;
			}

			return g1.getAuthority().compareTo(g2.getAuthority());
		}
	}
}
