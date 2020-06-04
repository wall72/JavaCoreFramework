package jcf.sua.ux.xplatform.dataset;

import java.util.List;

import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetImpl;
import jcf.sua.dataset.DataSetRow;

/**
 *
 * {@link DataSet} 의 Xplatform 구현체
 *
 * @author mina
 *
 */
public final class XplatformDataSet extends DataSetImpl {

	public XplatformDataSet(String dataSetId) {
		this(dataSetId, null, null);
	}

	public XplatformDataSet(String dataSetId, List<DataSetColumn> cols, List<DataSetRow> rows) {
		super(dataSetId, cols, rows);
	}
}
