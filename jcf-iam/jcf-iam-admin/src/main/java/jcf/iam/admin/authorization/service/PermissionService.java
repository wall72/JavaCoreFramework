package jcf.iam.admin.authorization.service;

import java.util.List;

import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authorization.PermissionMapping;
import jcf.query.core.QueryExecutorWrapper;
import jcf.query.core.evaluator.SimpleORMQueryType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* <pre>
* 리소스 세부 접근 권한(READ/CREATE/UPDATE/DELETE)과 관련한 연산을 수행한다.
* <pre
*
* @see jcf.iam.admin.authorization.controller.PermissionController
* @see jcf.iam.core.IamCustomizerFactory
*
* @author nolang
*
*/
@Service
public class PermissionService {

	private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

	@Autowired
	private IamCustomizerFactory factory;

	@Autowired
	private QueryExecutorWrapper queryExecutor;

	/**
	 *
	 * 시스템 정의 리소스 세부 접근 권한 조회
	 *
	 * @return
	 */
	public List<? extends PermissionMapping> getPermissions()	{
		final PermissionMapping queryPermission = BeanUtils.instantiate(factory.getCustomizer().getPermissionClass());

		return queryExecutor.queryForList(SimpleORMQueryType.SELECT, queryPermission, factory.getCustomizer().getPermissionClass());
	}

	/**
	 *
	 * 시스템 정의 리소스 세부 접근 권한 추가
	 *
	 * @param permission
	 * @return
	 */
	public int insertPermission(PermissionMapping permission) {
		if(logger.isDebugEnabled())	{
			logger.debug("[PermissionService] insertPermission - permissionid={} descrption={}", permission.getPermissionId(), permission.getDescription());
		}

		return queryExecutor.update(SimpleORMQueryType.INSERT, permission);
	}

	/**
	 *
	 * 시스템 정의 리소스 세부 접근 권한 수정
	 *
	 * @param permission
	 * @return
	 */
	public int updatePermission(PermissionMapping permission) {
		if(logger.isDebugEnabled())	{
			logger.debug("[PermissionService] updatePermission - permissionid={} descrption={}", permission.getPermissionId(), permission.getDescription());
		}

		return queryExecutor.update(SimpleORMQueryType.UPDATE, permission);
	}

	/**
	 *
	 * 시스템 정의 리소스 세부 접근 권한 삭제
	 *
	 * @param permissionId
	 * @return
	 */
	public int deletePermission(String permissionId) {
		final PermissionMapping permission = BeanUtils.instantiate(factory.getCustomizer().getPermissionClass());

		permission.setPermissionId(permissionId);

		if(logger.isDebugEnabled())	{
			logger.debug("[PermissionService] updatePermission - permissionid={} descrption={}", permission.getPermissionId(), permission.getDescription());
		}

		return queryExecutor.update(SimpleORMQueryType.DELETE, permission);
	}
}
