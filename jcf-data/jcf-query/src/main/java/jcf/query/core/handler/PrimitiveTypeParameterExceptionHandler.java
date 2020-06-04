package jcf.query.core.handler;

import java.util.List;
import java.util.Map;

import jcf.query.core.mapper.ParameterizedBeanRowMapper;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 *
 * @author nolang
 *
 */
public class PrimitiveTypeParameterExceptionHandler implements ParameterExceptionHandler {

	private SimpleJdbcTemplate jdbcTemplate;

	public PrimitiveTypeParameterExceptionHandler(SimpleJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@SuppressWarnings("unchecked")
	public <T> T primitiveTypeHandler(String statement, Object parameter, Class<T> clazz) {
		Object result = null;

		if (Integer.class.isAssignableFrom(clazz)) {
			result = jdbcTemplate.queryForInt(statement, parameter);
		} else if(Long.class.isAssignableFrom(Long.class))	{
			result = jdbcTemplate.queryForLong(statement, parameter);
		}

		return (T) result;
	}

	public Map<String, Object> mapHandler(String statement, Object parameter) {
		return jdbcTemplate.queryForMap(statement, parameter);
	}

	public <T> T objectHandler(String statement, Object parameter, Class<T> clazz) {
		return jdbcTemplate.queryForObject(statement, clazz, parameter);
	}

	public List<Map<String, Object>> mapListHandler(String statement, Object parameter) {
		return jdbcTemplate.queryForList(statement, parameter);
	}

	public <T> List<T> listHandler(String statement, Object parameter, Class<T> clazz) {
		return jdbcTemplate.query(statement, ParameterizedBeanRowMapper.newInstance(clazz), parameter);
	}

	public SqlRowSet sqlRowSetHandler(String statement, Object parameter) {
		return jdbcTemplate.getJdbcOperations().queryForRowSet(statement, parameter);
	}
}
