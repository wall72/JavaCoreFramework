package jcf.sua.ux.json.dataset;

import java.util.List;

import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetImpl;
import jcf.sua.dataset.DataSetRow;

/**
 *
 * {@link DataSet}
 *
 * @author nolang
 *
 */
public final class JsonDataSet extends DataSetImpl {

	public JsonDataSet(String dataSetId) {
		super(dataSetId, null, null);
	}

	public JsonDataSet(String dataSetId, List<DataSetColumn> cols, List<DataSetRow> rows) {
		super(dataSetId, cols, rows);
	}
}
