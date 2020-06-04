package jcf.sua.support.rad.dao;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import jcf.data.handler.StreamHandler;
import jcf.query.TemplateEngineType;
import jcf.query.core.QueryExecutor;
import jcf.query.core.QueryTemplate;
import jcf.query.core.mapper.CamelCaseMapRowMapper;
import jcf.sua.exception.MciException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.util.ReflectionUtils;

/**
 *
 * @author nolang
 *
 */
public class RadSupportDaoJcfQuery implements RadSupportDao {

	@Autowired
	private QueryExecutor queryExecutor;

	@Autowired
	private QueryTemplate queryTemplate;

	public void setQueryExecutor(QueryExecutor queryExecutor) {
		this.queryExecutor = queryExecutor;
	}

	public List<?> selectList(String sqlId, Object parameter) 	throws DataAccessException {
		return queryExecutor.queryForList(getStatement(sqlId), parameter, new CamelCaseMapRowMapper());
	}

	public void selectListByStream(String sqlId, Object parameter,	StreamHandler<Object> streamHandler) throws DataAccessException {
		queryExecutor.queryForStream(getStatement(sqlId), parameter, streamHandler);
	}

	public List<?> selectList(String sqlId, Object parameter, int skipRows, int maxRows) {
		return queryExecutor.queryForMapList(getStatement(sqlId), parameter, skipRows, maxRows, true);
	}

	public Object insertList(String sqlId, List<Map<String, String>> parameterList) {
		return queryExecutor.batchUpdate(getStatement(sqlId), parameterList);
	}

	public Object updateList(String sqlId, List<Map<String, String>> parameterList) {
		return queryExecutor.batchUpdate(getStatement(sqlId), parameterList);
	}

	public Object deleteList(String sqlId, List<Map<String, String>> parameterList) {
		return queryExecutor.batchUpdate(getStatement(sqlId), parameterList);
	}

	private Object getStatement(String sqlId)  {
		TemplateEngineType templateType = queryTemplate.getTemplateEngineType();

		if(templateType == TemplateEngineType.VELOCITY || templateType == TemplateEngineType.FREEMARKER) {
			Object query  = sqlId;
			int index = sqlId.lastIndexOf(".");

			try {
				query = Class.forName(sqlId.substring(0, index)).newInstance();
			} catch (Exception e) {
				throw new MciException("RAD 수행실패. sqlId를 확인하세요. - sqlId=" + sqlId);
			}

			Field field = ReflectionUtils.findField(query.getClass(), sqlId.substring(index + 1));
			field.setAccessible(true);

			return ReflectionUtils.getField(field, query);
		}

		return sqlId;
	}
}
