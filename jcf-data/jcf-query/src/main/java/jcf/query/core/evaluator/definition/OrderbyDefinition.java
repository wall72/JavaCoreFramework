package jcf.query.core.evaluator.definition;


/**
 * <pre>
 * Order by 절에 표현될 정보를 추상화한다.
 * <pre>
 *
 * @see    jcf.query.core.evaluator.ObjectRelationMappingQueryEvaluator
 * @see    jcf.query.core.evaluator.definition.TableDefinition
 * @see    jcf.query.core.evaluator.definition.ColumnDefinition
 *
 * @author nolang
 *
 */
public interface OrderbyDefinition {
	TableDefinition getTableDefinition();
	ColumnDefinition getColumnDefinition();
	int getSortOrder();
}
