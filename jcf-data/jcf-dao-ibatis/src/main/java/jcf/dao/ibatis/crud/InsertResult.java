package jcf.dao.ibatis.crud;

public class InsertResult implements ExecutionResult {

	private Object insertKey;
	private Exception exception;

	public InsertResult(Object insertKey) {
		this.insertKey = insertKey;
	}

	public InsertResult(Exception exception) {
		this.exception = exception;
	}

	public Object getInsertKey() {
		return insertKey;
	}

	public Exception getException() {
		return exception;
	}

	public int getAffectedRows() {
		return exception == null? 1 : 0;
	}

	public String getOperation() {
		return RowStatus.DEFAULT_ROW_STATUS_INSERT;
	}
}
