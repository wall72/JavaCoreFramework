package jcf.extproc.execution;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import jcf.extproc.process.ExternalProcess;
import jcf.extproc.process.JobInstanceInfo;


public interface ExternalProcessContext {

	ExternalProcess getExternalProcess();

	long run(ExecutorService executorService, JobInstanceInfo sourceJobInstanceInfo, Charset charset, Map<String, String> adhocEnvironment);

	boolean isRunning();

	boolean destroyExecution(String jobName);

	JobInstanceManager getInstanceManager();

}
