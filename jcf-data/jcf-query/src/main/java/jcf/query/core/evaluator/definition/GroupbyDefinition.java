package jcf.query.core.evaluator.definition;

/**
 * <pre>
 * Group by 절에 표현될 정보를 추상화한다.
 * <pre>
 *
 * @see    jcf.query.core.evaluator.definition.TableDefinition
 * @see    jcf.query.core.evaluator.definition.ColumnDefinition
 *
 * @author nolang
 *
 */
public interface GroupbyDefinition {
	TableDefinition getTableDefinition();
	ColumnDefinition getColumnDefinition();
	int getSortOrder();
}
