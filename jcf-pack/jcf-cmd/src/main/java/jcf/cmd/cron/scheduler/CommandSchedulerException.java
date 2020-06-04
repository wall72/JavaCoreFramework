package jcf.cmd.cron.scheduler;

import jcf.cmd.cron.CronException;

public class CommandSchedulerException extends CronException {

	private static final long serialVersionUID = 1L;

	public CommandSchedulerException(String message, Throwable t) {
		super(message, t);
	}

}
