package jcf.extproc.execution;

import jcf.extproc.process.ExternalProcess;


public interface UpdatableExternalProcessContext extends ExternalProcessContext {

	void setExternalProcess(ExternalProcess externalProcess);

}
