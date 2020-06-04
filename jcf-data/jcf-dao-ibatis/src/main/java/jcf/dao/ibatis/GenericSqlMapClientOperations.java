package jcf.dao.ibatis;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

//import com.ibatis.common.util.PaginatedList;
import com.ibatis.sqlmap.client.event.RowHandler;

public interface GenericSqlMapClientOperations {

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryForObject(String)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	<E> E queryForObject(String statementName) throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryForObject(String, Object)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	<E> E queryForObject(String statementName, Object parameterObject)
			throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryForObject(String, Object, Object)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	<E> E queryForObject(String statementName, Object parameterObject,	E resultObject)
			throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryForList(String)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	<E> List<E> queryForList(String statementName) throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryForList(String, Object)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	<E> List<E> queryForList(String statementName, Object parameterObject)
			throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryForList(String, int, int)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	<E> List<E> queryForList(String statementName, int skipResults, int maxResults)
			throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryForList(String, Object, int, int)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	<E> List<E> queryForList(String statementName, Object parameterObject, int skipResults, int maxResults)
			throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryWithRowHandler(String, RowHandler)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	void queryWithRowHandler(String statementName, RowHandler rowHandler)
			throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryWithRowHandler(String, Object, RowHandler)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	void queryWithRowHandler(String statementName, Object parameterObject, RowHandler rowHandler)
			throws DataAccessException;

//	/**
//	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryForPaginatedList(String, int)
//	 * @deprecated as of iBATIS 2.3.0
//	 * @throws org.springframework.dao.DataAccessException in case of errors
//	 */
//	PaginatedList queryForPaginatedList(String statementName, int pageSize)
//			throws DataAccessException;
//
//	/**
//	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryForPaginatedList(String, Object, int)
//	 * @deprecated as of iBATIS 2.3.0
//	 * @throws org.springframework.dao.DataAccessException in case of errors
//	 */
//	PaginatedList queryForPaginatedList(String statementName, Object parameterObject, int pageSize)
//			throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryForMap(String, Object, String)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	Map<String, ?> queryForMap(String statementName, Object parameterObject, String keyProperty)
			throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryForMap(String, Object, String, String)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	Map<String, ?> queryForMap(String statementName, Object parameterObject, String keyProperty, String valueProperty)
			throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#insert(String)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	<E> E insert(String statementName) throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#insert(String, Object)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	<E> E insert(String statementName, Object parameterObject) throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#update(String)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	int update(String statementName) throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#update(String, Object)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	int update(String statementName, Object parameterObject) throws DataAccessException;

	/**
	 * Convenience method provided by Spring: execute an update operation
	 * with an automatic check that the update affected the given required
	 * number of rows.
	 * @param statementName the name of the mapped statement
	 * @param parameterObject the parameter object
	 * @param requiredRowsAffected the number of rows that the update is
	 * required to affect
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	void update(String statementName, Object parameterObject, int requiredRowsAffected)
			throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#delete(String)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	int delete(String statementName) throws DataAccessException;

	/**
	 * @see com.ibatis.sqlmap.client.SqlMapExecutor#delete(String, Object)
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	int delete(String statementName, Object parameterObject) throws DataAccessException;

	/**
	 * Convenience method provided by Spring: execute a delete operation
	 * with an automatic check that the delete affected the given required
	 * number of rows.
	 * @param statementName the name of the mapped statement
	 * @param parameterObject the parameter object
	 * @param requiredRowsAffected the number of rows that the delete is
	 * required to affect
	 * @throws org.springframework.dao.DataAccessException in case of errors
	 */
	void delete(String statementName, Object parameterObject, int requiredRowsAffected)
			throws DataAccessException;

}
