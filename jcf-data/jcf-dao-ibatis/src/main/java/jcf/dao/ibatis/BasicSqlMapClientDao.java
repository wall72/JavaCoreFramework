package jcf.dao.ibatis;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import jcf.dao.StreamingRowHandler;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ibatis.sqlmap.client.event.RowHandler;


/**
 * SqlMapClientDaoSupport 확장한 데이터 접근 계층용 클래스.
 * @author setq
 *
 */
public class BasicSqlMapClientDao extends SqlMapClientDaoSupport implements
		BasicSqlMapClientOperations {

	private int batchSize = 100;

	/**
	 * batch insert/update를 할 때나 grid update를 할 때 실행할 갯수가 많으면 끊어서 보내주는데, jdbc
	 * batch로 한 묶음당 보낼 sql의 수. 기본값은 100개.
	 *
	 * @param batchSize
	 */
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public void stream(String statementName, Object param,
			StreamingRowHandler streamingRowHandler) {

		try {
			streamingRowHandler.open();
			getSqlMapClientTemplate().queryWithRowHandler(statementName, param,
					new StreamingRowHandlerAdapter(streamingRowHandler));

		} finally {
			streamingRowHandler.close();
		}
	}

	public int batchInsert(final String statementId,
			final Iterable<?> listOfParameterObject) {
		return batchInsert(statementId, listOfParameterObject, this.batchSize);
	}

	public int batchInsert(final String statementId,
			final Iterable<?> listOfParameterObject, final int batchSize) {

		return getSqlMapClientTemplate().execute(
				new SqlMapClientCallback<Integer>() {

					public Integer doInSqlMapClient(SqlMapExecutor executor)
							throws SQLException {

						int totalUpdateCount = 0;
						int count = 0;

						for (Object parameterObject : listOfParameterObject) {
							if (count++ == 0) {
								executor.startBatch();
							}

							executor.insert(statementId, parameterObject);

							if (count >= batchSize) {
								executor.executeBatch();
								totalUpdateCount += count;
								count = 0;
							}
						}

						if (count > 0) {
							executor.executeBatch();
							totalUpdateCount += count;
							count = 0;
						}

						return totalUpdateCount;
					}
				});
	}

	public int batchUpdate(final String statementId,
			final Iterable<?> listOfParameterObject) {
		return batchUpdate(statementId, listOfParameterObject, this.batchSize);
	}

	public int batchUpdate(final String statementId,
			final Iterable<?> listOfParameterObject, final int batchSize) {

		return getSqlMapClientTemplate().execute(
				new SqlMapClientCallback<Integer>() {

					public Integer doInSqlMapClient(SqlMapExecutor executor)
							throws SQLException {

						int totalUpdateCount = 0;
						int count = 0;

						for (Object parameterObject : listOfParameterObject) {
							if (count++ == 0) {
								executor.startBatch();
							}

							executor.update(statementId, parameterObject);

							if (count >= batchSize) {
								executor.executeBatch();
								totalUpdateCount += count;
								count = 0;
							}
						}

						if (count > 0) {
							executor.executeBatch();
							totalUpdateCount += count;
							count = 0;
						}

						return totalUpdateCount;
					}
				});
	}

	public int delete(String statementName) throws DataAccessException {
		return getSqlMapClientTemplate().delete(statementName);
	}

	public int delete(String statementName, Object parameterObject)	throws DataAccessException {
		return getSqlMapClientTemplate().delete(statementName, parameterObject);
	}

	public void delete(String statementName, Object parameterObject, int requiredRowsAffected) throws DataAccessException {
		getSqlMapClientTemplate().delete(statementName, parameterObject, requiredRowsAffected);
	}

	@SuppressWarnings("unchecked")
	public <E> E insert(String statementName) throws DataAccessException {
		return (E) getSqlMapClientTemplate().insert(statementName);
	}

	@SuppressWarnings("unchecked")
	public <E> E insert(String statementName, Object parameterObject)
			throws DataAccessException {
		return (E) getSqlMapClientTemplate().insert(statementName, parameterObject);
	}

	@SuppressWarnings("unchecked")
	public <E> List<E> queryForList(String statementName) throws DataAccessException {
		return getSqlMapClientTemplate().queryForList(statementName);
	}

	@SuppressWarnings("unchecked")
	public <E> List<E> queryForList(String statementName, Object parameterObject) throws DataAccessException {
		return getSqlMapClientTemplate().queryForList(statementName, parameterObject);
	}

	@SuppressWarnings("unchecked")
	public <E> List<E> queryForList(String statementName, int skipResults, int maxResults) throws DataAccessException {
		return getSqlMapClientTemplate().queryForList(statementName, skipResults, maxResults);
	}

	@SuppressWarnings("unchecked")
	public <E> List<E> queryForList(String statementName, Object parameterObject, int skipResults, int maxResults) throws DataAccessException {
		return getSqlMapClientTemplate().queryForList(statementName, parameterObject, skipResults, maxResults);
	}

	@SuppressWarnings("unchecked")
	public Map<String, ?> queryForMap(String statementName, Object parameterObject, String keyProperty) throws DataAccessException {
		return getSqlMapClientTemplate().queryForMap(statementName, parameterObject, keyProperty);
	}

	@SuppressWarnings("unchecked")
	public Map<String, ?> queryForMap(String statementName, Object parameterObject, String keyProperty, String valueProperty) throws DataAccessException {
		return getSqlMapClientTemplate().queryForMap(statementName, parameterObject, keyProperty, valueProperty);
	}

	@SuppressWarnings("unchecked")
	public <E> E queryForObject(String statementName) throws DataAccessException {
		return (E) getSqlMapClientTemplate().queryForObject(statementName);
	}

	@SuppressWarnings("unchecked")
	public <E> E queryForObject(String statementName, Object parameterObject) throws DataAccessException {
		return (E) getSqlMapClientTemplate().queryForObject(statementName, parameterObject);
	}

	@SuppressWarnings("unchecked")
	public <E> E queryForObject(String statementName, Object parameterObject, E resultObject) throws DataAccessException {
		return (E) getSqlMapClientTemplate().queryForObject(statementName, parameterObject, resultObject);
	}

//	/**
//	 * {@inheritDoc}
//	 * @deprecated
//	 */
//	public PaginatedList queryForPaginatedList(String statementName, int pageSize) throws DataAccessException {
//		return getSqlMapClientTemplate().queryForPaginatedList(statementName, pageSize);
//	}
//
//	/**
//	 * {@inheritDoc}
//	 * @deprecated
//	 */
//	public PaginatedList queryForPaginatedList(String statementName, Object parameterObject, int pageSize) throws DataAccessException {
//		return getSqlMapClientTemplate().queryForPaginatedList(statementName, parameterObject, pageSize);
//	}

	public void queryWithRowHandler(String statementName, RowHandler rowHandler) throws DataAccessException {
		getSqlMapClientTemplate().queryWithRowHandler(statementName, rowHandler);
	}

	public void queryWithRowHandler(String statementName, Object parameterObject, RowHandler rowHandler) throws DataAccessException {
		getSqlMapClientTemplate().queryWithRowHandler(statementName, parameterObject, rowHandler);
	}

	public int update(String statementName) throws DataAccessException {
		return getSqlMapClientTemplate().update(statementName);
	}

	public int update(String statementName, Object parameterObject) throws DataAccessException {
		return getSqlMapClientTemplate().update(statementName, parameterObject);
	}

	public void update(String statementName, Object parameterObject, int requiredRowsAffected) throws DataAccessException {
		getSqlMapClientTemplate().update(statementName, parameterObject, requiredRowsAffected);
	}

}
