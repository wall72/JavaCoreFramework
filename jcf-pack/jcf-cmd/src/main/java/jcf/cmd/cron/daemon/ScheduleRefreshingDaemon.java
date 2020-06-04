package jcf.cmd.cron.daemon;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import jcf.cmd.cron.monitor.ScheduleChangeChecker;
import jcf.cmd.cron.monitor.ScheduleUpdater;
import jcf.cmd.cron.scheduler.CommandScheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleRefreshingDaemon {

	private static final Logger logger = LoggerFactory
			.getLogger(ScheduleRefreshingDaemon.class);
	
	
	private ScheduleChangeChecker scheduleChangeChecker;

	private CommandScheduler commandScheduler;

	
	private Timer timer;
	private long period;
	

	private ScheduleUpdater updater = new ScheduleUpdater() {

		public void updateSchedule(String jobName, String cmdLine, String cronExpression) {
			logger.info("[cronExpression: {}] [jobName: {}] [cmdLine: {}]", 
					new Object[] {cronExpression, jobName, cmdLine});

			Scanner scanner = null;
			final String runnerName;
			final List<String> list = new ArrayList<String>();
			
			try {
				scanner = new Scanner(cmdLine);
				runnerName = scanner.next();
	
				while (scanner.hasNext()) {
					list.add(scanner.next());
				}
			
			} finally {
				if (scanner != null) {
					scanner.close();
				}
			}

			String[] args = list.toArray(new String[] {});

			try {
				commandScheduler.scheduleJob(jobName, runnerName, args, cronExpression);

			} catch (Exception e) {
				logger.warn("error scheduling job", e);
			}
		}
	};
	
	public ScheduleRefreshingDaemon(CommandScheduler commandScheduler, ScheduleChangeChecker scheduleChangeChecker, long period) {
		this.commandScheduler = commandScheduler;
		this.scheduleChangeChecker = scheduleChangeChecker;
		this.period = period;
	}

	private void refreshIfModified() {
		if (!scheduleChangeChecker.isModified()) {
			return;
		}

		commandScheduler.unscheduleAllJobs();
		
		scheduleChangeChecker.load(updater);
	}

	public void startup() {
		commandScheduler.start();
		scheduleChangeChecker.load(updater);
		
		timer = new Timer("changeChecker", true);
		timer.schedule(new TimerTask() {
			
			public void run() {
				refreshIfModified();
			}
		}, period, period);
	}

	public void shutdown() {
		timer.cancel();
		commandScheduler.shutdown();
	}


}
