package jcf.iam.core.authentication.userdetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authentication.AuthorityMapping;
import jcf.iam.core.jdbc.authentication.UserMapping;
import jcf.query.core.QueryExecutorWrapper;
import jcf.query.core.evaluator.SimpleORMQueryType;
import jcf.query.core.mapper.ObjectRelationMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * 시스템 인증시 사용되는 크리덴셜 정보와 관련한 연산을 수행한다.
 *
 * @see jcf.iam.core.authentication.userdetails.UserAccessControlService
 * @see jcf.iam.core.IamCustomizerFactory
 *
 * @author nolang
 *
 */
public class UserAuthenticationService implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationService.class);

	@Autowired
	protected UserAccessControlService userAccessControlService;

	protected boolean enableAuthorities = true;

	@Autowired
	protected IamCustomizerFactory customizerFactory;

	@Autowired
	protected QueryExecutorWrapper queryExecutor;

	protected ObjectRelationMapper resultMapper = new ObjectRelationMapper();

	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	/**
	 * 인증을 시도하는 사용자 정보 및 사용자가 가지는 시스템권한을 조회한다.
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        List<UserDetails> users = loadUsersByUsername(username);

        if (users.size() == 0) {
            logger.debug("Query returned no results for user '" + username + "'");

            throw new UsernameNotFoundException(messages.getMessage("JdbcDaoImpl.notFound", new Object[]{username}, "Username {0} not found"), username);
        }

        UserDetails user = null;

        if(users.size() > 1){

        } else {
        	user = users.get(0);
        }

        Set<GrantedAuthority> dbAuthsSet = new HashSet<GrantedAuthority>();

        if (enableAuthorities) {
            dbAuthsSet.addAll(loadUserAuthorities(user.getUsername()));
        }

        List<GrantedAuthority> dbAuths = new ArrayList<GrantedAuthority>(dbAuthsSet);

        if(customizerFactory.getCustomizer() != null && customizerFactory.getCustomizer().isUseJcfIamAcl())	{
        	addCustomAuthorities(user.getUsername(), dbAuths);
        }

        if (dbAuths.size() == 0) {
            logger.debug("User '" + username + "' has no authorities and will be treated as 'not found'");

            throw new UsernameNotFoundException(messages.getMessage("JdbcDaoImpl.noAuthority", new Object[] {username}, "User {0} has no GrantedAuthority"), username);
        }

        return createUserInfo(username, user, dbAuths);
    }

	/**
	 *
	 * JCF IAM ACL 처리에 필요한 사용자/사용자 권한별 인가 가능 자원을 조회하여 반환한다.
	 *
	 * @param username
	 * @param authorities
	 */
	protected void addCustomAuthorities(String username, List<GrantedAuthority> authorities) {
		logger.debug("[UserAuthenticationService] addCustomAuthorities() - username={}, authorities={}", username, authorities);

		if (userAccessControlService == null) {
			userAccessControlService = new UserAccessControlService();
			userAccessControlService.setQueryExecutor(queryExecutor);
		}

		authorities.addAll(userAccessControlService.getAccessControlList(username, authorities));
	}

	/**
	 *
	 * 사용자 정보를 조회한다.
	 *
	 * @param username
	 * @return
	 */
    protected List<UserDetails> loadUsersByUsername(String username) {
    	UserMapping user = BeanUtils.instantiate(customizerFactory.getCustomizer().getUserClass());

    	user.setUsername(username);

    	return queryExecutor.queryForList(SimpleORMQueryType.SELECT, user, new RowMapper<UserDetails>() {
            public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            	return resultMapper.mapper(rs, customizerFactory.getCustomizer().getUserClass());
            }
        } );
    }

    /**
     *
     * 인증 사용자의 권한을 조회한다.
     *
     * @param username
     * @return
     */
    protected List<GrantedAuthority> loadUserAuthorities(String username) {
    	AuthorityMapping userAuthority = BeanUtils.instantiate(customizerFactory.getCustomizer().getAuthorityClass());

    	userAuthority.setUsername(username);

    	return queryExecutor.queryForList(SimpleORMQueryType.SELECT, userAuthority, new RowMapper<GrantedAuthority>() {
            public GrantedAuthority mapRow(ResultSet rs, int rowNum) throws SQLException {
            	return resultMapper.mapper(rs, customizerFactory.getCustomizer().getAuthorityClass());
            }
        });
    }

    protected UserDetails createUserInfo(String username, UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {
    	((UserMapping) userFromUserQuery).setAuthorities(combinedAuthorities);
        return userFromUserQuery;
    }

	/**
	 *
	 * @param userAccessControlService
	 */
	public void setUserAccessControlService(UserAccessControlService userAccessControlService) {
		this.userAccessControlService = userAccessControlService;
	}

	/**
	 *
	 * @param customizerFactory
	 */
	public void setCustomizerFactory(IamCustomizerFactory customizerFactory) {
		this.customizerFactory = customizerFactory;
	}

	/**
	 *
	 * @param queryExecutor
	 */
	public void setQueryExecutor(QueryExecutorWrapper queryExecutor) {
		this.queryExecutor = queryExecutor;
	}
}
