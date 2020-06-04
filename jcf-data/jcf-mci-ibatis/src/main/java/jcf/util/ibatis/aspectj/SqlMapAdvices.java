package jcf.util.ibatis.aspectj;

import java.sql.ResultSet;
import java.util.List;

import com.ibatis.sqlmap.engine.mapping.statement.RowHandlerCallback;
import com.ibatis.sqlmap.engine.scope.StatementScope;
import com.ibatis.sqlmap.engine.transaction.Transaction;

public interface SqlMapAdvices {
	
	void beforeHandleResults(StatementScope statementScope, ResultSet rs, int skipResults, int maxResults, RowHandlerCallback callback);
	
	List aroundExecuteQueryForList(List originalList, StatementScope statementScope, Transaction trans, Object parameterObject, int skipResults, int maxResults);
	
	void beforeHandleRow(Object valueObject);
	
}
