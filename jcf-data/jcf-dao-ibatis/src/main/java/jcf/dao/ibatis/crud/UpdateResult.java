package jcf.dao.ibatis.crud;

public class UpdateResult implements ExecutionResult {

	private int affectedRows;
	private Exception exception;

	public UpdateResult(int affectedRows) {
		this.affectedRows = affectedRows;
	}

	public UpdateResult(Exception exception) {
		this.exception = exception;
	}

	public int getAffectedRows() {
		return affectedRows;
	}

	public Exception getException() {
		return exception;
	}

	public String getOperation() {
		return RowStatus.DEFAULT_ROW_STATUS_UPDATE;
	}
}
