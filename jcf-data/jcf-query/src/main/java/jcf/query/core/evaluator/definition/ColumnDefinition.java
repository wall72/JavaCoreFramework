package jcf.query.core.evaluator.definition;


/**
 * <pre>
 * 컬럼 정보를 추상화함
 * <pre>
 *
 * @see    jcf.query.core.evaluator.ObjectRelationMappingQueryEvaluator
 * @see    jcf.query.core.evaluator.definition.TableDefinition
 *
 * @author nolang
 *
 */
public interface ColumnDefinition {
	TableDefinition getTableDefinition();
	String getColumnName();
	String getColumnAlias();
	Object getColumnValue();
	ColumnType getColumnType();
	String getPrefix();
	String getSuffix();
}
