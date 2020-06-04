package jcf.extproc.exception;


public class ExternalProcessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExternalProcessException(String message, Throwable t) {
		super(message, t);
	}

	public ExternalProcessException(String message) {
		super(message);
	}
}
