package jcf.query.core.evaluator.definition;

/**
 * <pre>
 * 조건절의 타입을 결정한다.
 *  - VALUE : Column = 'Constant'
 *  - REFERENCE : 조인대상 컬럼
 * <pre>
 *
 * @see    jcf.query.core.evaluator.definition.ConditionDefinition
 *
 * @author nolang
 *
 */
public enum ConditionType {
	VALUE, REFERENCE
}
