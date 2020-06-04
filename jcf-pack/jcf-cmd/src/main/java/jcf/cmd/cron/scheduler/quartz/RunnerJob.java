package jcf.cmd.cron.scheduler.quartz;

import java.util.Arrays;

import jcf.cmd.cron.scheduler.JobData;
import jcf.cmd.issue.Issue;
import jcf.cmd.issue.IssueHandler;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@DisallowConcurrentExecution
public class RunnerJob implements Job {
	
	private static final Logger logger = LoggerFactory
			.getLogger(RunnerJob.class);
	
	public static final String JOBDATA_KEY = "jobData";
	public static final String ISSUE_HANDLER_KEY = "issueHandler";

	private static final String RUNNER_MDC_KEY = "runner";

	public void execute(JobExecutionContext jec) throws JobExecutionException {
		JobData jobData = (JobData)jec.getMergedJobDataMap().get(JOBDATA_KEY);
		IssueHandler issueHandler = (IssueHandler)jec.getMergedJobDataMap().get(ISSUE_HANDLER_KEY);
		
		MDC.put(RUNNER_MDC_KEY, jobData.getJobName());
		
		try {
			jobData.getCommandLineRunner().run(jobData.getArgs());
			
		} catch (Exception e) {
			logger.warn("[{}] {}", new Object[]{jobData.getJobName(), makeMessage(jobData), e});

			if (issueHandler != null) {
				issueHandler.report(new Issue(jobData.getJobName(), makeMessage(jobData), e));
			}
			
		} finally {
			MDC.remove(RUNNER_MDC_KEY);
		}
	}

	private String makeMessage(JobData jobData) {
		return Arrays.deepToString(jobData.getArgs());
	}

}
