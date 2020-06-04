package jcf.cmd.cron.scheduler.quartz;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;

import java.text.ParseException;

import jcf.cmd.cron.scheduler.CommandScheduler;
import jcf.cmd.cron.scheduler.CommandSchedulerException;
import jcf.cmd.cron.scheduler.JobData;
import jcf.cmd.issue.IssueHandler;
import jcf.cmd.runner.CommandLineRunnerRepository;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public class QuartzCommandScheduler implements CommandScheduler {
	
	private Scheduler scheduler;
	private CommandLineRunnerRepository repository;
	private IssueHandler issueHandler;

	public QuartzCommandScheduler(Scheduler scheduler, CommandLineRunnerRepository repository, IssueHandler issueHandler) {
		this.scheduler = scheduler;
		this.repository = repository;
		this.issueHandler = issueHandler;
	}

	public void scheduleJob(String jobName, String runnerName, String[] args, String cronExpression) {
		final CronTrigger cronTrigger; 
		try {
			cronTrigger = newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
			
		} catch (ParseException e) {
			throw new CommandSchedulerException("error parsing cron: " + cronExpression, e);
		}
		
		final JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(RunnerJob.JOBDATA_KEY, new JobData(jobName, repository.getRunner(runnerName), args));
		jobDataMap.put(RunnerJob.ISSUE_HANDLER_KEY, issueHandler);

		final JobDetail jobDetail = 
			newJob(RunnerJob.class)
			.withIdentity(new JobKey(jobName))
			.usingJobData(jobDataMap)
			.build();
		
		try {
			scheduler.scheduleJob(jobDetail, cronTrigger);

		} catch (SchedulerException e) {
			throw new CommandSchedulerException("error scheduling job " + runnerName, e);
		}
	}

	public void unscheduleAllJobs() {
		try {
			scheduler.clear();
			
		} catch (SchedulerException e) {
			throw new CommandSchedulerException("error clearing scheduler", e);
		}
	}

	public void start() {
		try {
			if (!scheduler.isShutdown() && !scheduler.isStarted()) {
				scheduler.start();
			}
			
		} catch (SchedulerException e) {
			throw new CommandSchedulerException("error while scheduler start", e);
		}
	}

	public void shutdown() {
		try {
			if (!scheduler.isShutdown()) {
				scheduler.shutdown();
			}
			
		} catch (SchedulerException e) {
			throw new CommandSchedulerException("error while scheduler standby", e);
		}
	}
	
}
