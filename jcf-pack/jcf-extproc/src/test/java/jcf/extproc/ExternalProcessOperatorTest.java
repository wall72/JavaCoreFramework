package jcf.extproc;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;

import jcf.extproc.process.CommandLineProcess;
import jcf.extproc.process.JobInstanceInfo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.ExecutorServiceAdapter;

public class ExternalProcessOperatorTest {

	final String SAMPLE_JOB = "sample";
	JobInstanceInfo jobInstanceInfo; 
	ExternalProcessOperator operator;
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void before() {
		 jobInstanceInfo = createJobInstanceInfo(); // entity 
		File baseDirectory = folder.newFolder("BASE_DIRECTORY") ;

		TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		ExecutorService executorService = new ExecutorServiceAdapter(taskExecutor);
		operator = new ExternalProcessOperatorImpl(executorService, Charset.defaultCharset(), baseDirectory, null);
		
		operator.register(createExternalProcess());
	}
	
	@Test
	public void 실행() {
		long instanceId = operator.execute(SAMPLE_JOB, null);
		assertEquals(0L, instanceId);
		
		assertTrue(operator.isRunning(SAMPLE_JOB));
		
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
