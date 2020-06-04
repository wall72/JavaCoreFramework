package jcf.query.core.decorator;

import jcf.query.TemplateEngineType;
import jcf.query.web.CommonVariableHolder;

/**
 *
 * @author nolang
 *
 */
public class QueryEvaluatorChanger implements QueryDecorator {

	private TemplateEngineType templateEngineType;

	public void beforeExecution(Object... args) {
		CommonVariableHolder.setTemplateType(templateEngineType);
	}

	public void afterExecution(Object... args) {
		CommonVariableHolder.clear();
	}

	public void setTemplateEngineType(TemplateEngineType templateEngineType) {
		this.templateEngineType = templateEngineType;
	}
}
