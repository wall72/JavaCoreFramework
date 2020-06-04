package jcf.query.core.evaluator;


/**
 *
 * @author nolang
 *
 */
public interface QueryEvaluator	{

	QueryMetaData evaluate(Object statementTemplate, Object param);

}
