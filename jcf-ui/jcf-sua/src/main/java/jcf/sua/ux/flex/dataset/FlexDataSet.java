package jcf.sua.ux.flex.dataset;

import java.util.ArrayList;
import java.util.List;

import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetImpl;
import jcf.sua.dataset.DataSetRow;
/**
 *
 * @author nolang
 *
 */
public final class FlexDataSet extends DataSetImpl {

	public FlexDataSet(){
		super();
	}

	public FlexDataSet(String dataSetId) {
		super(dataSetId, new ArrayList<DataSetColumn>(), new ArrayList<DataSetRow>());
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<DataSetColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DataSetColumn> columns) {
		this.columns = columns;
	}

	public void setRows(List<DataSetRow> rows) {
		this.rows = rows;
	}
}
