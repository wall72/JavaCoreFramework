package jcf.extproc.exception;

public class AlreadyRunningException extends ExternalProcessException {

	private static final long serialVersionUID = 1L;

	public AlreadyRunningException(String message) {
		super(message);
	}
	
}
