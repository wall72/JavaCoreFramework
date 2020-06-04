package jcf.extproc.config;

import jcf.extproc.process.AntProcess;
import jcf.extproc.process.CommandLineProcess;

/**
 * XStream serializalble entity class.
 * 
 * @author setq
 *
 */
public class JobConfig {

	private CommandLineProcess commandLineProcess;
	private AntProcess antProcess;
	
	public void setCommandLineProcess(CommandLineProcess commandLineProcess) {
		this.commandLineProcess = commandLineProcess;
	}
	public CommandLineProcess getCommandLineProcess() {
		return commandLineProcess;
	}
	public void setAntProcess(AntProcess antProcess) {
		this.antProcess = antProcess;
	}
	public AntProcess getAntProcess() {
		return antProcess;
	}

}
