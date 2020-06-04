package jcf.query.core.evaluator;

import jcf.query.TemplateEngineType;

/**
 *
 * @author nolang
 *
 */
public class DefaultQueryEvaluator implements QueryEvaluator {

	public QueryMetaData evaluate(Object statementTemplate, Object param) {
		return new QueryMetaData(TemplateEngineType.DEFAULT, (String) statementTemplate, SqlParameterSourceBuilder.getSqlParameterSource(param), null);
	}

}
