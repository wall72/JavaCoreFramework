package jcf.extproc.execution;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import jcf.extproc.config.ExtProcConstant;
import jcf.extproc.process.ExternalProcess;
import jcf.extproc.process.JobInstanceFilter;
import jcf.extproc.process.JobInstanceInfo;
import jcf.extproc.process.JobStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobInstanceManagerImpl implements JobInstanceManager {
	private static final Logger logger = LoggerFactory
			.getLogger(JobInstanceManagerImpl.class);
	
	private ExternalProcessRepository repository;
	private ExternalProcess externalProcess;
	
	private SortedSet<JobInstanceInfo> instances;

	public JobInstanceManagerImpl(ExternalProcess externalProcess, ExternalProcessRepository repository) {
		this.externalProcess = externalProcess;
		this.repository = repository;
		
		instances = repository.loadInstances(externalProcess);
	}
	
	public void deleteInstances(JobInstanceFilter jobInstanceFilter) {
		SortedSet<JobInstanceInfo> deletedJobInstanceInfos = new TreeSet<JobInstanceInfo>();
		
		logger.debug("no of instances: {}", instances.size());
		
		for (JobInstanceInfo jobInstanceInfo : instances) {
			if (jobInstanceFilter.isIncluded(jobInstanceInfo)) {
				repository.deleteInstance(jobInstanceInfo);
				deletedJobInstanceInfos.add(jobInstanceInfo);

				logger.debug("deleting jobInstance {}", jobInstanceInfo);

			} else {
				logger.debug("skipping jobInstance {}", jobInstanceInfo);
			}
		}
		instances.removeAll(deletedJobInstanceInfos );
	}

	public List<JobInstanceInfo> getList(JobInstanceFilter jobInstanceFilter) {
		List<JobInstanceInfo> list = new ArrayList<JobInstanceInfo>();
		
		for (JobInstanceInfo jobInstanceInfo : instances) {
			if (jobInstanceFilter == null 
					|| jobInstanceFilter.isIncluded(jobInstanceInfo)) {
				list.add(jobInstanceInfo);
			}
		}
		
		return list;
	}

	public JobInstanceInfo get(long instanceId) {
		for (JobInstanceInfo jobInstanceInfo : instances) {
			if (jobInstanceInfo.getJobInstance() == instanceId) {
				return jobInstanceInfo;
			}
		}
		return null;
	}

	public void update(JobInstanceInfo sourceJobInstanceInfo) {
		for (JobInstanceInfo jobInstanceInfo : instances) {
			if (jobInstanceInfo.getJobInstance() == sourceJobInstanceInfo.getJobInstance()) {
				jobInstanceInfo.setDescription(sourceJobInstanceInfo.getDescription());
				repository.saveInstance(jobInstanceInfo);
				break;
			}
		}
	}

	public JobInstanceInfo getLatest() {
		if (instances.isEmpty()) {
			return null;
		}
		return instances.last();
	}

	public JobInstanceInfo create(JobInstanceInfo sourceJobInstanceInfo) {
		JobInstanceInfo jobInstanceInfo = new JobInstanceInfo();
		jobInstanceInfo.setJobInstance(repository.getNextInstanceId(externalProcess.getName()));
		jobInstanceInfo.setJobInstanceName(getNewJobInstanceName() );
		jobInstanceInfo.setJobName(externalProcess.getName());
		jobInstanceInfo.setResult(JobStatus.RUNNING);
		jobInstanceInfo.setUser(getUserWithDefault(sourceJobInstanceInfo));
		jobInstanceInfo.setDescription(getDescriptionWithDefault(sourceJobInstanceInfo));
		jobInstanceInfo.setWorkDirectory(externalProcess.getWorkDirectory());
		
		repository.saveInstance(jobInstanceInfo);
		instances.add(jobInstanceInfo);
		
		return jobInstanceInfo;
	}

	private String getDescriptionWithDefault(
			JobInstanceInfo sourceJobInstanceInfo) {
		if (sourceJobInstanceInfo != null) {
				return sourceJobInstanceInfo.getDescription();
		}
		return null;
	}

	/**
	 * user가 주어지면 사용, null이면 작업 정의에 기록된 user 사용.
	 * @param sourceJobInstanceInfo
	 * @return
	 */
	private String getUserWithDefault(JobInstanceInfo sourceJobInstanceInfo) {
		if (sourceJobInstanceInfo != null && sourceJobInstanceInfo.getUser() != null) {
			return sourceJobInstanceInfo.getUser();
		}
		return externalProcess.getUser();
	}

	/**
	 * 인스턴스 디렉토리명명 규칙.
	 * @return
	 */
	private String getNewJobInstanceName() {
		return new SimpleDateFormat(ExtProcConstant.DATE_FORMAT).format(new Date() );
	}

	
}
