package jcf.cmd.cron;

import jcf.cmd.ExecutorException;

public class CronException extends ExecutorException {

	private static final long serialVersionUID = 1L;

	public CronException(String message) {
		super(message);
	}

	public CronException(String message, Throwable t) {
		super(message, t);
	}
	
}
