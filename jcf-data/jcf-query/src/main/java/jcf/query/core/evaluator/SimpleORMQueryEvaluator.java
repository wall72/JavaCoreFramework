package jcf.query.core.evaluator;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jcf.query.TemplateEngineType;
import jcf.query.core.annotation.orm.ColumnDef;
import jcf.query.core.annotation.orm.GroupBy;
import jcf.query.core.annotation.orm.OrderBy;
import jcf.query.core.annotation.orm.PrimaryKey;
import jcf.query.core.annotation.orm.ReferenceKey;
import jcf.query.core.annotation.orm.TableDef;
import jcf.query.core.annotation.orm.Unused;
import jcf.query.core.annotation.orm.Updatable;
import jcf.query.core.evaluator.definition.ColumnDefinition;
import jcf.query.core.evaluator.definition.ColumnType;
import jcf.query.core.evaluator.definition.ConditionDefinition;
import jcf.query.core.evaluator.definition.ConditionType;
import jcf.query.core.evaluator.definition.GroupbyDefinition;
import jcf.query.core.evaluator.definition.KeyType;
import jcf.query.core.evaluator.definition.OrderbyDefinition;
import jcf.query.core.evaluator.definition.QueryStatement;
import jcf.query.core.evaluator.definition.SelectStatement;
import jcf.query.core.evaluator.definition.TableDefinition;
import jcf.query.exception.StatementEvaluateException;
import jcf.query.util.QueryUtils;
import jcf.query.web.CommonVariableHolder;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.util.StringUtils;

/**
 *
 * @author nolang
 *
 */
public class SimpleORMQueryEvaluator implements QueryEvaluator {

	public QueryMetaData evaluate(Object statementTemplate, Object param) {

		if(param == null || QueryUtils.isPrimitiveType(param.getClass()) || QueryUtils.isAssignableFromMap(param.getClass())) {
			throw new StatementEvaluateException("SORM Evaluator는 JavaBean Type의 Parameter 만을 허용합니다.");
		}

		SqlParameterSource parameterSource = SqlParameterSourceBuilder.getSqlParameterSource(param);

		if (SimpleORMQueryType.SELECT == statementTemplate && param instanceof SelectStatement) {
			return new QueryMetaData(TemplateEngineType.SIMPLE_ORM, ((SelectStatement) param).getQuery(), parameterSource, null);
		}

		return new QueryMetaData(TemplateEngineType.SIMPLE_ORM, build((QueryStatement) param, (SimpleORMQueryType) statementTemplate), parameterSource, null);
	}

	protected String build(QueryStatement object, SimpleORMQueryType queryType) {
		List<TableDefinition> tableDefList = new ArrayList<TableDefinition>();
		List<ColumnDefinition> columnDefList = new ArrayList<ColumnDefinition>();
		List<ColumnDefinition> updatableColumnList = new ArrayList<ColumnDefinition>();
		List<ConditionDefinition> conditionDefList = new ArrayList<ConditionDefinition>();
		Map<Integer, GroupbyDefinition> groupbyMap = new TreeMap<Integer, GroupbyDefinition>();
		Map<Integer, OrderbyDefinition> orderbyMap = new TreeMap<Integer, OrderbyDefinition>();

		try {
			prepare(object, queryType, tableDefList, columnDefList, conditionDefList, groupbyMap, orderbyMap, updatableColumnList);
		} catch (Exception e) {
			throw new StatementEvaluateException("[SimpleORMQueryEvaluator] build() - Exception: Query 생성 실패", e);
		}

		String query = "";

		if (SimpleORMQueryType.SELECT == queryType) {
			query = buildSelectQuery(tableDefList, columnDefList, conditionDefList, orderbyMap);
		} else if (SimpleORMQueryType.INSERT == queryType) {
			query = buildInsertQuery(tableDefList, columnDefList);
		} else if (SimpleORMQueryType.DELETE == queryType) {
			query = buildDeleteQuery(tableDefList, conditionDefList);
		} else if (SimpleORMQueryType.UPDATE == queryType) {
			query = buildUpdateQuery(object, tableDefList, updatableColumnList, conditionDefList);
		}

		return query;
	}

	/**
	 * 클래스를 분석하여 SQL을 구성하는 각각의 요소를 추출한다.
	 *
	 * @param object
	 * @param tableDefList
	 * @param columnDefList
	 * @param conditionDefList
	 * @throws Exception
	 */
	protected void prepare(QueryStatement object, SimpleORMQueryType queryType, List<TableDefinition> tableDefList, List<ColumnDefinition> columnDefList,
						List<ConditionDefinition> conditionDefList, Map<Integer, GroupbyDefinition> groupbyMap,
						Map<Integer, OrderbyDefinition> orderbyMap, List<ColumnDefinition> updatableColumnList) throws Exception {
		TableDefinition tableDefinition = getTableDefinition(object);

		tableDefList.add(tableDefinition);

		Field[] columns = object.getClass().getDeclaredFields();

		for (Field column : columns) {

			Class<?> columnType = column.getType();

			if (!QueryUtils.isPrimitiveType(columnType) && !Date.class.isAssignableFrom(columnType)  && !BigDecimal.class.isAssignableFrom(columnType)) {
				if(QueryStatement.class.isAssignableFrom(columnType))	{
					Object columnValue = getFieldValue(column, object);

					if(columnValue == null)	{
						columnValue = columnType.newInstance();
					}

					prepare((QueryStatement) columnValue, queryType, tableDefList, columnDefList, conditionDefList, groupbyMap, orderbyMap, updatableColumnList);
				}

				continue;
			}

			ColumnDefinition columnDefinition = getColumnDefinition(object, tableDefinition, column);

			if (queryType == SimpleORMQueryType.SELECT) {
				if (column.isAnnotationPresent(ReferenceKey.class)) {
					ConditionDefinition reference = getReferenceCondition(object, columnDefinition, column);

					if(reference != null){
						conditionDefList.add(reference);
					}
				}

				if (column.isAnnotationPresent(GroupBy.class)) {
					GroupbyDefinition groupbyDef = getGroupbyDefinition(tableDefinition, columnDefinition, column);

					groupbyMap.put(groupbyDef.getSortOrder(), groupbyDef);
				}

				if (column.isAnnotationPresent(OrderBy.class)) {
					OrderbyDefinition orderbyDef = getOrderbyDefinition(tableDefinition, columnDefinition, column);

					orderbyMap.put(orderbyDef.getSortOrder(), orderbyDef);
				}
			}

			if (queryType != SimpleORMQueryType.INSERT) {
				if (column.isAnnotationPresent(PrimaryKey.class)) {
					ConditionDefinition condition = getValueCondition(object, tableDefinition, column);

					if(condition != null){
						conditionDefList.add(condition);
					}
				}
			}

			if (queryType == SimpleORMQueryType.UPDATE) {
				if (column.isAnnotationPresent(Updatable.class)) {
					updatableColumnList.add(columnDefinition);
				}
			}

			if (queryType != SimpleORMQueryType.DELETE && !column.isAnnotationPresent(Unused.class)) {
				columnDefList.add(columnDefinition);
			}
		}
	}

	protected TableDefinition getTableDefinition(final Object table) {

		final TableDef tableDef = table.getClass().getAnnotation(TableDef.class);

		return new TableDefinition() {
			public String getTableName() {
				return tableDef != null ? tableDef.tableName() : table.getClass().getSimpleName();
			}

			public String getTableAlias() {
				return tableDef != null ? tableDef.alias() : "";
			}
		};
	}

	protected ColumnDefinition getColumnDefinition(final Object object, final TableDefinition tableDef, final Field column) {

		final ColumnDef columnDef = column.getAnnotation(ColumnDef.class);

		return new ColumnDefinition() {

			public TableDefinition getTableDefinition() {
				return tableDef;
			}

			public String getColumnAlias() {
				String alias = column.getName();

				if(columnDef != null && StringUtils.hasText(columnDef.alias()))	{
					alias = columnDef.alias();
				}

				return alias;
			}

			public ColumnType getColumnType() {
				return columnDef.columnType();
			}

			public String getColumnName() {
				return columnDef != null ? columnDef.columnName() : column.getName();
			}

			public Object getColumnValue() {
				return getFieldValue(column, object);
			}

			public String getPrefix() {
				return columnDef.prefix();
			}

			public String getSuffix() {
				return columnDef.suffix();
			}
		};
	}

	protected ConditionDefinition getReferenceCondition(final Object object, final ColumnDefinition leftSide, final Field column)	{

		ReferenceKey referenceKey = column.getAnnotation(ReferenceKey.class);

		final Object targetObject = getFieldValue(getField(object.getClass(), referenceKey.targetObject()), object);

		if(targetObject == null)	{
			return null;
		}

		final TableDefinition tableDef = getTableDefinition(targetObject);
		final Field targetField = getField(targetObject.getClass(), referenceKey.targetField());

		return new ConditionDefinition() {

			public ConditionType getConditionType() {
				return ConditionType.REFERENCE;
			}

			public TableDefinition getTableDefinition() {
				return tableDef;
			}

			public ColumnDefinition getLeftSide() {
				return leftSide;
			}

			public ColumnDefinition getRightSide() {
				return getColumnDefinition(targetObject, tableDef, targetField);
			}

			public String getConditionValue() {
				throw new UnsupportedOperationException();
			}

			public String getPrefix() {
				return "";
			}

			public String getSuffix() {
				return "";
			}
		};
	}

	protected ConditionDefinition getValueCondition(final Object object, final TableDefinition tableDef, final Field column)	{
		final PrimaryKey primaryKey = column.getAnnotation(PrimaryKey.class);

		if (primaryKey.keyType() == KeyType.DYNAMIC) {
			Object columnValue = getFieldValue(column, object);

			if(columnValue == null){
				return null;
			}

			if (columnValue instanceof String) {
				if(!StringUtils.hasLength((String) columnValue)) {
					return null;
				}
			}
		}

		return new ConditionDefinition() {

			public ConditionType getConditionType() {
				return ConditionType.VALUE;
			}

			public TableDefinition getTableDefinition() {
				return tableDef;
			}

			public ColumnDefinition getRightSide() {
				throw new UnsupportedOperationException();
			}

			public ColumnDefinition getLeftSide() {
				return getColumnDefinition(object, tableDef, column);
			}

			public String getConditionValue() {
				String defaultValue = primaryKey.defaultValue();

				/*
				 * SessionAttribute key 가 지정된 경우 세션에서 값을 가져와서 세팅한다.
				 */
				if(StringUtils.hasText(primaryKey.sessionAttribute()))	{
					defaultValue = (String) CommonVariableHolder.get(primaryKey.sessionAttribute());
				}

				return defaultValue;
			}

			public String getPrefix() {
				return primaryKey.prefix();
			}

			public String getSuffix() {
				return primaryKey.suffix();
			}
		};
	}

	protected OrderbyDefinition getOrderbyDefinition(final TableDefinition tableDef, final ColumnDefinition columnDef, Field column) {

		final OrderBy orderBy = column.getAnnotation(OrderBy.class);

		return new OrderbyDefinition() {

			public TableDefinition getTableDefinition() {
				return tableDef;
			}

			public int getSortOrder() {
				return orderBy.sortOrder();
			}

			public ColumnDefinition getColumnDefinition() {
				return columnDef;
			}
		};
	}

	protected GroupbyDefinition getGroupbyDefinition(final TableDefinition tableDef, final ColumnDefinition columnDef, Field column) {

		final GroupBy orderBy = column.getAnnotation(GroupBy.class);

		return new GroupbyDefinition() {

			public TableDefinition getTableDefinition() {
				return tableDef;
			}

			public int getSortOrder() {
				return orderBy.sortOrder();
			}

			public ColumnDefinition getColumnDefinition() {
				return columnDef;
			}
		};
	}

	protected final Object getFieldValue(Field field, Object target)	{
		field.setAccessible(true);

		Object columnValue = null;

		try {
			 columnValue = field.get(target);

			 if(columnValue == null)	{
				 columnValue = field.getType().newInstance();
			 }
		}  catch (Exception e) {
		}

		field.setAccessible(false);

		return columnValue;
	}

	protected final Field getField(Class<?> clazz, String fieldName) {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
			throw new StatementEvaluateException(String.format("[SimpleORMQueryEvaluator] getField() - Class={%s} fieldName={%s}의 값을 조회할 수 없습니다.", clazz.getName(), fieldName), e);
		}
	}

	protected String buildSelectQuery(List<TableDefinition> tableDefList, List<ColumnDefinition> columnDefList, List<ConditionDefinition> conditionDefList, Map<Integer, OrderbyDefinition> orderbyMap){
		StringBuilder query = new StringBuilder();

		query.append("SELECT ");

		for (int i = 0; i < columnDefList.size(); ++i) {
			if(i > 0) {
				query.append(",");
			}

			ColumnDefinition columnDef = columnDefList.get(i);

			if(StringUtils.hasText(columnDef.getPrefix())){
				query.append(String.format("%s ", columnDef.getPrefix()));
			}

			if (StringUtils.hasText(columnDef.getTableDefinition().getTableAlias())) {
				query.append(String.format("%s.", columnDef.getTableDefinition().getTableAlias()));
			}

			query.append(String.format("%s ", columnDef.getColumnName()));

			if(StringUtils.hasText(columnDef.getSuffix())){
				query.append(String.format("%s ", columnDef.getSuffix()));
			}

			if (StringUtils.hasText(columnDef.getColumnAlias())) {
				query.append(String.format("%s ",  columnDef.getColumnAlias()));
			}
		}

		query.append("FROM ");

		for (int i = 0; i < tableDefList.size(); ++i) {
			if(i > 0) {
				query.append(",");
			}

			TableDefinition tableDef = tableDefList.get(i);

			query.append(String.format("%s ", tableDef.getTableName()));

			if (StringUtils.hasText(tableDef.getTableAlias())) {
				query.append(String.format("%s ", tableDef.getTableAlias()));
			}
		}

		for (int i = 0; i < conditionDefList.size(); ++i) {
			if (i == 0) {
				query.append(" WHERE ");
			} else {
				query.append(" AND ");
			}

			ConditionDefinition conditionDef = conditionDefList.get(i);

			ColumnDefinition leftside = conditionDef.getLeftSide();

			if(StringUtils.hasText(conditionDef.getPrefix())){
				query.append(String.format("%s ", conditionDef.getPrefix()));
			}

			if (StringUtils.hasText(leftside.getTableDefinition().getTableAlias())) {
				query.append(String.format("%s.", leftside.getTableDefinition().getTableAlias()));
			}

			query.append(String.format("%s ", leftside.getColumnName()));

			if(StringUtils.hasText(conditionDef.getSuffix())){
				query.append(String.format("%s ", conditionDef.getSuffix()));
			}

			query.append("= ");

			if (conditionDef.getConditionType().equals(ConditionType.REFERENCE)) {
				ColumnDefinition rightside = conditionDef.getRightSide();


				if (StringUtils.hasText(rightside.getTableDefinition().getTableAlias())) {
					query.append(String.format("%s.", rightside.getTableDefinition().getTableAlias()));
				}

				query.append(String.format("%s ", rightside.getColumnName()));
			} else {
				if(StringUtils.hasLength(conditionDef.getConditionValue()))	{
					query.append("'").append(conditionDef.getConditionValue()).append("' ");
				} else {
					query.append(":").append(leftside.getColumnAlias());
				}
			}
		}

		if(!orderbyMap.isEmpty()){
			query.append(" ORDER BY ");

			Iterator<Integer> it = orderbyMap.keySet().iterator();

			int index = 0;

			while(it.hasNext())	{
				OrderbyDefinition orderby = orderbyMap.get(it.next());
				ColumnDefinition columnDef = orderby.getColumnDefinition();

				if(index++ > 0){
					query.append(", ");
				}

				if (StringUtils.hasText(columnDef.getTableDefinition().getTableAlias())) {
					query.append(String.format("%s.", columnDef.getTableDefinition().getTableAlias()));
				}

				query.append(String.format("%s ", columnDef.getColumnName()));

			}
		}

		return query.toString();
	}

	protected String buildInsertQuery(List<TableDefinition> tableDefList, List<ColumnDefinition> columnDefList)	{

		if(tableDefList.size() == 0){
			throw new StatementEvaluateException("[SimpleORMQueryEvaluator] buildInsertQuery - 테이블 정보가 정의되지 않았습니다.");
		}

		TableDefinition table = tableDefList.get(0);

		StringBuilder query = new StringBuilder();

		query.append("INSERT INTO ");
		query.append(String.format("%s ", table.getTableName()));

		StringBuilder colNames = new StringBuilder();
		StringBuilder colValues = new StringBuilder();

		for(ColumnDefinition column : columnDefList){
			if(!table.getTableName().equals(column.getTableDefinition().getTableName())){
				continue;
			}

			if(colNames.length() > 0)	{
				colNames.append(", ");
				colValues.append(", ");
			}

			colNames.append(String.format("%s", column.getColumnName()));

			colNames.append(" ");

			if(column.getColumnType() == ColumnType.DATE){
				colValues.append(getTimeStamp());
			} else {
				colValues.append(":").append(column.getColumnAlias());
			}
		}

		query.append(String.format("(%s) VALUES (%s)", colNames.toString(), colValues.toString()));

		return query.toString();
	}

	protected String buildDeleteQuery(List<TableDefinition> tableDefList, List<ConditionDefinition> conditionDefList)	{
		if(tableDefList.size() == 0){
			throw new StatementEvaluateException("[SimpleORMQueryEvaluator] buildDeleteQuery - 테이블 정보가 정의되지 않았습니다.");
		}

		TableDefinition table = tableDefList.get(0);

		StringBuilder query = new StringBuilder();

		query.append("DELETE FROM ");
		query.append(String.format("%s ", table.getTableName()));

		if (StringUtils.hasText(table.getTableAlias())) {
			query.append(String.format("%s ", table.getTableAlias()));
		}

		for (int i = 0; i < conditionDefList.size(); ++i) {
			if (i == 0) {
				query.append(" WHERE ");
			} else {
				query.append(" AND ");
			}

			ConditionDefinition conditionDef = conditionDefList.get(i);

			ColumnDefinition leftside = conditionDef.getLeftSide();

			if(StringUtils.hasText(conditionDef.getPrefix())){
				query.append(String.format("%s ", conditionDef.getPrefix()));
			}

			if (StringUtils.hasText(leftside.getTableDefinition().getTableAlias())) {
				query.append(String.format("%s.", leftside.getTableDefinition().getTableAlias()));
			}

			query.append(String.format("%s ", leftside.getColumnName()));

			if(StringUtils.hasText(conditionDef.getSuffix())){
				query.append(String.format("%s ", conditionDef.getSuffix()));
			}

			query.append("= ");

			if(StringUtils.hasLength(conditionDef.getConditionValue()))	{
				query.append("'").append(conditionDef.getConditionValue()).append("' ");
			} else {
				query.append(":").append(leftside.getColumnAlias());
			}
		}

		return query.toString();
	}

	protected String buildUpdateQuery(Object object, List<TableDefinition> tableDefList, List<ColumnDefinition> updatableColumnList,	List<ConditionDefinition> conditionDefList) {
		if(tableDefList.size() == 0){
			throw new StatementEvaluateException("[SimpleORMQueryEvaluator] buildUpdateQuery - 테이블 정보가 정의되지 않았습니다.");
		}

		if(updatableColumnList.size() == 0){
			throw new StatementEvaluateException("[SimpleORMQueryEvaluator] buildUpdateQuery - Update 가능한 컬럼 정보가 등록되지 않았습니다. - Class={" + object.getClass() + "}");
		}

		TableDefinition table = tableDefList.get(0);

		StringBuilder query = new StringBuilder();

		query.append("UPDATE ");
		query.append(String.format("%s ", table.getTableName()));

		if (StringUtils.hasText(table.getTableAlias())) {
			query.append(String.format("%s ", table.getTableAlias()));
		}

		query.append("SET ");

		for (int i = 0; i < updatableColumnList.size(); ++i) {
			if (i > 0) {
				query.append(", ");
			}

			ColumnDefinition column = updatableColumnList.get(i);

			query.append(String.format("%s = ", column.getColumnName()));

			if(column.getColumnType() == ColumnType.DATE){
				query.append(getTimeStamp()).append(" ");
			} else {
				query.append(":").append(column.getColumnAlias());
			}
		}

		for (int i = 0; i < conditionDefList.size(); ++i) {
			if (i == 0) {
				query.append(" WHERE ");
			} else {
				query.append(" AND ");
			}

			ConditionDefinition conditionDef = conditionDefList.get(i);

			ColumnDefinition leftside = conditionDef.getLeftSide();

			if(StringUtils.hasText(conditionDef.getPrefix())){
				query.append(String.format("%s ", conditionDef.getPrefix()));
			}

			if (StringUtils.hasText(leftside.getTableDefinition().getTableAlias())) {
				query.append(String.format("%s.", leftside.getTableDefinition().getTableAlias()));
			}

			query.append(String.format("%s ", leftside.getColumnName()));

			if(StringUtils.hasText(conditionDef.getSuffix())){
				query.append(String.format("%s ", conditionDef.getSuffix()));
			}

			query.append("= ");

			if(StringUtils.hasLength(conditionDef.getConditionValue()))	{
				query.append("'").append(conditionDef.getConditionValue()).append("' ");
			} else {
				query.append(":").append(leftside.getColumnAlias());
			}
		}

		return query.toString();
	}

	protected String getTimeStamp()	{
		/*
		 * POSTGRESQL -> current_timestamp
		 */

		return "sysdate";
	}
}
