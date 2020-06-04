package jcf.sua.ux.nexacro.dataset;

import java.util.List;

import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetImpl;
import jcf.sua.dataset.DataSetRow;

public class NexacroDataSet extends DataSetImpl {

	public NexacroDataSet(String dataSetId){
		this(dataSetId, null, null);
	}
	
	public NexacroDataSet(String dataSetId, List<DataSetColumn> cols, List<DataSetRow> rows) {
		super(dataSetId, cols, rows);
	}

}
