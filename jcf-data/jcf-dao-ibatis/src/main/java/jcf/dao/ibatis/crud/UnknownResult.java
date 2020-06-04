package jcf.dao.ibatis.crud;


public class UnknownResult implements ExecutionResult {


	private String rowStatus;


	public UnknownResult(String rowStatus) {
		this.rowStatus = rowStatus;
	}



	public String getRowStatus() {
		return rowStatus;
	}

	public int getAffectedRows() {
		return 0;
	}

	public Exception getException() {
		return new Exception("unsupported rowstatus : " + rowStatus);
	}

	public String getOperation() {
		return RowStatus.DEFAULT_ROW_STATUS_UNKNOWN;
	}

}
