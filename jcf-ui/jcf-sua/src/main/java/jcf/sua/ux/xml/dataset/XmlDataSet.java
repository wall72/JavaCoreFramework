package jcf.sua.ux.xml.dataset;

import java.util.List;

import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetImpl;
import jcf.sua.dataset.DataSetRow;

/**
 *
 * {@link DataSet} 의 XML 구현체
 *
 * @author nolang
 *
 */
public class XmlDataSet extends DataSetImpl {

	public XmlDataSet(String dataSetId) {
		super(dataSetId, null, null);
	}

	public XmlDataSet(String dataSetId, List<DataSetColumn> cols, List<DataSetRow> rows) {
		super(dataSetId, cols, rows);
	}

}
