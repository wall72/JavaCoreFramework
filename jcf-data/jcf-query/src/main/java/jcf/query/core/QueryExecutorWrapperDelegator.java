package jcf.query.core;

import jcf.query.TemplateEngineType;
import jcf.query.web.CommonVariableHolder;

import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author nolang
 *
 */
public class QueryExecutorWrapperDelegator {

	@Autowired
	private QueryExecutorWrapper queryExecutorWrapper;

	public void setQueryExecutorWrapper(
			QueryExecutorWrapper queryExecutorWrapper) {
		this.queryExecutorWrapper = queryExecutorWrapper;
	}

	public QueryExecutorWrapper getQueryExecutorWrapper() {
		return queryExecutorWrapper;
	}

	public QueryExecutorWrapperDelegator withUserTemplate(TemplateEngineType templateType) {
		CommonVariableHolder.setTemplateType(templateType);
		return this;
	}

	public QueryExecutorWrapperDelegator withCache(String cacheKey) {
		CommonVariableHolder.setCacheKey(cacheKey);
		return this;
	}

	public QueryExecutorWrapperDelegator withQueryHint(String queryHint) {
		CommonVariableHolder.setQueryHint(queryHint);
		return this;
	}

	public QueryExecutorWrapperDelegator withoutQueryEvent()	{
		CommonVariableHolder.setWithoutEvent(true);
		return this;
	}

}
