package jcf.extproc.execution;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcf.extproc.env.EnvMapPopulator;
import jcf.extproc.exception.AlreadyRunningException;
import jcf.extproc.process.ExternalProcess;
import jcf.extproc.process.JobInstanceFilter;
import jcf.extproc.process.JobInstanceInfo;
import jcf.extproc.process.JobStatus;
import jcf.extproc.process.LogFileKeepingPolicy;


public class ExternalProcessContextImpl implements UpdatableExternalProcessContext {

	private static final Logger logger = LoggerFactory
			.getLogger(ExternalProcessContextImpl.class);
	
	private ExternalProcess externalProcess;
	
	private transient ExternalProcessExecution execution;

	private EnvMapPopulator envMapPopulator;
	
	private ExternalProcessRepository repository;

	private JobInstanceManager jobInstanceManager;
	
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	
	public ExternalProcessContextImpl(ExternalProcess externalProcess, ExternalProcessRepository repository, EnvMapPopulator envMapPopulator) {
		this.externalProcess = externalProcess;
		this.repository = repository;
		jobInstanceManager = new JobInstanceManagerImpl(externalProcess, repository);
		this.envMapPopulator = envMapPopulator;
	}
	

	public ExternalProcess getExternalProcess() {
		return externalProcess;
	}

	public void setExternalProcess(ExternalProcess externalProcess) {
		this.externalProcess = externalProcess;
	}
	
	public long run(ExecutorService executorService, JobInstanceInfo sourceJobInstanceInfo, final Charset cs, final Map<String, String> adhocEnvironment) {

		if (! isRunning.compareAndSet(false, true)) {
			throw new AlreadyRunningException("cannot start job " + externalProcess.getName());
		}

		final JobInstanceInfo jobInstanceInfo = jobInstanceManager.create(sourceJobInstanceInfo);

		executorService.execute(new Runnable() {
			
			public void run() {
				try {
					File instanceDirectory = repository.getInstanceDirectory(jobInstanceInfo);
					
					execution = new ExternalProcessExecution(jobInstanceInfo, instanceDirectory, externalProcess, envMapPopulator, adhocEnvironment, cs);
					
					execution.run();
						
					cleanUpJobInstanceDirectories(jobInstanceInfo);
					
				} catch (Exception e) {
					logger.warn("exception occurred while running job " + jobInstanceInfo, e);
					jobInstanceInfo.setResult(JobStatus.FAIL);
					
				} finally {
					isRunning.set(false);
					jobInstanceManager.update(jobInstanceInfo);
				}
				
			}
		});
		
		return jobInstanceInfo.getJobInstance();
			
	}


	public boolean isRunning() {
		return isRunning.get();
	}

	public boolean destroyExecution(String jobName) {
		if (execution == null) {
			return false;
		}
		return execution.destroy();
	}


	public JobInstanceManager getInstanceManager() {
		return jobInstanceManager;
	}


	private void cleanUpJobInstanceDirectories(final JobInstanceInfo currentJobInstanceInfo) {
		final LogFileKeepingPolicy logFileKeepingPolicy = externalProcess.getLogFileKeepingPolicy();
	
		if (logFileKeepingPolicy == null) {
			return;
		}
		
		jobInstanceManager.deleteInstances(
				new JobInstanceFilter() {
					public boolean isIncluded(JobInstanceInfo jobInstanceInfo) {
						return logFileKeepingPolicy.isOutdated(jobInstanceInfo, currentJobInstanceInfo);
					}
				}
				
		);
	}

}
