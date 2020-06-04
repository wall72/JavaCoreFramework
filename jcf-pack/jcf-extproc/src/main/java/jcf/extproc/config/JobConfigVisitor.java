package jcf.extproc.config;

import jcf.extproc.process.AntProcess;
import jcf.extproc.process.CommandLineProcess;
import jcf.extproc.process.ExternalProcessVisitor;

public class JobConfigVisitor implements ExternalProcessVisitor {

	private JobConfig jobConfig = new JobConfig();
	
	public void visit(CommandLineProcess commandLineProcess) {
		jobConfig.setCommandLineProcess(commandLineProcess);
	}

	public void visit(AntProcess antProcess) {
		jobConfig.setAntProcess(antProcess);
	}
	
	public JobConfig getJobConfig() {
		return jobConfig;
	}

}
