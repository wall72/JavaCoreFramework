package jcf.sua.support.trace.element;

/**
 *
 * @author nolang
 *
 */
public class ExceptionTraceElement implements ErrorTraceElement {

	private Throwable cause;
	private String message;

	public ExceptionTraceElement(String message, Throwable cause) {
		this.message = message;
		this.cause = cause;
	}

	public String getElementContents() {
		return cause.getMessage();
	}

	public String getElementName() {
		return message;
	}

	public Throwable getCause() {
		return cause;
	}

	public String getMessage() {
		return message;
	}
}
