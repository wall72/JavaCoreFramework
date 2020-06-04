package jcf.iam.core.authentication.userdetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.authentication.userdetails.model.GrantedResourceAuthority;
import jcf.iam.core.jdbc.authorization.ViewResourcesRoleMapping;
import jcf.query.core.QueryExecutorWrapper;
import jcf.query.core.evaluator.SimpleORMQueryType;
import jcf.query.core.mapper.ObjectRelationMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * JCFIAM ALC 기능을 황성화 하였을때 동작하며, 화면접근 세부권한과 관련한 연산을 수행한다.
 *
 * @see jcf.iam.core.authentication.userdetails.UserAuthenticationService
 *
 * @author nolang
 *
 */
public class UserAccessControlService {

	private static final Logger logger = LoggerFactory.getLogger(UserAccessControlService.class);

	@Autowired
	private QueryExecutorWrapper queryExecutor;

	@Autowired
	private IamCustomizerFactory customizerFactory;

	private ObjectRelationMapper resultMapper = new ObjectRelationMapper();

	/**
	 *
	 * JCFIAM ALC 기능을 황성화 하였을때 동작하며, 사용자별 권한 및 사용자 개인에 대한 화면 접근 권한을 조회하여 반환한다.
	 *
	 * @param username
	 * @param authorities
	 * @return
	 */
	public List<GrantedAuthority> getAccessControlList(String username,	List<GrantedAuthority> authorities) {
		Set<GrantedResourceAuthority> permSet = new HashSet<GrantedResourceAuthority>();

		permSet.addAll(loadPermissionsByUsername(username));
		permSet.addAll(loadPermissionsByAuthorities(authorities));

		Iterator<GrantedResourceAuthority> it = permSet.iterator();

		List<GrantedAuthority> resourceAuthorities = new ArrayList<GrantedAuthority>();

		while (it.hasNext()) {
			GrantedAuthority authority = it.next();

			if(!resourceAuthorities.contains(authority))	{
				resourceAuthorities.add(authority);
			}
		}

		return resourceAuthorities;
	}

	/**
	 *
	 * 사용자별 인가자원을 조회하여 반환한다.
	 *
	 * @param username
	 * @return
	 */
	protected List<GrantedResourceAuthority> loadPermissionsByUsername(String username) {

		List<GrantedResourceAuthority> list = null;

		final Class<? extends ViewResourcesRoleMapping> viewMapping = customizerFactory.getCustomizer().getPermissionByUserClass();

		if(viewMapping != null){
			ViewResourcesRoleMapping viewObject = BeanUtils.instantiate(viewMapping);

			viewObject.setAuthority(username);

			list = queryExecutor.queryForList(SimpleORMQueryType.SELECT, viewObject, new RowMapper<GrantedResourceAuthority>() {
						public GrantedResourceAuthority mapRow(ResultSet rs, int rowNum) throws SQLException {
							ViewResourcesRoleMapping userPermission = resultMapper.mapper(rs, viewMapping);

							if(logger.isDebugEnabled()){
								logger.debug("[UserAccessControlService] loadPermissionsByUsername() - 화면접근권한: ViewResourceId={} Permission={}", userPermission.getViewResourceId(), userPermission.getPermissionId());
							}

							return new GrantedResourceAuthority(userPermission.getViewResourceId(), userPermission.getPermissionId());
						}
					});
		} else {
			list = new ArrayList<GrantedResourceAuthority>();
		}

		return list;
	}

	/**
	 *
	 * 사용자의 권한별 인가자원을 조회하여 반환한다.
	 *
	 * @param authorities
	 * @return
	 */
	protected List<GrantedResourceAuthority> loadPermissionsByAuthorities(List<GrantedAuthority> authorities) {
		List<GrantedResourceAuthority> resourcePermissions = new ArrayList<GrantedResourceAuthority>();

		final Class<? extends ViewResourcesRoleMapping> viewMapping = customizerFactory.getCustomizer().getPermissionByRoleClass();

		if(viewMapping != null){
			ViewResourcesRoleMapping viewObject = BeanUtils.instantiate(viewMapping);

			for (GrantedAuthority authority : authorities) {
				viewObject.setAuthority(authority.getAuthority());

				resourcePermissions.addAll(queryExecutor.queryForList(SimpleORMQueryType.SELECT,  viewObject, new RowMapper<GrantedResourceAuthority>() {
							public GrantedResourceAuthority mapRow(ResultSet rs, int rowNum)	throws SQLException {
								ViewResourcesRoleMapping rolePermission = resultMapper.mapper(rs, viewMapping);

								if(logger.isDebugEnabled()){
									logger.debug("[UserAccessControlService] loadPermissionsByAuthorities() - 화면접근권한: ViewResourceId={} Permission={}", rolePermission.getViewResourceId(), rolePermission.getPermissionId());
								}

								return new GrantedResourceAuthority(rolePermission.getViewResourceId(), rolePermission.getPermissionId());
							}
						}));
			}
		}

		return resourcePermissions;
	}

	public void setQueryExecutor(QueryExecutorWrapper queryExecutor) {
		this.queryExecutor = queryExecutor;
	}

	public void setCustomizerFactory(IamCustomizerFactory customizerFactory) {
		this.customizerFactory = customizerFactory;
	}
}
