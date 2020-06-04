package jcf.cmd.cron.scheduler;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import jcf.cmd.AbstractTestParent;
import jcf.cmd.cron.scheduler.quartz.RunnerJob;
import jcf.cmd.runner.CommandLineRunner;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RunnerJobTest extends AbstractTestParent {

	@Test
	public void commandLineRunner정상처리() throws JobExecutionException {
		CommandLineRunner commandLineRunner = mock(CommandLineRunner.class);
		
		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) throws Throwable {
				System.out.println("running..........");
				return null;
			}
		}).when(commandLineRunner).run(any(String[].class));
		
		String[] args = null;

		executeQuartzJob(commandLineRunner, args);
		
		verify(commandLineRunner).run(any(String[].class));
	}

	@Test
	public void commandLineRunner예외_차단_처리() throws JobExecutionException {
		CommandLineRunner commandLineRunner = mock(CommandLineRunner.class);
		
		doThrow(new RuntimeException("ex")).when(commandLineRunner).run(any(String[].class));
		
		String[] args = null;

		executeQuartzJob(commandLineRunner, args);
		
		verify(commandLineRunner).run(any(String[].class));
	}

	private void executeQuartzJob(CommandLineRunner commandLineRunner,
			String[] args) throws JobExecutionException {
		JobData jobData = new JobData("JOB_NAME", commandLineRunner, args);

		JobExecutionContext jec = mock(JobExecutionContext.class);
		when(jec.getMergedJobDataMap()).thenReturn(new JobDataMap(Collections.singletonMap(RunnerJob.JOBDATA_KEY, jobData)));
		
		RunnerJob runnerJob = new RunnerJob();
		runnerJob.execute(jec);
	}

}
