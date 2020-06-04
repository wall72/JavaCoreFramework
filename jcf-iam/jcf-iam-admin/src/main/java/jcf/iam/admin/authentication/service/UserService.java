package jcf.iam.admin.authentication.service;

import java.util.List;

import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authentication.UserMapping;
import jcf.query.core.QueryExecutorWrapper;
import jcf.query.core.evaluator.SimpleORMQueryType;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * 사용자 정보를 처리하는 연산을 수행한다.
 * <pre>
 *
 * @see jcf.iam.admin.authentication.controller.UserController
 * @see jcf.iam.core.IamCustomizerFactory
 *
 * @author nolang
 *
 */
@Service
public class UserService {

	@Autowired
	private IamCustomizerFactory customizer;

	@Autowired
	private QueryExecutorWrapper queryExecutor;

	/**
	 *
	 * 사용자 리스트 정보 조회
	 *
	 * @param user
	 * @return
	 */
	public List<? extends UserMapping> getUserList(final UserMapping userMapping) {
		UserMapping user = userMapping;

		if(user == null) {
			user = BeanUtils.instantiate(customizer.getCustomizer().getUserClass());
		}

		return queryExecutor.queryForList(SimpleORMQueryType.SELECT, userMapping, customizer.getCustomizer().getUserClass());
	}

	/**
	 *
	 * 사용자 리스트 정보 조회
	 *
	 * @param username
	 * @return
	 */
	public List<? extends UserMapping> getUserList(String username) {
		final UserMapping user = BeanUtils.instantiate(customizer.getCustomizer().getUserClass());

		user.setUsername(username);

		return queryExecutor.queryForList(SimpleORMQueryType.SELECT, user, customizer.getCustomizer().getUserClass());
	}

	/**
	 *
	 * 사용자 정보 조회
	 *
	 * @param username
	 * @return
	 */
	public UserMapping getUser(String username) {
		final UserMapping user = BeanUtils.instantiate(customizer.getCustomizer().getUserClass());

		user.setUsername(username);

		return queryExecutor.queryForObject(SimpleORMQueryType.SELECT, user, customizer.getCustomizer().getUserClass());
	}

	/**
	 *
	 * 사용자 추가
	 *
	 * @param queryUser
	 * @return
	 */
	public int insertUser(UserMapping queryUser){
		return queryExecutor.update(SimpleORMQueryType.INSERT, queryUser);
	}

	/**
	 *
	 * 사용자 수정
	 *
	 * @param queryUser
	 * @return
	 */
	public int updateUser(UserMapping queryUser){
		return queryExecutor.update(SimpleORMQueryType.UPDATE, queryUser);
	}

	/**
	 *
	 * 사용자 삭제
	 *
	 * @param username
	 * @return
	 */
	public int deleteUser(String username){
		UserMapping queryUser = BeanUtils.instantiate(customizer.getCustomizer().getUserClass());

		queryUser.setUsername(username);

		return queryExecutor.update(SimpleORMQueryType.DELETE, queryUser);
	}

}
