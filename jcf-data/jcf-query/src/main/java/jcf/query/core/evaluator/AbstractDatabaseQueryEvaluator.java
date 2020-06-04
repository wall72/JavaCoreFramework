package jcf.query.core.evaluator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import jcf.query.TemplateEngineType;
import jcf.query.exception.StatementEvaluateException;
import jcf.query.util.QueryUtils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.util.ClassUtils;

/**
 *
 * @author nolang
 *
 */
public abstract class AbstractDatabaseQueryEvaluator implements QueryEvaluator, InitializingBean {

	protected DataSource dataSource;
	protected SimpleJdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void afterPropertiesSet() throws Exception {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}

	public QueryMetaData evaluate(Object statementTemplate, Object param) {
		String statement = getStatement(jdbcTemplate, statementTemplate, param);
		SqlParameterSource parameterSource = null;

		if(param != null && QueryUtils.isPrimitiveType(param.getClass())) {
			List<String> namedParameters = QueryUtils.getNamedParameters(statement);

			Object[] p = null;

			if(ClassUtils.isPrimitiveArray(param.getClass()) || ClassUtils.isPrimitiveWrapperArray(param.getClass()) || String[].class.isAssignableFrom(param.getClass()))	{
				p = (Object[]) param;
			} else {
				p = new Object[] { param };
			}

			if(namedParameters.size() != p.length)	{
				throw new StatementEvaluateException("CUSTOM (DATABASE) 쿼리 생성 실패! - Parameter의 개수와 Bind변수의 개수가 일치하지 않습니다.");
			}

			Map<String, Object> pMap = new HashMap<String, Object>();

			for (int i = 0; i < namedParameters.size(); ++i) {
				pMap.put(namedParameters.get(i), p[i]);
			}

			parameterSource = SqlParameterSourceBuilder.getSqlParameterSource(pMap);
		} else {
			parameterSource = SqlParameterSourceBuilder.getSqlParameterSource(param);
		}

		return new QueryMetaData(TemplateEngineType.CUSTOM, statement, parameterSource, null);
	}

	protected abstract String getStatement(SimpleJdbcTemplate jdbcTemplate, Object statementTemplate, Object param);

}
