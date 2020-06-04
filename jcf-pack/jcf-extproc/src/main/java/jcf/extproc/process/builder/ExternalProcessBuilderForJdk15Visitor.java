package jcf.extproc.process.builder;

import java.io.File;
import java.util.Map;

import jcf.extproc.process.AntProcess;
import jcf.extproc.process.CommandLineProcess;



public class ExternalProcessBuilderForJdk15Visitor implements ExternalProcessBuilderVisitor {

	private File logDirectory;
	private ExternalProcessBuilder externalProcessBuilder;

	public ExternalProcessBuilderForJdk15Visitor(File logDirectory) {
		this.logDirectory = logDirectory;
	}

	public void visit(AntProcess antProcess) {
		externalProcessBuilder = createAntExternalProcessBuilder(antProcess);
	}

	public void visit(CommandLineProcess commandLineProcess) {
		externalProcessBuilder = createCommandLineExternalProcessBuilder(commandLineProcess);
	}
	
	private ExternalProcessBuilder createAntExternalProcessBuilder(AntProcess antProcess) {
		ExternalProcessBuilder externalProcessBuilder = new ExternalProcessBuilder(antProcess.getCommandLine());
		File workDirectory;
		if (antProcess.getWorkDirectory() == null) {
			workDirectory = logDirectory;
			
		} else {
			workDirectory = new File(antProcess.getWorkDirectory());
		}
		
		externalProcessBuilder.directory(workDirectory);

		Map<String, String> workEnvMap = externalProcessBuilder.environment();
		if (antProcess.getEnvMap() != null) {
			workEnvMap.putAll(antProcess.getEnvMap());
		}
		return externalProcessBuilder;
	}

	private ExternalProcessBuilder createCommandLineExternalProcessBuilder(CommandLineProcess comnandLineProcess) {
		ExternalProcessBuilder externalProcessBuilder = new ExternalProcessBuilder(comnandLineProcess.getCommandLine());
		File workDirectory;
		if (comnandLineProcess.getWorkDirectory() == null) {
			workDirectory = logDirectory;
			
		} else {
			workDirectory = new File(comnandLineProcess.getWorkDirectory() );
		}

		externalProcessBuilder.directory(workDirectory);

		Map<String, String> workEnvMap = externalProcessBuilder.environment();
		if (comnandLineProcess.getEnvMap() != null) {
			workEnvMap.putAll(comnandLineProcess.getEnvMap());
		}
		return externalProcessBuilder;
	}

	public ExternalProcessBuilder getExternalProcessBuilder() {
		return externalProcessBuilder;
	}

}
