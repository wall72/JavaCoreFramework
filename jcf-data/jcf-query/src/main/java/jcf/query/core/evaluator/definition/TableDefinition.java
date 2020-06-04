package jcf.query.core.evaluator.definition;


/**
 * <pre>
 * 테이블 정보를 추상화한다.
 * <pre>
 *
 * @see    jcf.query.core.evaluator.ObjectRelationMappingQueryEvaluator
 *
 * @author nolang
 *
 */
public interface TableDefinition {
	String getTableName();
	String getTableAlias();
}
