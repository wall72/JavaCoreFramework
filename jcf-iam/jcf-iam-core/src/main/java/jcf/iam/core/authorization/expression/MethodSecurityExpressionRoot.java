package jcf.iam.core.authorization.expression;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.authentication.userdetails.UserAccessControlService;
import jcf.iam.core.authentication.userdetails.model.GrantedResourceAuthority;
import jcf.iam.core.common.util.UserInfoHolder;
import jcf.iam.core.jdbc.authentication.UserMapping;
import jcf.iam.core.jdbc.authorization.ViewResourcesMapping;
import jcf.iam.core.jdbc.authorization.ViewResourcesRoleMapping;
import jcf.query.core.QueryExecutorWrapper;
import jcf.query.core.evaluator.SimpleORMQueryType;
import jcf.query.core.mapper.ObjectRelationMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * 메소드 접근제어를 위한 Expression 정의
 *
 * @see jcf.iam.core.authorization.expression.MethodSecurityExpressionHandler
 *
 * @author nolang
 *
 */
public class MethodSecurityExpressionRoot extends SecurityExpressionRoot {

	private static final Logger logger = LoggerFactory.getLogger(MethodSecurityExpressionRoot.class);

	private PermissionEvaluator permissionEvaluator;
    private Object filterObject;
    private Object returnObject;

    MethodSecurityExpressionRoot(Authentication a) {
        super(a);
    }

    public boolean hasPermission(Object target, Object permission) {
        return permissionEvaluator.hasPermission(authentication, target, permission);
    }

    public boolean hasPermission(Object targetId, String targetType, Object permission) {
        return permissionEvaluator.hasPermission(authentication, (Serializable)targetId, targetType, permission);
    }

    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    public Object getFilterObject() {
        return filterObject;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    public Object getReturnObject() {
        return returnObject;
    }

    public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    /**
     *
     * 메소드 단위의 접근제어를 수행을 위한 체크메소드
     *
     * @param resourceId
     * @param permission
     * @return
     */
	public boolean hasViewRole(String resourceId, String permission) {

//		UserMapping user = UserInfoHolder.getUserInfo(customizerFactory.getCustomizer().getUserClass());
//
//		if(user == null){
//			return false;
//		}
//
//		Collection<GrantedAuthority> grantedAuthorities = user.getAuthorities();
//
//		for(GrantedAuthority grantedAuthority : grantedAuthorities)	{
//			ViewResourcesRoleMapping rolePermission = loadPermissionsByAuthority(resourceId, permission, grantedAuthority.getAuthority());
//
//			if(rolePermission != null)	{
//				return true;
//			}
//		}
//
//		ViewResourcesRoleMapping userPermission = loadPermissionsByUsername(resourceId, permission, user.getUsername());
//
//		if(userPermission != null)	{
//			return true;
//		}
//
//		return false;
		return hasRole(String.format("%s(%s)", resourceId, permission));
	}

	@Autowired
	private QueryExecutorWrapper queryExecutor;

	@Autowired
	private IamCustomizerFactory customizerFactory;

	private ObjectRelationMapper resultMapper = new ObjectRelationMapper();

	protected ViewResourcesRoleMapping loadPermissionsByUsername(String resourceId, String permission, String username) {

		ViewResourcesRoleMapping list = null;

		final Class<? extends ViewResourcesRoleMapping> viewMapping = customizerFactory.getCustomizer().getPermissionByUserClass();

		if(viewMapping != null){
			ViewResourcesRoleMapping viewObject = BeanUtils.instantiate(viewMapping);

			viewObject.setAuthority(username);
			viewObject.setViewResourceId(resourceId);
			viewObject.setPermissionId(permission);

			list = queryExecutor.queryForObject(SimpleORMQueryType.SELECT, viewObject, new RowMapper<ViewResourcesRoleMapping>() {
						public ViewResourcesRoleMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
							ViewResourcesRoleMapping userPermission = resultMapper.mapper(rs, viewMapping);

							if(logger.isDebugEnabled()){
								logger.debug("[MethodSecurityExpressionRoot] loadPermissionsByUsername() - 화면접근권한: ViewResourceId={} Permission={} Username={}", new Object[]{ userPermission.getViewResourceId(), userPermission.getPermissionId(), userPermission.getAuthority()});
							}

							return userPermission;
						}
					});
		}

		return list;
	}

	protected ViewResourcesRoleMapping loadPermissionsByAuthority(String resourceId, String permission, String authority) {

		ViewResourcesRoleMapping list = null;

		final Class<? extends ViewResourcesRoleMapping> viewMapping = customizerFactory.getCustomizer().getPermissionByRoleClass();

		if(viewMapping != null){
			ViewResourcesRoleMapping viewObject = BeanUtils.instantiate(viewMapping);

			viewObject.setAuthority(authority);
			viewObject.setViewResourceId(resourceId);
			viewObject.setPermissionId(permission);

			list = queryExecutor.queryForObject(SimpleORMQueryType.SELECT, viewObject, new RowMapper<ViewResourcesRoleMapping>() {
				public ViewResourcesRoleMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
					ViewResourcesRoleMapping rolePermission = resultMapper.mapper(rs, viewMapping);

					if(logger.isDebugEnabled()){
						logger.debug("[MethodSecurityExpressionRoot] loadPermissionsByAuthority() - 화면접근권한: ViewResourceId={} Permission={} Role={}", new Object[]{ rolePermission.getViewResourceId(), rolePermission.getPermissionId(), rolePermission.getAuthority()});
					}

					return rolePermission;
				}
			});
		}

		return list;
	}
}
