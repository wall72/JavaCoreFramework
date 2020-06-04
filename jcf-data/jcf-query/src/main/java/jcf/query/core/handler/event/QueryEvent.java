package jcf.query.core.handler.event;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author nolang
 *
 */
@SuppressWarnings("serial")
public class QueryEvent extends ApplicationEvent {

	private String traceId;
	private String statement;
	private Object parameter;
	private long startTime;
	private long endTime;
	private String exceptionMessage;
	private Exception exception;

	public QueryEvent(String traceId, String statement, Object parameter, long startTime, long endTime, Exception exception) {
		super(statement);

		this.traceId = traceId;
		this.statement = statement;
		this.parameter = parameter;
		this.startTime = startTime;
		this.endTime = endTime;
		this.exception = exception;
		this.exceptionMessage = ExceptionUtils.getRootCauseMessage(exception);
	}

	public String getTraceId() {
		return traceId;
	}

	public String getStatement() {
		return statement;
	}

	public Object getParameter() {
		return parameter;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public Exception getException() {
		return exception;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}
}
