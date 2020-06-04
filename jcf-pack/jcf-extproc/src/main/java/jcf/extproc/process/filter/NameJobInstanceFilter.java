package jcf.extproc.process.filter;

import jcf.extproc.exception.ExternalProcessException;
import jcf.extproc.process.JobInstanceInfo;

public class NameJobInstanceFilter extends AbstractJobInstanceFilter {

	private static final long serialVersionUID = 8375286709559755943L;
	private String jobName;
	
	public NameJobInstanceFilter(String jobName) {
		setJobName(jobName);
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public boolean isIncluded(JobInstanceInfo jobInstance) {
		if (jobName == null) {
			throw new ExternalProcessException("jobName is not set");
		}
		return jobInstance.getJobName().equals(jobName);
	}

}
