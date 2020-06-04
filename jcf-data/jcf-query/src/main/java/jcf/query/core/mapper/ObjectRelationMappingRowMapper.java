package jcf.query.core.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author nolang
 *
 * @param <T>
 */
public class ObjectRelationMappingRowMapper<T> implements RowMapper<T> {

	private static final Logger logger = LoggerFactory.getLogger(ObjectRelationMappingRowMapper.class);

	private ObjectRelationMapper mapper = new ObjectRelationMapper();
	private Class<T> clazz;

	public ObjectRelationMappingRowMapper(Class<T> clazz) {
		this.clazz = clazz;
	}

	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		T result =  mapper.mapper(rs, clazz);

		if(logger.isDebugEnabled())	{
			logger.debug(PropertyUtils.beanToString(result));
		}

		return result;
	}
}
