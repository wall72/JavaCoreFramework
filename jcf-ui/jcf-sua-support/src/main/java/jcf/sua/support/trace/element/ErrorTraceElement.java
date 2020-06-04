package jcf.sua.support.trace.element;

/**
 *
 * @author nolang
 *
 */
public interface ErrorTraceElement extends TraceElement {

	Throwable getCause();

	String getMessage();
}
