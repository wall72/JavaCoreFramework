package jcf.extproc.process.filter;

import jcf.extproc.exception.ExternalProcessException;
import jcf.extproc.process.JobInstanceInfo;

public class RegExpNameJobInstanceFilter extends AbstractJobInstanceFilter {

	private static final long serialVersionUID = 8839820823006979427L;
	private String regex;
	
	public RegExpNameJobInstanceFilter(String regex) {
		setRegExp(regex);
	}
	
	public void setRegExp(String regex) {
		this.regex = regex;
	}

	public boolean isIncluded(JobInstanceInfo jobInstance) {
		if (regex == null) {
			throw new ExternalProcessException("regex is not set");
		}
		return jobInstance.getJobName().matches(regex);
	}

}
