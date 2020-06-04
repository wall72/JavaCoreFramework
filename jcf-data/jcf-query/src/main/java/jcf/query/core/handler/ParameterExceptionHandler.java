package jcf.query.core.handler;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 *
 * @author nolang
 *
 */
public interface ParameterExceptionHandler {

	<T> T primitiveTypeHandler(String statement, Object parameter, Class<T> clazz);

	Map<String, Object> mapHandler(String statement, Object parameter);

	<T> T objectHandler(String statement, Object parameter, Class<T> clazz);

	List<Map<String, Object>> mapListHandler(String statement, Object parameter);

	<T> List<T> listHandler(String statement, Object parameter, Class<T> clazz);

	SqlRowSet sqlRowSetHandler(String statement, Object parameter);

}
