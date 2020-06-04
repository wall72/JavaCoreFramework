package jcf.iam.admin.authentication.service;

import java.util.List;

import jcf.data.GridData;
import jcf.data.RowStatus;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authentication.AuthorityMapping;
import jcf.query.core.QueryExecutorWrapper;
import jcf.query.core.evaluator.SimpleORMQueryType;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * 사용자별 권한 매핑과 관련한 연산을 수행한다.
 * <pre>
 *
 * @see jcf.iam.admin.authentication.controller.AuthorityController
 * @see jcf.iam.core.IamCustomizerFactory
 * @see jcf.iam.core.jdbc.builder.QueryBuilder
 * @see jcf.iam.core.jdbc.builder.ResultMapper
 *
 * @author nolang
 *
 */
@Service
public class AuthorityService {

	@Autowired
	private IamCustomizerFactory factory;

	@Autowired
	private QueryExecutorWrapper queryExecutor;

	/**
	 *
	 * 사용자별 권한 매핑 리스트를 조회한다.
	 *
	 * @param username
	 * @return
	 */
	public List<? extends AuthorityMapping> getAuthorities(String username)	{
		final AuthorityMapping queryAuthority = BeanUtils.instantiate(factory.getCustomizer().getAuthorityClass());

		queryAuthority.setUsername(username);

		return queryExecutor.queryForList(SimpleORMQueryType.SELECT, queryAuthority, factory.getCustomizer().getAuthorityClass());
	}

	/**
	 *
	 * 사용자별 권한 매핑 정보를 추가한다.
	 *
	 * @param userAuthority
	 * @return
	 */
	public int insertUserAuthority(AuthorityMapping userAuthority) {
		return queryExecutor.update(SimpleORMQueryType.INSERT, userAuthority);
	}

	/**
	 *
	 * 사용자별 권한 매핑 정보를 수정한다.
	 *
	 * @param userAuthority
	 * @return
	 */
	public int updateUserAuthority(AuthorityMapping userAuthority) {
		return queryExecutor.update(SimpleORMQueryType.UPDATE, userAuthority);
	}

	/**
	 *
	 * 사용자별 권한 매핑 정보를 삭제한다.
	 *
	 * @param username
	 * @param authority
	 * @return
	 */
	public int deleteUserAuthority(String username, String authority) {
		AuthorityMapping userAuthority = BeanUtils.instantiate(factory.getCustomizer().getAuthorityClass());

		userAuthority.setUsername(username);
		userAuthority.setAuthority(authority);

		return deleteUserAuthority(userAuthority);
	}

	public int deleteUserAuthority(AuthorityMapping userAuthority) {
		return queryExecutor.update(SimpleORMQueryType.DELETE, userAuthority);
	}

	/**
	 *
	 * VIEW RESOURCE (MENU) 추가/수정/삭제
	 *
	 * @param gridData
	 * @return
	 */
	public int saveUserAuthorities(GridData<? extends AuthorityMapping> gridData)	{
		List<? extends AuthorityMapping> list = gridData.getList();

		int count = 0;

		for (int i = 0; i < list.size(); ++i) {
			RowStatus rowStatus = gridData.getStatusOf(i);
			AuthorityMapping authority = list.get(i);

			if (rowStatus == RowStatus.INSERT) {
				count += insertUserAuthority(authority);
			} else if (rowStatus == RowStatus.UPDATE) {
				count += updateUserAuthority(authority);
			} else if (rowStatus == RowStatus.DELETE) {
				count += deleteUserAuthority(authority);
			}
		}

		return count;
	}
}
