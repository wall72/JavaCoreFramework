package jcf.extproc.process.filter;

import jcf.extproc.process.JobInstanceFilter;
import jcf.extproc.process.JobInstanceInfo;

public class NotJobInstanceFilter extends AbstractJobInstanceFilter {

	private static final long serialVersionUID = -2576325013237382861L;
	private JobInstanceFilter jobInstanceFilter;
	
	public NotJobInstanceFilter(JobInstanceFilter jobInstanceFilter) {
		setJobInstanceFilter(jobInstanceFilter);
	}
	
	public void setJobInstanceFilter(JobInstanceFilter jobInstanceFilter) {
		this.jobInstanceFilter = jobInstanceFilter;
	}
	
	public boolean isIncluded(JobInstanceInfo jobInstance) {
		return !jobInstanceFilter.isIncluded(jobInstance);
	}

}
