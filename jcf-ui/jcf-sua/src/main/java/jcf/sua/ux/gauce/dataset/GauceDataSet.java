package jcf.sua.ux.gauce.dataset;

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
public final class GauceDataSet extends DataSetImpl {

	public GauceDataSet(String dataSetId) {
		this(dataSetId, null, null);
	}

	public GauceDataSet(String dataSetId, List<DataSetColumn> cols, List<DataSetRow> rows) {
		super(dataSetId, cols, rows);
	}
}
