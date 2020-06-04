package jcf.sua.exception;

public class RowStatusException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 8737632253185272562L;

	public RowStatusException() {
		super();
	}

	public RowStatusException(String msg) {
		super(msg);
	}

	public RowStatusException(Exception e) {
		super(e);
	}

	public RowStatusException(String msg, Exception e) {
		super(msg, e);
	}
}
