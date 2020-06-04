package jcf.cmd.cron.scheduler;


public interface CommandScheduler {
	
	void scheduleJob(String jobName, String runnerName, String[] args, String cronExpression);

	void unscheduleAllJobs();
	
	void start();
	
	void shutdown();
	
}
