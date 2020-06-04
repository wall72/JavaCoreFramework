package jcf.query.core.mapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import jcf.query.core.evaluator.adapter.ResultMappingAdapter;
import jcf.query.util.QueryUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.ibatis.sqlmap.engine.type.TypeHandler;
import com.ibatis.sqlmap.engine.type.TypeHandlerFactory;

/**
 *
 * @author nolang
 *
 * @param <T>
 */
public class IBatisResultMapper<T> implements RowMapper<T> {

	private static final Logger logger = LoggerFactory.getLogger(IBatisResultMapper.class);

	private TypeHandlerFactory factory = new TypeHandlerFactory();
	private ResultMappingAdapter[] resultMappings;
	private Class<T> resultClass;

	private RowMapper<?> mapRowMapper = new CamelCaseMapRowMapper();
	private GenericConversionService conversionService = ConversionServiceFactory.createDefaultConversionService();
	private Map<String, Field> fields = new HashMap<String, Field>();
	private Map<String, String> fieldNames = new HashMap<String, String>();

	public IBatisResultMapper(ResultMappingAdapter[] resultMappings, Class<T> resultClass) {
		this.resultMappings = resultMappings;
		this.resultClass = resultClass;

		ReflectionUtils.doWithFields(this.resultClass, new ReflectionUtils.FieldCallback() {
			public void doWith(Field field) {
				if (fields.containsKey(field.getName())) {
					// ignore superclass declarations of fields already found in a subclass
				} else {
					fields.put(field.getName(), field);
				}
			}
		});
	}

	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		T result = null;

		if(Map.class.isAssignableFrom(resultClass))	{
			result = resultClass.cast(mapRowMapper.mapRow(rs, rowNum));
		} else if(QueryUtils.isPrimitiveType(resultClass))	{
			result = resultClass.cast(factory.getTypeHandler(resultClass).getResult(rs, 1));

			if (logger.isDebugEnabled()) {
				logger.debug(PropertyUtils.toString(result));
			}
		} else {
			result = BeanUtils.instantiate(resultClass);

			ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(result);
			propertyAccessor.setConversionService(conversionService);

			if (resultMappings != null) {
				for (ResultMappingAdapter mapping : resultMappings) {
					propertyAccessor.setPropertyValue(mapping.getPropertyName(), mapping.getTypeHandler().getResult(rs, mapping.getColumnName()));
				}
			} else {
				ResultSetMetaData metaData = rs.getMetaData();

				for (int i = 1; i <= metaData.getColumnCount(); ++i) {
					String fieldName = fieldNames.get(metaData.getColumnName(i)) ;
							
					if(!StringUtils.hasText(fieldName))	{
						fieldNames.put(metaData.getColumnName(i), fieldName = StringUtils.uncapitalize(JdbcUtils.convertUnderscoreNameToPropertyName(metaData.getColumnName(i))));
					}

					Field field = fields.get(fieldName);

					if(field != null){
						TypeHandler typeHandler = factory.getTypeHandler(field.getType(), metaData.getColumnTypeName(i));

						if(typeHandler != null){
							propertyAccessor.setPropertyValue(fieldName, typeHandler.getResult(rs, metaData.getColumnName(i)));
						}
					} else {
						logger.debug("Property mapping failed! - ColumnName={}, Property={}",metaData.getColumnName(i), fieldName);
					}
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug(PropertyUtils.toString(result));
			}
		}

		return result;
	}

}
