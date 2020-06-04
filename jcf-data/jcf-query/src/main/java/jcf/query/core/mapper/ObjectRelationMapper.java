package jcf.query.core.mapper;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import jcf.query.core.annotation.orm.Unused;
import jcf.query.core.evaluator.definition.QueryStatement;
import jcf.query.exception.MappingRowPopulationException;
import jcf.query.util.QueryUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * <pre>
 * 조회된 결과를 전달받은 클래스 객체에 담아서 반환한다.
 * <pre>
 *
 * @see jcf.query.core.evaluator.ObjectRelationMappingQueryEvaluator
 *
 * @author nolang
 *
 */
public class ObjectRelationMapper {

	private static final Logger logger = LoggerFactory.getLogger(ObjectRelationMapper.class);

	public <T> T mapper(ResultSet rs, Class<T> clazz) throws SQLException {
		T resultObject = BeanUtils.instantiate(clazz);

		try {
			populate(rs, resultObject);
		} catch (Exception e) {
			new MappingRowPopulationException("mapper() - 데이터 매핑 실패", e);
		}

		return resultObject;
	}

	private void populate(ResultSet rs, Object resultObject) throws Exception {
		Field[] fields = resultObject.getClass().getDeclaredFields();

		for (Field field : fields) {
			if (field.isAnnotationPresent(Unused.class)) {
				if (logger.isDebugEnabled()) {
					logger.debug("populate() - Unused Field: columnName={}", field.getName());
				}

				continue;
			}

			if(!QueryUtils.isPrimitiveType(field.getType()) && !Date.class.isAssignableFrom(field.getType()) && !BigDecimal.class.isAssignableFrom(field.getType())) {
				if(QueryStatement.class.isAssignableFrom(field.getType()))	{
					Object object = BeanUtils.instantiate(field.getType());
					populate(rs, object);
					setFieldValue(field, resultObject, object);
				}

				continue;
			}

			String columnName = field.getName();

			try{
				if (logger.isTraceEnabled()) {
					logger.debug("populate() - MappingInfomation: Class={} columnName={} value={}", new Object[]{resultObject.getClass().getName(), columnName, rs.getObject(columnName)});
				}

				setFieldValue(field, resultObject, rs.getObject(columnName));

			} catch (Exception e) {
				logger.debug("populate() - Exception: Class={} field={} 가 존재하지 않습니다.", new Object[]{resultObject.getClass().getName(), columnName});
			}
		}
	}

	private void setFieldValue(Field field, Object object, Object value) throws Exception {
		field.setAccessible(true);
		field.set(object, value);
		field.setAccessible(false);
	}

	protected String getColumnKey(String columnName) {
		return JdbcUtils.convertUnderscoreNameToPropertyName(columnName);
	}
}
