package jcf.cmd.cron.scheduler;

import jcf.cmd.runner.CommandLineRunner;

/**
 * 여러 개의 JobDataMap Entry를 모아두기 위한 Value Object.
 * 
 * @author setq
 *
 */
public class JobData {

	private String jobName;
	private CommandLineRunner commandLineRunner;
	private String[] args;
	
	
	public JobData(String jobName, CommandLineRunner commandLineRunner, String[] args) {
		this.jobName = jobName;
		this.commandLineRunner = commandLineRunner;
		this.args = args;
	}

	public String getJobName() {
		return jobName;
	}
	
	public CommandLineRunner getCommandLineRunner() {
		return commandLineRunner;
	}
	
	public String[] getArgs() {
		return args;
	}
	
}
