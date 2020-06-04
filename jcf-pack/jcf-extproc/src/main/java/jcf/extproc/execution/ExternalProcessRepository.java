package jcf.extproc.execution;

import java.io.File;
import java.util.Set;
import java.util.SortedSet;

import jcf.extproc.process.ExternalProcess;
import jcf.extproc.process.JobInstanceInfo;

public interface ExternalProcessRepository {

	File getInstanceDirectory(JobInstanceInfo instanceInfo);

	SortedSet<JobInstanceInfo> loadInstances(ExternalProcess externalProcess);

	void saveInstance(JobInstanceInfo jobInstanceInfo);
	void deleteInstance(JobInstanceInfo jobInstanceInfo);
	long getNextInstanceId(String jobName);
	
	
	Set<ExternalProcess> loadExternalProcesses();
	void saveExternalProcess(ExternalProcess externalProcess);
	void deleteExternalProcess(String jobName);
	

}
