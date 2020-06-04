package jcf.query.core;

import java.util.Map;

import jcf.query.TemplateEngineType;
import jcf.query.core.evaluator.QueryEvaluator;
import jcf.query.core.evaluator.QueryMetaData;
import jcf.query.web.CommonVariableHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nolang
 *
 */
public class QueryTemplate {
	private static final Logger logger = LoggerFactory.getLogger(QueryTemplate.class);

//	private static final String expr = ":[_A-Za-z]+[A-Za-z0-9_.-]*";

	private Map<TemplateEngineType, QueryEvaluator> queryEvaluators;
	private TemplateEngineType defaultTemplate = TemplateEngineType.DEFAULT;

	/**
	 *
	 * Template Engine 을 이용하여 쿼리를 생성한다.
	 *
	 * @param statementTemplate
	 * @param param
	 * @return
	 */
	public QueryMetaData getQuery(Object statementTemplate, Object param)	{
		QueryMetaData queryMetadata = getQueryEvaluator().evaluate(statementTemplate, param);

		logger.trace("[Generated Query] {}", queryMetadata.getStatement());

		return queryMetadata;
	}

	private QueryEvaluator getQueryEvaluator()	{
		TemplateEngineType type = CommonVariableHolder.getTemplateType();

		if(type == null){
			type = defaultTemplate;
		}

		return queryEvaluators.get(type);
	}

	public TemplateEngineType getTemplateEngineType()	{
		TemplateEngineType type = CommonVariableHolder.getTemplateType();

		if(type == null){
			type = defaultTemplate;
		}

		return type;
	}

	public void setQueryEvaluators(Map<TemplateEngineType, QueryEvaluator> queryEvaluators) {
		this.queryEvaluators = queryEvaluators;
	}

	public void setDefaultTemplate(TemplateEngineType defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}
}
