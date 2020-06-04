package jcf.iam.admin.authentication.service;

import java.util.List;

import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authentication.RoleMapping;
import jcf.query.core.QueryExecutorWrapper;
import jcf.query.core.evaluator.SimpleORMQueryType;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * 시스템에 정의된 권한정보를 처리하는 연산을 수행한다.
 * <pre>
 *
 * @see jcf.iam.admin.authentication.controller.RoleController
 * @see jcf.iam.core.IamCustomizerFactory
 *
 * @author nolang
 *
 */
@Service
public class RoleService {

	@Autowired
	private IamCustomizerFactory factory;

	@Autowired
	private QueryExecutorWrapper queryExecutor;

	/**
	 *
	 * 시스템에 정의된 권한리스트를 조회한다.
	 *
	 * @return
	 */
	public List<? extends RoleMapping> getRoleList()	{
		final RoleMapping queryRole = BeanUtils.instantiate(factory.getCustomizer().getRoleClass());

		return queryExecutor.queryForList(SimpleORMQueryType.SELECT, queryRole, factory.getCustomizer().getRoleClass());
	}

	/**
	 *
	 * 권한 세부정보 조회
	 *
	 * @param roleId
	 * @return
	 */
	public RoleMapping getRole(String roleId)	{
		final RoleMapping queryRole = BeanUtils.instantiate(factory.getCustomizer().getRoleClass());

		queryRole.setRoleId(roleId);

		return queryExecutor.queryForObject(SimpleORMQueryType.SELECT, queryRole, factory.getCustomizer().getRoleClass());
	}

	/**
	 *
	 * 권한 추가
	 *
	 * @param role
	 * @return
	 */
	public int insertRole(RoleMapping role) {
		return queryExecutor.update(SimpleORMQueryType.INSERT, role);
	}

	/**
	 *
	 * 권한 수정
	 *
	 * @param role
	 * @return
	 */
	public int updateRole(RoleMapping role) {
		return queryExecutor.update(SimpleORMQueryType.UPDATE, role);
	}

	/**
	 *
	 * 권한 삭제
	 *
	 * @param roleId
	 * @return
	 */
	public int deleteUser(String roleId){
		RoleMapping queryRole = BeanUtils.instantiate(factory.getCustomizer().getRoleClass());

		queryRole.setRoleId(roleId);

		return queryExecutor.update(SimpleORMQueryType.DELETE, queryRole);
	}
}
