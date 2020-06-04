package jcf.sua.ux.webplus.dataset;

import java.util.List;
import java.util.Map;

import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetImpl;
import jcf.sua.dataset.DataSetRow;

/**
 *
 * {@link DataSet} 의 웹플러스 구현체
 *
 * @author nolang
 *
 */
public class WebplusDataSet extends DataSetImpl {

	public WebplusDataSet(String dataSetId){
		super(dataSetId, null, null);
	}

	public WebplusDataSet(String dataSetId, List<DataSetColumn> dataSetColumns,
			List<DataSetRow> dataSetRows) {
		super(dataSetId, dataSetColumns, dataSetRows);
	}

	protected DataSetRow newDataSetRowFromBean(Object bean) {
		DataSetRow row = super.newDataSetRowFromBean(bean);
		row.setRowStatus("RETRIEVE");
		return row;
	}

	protected DataSetRow newDataSetRowFromMap(Map<String, ?> map) {
		DataSetRow row = super.newDataSetRowFromMap(map);
		row.setRowStatus("RETRIEVE");
		return row;
	}
}
