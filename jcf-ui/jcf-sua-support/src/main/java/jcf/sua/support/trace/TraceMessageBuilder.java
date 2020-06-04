package jcf.sua.support.trace;

import jcf.sua.support.trace.element.StringTraceElement;
import jcf.sua.support.trace.element.StringTraceWarningElement;

/**
 *
 * @author nolang
 *
 */
public class TraceMessageBuilder {

	public static void addTraceMessage(String name, String contents) {
		TraceContextHolder.get().getTrace()
				.addElement(new StringTraceElement(name, contents));
	}

	public static void addTraceWarningMessage(String name, String contents) {
		TraceContextHolder.get().getTrace()
				.addElement(new StringTraceWarningElement(name, contents));
	}

}
