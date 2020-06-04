package jcf.query.core;

import jcf.query.TemplateEngineType;
import jcf.query.web.CommonVariableHolder;

import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author nolang
 *
 */
public class QueryExecutorDelegator {

	@Autowired
	private QueryExecutor queryExecutor;

	public void setQueryExecutor(QueryExecutor queryExecutor) {
		this.queryExecutor = queryExecutor;
	}

	public QueryExecutor getQueryExecutor() {
		return queryExecutor;
	}

	public QueryExecutorDelegator withUserTemplate(TemplateEngineType templateType) {
		CommonVariableHolder.setTemplateType(templateType);
		return this;
	}

	public QueryExecutorDelegator withCache(String cacheKey) {
		CommonVariableHolder.setCacheKey(cacheKey);
		return this;
	}

	public QueryExecutorDelegator withQueryHint(String queryHint) {
		CommonVariableHolder.setQueryHint(queryHint);
		return this;
	}

	public QueryExecutorDelegator withoutQueryEvent()	{
		CommonVariableHolder.setWithoutEvent(true);
		return this;
	}

}
