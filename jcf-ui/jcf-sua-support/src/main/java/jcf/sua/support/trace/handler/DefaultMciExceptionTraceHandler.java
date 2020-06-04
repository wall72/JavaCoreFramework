package jcf.sua.support.trace.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;

import jcf.sua.mvc.MciExceptionTraceHandler;
import jcf.sua.support.trace.TraceContextHolder;
import jcf.sua.support.trace.element.ExceptionTraceElement;

/**
 *
 * @author nolang
 *
 */
public class DefaultMciExceptionTraceHandler implements MciExceptionTraceHandler {

	public void handler(HttpServletRequest request,
			HttpServletResponse response, Exception exception) {
		TraceContextHolder
				.get()
				.getTrace()
				.addElement(
						new ExceptionTraceElement(ExceptionUtils.getRootCauseMessage(exception),
								exception));
	}

}
