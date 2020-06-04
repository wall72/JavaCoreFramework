package jcf.util.ibatis.aspectj;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jcf.util.metadata.JavaTypes;
import jcf.util.metadata.MetadataAwareList;
import jcf.util.metadata.ResultSetMetadataHolder;
import jcf.util.metadata.SQLMetaMapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.result.ResultMapping;
import com.ibatis.sqlmap.engine.mapping.statement.RowHandlerCallback;
import com.ibatis.sqlmap.engine.scope.StatementScope;
import com.ibatis.sqlmap.engine.transaction.Transaction;

/**
 * <pre>
 * Aspects' applying order is in sequence.
 * 1. around 	: GeneralStatement.executeQueryForList(..)
 * 		- after completion of all query, make the MetaAwareList and return.
 * 2. before	: SqlExecutor.handleRequest(..)
 * 		- before get the object from resultset, clone the ResultSetMetaData into session.
 * 3. before	: DefaultRowHandler.handleRow(..)
 * </pre>
 *
 * @author Administrator
 *
 */
public class MetadataAwareSqlMapAdvices implements SqlMapAdvices {

	
	private static final Log logger = LogFactory.getLog(MetadataAwareSqlMapAdvices.class);

	private ThreadLocal localMetaHolder =  new ThreadLocal();
	

	public void beforeHandleResults(StatementScope statementScope, ResultSet rs, int skipResults, int maxResults, RowHandlerCallback callback) {

		if (logger.isDebugEnabled())logger.debug("before JoinPoint afterHandleResults - try to bring and store sql metadata from ibatis");
		ResultSetMetaData resultsetMetadata = null;

		// This block is for handling <ResultMap> usecase.
		// Create the Map<String columnName, String propertyName>(key(columnName) is only modified to uppercase).
		// Unless using <ResultMap>, columnPropertyNameResultMap will be null.
		Map columnPropertyNameResultMap = createColumnPropertyNameResultMap(statementScope);

		if( logger.isDebugEnabled() ) logger.debug("before JoinPoint afterHandleResults - name mapping info of column and Property :" + columnPropertyNameResultMap);

		// This block is for handling not-considered but could-be-used fields
		// This is only for Value Object, which is not for Map type.
		List omittedFields = findOmittedFields(statementScope, rs, columnPropertyNameResultMap);

		try {
			resultsetMetadata = rs.getMetaData();
			ResultSetMetadataHolder holder = new ResultSetMetadataHolder(resultsetMetadata.getColumnCount() + omittedFields.size());

			for (int column = 1; column <= resultsetMetadata.getColumnCount(); column++) {
				holder.setColumnName(getPropertyName(columnPropertyNameResultMap, resultsetMetadata.getColumnName(column)), column);

				holder.setColumnType(resultsetMetadata.getColumnType(column), column);
				holder.setColumnDisplaySize(resultsetMetadata.getColumnDisplaySize(column), column);
				holder.setPrecision(resultsetMetadata.getPrecision(column), column);
				holder.setScale(resultsetMetadata.getScale(column), column);
			}

			for (int i = 0, column = resultsetMetadata.getColumnCount() + 1; i < omittedFields.size(); i++, column++) {
				String fieldTypeName = ((Field)omittedFields.get(i)).getType().getName();
				
				holder.setColumnName(((Field)omittedFields.get(i)).getName(), column);
				holder.setColumnType(SQLMetaMapper.toDBType(fieldTypeName), column);
				holder.setColumnDisplaySize(SQLMetaMapper.toDisplaySize(fieldTypeName), column);
				holder.setPrecision(SQLMetaMapper.toPrecision(fieldTypeName), column);
				holder.setScale(SQLMetaMapper.toScale(fieldTypeName), column);
			}

			this.localMetaHolder.set(holder);

		} catch (SQLException e) {
			logger.warn("error obtaining resultset metadata", e);
		}
	}

	public List aroundExecuteQueryForList(List originalList, StatementScope statementScope, Transaction trans, Object parameterObject, int skipResults, int maxResults) {
		if(  !( originalList instanceof MetadataAwareList) ){
			if (logger.isDebugEnabled()) {
				if( null == originalList ){
					logger.debug("Target List :" + originalList);
				}
				else{
					logger.debug("Target List's type :" + originalList.getClass());
					logger.debug("Target List :" + originalList);
				}
			}
//			SessionScope session = statementScope.getSession();
//			ResultSetMetadataHolder metadata = (ResultSetMetadataHolder) session.getAttribute("metadata");
			ResultSetMetadataHolder metadata = (ResultSetMetadataHolder) this.localMetaHolder.get();
			this.localMetaHolder.set(null);
			MetadataAwareList malist = new MetadataAwareList(originalList);
			malist.setMetadata(metadata);

			if (logger.isDebugEnabled()) logger.debug(malist.toString());
			return malist;
		}

		return originalList;
	}

	public void beforeHandleRow(Object valueObject) {
	}

	/**
	 * Return the property name matched with given column name.
	 * If columnPropertyNameResultMap has no matched property with given column name, return the column name.
	 *
	 */
	private String getPropertyName(Map columnPropertyNameResultMap, String columnName) {
		if( null != columnPropertyNameResultMap && columnPropertyNameResultMap.get(columnName.toUpperCase()) != null) return (String) columnPropertyNameResultMap.get(columnName.toUpperCase());
		else return columnName;
	}

	private Map createColumnPropertyNameResultMap(StatementScope request){
		Map columnPropertyNameResultMap = null;
		ResultMap resultMap = request.getResultMap();
		if( null != resultMap && null != resultMap.getResultMappings()){
			columnPropertyNameResultMap = new HashMap();
			ResultMapping[] resultMappings = (ResultMapping[]) resultMap.getResultMappings();
			// if( log.isDebugEnabled() ) log.debug("create columnPropertyNameResultMap by BasicResultMapping, BasicResultMapping's length is " + resultMappings.length);
			for (int i = 0; i < resultMappings.length; i++) {
				columnPropertyNameResultMap.put(resultMappings[i].getColumnName().toUpperCase(), resultMappings[i].getPropertyName());
			}
		}
		else if( null != resultMap && null != resultMap.getResultClass() && !JavaTypes.isNormalType(resultMap.getResultClass()) && !Map.class.isAssignableFrom(resultMap.getResultClass())){
			columnPropertyNameResultMap = new HashMap();
			Class resultClass = resultMap.getResultClass();
			Field[] fields = getReadableFieldsIncludingSuper(resultClass);
			// if( log.isDebugEnabled() ) log.debug("create columnPropertyNameResultMap by resultClass(not for Map type), resultClass type is " + resultClass.getClass());
			for (int i = 0; i < fields.length; i++) {
				columnPropertyNameResultMap.put(fields[i].getName().toUpperCase(), fields[i].getName());
			}
		}
		else{
			if (logger.isDebugEnabled()) logger.debug("ResultMap has no resultClass and resultMapping property, or is Map Assignable type.");
		}

		return columnPropertyNameResultMap;
	}

	/**
	 *
	 * <p>Return the omitted fields.<br/>
	 * 1. get the ibatis result class.<br/>
	 * 2. get the fields from result class.<br/>
	 * 3. remove the field matched with metadata column name from the fields<br/>
	 *
	 *
	 *
	 */
	private List findOmittedFields(StatementScope request, ResultSet rs, Map columnPropertyNameResultMap){
		List omittedFields = new ArrayList();
		ResultMap resultMap = request.getResultMap();
		ResultSetMetaData rsmd;
		int columnCount = 0;
		try {
			rsmd = rs.getMetaData();
			columnCount = rsmd.getColumnCount();

			if( null != resultMap
				&& null != resultMap.getResultClass()
				&& !Map.class.isAssignableFrom(resultMap.getResultClass()) ){

				Class resultClass = resultMap.getResultClass();
				if(logger.isDebugEnabled()) logger.debug("resultClass is " + resultClass);

				// find the omitted fields.
				Field[] fields = getReadableFieldsIncludingSuper(resultClass);
				Map fieldMap = new HashMap();
				for (int i = 0; i < fields.length; i++) {
					if( JavaTypes.isNormalType(fields[i].getType()) && !"rowStatus".equalsIgnoreCase(fields[i].getName())){
						fieldMap.put(fields[i].getName(), fields[i]);
					}
				}
				if( fieldMap.size() <= 0 ) return omittedFields;
				for (int column = 1; column <= columnCount; column++) {
					String propertyName = getPropertyName(columnPropertyNameResultMap, rsmd.getColumnName(column));
					fieldMap.remove(propertyName);
				}

				Iterator fieldMapIter = fieldMap.keySet().iterator();
				while( fieldMapIter.hasNext()){
					omittedFields.add( fieldMap.get((String)fieldMapIter.next()));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return omittedFields;
	}
	

	/**
	 * This is only for JavaBeans.<br/>
	 * Return all readable fields which the given class and the all parent classes have.<br/><br/>
	 * 
	 * Be careful that those fields may include unwritable fields.
	 * 
	 * @param clazz
	 * @return
	 */
	private Field[] getReadableFieldsIncludingSuper(Class clazz){
		Class currentClazz = clazz;
		Field[] fields = null;
		Field[] tempFields = null;
		
		boolean doLoop = true;
		while( doLoop ){
			tempFields = getReadableFields(currentClazz);
			fields = mergeFields(fields, tempFields);
			currentClazz = currentClazz.getSuperclass();
			if( "java.lang.Object".equals(currentClazz.getName()) ) doLoop = false;
		}
		return fields;
	}
	
	/**
	 * This is only for JavaBeans.<br/>
	 * Return all readable fields which the given class.<br/><br/>
	 * 
	 * Be careful that those fields may include unwritable fields.
	 * @param clazz
	 * @return
	 */
	private Field[] getReadableFields(Class clazz){
		Field[] fields = clazz.getDeclaredFields();		
		
		if( fields.length > 0){
			Map methodsMap = new HashMap();
			Method[] methods = clazz.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				if( methods[i].getName().startsWith("get") ) {
					methodsMap.put(methods[i].getName().substring(3).toLowerCase(), methods[i].getReturnType());
				}
			}
			
			List readbleFieldList = new ArrayList();
			for (int i = 0; i < fields.length; i++) {				
				if( null != methodsMap.get(fields[i].getName().toLowerCase()) &&
						methodsMap.get(fields[i].getName().toLowerCase()) == fields[i].getType()
						){
					readbleFieldList.add(fields[i]);
				}				
			}
			
			Field[] readableFields = new Field[readbleFieldList.size()];
			for (int i = 0; i < readableFields.length; i++) {
				readableFields[i] = (Field) readbleFieldList.get(i);
			}
			
			return readableFields;
		}
		
		return new Field[0];
	}
	
	private Field[] mergeFields(Field[] fields1, Field[] fields2) {
		int fieldLength = 0;
		fieldLength = ( null == fields1 ) ? fieldLength : (fieldLength + fields1.length);
		fieldLength = ( null == fields2 ) ? fieldLength : (fieldLength + fields2.length);
		Field[] fields = new Field[fieldLength];
		
		int index = 0;
		
		if( null != fields1){
			for(int i = 0; i < fields1.length; i++ ){
				fields[index++] = fields1[i];
			}
		}
		
		if( null != fields2){
			for(int i = 0; i < fields2.length; i++ ){
				fields[index++] = fields2[i];
			}
		}

		return fields;
	}
	
}
