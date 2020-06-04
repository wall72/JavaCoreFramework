package jcf.extproc.process.filter;

import jcf.extproc.exception.ExternalProcessException;
import jcf.extproc.process.JobInstanceInfo;
import jcf.extproc.process.JobStatus;

public class ResultJobInstanceFilter extends AbstractJobInstanceFilter {

	private static final long serialVersionUID = 2435114669336421838L;
	private JobStatus result;
	
	public ResultJobInstanceFilter(JobStatus result) {
		setResult(result);
	}
	
	public void setResult(JobStatus result) {
		this.result = result;
	}
	
	public boolean isIncluded(JobInstanceInfo jobInstance) {
		if (result == null) {
			throw new ExternalProcessException("result is not set");
		}
		return jobInstance.getResult().equals(result);
	}

}
