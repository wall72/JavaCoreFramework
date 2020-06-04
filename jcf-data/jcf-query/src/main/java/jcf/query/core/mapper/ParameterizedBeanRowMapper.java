package jcf.query.core.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

public class ParameterizedBeanRowMapper<T> extends ParameterizedBeanPropertyRowMapper<T>	{

	private static final Logger logger = LoggerFactory.getLogger(ParameterizedBeanRowMapper.class);

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		T result = super.mapRow(rs, rowNum);

		if(logger.isDebugEnabled())	{
			logger.debug(PropertyUtils.beanToString(result));
		}

		return result;
	}

	public static <T> ParameterizedBeanRowMapper<T> newInstance(Class<T> mappedClass) {
		ParameterizedBeanRowMapper<T> newInstance = new ParameterizedBeanRowMapper<T>();
		newInstance.setMappedClass(mappedClass);
		return newInstance;
	}
}
