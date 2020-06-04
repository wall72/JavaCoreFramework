package jcf.cmd;


public class ExecutorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExecutorException(String message, Throwable t) {
		super(message, t);
	}

	public ExecutorException(String message) {
		super(message);
	}

}
