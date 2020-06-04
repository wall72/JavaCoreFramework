package jcf.cmd.cron.monitor;

public interface ScheduleUpdater {

	void updateSchedule(String jobName, String cmdLine, String cronExpression);
	
}
