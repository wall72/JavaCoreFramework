package jcf.sua.ux.webflow.dataset;

import java.util.List;

import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetImpl;
import jcf.sua.dataset.DataSetRow;

/**
 *
 * {@link DataSet} 의 표준웹 구현체
 *
 * @author nolang
 *
 */
public class WebFlowDataSet extends DataSetImpl {

	public WebFlowDataSet(String datasetId) {
		super(datasetId, null, null);
	}

	public WebFlowDataSet(String dataSetId, List<DataSetColumn> dataSetColumns, List<DataSetRow> dataSetRows) {
		super(dataSetId, dataSetColumns, dataSetRows);
	}
}
