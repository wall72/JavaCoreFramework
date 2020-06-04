package jcf.extproc.process.filter;

import jcf.extproc.exception.ExternalProcessException;
import jcf.extproc.process.JobInstanceFilter;
import jcf.extproc.process.JobInstanceInfo;

/**
 * 여러 필터를 OR 조건으로 묶음.
 * @author setq
 *
 */
public class OrJobInstanceFilter extends AbstractJobInstanceFilter {

	private static final long serialVersionUID = -3207625864183244862L;
	private JobInstanceFilter[] filters;
	public OrJobInstanceFilter(JobInstanceFilter... filters) {
		this.filters = filters;
	}
	
	public void setFilters(JobInstanceFilter... filters) {
		this.filters = filters;
	}
	
	public boolean isIncluded(JobInstanceInfo jobInstance) {
		if (filters == null) {
			throw new ExternalProcessException("filters are not set");
		}
		for (JobInstanceFilter filter : filters) {
			if (filter.isIncluded(jobInstance)) {
				return true;
			}
		}
		return false;
	}

}
