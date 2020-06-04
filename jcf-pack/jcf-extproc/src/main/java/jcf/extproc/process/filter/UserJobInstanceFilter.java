package jcf.extproc.process.filter;

import jcf.extproc.exception.ExternalProcessException;
import jcf.extproc.process.JobInstanceInfo;

public class UserJobInstanceFilter extends AbstractJobInstanceFilter {

	private static final long serialVersionUID = -7299731595428692955L;
	private String user;
	
	public UserJobInstanceFilter(String user) {
		setUser(user);
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public boolean isIncluded(JobInstanceInfo jobInstance) {
		if (user == null) {
			throw new ExternalProcessException("user is not set");
		}
		return jobInstance.getUser().equals(user);
	}

}
