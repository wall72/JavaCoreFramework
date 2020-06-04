package jcf.sua.ux.websquare.dataset;

import java.util.List;

import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetImpl;
import jcf.sua.dataset.DataSetRow;

/**
 *
 * {@link DataSet} 의 웹스퀘어 구현체
 *
 * @author nolang
 *
 */
public class WebsquareDataSet extends DataSetImpl {

	public WebsquareDataSet(String dataSetId) {
		this(dataSetId, null, null);
	}

	public WebsquareDataSet(String dataSetId, List<DataSetColumn> cols, List<DataSetRow> rows) {
		super(dataSetId, cols, rows);
	}
}
