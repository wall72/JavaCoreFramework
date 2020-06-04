package jcf.query.core.handler;

import java.util.List;
import java.util.Map;

import jcf.query.exception.UnsupportedParameterException;

import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 *
 * @author nolang
 *
 */
public class DefaultParameterExceptionHandler implements ParameterExceptionHandler {

	public <T> T primitiveTypeHandler(String statement, Object parameter, Class<T> clazz) {
		throw new UnsupportedParameterException("[허용되지 않는 클래스 타입] " + parameter.getClass().getName());
	}

	public Map<String, Object> mapHandler(String statement, Object parameter) {
		throw new UnsupportedParameterException("[허용되지 않는 클래스 타입] " + parameter.getClass().getName());
	}

	public <T> T objectHandler(String statement, Object parameter, Class<T> clazz) {
		throw new UnsupportedParameterException("[허용되지 않는 클래스 타입] " + parameter.getClass().getName());
	}

	public List<Map<String, Object>> mapListHandler(String statement, Object parameter) {
		throw new UnsupportedParameterException("[허용되지 않는 클래스 타입] " + parameter.getClass().getName());
	}

	public <T> List<T> listHandler(String statement, Object parameter,	Class<T> clazz) {
		throw new UnsupportedParameterException("[허용되지 않는 클래스 타입] " + parameter.getClass().getName());
	}

	public SqlRowSet sqlRowSetHandler(String statement, Object parameter) {
		throw new UnsupportedParameterException("[허용되지 않는 클래스 타입] " + parameter.getClass().getName());
	}

}
