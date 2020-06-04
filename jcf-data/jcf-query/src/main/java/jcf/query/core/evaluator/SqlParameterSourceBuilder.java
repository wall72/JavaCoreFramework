package jcf.query.core.evaluator;

import java.lang.reflect.Field;
import java.util.Map;

import jcf.query.core.annotation.DomainNamespace;
import jcf.query.util.QueryUtils;
import jcf.query.web.CommonVariableHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.util.StringUtils;

/**
 *
 * @author nolang
 *
 */
public class SqlParameterSourceBuilder {

	private static final Logger logger = LoggerFactory.getLogger(SqlParameterSourceBuilder.class);

	@SuppressWarnings("unchecked")
	public static SqlParameterSource getSqlParameterSource(Object parameter)	{
		SqlParameterSource source = new MapSqlParameterSource();

		if (parameter != null) {
			if(SqlParameterSource.class.isAssignableFrom(parameter.getClass()))	{
				source = (SqlParameterSource) parameter;
			} else {
				if(QueryUtils.isAssignableFromMap(parameter.getClass()))	{
					source = new MapSqlParameterSource((Map<String, ?>) parameter);
				} else {
					String namespace = "";

					if (parameter.getClass().isAnnotationPresent(DomainNamespace.class)) {
						namespace = parameter.getClass().getAnnotation(DomainNamespace.class).value();
					}

					if(StringUtils.hasText(namespace)){
						((MapSqlParameterSource) source).addValue(namespace, parameter);
					} else {
						 Class targetClass = parameter.getClass();
						 do {
				  	   	   Field[] fields =targetClass. getDeclaredFields();
			 					for(Field field : fields){
									try {
										field.setAccessible(true);
										logger.debug("[SqlParameterSource] Property Name={} Property Value={}", field.getName(), field.get(parameter));
										((MapSqlParameterSource) source).addValue(field.getName(), field.get(parameter));
									} catch (Exception e) {
									}
								}
			 					targetClass=targetClass.getSuperclass();
						 }
							while(!(targetClass== Object.class) &&  !(targetClass==null));{
								Field[] fields = parameter.getClass().getDeclaredFields();
								for(Field field : fields){
									try {
										field.setAccessible(true);
										logger.debug("[SqlParameterSource] Property Name={} Property Value={}", field.getName(), field.get(parameter));
										((MapSqlParameterSource) source).addValue(field.getName(), field.get(parameter));
									} catch (Exception e) {
									}
								}
							}
							}

					}
				}
			}


		/**
		 * TODO 글로벌변수(세션등)와 관련한 처리, Parameter가  Map일 경우만 가능해보임.. ㅜㅡ -> Bean일 경우도 MapSqlParameterSource를 사용하도록 변경
		 */
		Map<String, Object> commonVariables = CommonVariableHolder.getVariables();

		if(!commonVariables.isEmpty())	{
			for(Map.Entry<String, Object> e: commonVariables.entrySet())	{
				((MapSqlParameterSource) source).addValue(e.getKey(), e.getValue());
			}
		}

		return source;
	}


	/**
	 *
	 * @param source
	 * @param keyProperty
	 * @param parameter
	 */
	public static void addSqlParameter(SqlParameterSource source,
			String keyProperty, Object parameter) {
		if (parameter != null) {
			((MapSqlParameterSource) source).addValue(keyProperty, parameter);
		}
	}

}
