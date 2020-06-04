package jcf.sua.ux.miplatform.dataset;

import java.util.List;

import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetImpl;
import jcf.sua.dataset.DataSetRow;

/**
 *
 * {@link DataSet}
 *
 * @author mina
 *
 */
public final class MiplatformDataSet extends DataSetImpl {

	public MiplatformDataSet(String dataSetId) {
		this(dataSetId, null, null);
	}

	public MiplatformDataSet(String dataSetId, List<DataSetColumn> cols, List<DataSetRow> rows) {
		super(dataSetId, cols, rows);
	}
}
