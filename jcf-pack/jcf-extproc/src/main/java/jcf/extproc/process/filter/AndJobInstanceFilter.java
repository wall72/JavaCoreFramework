package jcf.extproc.process.filter;

import jcf.extproc.exception.ExternalProcessException;
import jcf.extproc.process.JobInstanceFilter;
import jcf.extproc.process.JobInstanceInfo;

/**
 * 여러 필터를 AND 조건으로 묶음. 
 * @author setq
 *
 */
public class AndJobInstanceFilter extends AbstractJobInstanceFilter {

//	public static void main(String[] args) throws IOException {
//		AndJobInstanceFilter filter = new AndJobInstanceFilter(
//				new DateJobInstanceFilter(new Date(), new Date())
//				, new DurationJobInstanceFilter(0, 1000)
//				, new NameJobInstanceFilter("hello")
//				, new NotJobInstanceFilter(new NameJobInstanceFilter(""))
//				, new OrJobInstanceFilter(new NameJobInstanceFilter(""), new NameJobInstanceFilter(""))
//				, new RegExpNameJobInstanceFilter(".*")
//				, new ResultJobInstanceFilter("SUCCESS")
//				, new UserJobInstanceFilter("NONAME")
//		);
//		
//		ObjectOutputStream os = new ObjectOutputStream(new BASE64EncoderStream(System.out));
//		os.writeObject(filter);
//		os.flush();
//	}
	
	private static final long serialVersionUID = -2768908221318042932L;
	private JobInstanceFilter[] filters;
	public AndJobInstanceFilter(JobInstanceFilter... filters) {
		this.filters = filters;
	}
	
	public void setJobInstanceFilters(JobInstanceFilter... filters) {
		this.filters = filters;
	}
	
	public boolean isIncluded(JobInstanceInfo jobInstance) {
		if (filters == null) {
			throw new ExternalProcessException("filters are not set");
		}
		for (JobInstanceFilter filter : filters) {
			if (!filter.isIncluded(jobInstance)) {
				return false;
			}
		}
		return true;
	}

}
