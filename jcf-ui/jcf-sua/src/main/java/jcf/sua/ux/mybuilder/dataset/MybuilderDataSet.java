package jcf.sua.ux.mybuilder.dataset;

import java.util.List;

import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetImpl;
import jcf.sua.dataset.DataSetRow;

/**
 *
 * {@link DataSet}
 *
 * @author Jeado
 *
 */
public class MybuilderDataSet extends DataSetImpl {

	public MybuilderDataSet(String dataSetId) {
		super(dataSetId, null, null);
	}

	public MybuilderDataSet(String dataSetId, List<DataSetColumn> cols, List<DataSetRow> rows) {
		super(dataSetId, cols, rows);
	}
}
