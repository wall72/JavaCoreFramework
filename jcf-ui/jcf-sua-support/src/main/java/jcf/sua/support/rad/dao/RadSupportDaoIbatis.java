package jcf.sua.support.rad.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jcf.data.handler.StreamHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.ibatis.sqlmap.client.event.RowHandler;

/**
 *
 * RAD ��� DAO Ŭ����
 *
 * @author nolang
 *
 */
public class RadSupportDaoIbatis implements RadSupportDao  {

	@Autowired
	private SqlMapClientTemplate sqlMapClientTemplate;

	public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
		this.sqlMapClientTemplate = sqlMapClientTemplate;
	}

	/* (non-Javadoc)
	 * @see jcf.sua.support.rad.dao.RadSupportDao#selectList(java.lang.String, java.lang.Object)
	 */
	public List<?> selectList(String sqlId, Object object)
			throws DataAccessException {
		return sqlMapClientTemplate.queryForList(sqlId, object);
	}

	/* (non-Javadoc)
	 * @see jcf.sua.support.rad.dao.RadSupportDao#selectListByStream(java.lang.String, java.lang.Object, java.lang.String, int)
	 */
	public void selectListByStream(String sqlId, Object object, final StreamHandler<Object> streamHandler) throws DataAccessException {
		streamHandler.open();

		try {
			sqlMapClientTemplate.queryWithRowHandler(sqlId, object,	new RowHandler() {
						public void handleRow(Object valueObject) {
							streamHandler.handleRow(valueObject);
						}
					});
		} finally {
			streamHandler.close();
		}
	}

	/* (non-Javadoc)
	 * @see jcf.sua.support.rad.dao.RadSupportDao#selectList(java.lang.String, java.lang.Object, int, int)
	 */
	public List<?> selectList(String sqlId, Object object, int skipRows,
			int maxRows) {
		return sqlMapClientTemplate.queryForList(sqlId, object, skipRows, maxRows);
	}

	/* (non-Javadoc)
	 * @see jcf.sua.support.rad.dao.RadSupportDao#insertList(java.lang.String, java.util.List)
	 */
	public Object insertList(String sqlId, List<Map<String, String>> parameterList) {
		Iterator<?> iter = parameterList.iterator();

		while (iter.hasNext()) {
			sqlMapClientTemplate.insert(sqlId, iter.next());
		}

		return new Integer(1);
	}

	/* (non-Javadoc)
	 * @see jcf.sua.support.rad.dao.RadSupportDao#updateList(java.lang.String, java.util.List)
	 */
	public Object updateList(String sqlId, List<Map<String, String>> parameterList) {
		Iterator<?> iter = parameterList.iterator();
		int executeCount = 0;

		while (iter.hasNext()) {
			executeCount += sqlMapClientTemplate.update(sqlId, iter.next());
		}

		return executeCount;
	}

	/* (non-Javadoc)
	 * @see jcf.sua.support.rad.dao.RadSupportDao#deleteList(java.lang.String, java.util.List)
	 */
	public Object deleteList(String sqlId, List<Map<String, String>> parameterList) {
		Iterator<?> iter = parameterList.iterator();
		int executeCount = 0;

		while (iter.hasNext()) {
			executeCount += sqlMapClientTemplate.delete(sqlId, iter.next());
		}

		return executeCount;
	}
}
