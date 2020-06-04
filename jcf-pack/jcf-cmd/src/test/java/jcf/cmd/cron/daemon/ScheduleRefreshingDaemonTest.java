package jcf.cmd.cron.daemon;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import jcf.cmd.AbstractTestParent;
import jcf.cmd.cron.monitor.ScheduleChangeChecker;
import jcf.cmd.cron.monitor.ScheduleUpdater;
import jcf.cmd.cron.scheduler.CommandScheduler;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ScheduleRefreshingDaemonTest extends AbstractTestParent {

	@Test
	public void scheduleRefresher() throws InterruptedException {
		CommandScheduler commandScheduler = mock(CommandScheduler.class);
		
		doAnswer(new Answer<Object>() {
		      public Object answer(InvocationOnMock invocation) {
		          System.out.println("commandScheduler.scheduleJob(...)");
		          return null;
		      }})
		  .when(commandScheduler).scheduleJob(anyString(), anyString(), any(String[].class), anyString());

//		(commandScheduler.start()).then
//		when(commandScheduler.scheduleJob(anyString(), anyString(), any(String[].class), anyString()))
		
		ScheduleChangeChecker scheduleChangeChecker = mock(ScheduleChangeChecker.class);
		when(scheduleChangeChecker.isModified()).thenReturn(false, true, false);
		doAnswer(new Answer<Object>() {

			public Object answer(InvocationOnMock invocation) throws Throwable {
				System.out.println("scheduleCnahgeChecker.load(updater)");
				ScheduleUpdater scheduleUpdater = (ScheduleUpdater)invocation.getArguments()[0];
				scheduleUpdater.updateSchedule("SAMPLE_JOB", "ps -ef | grep java", "0/5 * * * * ?");
				
				return null;
			}
			
		})
		.doAnswer(new Answer<Object>() {

			public Object answer(InvocationOnMock invocation) throws Throwable {
				System.out.println("scheduleCnahgeChecker.load(updater)");
				ScheduleUpdater scheduleUpdater = (ScheduleUpdater)invocation.getArguments()[0];
				scheduleUpdater.updateSchedule("OTHER_JOB", "java -Xmx64m -cp . sample/Main", "0 0 * * * MON-FRI");
				
				return null;
			}
			
		})
		.when(scheduleChangeChecker).load(any(ScheduleUpdater.class));
		
		ScheduleRefreshingDaemon daemon = new ScheduleRefreshingDaemon(commandScheduler, scheduleChangeChecker, 10);
		daemon.startup();
		
		Thread.sleep(50);
		
		daemon.shutdown();
	}
}
