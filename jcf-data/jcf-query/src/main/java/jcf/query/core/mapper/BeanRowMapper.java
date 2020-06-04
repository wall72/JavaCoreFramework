package jcf.query.core.mapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.ReflectionUtils;

public class BeanRowMapper<T> implements RowMapper<T>	{

	private static final Logger logger = LoggerFactory.getLogger(BeanRowMapper.class);

	private Class<T> clazz;

	public BeanRowMapper(Class<T> clazz) {
		this.clazz = clazz;
	}

	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();

		T result = BeanUtils.instantiate(clazz);

		for (int i = 1; i <= metaData.getColumnCount(); ++i) {
			Field field = ReflectionUtils.findField(clazz, JdbcUtils.convertUnderscoreNameToPropertyName(metaData.getColumnName(i)));

			if(field == null)	{
				logger.warn("Column[{}]와 매핑되는 Property[{}]가 존재하지 않습니다.", metaData.getColumnName(i), JdbcUtils.convertUnderscoreNameToPropertyName(metaData.getColumnName(i)));
			} else {
				ReflectionUtils.makeAccessible(field);
				ReflectionUtils.setField(field, result, JdbcUtils.getResultSetValue(rs, i));
			}
		}

		if(logger.isDebugEnabled())	{
			logger.debug(PropertyUtils.beanToString(result));
		}

		return result;
	}
}
