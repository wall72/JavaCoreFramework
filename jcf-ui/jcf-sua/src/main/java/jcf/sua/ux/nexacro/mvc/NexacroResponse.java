package jcf.sua.ux.nexacro.mvc;

import jcf.sua.dataset.DataSet;
import jcf.sua.mvc.AbstractMciResponse;
import jcf.sua.ux.nexacro.dataset.NexacroDataSet;

public class NexacroResponse extends AbstractMciResponse {

	public DataSet createUxDataSet(String datasetId) {
		return new NexacroDataSet(datasetId);
	}

}
