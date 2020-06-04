package jcf.sua.ux.extJs.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import jcf.sua.dataset.DataSet;
import jcf.sua.mvc.AbstractMciResponse;
import jcf.sua.ux.extJs.dataset.ExtJsDataSet;

/**
 *
 * {@link MciResponse}
 *
 * @author nolang
 *
 */
public class ExtJsResponse extends AbstractMciResponse {

	private Map<String, Boolean> srcDataTypeMap = new HashMap<String, Boolean>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataSet createUxDataSet(String datasetId) {
		return new ExtJsDataSet(datasetId);
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
	@Deprecated
	public void setMap(String datasetId, Map<String, ?> map) {
		super.set(datasetId, map);
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
	@Deprecated
	public void setMapList(String datasetId,
			List<? extends Map<String, ?>> mapList) {
		super.setList(datasetId, mapList);
		srcDataTypeMap.put(datasetId, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isFromCollection(String datasetId)	{
		return srcDataTypeMap.get(datasetId);
	}
	
}
