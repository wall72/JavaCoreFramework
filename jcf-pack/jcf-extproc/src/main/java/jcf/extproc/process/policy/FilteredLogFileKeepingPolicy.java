package jcf.extproc.process.policy;

import jcf.extproc.process.JobInstanceFilter;
import jcf.extproc.process.JobInstanceInfo;
import jcf.extproc.process.LogFileKeepingPolicy;

public class FilteredLogFileKeepingPolicy implements LogFileKeepingPolicy {

	private static final long serialVersionUID = 1L;
	private JobInstanceFilter filter;

	
	public FilteredLogFileKeepingPolicy(JobInstanceFilter filter) {
		this.filter = filter;
	}
	
	public boolean isOutdated(JobInstanceInfo jobInstanceInfo,
			JobInstanceInfo currentJobInstanceInfo) {
		
		return filter.isIncluded(jobInstanceInfo);
	}

}
