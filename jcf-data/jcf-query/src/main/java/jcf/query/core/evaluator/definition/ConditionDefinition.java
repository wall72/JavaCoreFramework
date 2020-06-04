package jcf.query.core.evaluator.definition;


/**
 * <pre>
 * 조건절에 사용될 정보를 추상화한다.
 * <pre>
 *
 * @see    jcf.query.core.evaluator.ObjectRelationMappingQueryEvaluator
 * @see    jcf.query.core.evaluator.definition.ConditionType
 * @see    jcf.query.core.evaluator.definition.TableDefinition
 * @see    jcf.query.core.evaluator.definition.ColumnDefinition
 *
 * @author nolang
 *
 */
public interface ConditionDefinition {
	ConditionType getConditionType();
	TableDefinition getTableDefinition();
	ColumnDefinition getLeftSide();
	ColumnDefinition getRightSide();
	String getConditionValue();
	String getPrefix();
	String getSuffix();
}
