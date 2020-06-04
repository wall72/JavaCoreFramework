package jcf.query.core.evaluator;

import jcf.query.TemplateEngineType;
import jcf.query.core.evaluator.adapter.ParameterMappingAdapter;
import jcf.query.core.evaluator.adapter.ResultMappingAdapter;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 *
 * @author nolang
 *
 */
public class QueryMetaData {

	private TemplateEngineType templateType;

	private Object statementTemplate;

	private String statement;

	private SqlParameterSource sqlParameterSource;

	private ParameterMappingAdapter[] parameterMappings;

	private Class<?> resultClass;

	private ResultMappingAdapter[] resultMappings;

	private CallMetaData callMetaData;

	private SubQuery subQuery;

	public QueryMetaData(TemplateEngineType templateType, String statement, SqlParameterSource sqlParameterSource, CallMetaData callMetaData) {
		this(templateType, null, statement, sqlParameterSource, null, null, null, null, null);
	}

	public QueryMetaData(TemplateEngineType templateType, Object statementTemplate, String statement, SqlParameterSource sqlParameterSource, ParameterMappingAdapter[] parameterMappings, Class<?> resultClass, ResultMappingAdapter[] resultMappings, CallMetaData callMetaData, SubQuery subQuery) {
		this.templateType = templateType;
		this.statementTemplate = statement;
		this.statement = statement;
		this.sqlParameterSource = sqlParameterSource;
		this.parameterMappings = parameterMappings;
		this.resultClass = resultClass;
		this.resultMappings = resultMappings;
		this.callMetaData = callMetaData;
		this.subQuery = subQuery;
	}

	/**
	 *
	 * @return
	 */
	public TemplateEngineType getTemplateType() {
		return templateType;
	}

	/**
	 *
	 * @return
	 */
	public Object getStatementTemplate() {
		return statementTemplate;
	}

	/**
	 *
	 * @return
	 */
	public String getStatement() {
		return statement;
	}

	/**
	 *
	 * @return
	 */
	public CallMetaData getCallMetaData() {
		return callMetaData;
	}

	/**
	 *
	 * @return
	 */
	public SqlParameterSource getSqlParameterSource() {
		return sqlParameterSource;
	}

	/**
	 *
	 * @return
	 */
	public ParameterMappingAdapter[] getParameterMappings() {
		return parameterMappings;
	}

	/**
	 *
	 * @return
	 */
	public Class<?> getResultClass() {
		return resultClass;
	}

	/**
	 *
	 * @return
	 */
	public ResultMappingAdapter[] getResultMappings() {
		return resultMappings;
	}

	/**
	 *
	 * @return
	 */
	public SubQuery getSubQuery() {
		return subQuery;
	}
}
