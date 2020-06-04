package jcf.dao.ibatis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jcf.dao.StatementMappingDataAccessOperations;
import jcf.dao.StreamingRowHandler;
import jcf.dao.ibatis.crud.DeleteResult;
import jcf.dao.ibatis.crud.ExecutionResult;
import jcf.dao.ibatis.crud.InsertResult;
import jcf.dao.ibatis.crud.RowStatus;
import jcf.dao.ibatis.crud.UnknownResult;
import jcf.dao.ibatis.crud.UpdateResult;
import jcf.dao.ibatis.crud.util.DefaultSaveStatusMapping;
import jcf.dao.ibatis.crud.util.SaveStatusMapping;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * @author purple
 * @author mina
 * @desc 1/12 jcf-mci에 있던 CommonDao 와 기존의 BatchAndStreamSqlMapCientDao 와
 *       CommonGridSqlMapClient를 통합함 jcf-mci에 있던 CommonDao 에 스트림
 *       조회(selectListbyStream), 롤백없이 저장(saveListWithoutRollback), selectMap 부분
 *       추가함
 */
@SuppressWarnings("rawtypes")
public class IBatisStatementMappingDao extends SqlMapClientDaoSupport implements
		StatementMappingDataAccessOperations, InitializingBean {

	private BasicSqlMapClientOperations basicSqlMapClientOperations;

	@Override
	protected void initDao() throws Exception {
		super.initDao();
		BasicSqlMapClientDao basicSqlMapClientDao = new BasicSqlMapClientDao();
		basicSqlMapClientDao.setSqlMapClientTemplate(getSqlMapClientTemplate());

		basicSqlMapClientOperations = basicSqlMapClientDao;
	}

	public void selectListbyStream(String statementName, Object param,
			StreamingRowHandler streamingRowHandler) {
		basicSqlMapClientOperations.stream(statementName, param,
				streamingRowHandler);
	}

	//
	// /**
	// * Simple added method to flush the all contents in cache.
	// * @author purple
	// * @since 2005.05.16
	// * @return void
	// */
	// public void flush(){
	// getSqlMapClient().flushDataCache();
	// }

	/**
	 *
	 * @deprecated 확장성이 떨어지고 중복되어 개선할 예정임.
	 * @param queryStatement
	 * @return
	 */
	protected Object executeDynamicQueryForObject(String queryStatement) {
		return (Object) basicSqlMapClientOperations.queryForObject(
				"dynamicQuery", queryStatement);
	}

	/**
	 *
	 * @deprecated 확장성이 떨어지고 중복되어 개선할 예정임.
	 * @param queryStatement
	 * @return
	 */
	protected List executeDynamicQueryForList(String queryStatement) {
		return (List) basicSqlMapClientOperations.queryForList("dynamicQuery",
				queryStatement);
	}

	/**
	 *
	 *
	 * @param statementName
	 *            SQL 문장명
	 * @param parameterList
	 *            객체 리스트
	 * @return
	 */
	public Object insertList(String statementName, List parameterList) {
		Iterator iter = parameterList.iterator();
		while (iter.hasNext()) {
			basicSqlMapClientOperations.insert(statementName, iter.next());
		}
		return new Integer(1);
	}

	/**
	 *
	 *
	 * @param statementName
	 *            SQL 문장명
	 * @param parameterList
	 *            객체 리스트
	 * @return
	 */
	public Object updateList(String statementName, List parameterList) {
		Iterator iter = parameterList.iterator();
		while (iter.hasNext()) {
			basicSqlMapClientOperations.update(statementName, iter.next());
		}
		return new Integer(1);
	}

	/**
	 *
	 *
	 * @param statementName
	 *            SQL 문장명
	 * @param parameterList
	 *            객체 리스트
	 * @return
	 */
	public Object deleteList(String statementName, List parameterList) {
		Iterator iter = parameterList.iterator();
		while (iter.hasNext()) {
			basicSqlMapClientOperations.delete(statementName, iter.next());
		}
		return new Integer(1);
	}

	/**
	 * <p>
	 * statusWildcardQueryMapping는 와일드 카드를 갖는 매핑 스트링을 받는다.
	 * <ul>
	 * <li>"user.*User"를 입력한 경우, user.insertUser, user.updateUser,
	 * user.deleteUser에 매핑
	 * </ul>
	 *
	 * <p>
	 * 파라미터 객체가 rowStatus 필드를 갖지 않으면, 예외를 발생시킨다.
	 *
	 * @param statusWildcardQueryMapping
	 *            와일드카드를 사용한 쿼리 매핑 스트링.
	 * @param parameterObject
	 *            파라미터 객체
	 * @return
	 */
	public Object save(String statusWildcardQueryMapping, Object parameterObject) {
		SaveStatusMapping saveStatusMapping = new DefaultSaveStatusMapping(
				statusWildcardQueryMapping);
		if (null == parameterObject)
			return 0;

		String status = getStatusPropertyForSave(parameterObject,
				saveStatusMapping.getStatusPropertyName());
		String sqlId = saveStatusMapping.getStatusMappedSqlId(status);

		if (StringUtils.isBlank(sqlId)) {
			throw new RuntimeException(
					"Given parameter object's status value is not mapped with any configured status. [ given status value : "
							+ status
							+ " ], [ status options : "
							+ saveStatusMapping.getStatusInfo() + " ]");
		}

		return new Integer(this.basicSqlMapClientOperations.update(sqlId,
				parameterObject));
	}

	/**
	 * <p>
	 * statusWildcardQueryMapping는 와일드 카드를 갖는 매핑 스트링을 받는다.
	 * <ul>
	 * <li>"user.*User"를 입력한 경우, user.insertUser, user.updateUser,
	 * user.deleteUser에 매핑
	 * </ul>
	 *
	 * <p>
	 * 파라미터 객체가 rowStatus 필드를 갖지 않으면, 예외를 발생시킨다.
	 *
	 * @param statusWildcardQueryMapping
	 *            와일드카드를 사용한 쿼리 매핑 스트링.
	 * @param parameterList
	 * @return
	 */
	public Object saveList(String statusWildcardQueryMapping, List parameterList) {
		Iterator iter = parameterList.iterator();
		SaveStatusMapping saveStatusMapping = new DefaultSaveStatusMapping(
				statusWildcardQueryMapping);

		while (iter.hasNext()) {
			Object object = iter.next();
			String status = getStatusPropertyForSave(object, saveStatusMapping
					.getStatusPropertyName());
			String sqlId = saveStatusMapping.getStatusMappedSqlId(status);

			if (status.equals(RowStatus.DEFAULT_ROW_STATUS_INSERT)) {
				basicSqlMapClientOperations.insert(sqlId, object);
			} else if (status.equals(RowStatus.DEFAULT_ROW_STATUS_DELETE)) {
				basicSqlMapClientOperations.delete(sqlId, object);
			} else if (status.equals(RowStatus.DEFAULT_ROW_STATUS_UPDATE)) { // "UPDATE"
				basicSqlMapClientOperations.update(sqlId, object);
			} else

				throw new RuntimeException(
						"Given parameter object's status value is not mapped with any configured status. [ given status value : "
								+ status
								+ " ], [ status options : "
								+ saveStatusMapping.getStatusInfo() + " ]");
		}

		return new Integer(1);
	}

	/**
	 * <p>
	 * statusWildcardQueryMapping는 와일드 카드를 갖는 매핑 스트링을 받는다. 에러 발생 시 콜백없이 진행하고 에러난
	 * 데이터만 리턴한다.
	 * <ul>
	 * <li>"user.*User"를 입력한 경우, user.insertUser, user.updateUser,
	 * user.deleteUser에 매핑
	 * </ul>
	 *
	 * <p>
	 * 파라미터 객체가 rowStatus 필드를 갖지 않으면, 예외를 발생시킨다.
	 *
	 * @param statusWildcardQueryMapping
	 *            와일드카드를 사용한 쿼리 매핑 스트링.
	 * @param parameterList
	 * @return
	 */
	public List<ExecutionResult> saveListWithoutRollback(
			String statusWildcardQueryMapping, List parameterList) {
		Iterator iter = parameterList.iterator();
		SaveStatusMapping saveStatusMapping = new DefaultSaveStatusMapping(
				statusWildcardQueryMapping);

		List<ExecutionResult> executionResults = new ArrayList<ExecutionResult>();
		while (iter.hasNext()) {
			Object object = iter.next();
			String status = getStatusPropertyForSave(object, saveStatusMapping
					.getStatusPropertyName());
			String sqlId = saveStatusMapping.getStatusMappedSqlId(status);

			if (status.equals(RowStatus.DEFAULT_ROW_STATUS_INSERT)) {
				try {
					executionResults.add(new InsertResult(
							basicSqlMapClientOperations.insert(sqlId, object)));

				} catch (Exception e) {
					executionResults.add(new InsertResult(e));
				}

			} else if (status.equals(RowStatus.DEFAULT_ROW_STATUS_DELETE)) {
				try {
					executionResults.add(new DeleteResult(
							basicSqlMapClientOperations.delete(sqlId, object)));

				} catch (Exception e) {
					executionResults.add(new DeleteResult(e));
				}
			} else if (status.equals(RowStatus.DEFAULT_ROW_STATUS_UPDATE)) { // "UPDATE"
				try {
					executionResults.add(new UpdateResult(
							basicSqlMapClientOperations.update(sqlId, object)));

				} catch (Exception e) {
					executionResults.add(new UpdateResult(e));
				}
			}

			else {
				executionResults.add(new UnknownResult(status));
			}
		}
		return executionResults;
	}

	/**
	 * <p>
	 * 파라미터 객체가 갖는 입력/수정/삭제의 상태값을 가져온다. 만약 상태갑에 매핑되는 필드가 없거나, 스트링 값이 없다면 예외를
	 * 발생시킨다.
	 *
	 * @param object
	 * @param statusPropertyName
	 * @return
	 */
	private String getStatusPropertyForSave(Object object,
			String statusPropertyName) {
		try {
			String status = (String) PropertyUtils.getProperty(object,
					statusPropertyName);
			if (StringUtils.isBlank(status)) {
				throw new RuntimeException("Status property '"
						+ statusPropertyName
						+ "' of parameter object property should be not null.");
			}
			return status;
		} catch (ClassCastException ccs) {
			throw new RuntimeException("Status property '" + statusPropertyName
					+ "' should be String.");
		} catch (IllegalArgumentException iae) {
			throw new RuntimeException("Given object's " + object
					+ " couldn't be handled to obtain row status property.");
		} catch (Exception e) {
			throw new RuntimeException(
					"Given object "
							+ object
							+ "'s property '"
							+ statusPropertyName
							+ "' can't be accessed. Please check if property exist and has getter method.");
		}
	}

	public Object insert(String sqlId, Object object)
			throws DataAccessException {
		return basicSqlMapClientOperations.insert(sqlId, object);
	}

	public Object update(String sqlId, Object object)
			throws DataAccessException {

		return new Integer(basicSqlMapClientOperations.update(sqlId, object));
	}

	public void update(String sqlId, Object object, int requiredRowsAffected)
			throws DataAccessException {
		basicSqlMapClientOperations.update(sqlId, object, requiredRowsAffected);
	}

	public Object delete(String sqlId, Object object)
			throws DataAccessException {
		return new Integer(basicSqlMapClientOperations.delete(sqlId, object));
	}

	public void delete(String sqlId, Object object, int requiredRowsAffected)
			throws DataAccessException {
		basicSqlMapClientOperations.delete(sqlId, object, requiredRowsAffected);
	}

	public Object select(String sqlId, Object object)
			throws DataAccessException {
		return basicSqlMapClientOperations.queryForObject(sqlId, object);
	}

	public List selectList(String sqlId, Object object)
			throws DataAccessException {
		return basicSqlMapClientOperations.queryForList(sqlId, object);
	}

	public Map selectMap(String sqlId, Object object, String keyProperty)
			throws DataAccessException {
		return basicSqlMapClientOperations.queryForMap(sqlId, object,
				keyProperty);
	}

	public Map selectMap(String sqlId, Object object, String keyProperty,
			String valueProperty) throws DataAccessException {
		return basicSqlMapClientOperations.queryForMap(sqlId, object,
				keyProperty, valueProperty);
	}

	public List selectList(String sqlId, Object object, int skipRows,
			int maxRows) {
		return basicSqlMapClientOperations.queryForList(sqlId, object,
				skipRows, maxRows);
	}
}
