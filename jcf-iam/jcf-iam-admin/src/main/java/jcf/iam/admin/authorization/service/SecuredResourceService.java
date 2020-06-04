package jcf.iam.admin.authorization.service;

import java.util.List;

import jcf.data.GridData;
import jcf.data.RowStatus;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authorization.ViewResourcesRoleMapping;
import jcf.query.core.QueryExecutorWrapper;
import jcf.query.core.evaluator.SimpleORMQueryType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * 사용자 및 권한별 인가 자원 처리와 관련한 연산을 수행한다.
 * <pre>
 *
 * @see jcf.iam.admin.authorization.controller.SecuredResourceController
 * @see jcf.iam.core.IamCustomizerFactory
 *
 * @author nolang
 *
 */
@Service
public class SecuredResourceService {

	private static final Logger logger = LoggerFactory.getLogger(SecuredResourceService.class);

	@Autowired
	private QueryExecutorWrapper queryExecutor;

	@Autowired
	private IamCustomizerFactory factory;



	/**
	 *
	 * 권한별 인가 자원 리스트 조회
	 *
	 * @param roleId
	 * @return
	 */
	public List<? extends ViewResourcesRoleMapping> getSecuredResourceListByRole(String roleId) {
		final ViewResourcesRoleMapping resourceQuery = BeanUtils.instantiate(factory.getCustomizer().getPermissionByRoleClass());

		resourceQuery.setAuthority(roleId);

		return queryExecutor.queryForList(SimpleORMQueryType.SELECT, resourceQuery, factory.getCustomizer().getPermissionByRoleClass());
	}

	/**
	 *
	 * 권한별 인가 자원 추가
	 *
	 * @param resource
	 * @return
	 */
	public int insertSecuredResourceRole(ViewResourcesRoleMapping resource) {
		if(logger.isDebugEnabled())	{
			logger.debug(
					"[SecuredResourceService] insertSecuredResourceRole - viewResourceId={} roleId={} permissionId",
					new Object[] { resource.getViewResourceId(),
							resource.getAuthority(), resource.getPermissionId() });
		}

		return queryExecutor.update(SimpleORMQueryType.INSERT, resource);
	}

	/**
	 *
	 * 권한별 인가 자원 수정
	 *
	 * @param resource
	 * @return
	 */
	public int updateSecuredResourceRole(ViewResourcesRoleMapping resource) {
		if(logger.isDebugEnabled())	{
			logger.debug(
					"[SecuredResourceService] updateSecuredResourceRole - viewResourceId={} roleId={} permissionId",
					new Object[] { resource.getViewResourceId(),
							resource.getAuthority(), resource.getPermissionId() });
		}

		return queryExecutor.update(SimpleORMQueryType.UPDATE, resource);
	}

	/**
	 *
	 * 권한별 인가 자원 삭제
	 *
	 * @param viewResourceId
	 * @param roleId
	 * @param permissionId
	 * @return
	 */
	public int deleteSecuredResourceRole(String viewResourceId, String roleId, String permissionId) {
		final ViewResourcesRoleMapping resource = BeanUtils.instantiate(factory.getCustomizer().getPermissionByRoleClass());

		resource.setViewResourceId(viewResourceId);
		resource.setAuthority(roleId);
		resource.setPermissionId(permissionId);

		if(logger.isDebugEnabled())	{
			logger.debug(
					"[SecuredResourceService] deleteSecuredResourceRole - viewResourceId={} roleId={} permissionId",
					new Object[] { resource.getViewResourceId(),
							resource.getAuthority(), resource.getPermissionId() });
		}

		return queryExecutor.update(SimpleORMQueryType.DELETE, resource);
	}

	/**
	 *
	 * 권한별 인가 자원 추가/삭제/수정
	 *
	 * @param gridData
	 * @return
	 */
	public int saveSecuredRoleResources(GridData<? extends ViewResourcesRoleMapping> gridData)	{
		List<? extends ViewResourcesRoleMapping> list = gridData.getList();

		int count = 0;

		for (int i = 0; i < list.size(); ++i) {
			RowStatus rowStatus = gridData.getStatusOf(i);
			ViewResourcesRoleMapping resource = list.get(i);

			if (rowStatus == RowStatus.INSERT) {
				count += insertSecuredResourceRole(resource);
			} else if (rowStatus == RowStatus.UPDATE) {
				count += updateSecuredResourceRole(resource);
			} else if (rowStatus == RowStatus.DELETE) {
				count += deleteSecuredResourceRole(resource.getViewResourceId(), resource.getAuthority(), resource.getPermissionId());
			}
		}

		return count;
	}


	/**
	 *
	 * 사용자별 인가 자원 조회
	 *
	 * @param userId
	 * @return
	 */
	public List<? extends ViewResourcesRoleMapping> getSecuredResourceListByUser(String userId) {
		final ViewResourcesRoleMapping resourceQuery = BeanUtils.instantiate(factory.getCustomizer().getPermissionByUserClass());

		resourceQuery.setAuthority(userId);

		return queryExecutor.queryForList(SimpleORMQueryType.SELECT, resourceQuery, factory.getCustomizer().getPermissionByUserClass());
	}

	/**
	 *
	 * 사용자별 인가 자원 추가
	 *
	 * @param resource
	 * @return
	 */
	public int insertSecuredResourceUser(ViewResourcesRoleMapping resource) {
		if(logger.isDebugEnabled())	{
			logger.debug(
					"[SecuredResourceService] insertSecuredResourceUser - viewResourceId={} userId={} permissionId",
					new Object[] { resource.getViewResourceId(),
							resource.getAuthority(), resource.getPermissionId() });
		}

		return queryExecutor.update(SimpleORMQueryType.INSERT, resource);
	}

	/**
	 *
	 * 사용자별 인가 자원 수정
	 *
	 * @param resource
	 * @return
	 */
	public int updateSecuredResourceUser(ViewResourcesRoleMapping resource) {
		if(logger.isDebugEnabled())	{
			logger.debug(
					"[SecuredResourceService] updateSecuredResourceUser - viewResourceId={} userId={} permissionId",
					new Object[] { resource.getViewResourceId(),
							resource.getAuthority(), resource.getPermissionId() });
		}

		return queryExecutor.update(SimpleORMQueryType.UPDATE, resource);
	}

	/**
	 *
	 * 사용자별 인가 자원 삭제
	 *
	 * @param viewResourceId
	 * @param roldId
	 * @param permissionId
	 * @return
	 */
	public int deleteSecuredResourceUser(String viewResourceId, String username, String permissionId) {
		final ViewResourcesRoleMapping resource = BeanUtils.instantiate(factory.getCustomizer().getPermissionByUserClass());

		resource.setViewResourceId(viewResourceId);
		resource.setAuthority(username);
		resource.setPermissionId(permissionId);

		if(logger.isDebugEnabled())	{
			logger.debug(
					"[SecuredResourceService] deleteSecuredResourceUser - viewResourceId={} userId={} permissionId",
					new Object[] { resource.getViewResourceId(),
							resource.getAuthority(), resource.getPermissionId() });
		}

		return queryExecutor.update(SimpleORMQueryType.DELETE, resource);
	}

	/**
	 *
	 * 사용자별 인가 자원 추가/삭제/수정
	 *
	 * @param gridData
	 * @return
	 */
	public int saveSecuredUserResources(GridData<? extends ViewResourcesRoleMapping> gridData)	{
		List<? extends ViewResourcesRoleMapping> list = gridData.getList();

		int count = 0;

		for (int i = 0; i < list.size(); ++i) {
			RowStatus rowStatus = gridData.getStatusOf(i);
			ViewResourcesRoleMapping resource = list.get(i);

			if (rowStatus == RowStatus.INSERT) {
				count += insertSecuredResourceUser(resource);
			} else if (rowStatus == RowStatus.UPDATE) {
				count += updateSecuredResourceUser(resource);
			} else if (rowStatus == RowStatus.DELETE) {
				count += deleteSecuredResourceUser(resource.getViewResourceId(), resource.getAuthority(), resource.getPermissionId());
			}
		}

		return count;
	}
}
