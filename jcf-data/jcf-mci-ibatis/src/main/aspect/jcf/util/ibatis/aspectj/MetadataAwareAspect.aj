package jcf.util.ibatis.aspectj;


import java.sql.ResultSet;
import java.util.List;

import jcf.util.ibatis.aspectj.SqlMapAdvices;

import com.ibatis.sqlmap.engine.mapping.statement.RowHandlerCallback;
import com.ibatis.sqlmap.engine.scope.StatementScope;
import com.ibatis.sqlmap.engine.transaction.Transaction;


public aspect MetadataAwareAspect {

	private static final String MCI_ADVICES_CLASS = "jcf.util.ibatis.aspectj.MciSqlMapAdvices";
	
	private SqlMapAdvices sqlMapAdvices;// = new MciSqlMapAdvices();
	
	
	public void setSqlMapAdvices(SqlMapAdvices sqlMapAdvices) {
		this.sqlMapAdvices = sqlMapAdvices;
	}
	
	public SqlMapAdvices getSqlMapAdvices() {
		if (sqlMapAdvices == null) {
			sqlMapAdvices = getMCISqlMapAdvices();
		}
		return sqlMapAdvices;
	}
	

	private SqlMapAdvices getMCISqlMapAdvices() {
		Class clazz;
		try {
			 clazz = Class.forName(MCI_ADVICES_CLASS);
			 
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		SqlMapAdvices sqlMapAdvices;
		try {
			sqlMapAdvices = (SqlMapAdvices)clazz.newInstance();
			
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
			
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
		return sqlMapAdvices;
	}
	
	
	public pointcut handleResults()
		: execution(void com.ibatis.sqlmap.engine.execution.SqlExecutor.handleResults(..));

	public pointcut executeQueryForList(StatementScope request, Transaction transaction, Object paramObject, int x, int y)
		: execution(java.util.List com.ibatis.sqlmap.engine.mapping.statement.MappedStatement.executeQueryForList(..))
		 && args(request, transaction, paramObject, x, y);

	public pointcut handleRow(Object valueObject)
		: execution(void com.ibatis.sqlmap.engine.mapping.statement.DefaultRowHandler.handleRow(..))
		 && args(valueObject);

	before (StatementScope statementScope, ResultSet rs, int skipResults, int maxResults, RowHandlerCallback callback)
		: handleResults() && args(statementScope, rs, skipResults, maxResults, callback) {
		
		getSqlMapAdvices().beforeHandleResults(statementScope, rs, skipResults, maxResults, callback);
	}

	Object around(StatementScope request, Transaction transaction, Object paramObject, int x, int y)
		: executeQueryForList(request, transaction, paramObject, x, y) {

		List list = (List) proceed(request, transaction, paramObject, x, y);

		return getSqlMapAdvices().aroundExecuteQueryForList(list, request, transaction, paramObject, x, y);
	}
	
	before (Object valueObject)
	: handleRow(valueObject) {
		
		getSqlMapAdvices().beforeHandleRow(valueObject);
	}

}
