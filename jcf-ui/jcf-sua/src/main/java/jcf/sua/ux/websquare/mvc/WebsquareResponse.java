package jcf.sua.ux.websquare.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcf.sua.dataset.DataSet;
import jcf.sua.mvc.AbstractMciResponse;
import jcf.sua.ux.websquare.dataset.WebsquareDataSet;

/**
 *
 * {@link MciResponse} 의 웹스퀘어 구현체
 *
 * @author nolang
 *
 */
public class WebsquareResponse extends AbstractMciResponse {

	private Map<String, Boolean> srcDataTypeMap = new HashMap<String, Boolean>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataSet createUxDataSet(String datasetId) {
		return new WebsquareDataSet(datasetId);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> void set(String datasetId, E bean) {
		super.set(datasetId, bean);
		srcDataTypeMap.put(datasetId, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> void setList(String datasetId, List<E> listOfModel) {
		super.setList(datasetId, listOfModel);
		srcDataTypeMap.put(datasetId, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isFromCollection(String datasetId)	{
		return srcDataTypeMap.get(datasetId);
	}

}
