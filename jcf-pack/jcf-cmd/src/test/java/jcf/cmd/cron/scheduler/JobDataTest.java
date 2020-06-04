package jcf.cmd.cron.scheduler;

import static org.junit.Assert.*;
import jcf.cmd.AbstractTestParent;
import jcf.cmd.runner.CommandLineRunner;

import org.junit.Test;

public class JobDataTest extends AbstractTestParent {

	@Test
	public void test() {
		CommandLineRunner commandLineRunner = new CommandLineRunner() {
			
			public void run(String[] args) {
				
			}
		};
		
		String jobName = "JOB_NAME";
		String[] args = {""};
		JobData jobData = new JobData(jobName, commandLineRunner, args);

		assertEquals(jobName, jobData.getJobName());
		assertEquals(commandLineRunner, jobData.getCommandLineRunner());
		assertArrayEquals(args, jobData.getArgs());
	}

}
