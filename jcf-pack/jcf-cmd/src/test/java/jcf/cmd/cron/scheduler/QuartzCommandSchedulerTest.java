package jcf.cmd.cron.scheduler;

import static org.mockito.Mockito.*;
import jcf.cmd.AbstractTestParent;
import jcf.cmd.cron.scheduler.quartz.QuartzCommandScheduler;
import jcf.cmd.runner.CommandLineRunnerRepository;
import jcf.cmd.runner.spring.SpringRunnerRepository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzCommandSchedulerTest extends AbstractTestParent {
	
	private Scheduler scheduler;
	
	public QuartzCommandSchedulerTest() throws SchedulerException {
		scheduler = StdSchedulerFactory.getDefaultScheduler();
	}
	
	@Rule
	public ExternalResource exr = new ExternalResource() {
		protected void before() throws Throwable {
			scheduler.start();
		}
		protected void after() {
			try {
				scheduler.shutdown(true);

			} catch (SchedulerException e) {
				throw new RuntimeException(e);
			}
		}
		
	};

	@Test
	public void commandScheduler정상_라이프사이클() throws SchedulerException {
		SpringRunnerRepository repository = mock(SpringRunnerRepository.class);
		
//		doAnswer(new ThrowsException(new SchedulerException("test-exception-start"))).when(scheduler).start();
		
		callCommandScheduler(scheduler, repository);
	}

	@Test(expected=CommandSchedulerException.class)
	public void commandScheduler_스케쥴러_예외() throws SchedulerException {
		CommandLineRunnerRepository repository = mock(CommandLineRunnerRepository.class);
		
		scheduler.shutdown();
		
		callCommandScheduler(scheduler, repository);
	}

	private void callCommandScheduler(Scheduler scheduler,
			CommandLineRunnerRepository repository) throws SchedulerException {
		CommandScheduler commandScheduler = new QuartzCommandScheduler(scheduler, repository, null);
		
		commandScheduler.start();
		
		String jobName = "SAMPLE_JOB";
		String runnerName = "SAMPLE_RUNNER";
		String[] args = {""};
		String cronExpression = "0/5 * * * * ?";
		
		commandScheduler.scheduleJob(jobName, runnerName, args, cronExpression);
		commandScheduler.unscheduleAllJobs();
		
		commandScheduler.shutdown();
		
		verify(repository).getRunner(runnerName);
	}

}
