package jcf.extproc.process;

import java.io.Serializable;


public interface LogFileKeepingPolicy extends Serializable {

	boolean isOutdated(JobInstanceInfo jobInstanceInfo, JobInstanceInfo currentJobInstanceInfo);

}
