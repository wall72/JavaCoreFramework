package jcf.cmd.cron.monitor;

import jcf.cmd.cron.CronException;


public class ScheduleChangeCheckerException extends CronException {

	private static final long serialVersionUID = 1L;

	public ScheduleChangeCheckerException(String message) {
		super(message);
	}
	
	public ScheduleChangeCheckerException(String message, Throwable t) {
		super(message, t);
	}
	
}
