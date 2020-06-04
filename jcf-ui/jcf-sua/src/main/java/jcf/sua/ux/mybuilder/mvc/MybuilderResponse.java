package jcf.sua.ux.mybuilder.mvc;

import java.util.List;
import java.util.Map;

import jcf.sua.dataset.DataSet;
import jcf.sua.mvc.AbstractMciResponse;
import jcf.sua.ux.mybuilder.dataset.MybuilderDataSet;

/**
 * @author Jeado
 *
 */
public class MybuilderResponse extends AbstractMciResponse {

	@Override
	public DataSet createUxDataSet(String datasetId) {
		return new MybuilderDataSet(datasetId);
	}

	@Override
	public <E> void set(String datasetId, E bean) {
		super.set(datasetId, bean);
	}

	@Override
	@Deprecated
	public void setMap(String datasetId, Map<String, ?> map) {
		super.setMap(datasetId, map);
	}

	@Override
	public <E> void setList(String datasetId, List<E> listOfModel) {
		super.setList(datasetId, listOfModel);
	}
}
