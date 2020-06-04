package jcf.iam.core.authentication.oauth2.provider.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jcf.iam.core.jdbc.oauth2.ClientDetailsMapping;
import jcf.query.core.annotation.orm.ColumnDef;
import jcf.query.core.annotation.orm.PrimaryKey;
import jcf.query.core.annotation.orm.TableDef;
import jcf.query.core.annotation.orm.Updatable;
import jcf.query.core.evaluator.definition.ColumnType;
import jcf.query.core.evaluator.definition.KeyType;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

/**
 *
 * OAuth 인증이 허용된 Client 처리 모델
 *
 * @author nolang
 *
 */
@TableDef(tableName = "jcfiam_oauth_client")
public class ExternalClientDetails implements ClientDetailsMapping {

	@PrimaryKey(keyType = KeyType.DYNAMIC)
	@ColumnDef(columnName = "client_id")
	private String clientId;

	@ColumnDef(columnName = "client_secret")
	private String clientSecret;

	@ColumnDef(columnName = "scope")
	private String scope;

	@ColumnDef(columnName = "grant_type")
	private String authorizedGrantTypes;

	@ColumnDef(columnName = "redirect_uri")
	private String webServerRedirectUri;

	@ColumnDef(columnName = "CREATE_DATE", columnType = ColumnType.DATE, prefix = "to_char(", suffix = ", 'yyyy-mm-dd hh24:mi:ss')")
	private String createDate;

	@ColumnDef(columnName = "CREATE_USER_ID")
	private String createUserId;

	@Updatable
	@ColumnDef(columnName = "LAST_MODIFY_DATE", columnType = ColumnType.DATE, prefix = "to_char(", suffix = ", 'yyyy-mm-dd hh24:mi:ss')")
	private String lastModifyDate;

	@Updatable
	@ColumnDef(columnName = "LAST_MODIFY_USER_ID")
	private String lastModifyUserId;

	private List<GrantedAuthority> authorities = Collections.emptyList();

	public String getClientId() {
		return clientId;
	}

	public boolean isSecretRequired() {
		return StringUtils.hasText(clientSecret);
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public boolean isScoped() {
		return StringUtils.hasText(scope);
	}

	public List<String> getScope() {
		return Arrays.asList(StringUtils.commaDelimitedListToStringArray(scope));
	}

	public List<String> getAuthorizedGrantTypes() {
		return Arrays.asList(StringUtils
				.commaDelimitedListToStringArray(authorizedGrantTypes));
	}

	public String getWebServerRedirectUri() {
		return webServerRedirectUri;
	}

	public List<GrantedAuthority> getAuthorities() {
		return authorities;
	}
}
