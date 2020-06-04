package jcf.sua.support.rad.dao;

import java.util.List;
import java.util.Map;

import jcf.data.handler.StreamHandler;

import org.springframework.dao.DataAccessException;

public interface RadSupportDao {

	/**
	 *
	 * vȸ
	 *
	 * @param sqlId
	 * @param parameter
	 * @return
	 * @throws DataAccessException
	 */
	List<?> selectList(String sqlId, Object parameter) throws DataAccessException;

	/**
	 *
	 * ��뷮������ ó���� '�� ��Ʈ���� ó��
	 *
	 * @param sqlId
	 * @param parameter
	 * @param streamHandler
	 * @throws DataAccessException
	 */
	void selectListByStream(String sqlId, Object parameter, StreamHandler<Object> streamHandler) throws DataAccessException;

	/**
	 *
	 * vȸ
	 *
	 * @param sqlId
	 * @param parameter
	 * @param skipRows
	 * @param maxRows
	 * @return
	 */
	List<?> selectList(String sqlId, Object parameter, int skipRows, int maxRows);

	/**
	 *
	 * �߰�
	 *
	 * @param sqlId
	 * @param parameterList
	 * @return
	 */
	Object insertList(String sqlId, List<Map<String, String>> parameterList);

	/**
	 *
	 * ��d
	 *
	 * @param sqlId
	 * @param parameterList
	 * @return
	 */
	Object updateList(String sqlId, List<Map<String, String>> parameterList);

	/**
	 *
	 * ��f
	 *
	 * @param sqlId
	 * @param parameterList
	 * @return
	 */
	Object deleteList(String sqlId, List<Map<String, String>> parameterList);

}