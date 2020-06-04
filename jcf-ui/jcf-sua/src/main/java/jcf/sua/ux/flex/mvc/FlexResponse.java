package jcf.sua.ux.flex.mvc;

import java.util.List;
import java.util.Map;

import jcf.sua.dataset.DataSet;
import jcf.sua.mvc.AbstractMciResponse;
import jcf.sua.ux.flex.dataset.FlexDataSet;

/**
 *
 * @author nolang
 *
 */
public final class FlexResponse extends AbstractMciResponse {

	@Override
	public <E> void setList(String datasetId, List<E> listOfModel) {
		DataSet dataSet = createUxDataSet(datasetId);

		if (listOfModel != null) {

			for (E bean : listOfModel) {
				if (Map.class.isAssignableFrom(bean.getClass())) {
					dataSet.addRowMap((Map) bean, null);
				} else {
					dataSet.addRowBean(bean, null);
				}
			}
		}

		addDataSet(datasetId, dataSet);
	}

	public DataSet createUxDataSet(String datasetId) {
		return new FlexDataSet(datasetId);
	}
}
