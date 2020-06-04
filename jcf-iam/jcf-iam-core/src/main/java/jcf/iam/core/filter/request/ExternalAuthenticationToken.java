package jcf.iam.core.filter.request;

/**
 *
 * @author nolang
 *
 */
public class ExternalAuthenticationToken {

	private String username;
	private String password;

	public ExternalAuthenticationToken(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
