package jcf.extproc;


import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import jcf.extproc.env.EnvMapPopulator;
import jcf.extproc.execution.ExternalProcessContext;
import jcf.extproc.execution.ExternalProcessContextManager;
import jcf.extproc.execution.ExternalProcessContextManagerImpl;
import jcf.extproc.execution.FileBasedExternalProcessRepository;
import jcf.extproc.process.ExternalProcess;
import jcf.extproc.process.JobInstanceFilter;
import jcf.extproc.process.JobInstanceInfo;

public class ExternalProcessOperatorImpl implements ExternalProcessOperator {
	
	private ExternalProcessContextManager contextManager;
	private Charset charset;
	private ExecutorService executorService;

	public ExternalProcessOperatorImpl(ExecutorService executorService, Charset charset, File baseDirectory, EnvMapPopulator envMapPopulator) {
		this.executorService = executorService;
		this.charset = charset;
		contextManager = new ExternalProcessContextManagerImpl(new FileBasedExternalProcessRepository(baseDirectory), envMapPopulator);
	}
	
	public void register(ExternalProcess externalProcess) {
		contextManager.put(externalProcess);
	}

	public boolean delete(String jobName) {
		return contextManager.remove(jobName);
	}

	public long execute(String jobName, Map<String, String> adhocEnvironment) {
		return execute(jobName, adhocEnvironment, null);
	}
	
	public long execute(final String jobName, final Map<String, String> adhocEnvironment, final JobInstanceInfo jobInstanceInfo) {
		ExternalProcessContext context = contextManager.get(jobName);
		return context.run(executorService, jobInstanceInfo, charset, adhocEnvironment);
	}
		
	public ExternalProcess getJob(String jobName) {
		return contextManager.get(jobName).getExternalProcess();
	}
	
	public List<Long> getJobInstanceIdList(String jobName, JobInstanceFilter jobInstanceFilter) {
		List<JobInstanceInfo> infoList = contextManager.get(jobName).getInstanceManager().getList(jobInstanceFilter);
		List<Long> list = new ArrayList<Long>(infoList.size());
		for (JobInstanceInfo info : infoList) {
			list.add(info.getJobInstance());
		}
		
		return list;
	}
	
	public void deleteJobInstances(String jobName, JobInstanceFilter jobInstanceFilter) {
		contextManager.get(jobName).getInstanceManager().deleteInstances(jobInstanceFilter);
	}

	public JobInstanceInfo getJobInstanceInfo(String jobName, long instanceId) {
		return contextManager.get(jobName).getInstanceManager().get(instanceId);
	}
	
	public void updateJobInstanceInfo(JobInstanceInfo jobInstanceInfo) {
		contextManager.get(jobInstanceInfo.getJobName()).getInstanceManager().update(jobInstanceInfo);
	}

	public JobInstanceInfo getLastInstanceInfo(String jobName) {
		return contextManager.get(jobName).getInstanceManager().getLatest();
	}

	public boolean isRunning(String jobName) {
		return contextManager.get(jobName).isRunning();
	}

	public boolean destroy(String jobName) {
		return contextManager.get(jobName).destroyExecution(jobName);
	}

	public List<String> getJobNames() {
		return contextManager.getJobNames();
	}
	
}
