package jcf.extproc.execution;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

import jcf.extproc.process.CommandLineProcess;
import jcf.extproc.process.JobInstanceInfo;
import jcf.extproc.process.JobStatus;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ExternalProcessExecutionTest {

	final String SAMPLE_JOB = "sample";
	JobInstanceInfo jobInstanceInfo; 
	ExternalProcessExecution execution;
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void before() {
		 jobInstanceInfo = createJobInstanceInfo(); // entity 
		CommandLineProcess cmd = createExternalProcess(); // value object
		File instanceDirectory = folder.newFolder("INSTANCE_DIRECTORY") ;

		execution = new ExternalProcessExecution(jobInstanceInfo, instanceDirectory, cmd, null, null, Charset.defaultCharset());
	}

	@Test
	public void 일반_실행_테스트() throws InterruptedException {
		//when
		execution.run();  // blocking method (until finished or destroyed)
	
		// then
		assertThat(jobInstanceInfo.getResult(), is(JobStatus.SUCCESS));
	}
	
	@Test
	public void 중단_테스트() throws InterruptedException {
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				execution.destroy();
			}
		}, 1000);

		//when
		execution.run();  // blocking method (until finished or destroyed)
	
		// then
		assertThat(jobInstanceInfo.getResult(), is(JobStatus.STOPPED));
	}

	private CommandLineProcess createExternalProcess() {
		CommandLineProcess cmd = new CommandLineProcess(SAMPLE_JOB);
		
		cmd.setWorkDirectory(".");
		cmd.setCommandLine("java", "-cp", "target/classes", "-cp", "target/test-classes", "jcf.extproc.execution.SampleMain");
		return cmd;
	}

	private JobInstanceInfo createJobInstanceInfo() {
		JobInstanceInfo jobInstanceInfo = new JobInstanceInfo();
		jobInstanceInfo.setUser("TEST_USER");
		jobInstanceInfo.setJobInstance(0);
		jobInstanceInfo.setJobInstanceName("TEST INSTANCE");
		jobInstanceInfo.setJobName(SAMPLE_JOB);

		jobInstanceInfo.setDescription("JUNIT TEST RUN");
		return jobInstanceInfo;
	}

}
