package jcf.dao.ibatis.crud;

public class DeleteResult implements ExecutionResult {

	private int affectedRows;
	private Exception exception;

	public DeleteResult(int affectedRows) {
		this.affectedRows =affectedRows;
	}

	public DeleteResult(Exception exception) {
		this.exception = exception;
	}

	public int getAffectedRows() {
		return affectedRows;
	}

	public Exception getException() {
		return exception;
	}

	public String getOperation() {
		return RowStatus.DEFAULT_ROW_STATUS_DELETE;
	}
}
