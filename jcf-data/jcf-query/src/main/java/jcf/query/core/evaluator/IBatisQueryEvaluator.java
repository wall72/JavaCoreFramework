package jcf.query.core.evaluator;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import jcf.query.TemplateEngineType;
import jcf.query.core.evaluator.adapter.ParameterMappingAdapter;
import jcf.query.core.evaluator.adapter.ResultMappingAdapter;
import jcf.query.exception.StatementEvaluateException;
import jcf.query.web.CommonVariableHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMap;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMapping;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.result.ResultMapping;
import com.ibatis.sqlmap.engine.mapping.sql.Sql;
import com.ibatis.sqlmap.engine.mapping.statement.InsertStatement;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.mapping.statement.ProcedureStatement;
import com.ibatis.sqlmap.engine.mapping.statement.SelectKeyStatement;
import com.ibatis.sqlmap.engine.scope.SessionScope;
import com.ibatis.sqlmap.engine.scope.StatementScope;

/**
 *
 * @author nolang
 *
 */
public class IBatisQueryEvaluator implements QueryEvaluator {

	private static final Logger logger = LoggerFactory.getLogger(IBatisQueryEvaluator.class);

	private SqlMapClient sqlMapClient;

	private boolean isRunningProxyMode = false;

	private Method proxyMethod;

	public void setSqlMapClient(SqlMapClient sqlMapClient) {
		this.sqlMapClient = sqlMapClient;

		if(Proxy.isProxyClass(sqlMapClient.getClass()))	{
			isRunningProxyMode = true;

			try {
				proxyMethod = SqlMapClientImpl.class.getMethod("getMappedStatement", String.class);
			} catch (Exception e) {
				throw new StatementEvaluateException("iBatis 초기화 작업 실패!", e);
			}
		}
	}

	public QueryMetaData evaluate(Object statementTemplate, Object param) {
		MappedStatement mappedStatement = null;

		try	{
			if(isRunningProxyMode)	{
				InvocationHandler h = Proxy.getInvocationHandler(sqlMapClient);

				try {
					mappedStatement = (MappedStatement) h.invoke(sqlMapClient, proxyMethod, new Object[]{(String) statementTemplate});
				} catch (Throwable e) {
					throw new Exception(e);
				}
			} else {
				mappedStatement = ((SqlMapClientImpl) sqlMapClient).getMappedStatement((String) statementTemplate);
			}
		}catch(Exception e){
			if(logger.isDebugEnabled()){
				logger.error("[iBATIS] 쿼리 생성 실패! - {}", e.getMessage());
			}

			throw new StatementEvaluateException("iBatis 쿼리 생성 실패!", e);
		}

		return evaluateInternal(mappedStatement, param);
	}

	protected QueryMetaData evaluateInternal(MappedStatement mappedStatement, Object param) {
		SessionScope sessionScope = beginSessionScope();
		StatementScope statementScope = beginStatementScope(sessionScope, mappedStatement);

		setCommmonVariableParam(param);

		/**
		 * TODO 요놈 캐시대상..
		 */
		Sql mappedSql = mappedStatement.getSql();

		String queryTemplate = mappedSql.getSql(statementScope, param).replaceAll("\\t", " ");

		ParameterMap parameterMap = mappedSql.getParameterMap(statementScope, param);
		ParameterMapping[] parameterMappings = parameterMap.getParameterMappings();
		Object[] parameters = parameterMap.getDataExchange().getData(statementScope, parameterMap, param);

		/*
		 * build named statement
		 */
		String statement = buildSqlStatement(queryTemplate,	parameterMappings);

		endStatementScope(statementScope);
		endSessionScope(sessionScope);

		SqlParameterSource parameterSource = SqlParameterSourceBuilder.getSqlParameterSource(getSqlParameterMap(parameterMappings, parameters));
		Class<?> resultMapClass = null;
		ResultMapping[] resultMappings = null;

		if(mappedSql.getResultMap(statementScope, param) != null){
			ResultMap resultMap = mappedSql.getResultMap(statementScope, param);

			resultMapClass = resultMap.getResultClass();
			resultMappings = resultMap.getResultMappings();
		}

		/*
		 * creates sp/function metadata
		 */
		CallMetaData callMetaData = null;

		if(mappedStatement instanceof ProcedureStatement)	{
			callMetaData = CallMetaDataBuilder.buildCallMataData(queryTemplate.trim());
		}

		SubQuery subQuery = null;

		if (mappedStatement instanceof InsertStatement) {
			SelectKeyStatement selectKeyStatement = ((InsertStatement) mappedStatement).getSelectKeyStatement();

			if (selectKeyStatement != null) {
				subQuery = new SubQuery(evaluateInternal(selectKeyStatement, param), selectKeyStatement.getKeyProperty(), selectKeyStatement.isRunAfterSQL());
			}
		}

		return new QueryMetaData(TemplateEngineType.IBATIS, queryTemplate, statement, parameterSource, convertParameterMapping(parameterMappings), resultMapClass, convertResultMapping(resultMappings), callMetaData, subQuery);
	}

	protected String buildSqlStatement(String queryTemplate, ParameterMapping[] parameterMappings) {
		StringBuilder statement = new StringBuilder();

		int fromIndex = 0;
		int toIndex = 0;

		for(ParameterMapping mapping : parameterMappings)	{
			if((toIndex = queryTemplate.indexOf("?", fromIndex)) >= fromIndex)	{
				statement.append(queryTemplate.substring(fromIndex, toIndex));
				statement.append(":");
				statement.append(mapping.getPropertyName());

				fromIndex = toIndex + 1;
			} else {
				break;
			}
		}

		if(fromIndex < queryTemplate.length()){
			statement.append(queryTemplate.substring(fromIndex));
		}
		return statement.toString();
	}

	protected ParameterMappingAdapter[] convertParameterMapping(ParameterMapping[] mapping) {
		ParameterMappingAdapter[] mappingAdapter = null;

		if (mapping != null) {
			mappingAdapter = new ParameterMappingAdapter[mapping.length];

			for (int i = 0; i < mapping.length; ++i) {
				mappingAdapter[i] = new ParameterMappingAdapter(mapping[i]);
			}
		}

		return mappingAdapter;
	}

	protected ResultMappingAdapter[] convertResultMapping(ResultMapping[] mapping) {
		ResultMappingAdapter[] mappingAdapter = null;

		if (mapping != null) {
			mappingAdapter = new ResultMappingAdapter[mapping.length];

			for (int i = 0; i < mapping.length; ++i) {
				mappingAdapter[i] = new ResultMappingAdapter(mapping[i]);
			}
		}

		return mappingAdapter;
	}

	protected Map<String, Object> getSqlParameterMap(ParameterMapping[] names, Object[] values){
		Map<String, Object> parameterMap = new HashMap<String, Object>();

		if (values != null && names.length > values.length) {
			throw new StatementEvaluateException("iBatis 쿼리 생성 실패! - Parameter의 개수와 Bind변수의 개수가 일치하지 않습니다.");
		}

		for (int i = 0; i < names.length; ++i) {
			parameterMap.put(names[i].getPropertyName(), values[i]);
		}

		return parameterMap;
	}

	protected StatementScope beginStatementScope(SessionScope sessionScope, MappedStatement mappedStatement) {
		StatementScope statementScope = new StatementScope(sessionScope);
		sessionScope.incrementRequestStackDepth();
		mappedStatement.initRequest(statementScope);
		return statementScope;
	}

	protected void endStatementScope(StatementScope statementScope) {
		statementScope.getSession().decrementRequestStackDepth();
	}

	protected SessionScope beginSessionScope() {
		return new SessionScope();
	}

	protected void endSessionScope(SessionScope sessionScope) {
		sessionScope.cleanup();
	}

	@SuppressWarnings("unchecked")
	protected Object setCommmonVariableParam(Object param) {
		if (param != null && Map.class.isAssignableFrom(param.getClass())) {
			Map<String, Object> commonVariables = CommonVariableHolder.getVariables();

			if (!commonVariables.isEmpty()) {
				for (Map.Entry<String, Object> e : commonVariables.entrySet()) {
					String key = e.getKey();

					if (!((Map<String, Object>) param).containsKey(key)) {
						((Map<String, Object>) param).put(key, e.getValue());
					}
				}
			}
		}

		return param;
	}
}