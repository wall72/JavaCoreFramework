package jcf.query.core.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.ibatis.sqlmap.engine.type.TypeHandler;
import com.ibatis.sqlmap.engine.type.TypeHandlerFactory;

/**
 *
 * @author nolang
 *
 * @param <T>
 */
public class PrimitiveTypeRowMapper<T> implements RowMapper<T> {

	private static final Logger logger = LoggerFactory.getLogger(PrimitiveTypeRowMapper.class);

	private TypeHandler typeHandler;
	private Class<T> resultClass;

	public PrimitiveTypeRowMapper(Class<T> resultClass) {
		this.typeHandler = (new TypeHandlerFactory()).getTypeHandler(resultClass);
		this.resultClass = resultClass;
	}

	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		T result = resultClass.cast(typeHandler.getResult(rs, 1));

		if (logger.isDebugEnabled()) {
			logger.debug(PropertyUtils.primitiveToString(result));
		}

		return result;
	}

}
