package jcf.sua.ux.webplus.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcf.data.GridData;
import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.GridDataImpl;
import jcf.sua.mvc.AbstractMciRequest;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.webplus.dataset.WebplusDataSet;
import jcf.sua.ux.webplus.dataset.WebplusDataSetReader;

/**
 *
 * {@link MciRequest} 의 웹플러스 구현체
 *
 * @author Jeado
 *
 */
public class WebplusRequest extends AbstractMciRequest{

	private final String DEFUALT_DATASET_ID = "defaultId";

	protected Map<String, DataSet> dataSetMap = new HashMap<String, DataSet>();
	protected Map<String, Object> paramMap = new HashMap<String, Object>();

	public WebplusRequest(WebplusDataSetReader reader, MciRequestValidator requestValidator) {
		dataSetMap.put(DEFUALT_DATASET_ID, new WebplusDataSet(DEFUALT_DATASET_ID, reader.getDataSetColumns(DEFUALT_DATASET_ID), reader.getDataSetRows(DEFUALT_DATASET_ID)));
		paramMap = reader.getParamMap();

		this.requestValidator = requestValidator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getDataSetIds() {
		return Collections.unmodifiableList(new ArrayList<String>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> E get(String datasetId, Class<E> clazz) {
		return dataSetMap.get(DEFUALT_DATASET_ID).getBean(clazz, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> E get(String datasetId, int rowNum, Class<E> clazz) {
		return dataSetMap.get(DEFUALT_DATASET_ID).getBean(clazz, rowNum);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, ?> getMap(String datasetId) {
		return dataSetMap.get(DEFUALT_DATASET_ID).getBean(HashMap.class, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, ?> getMap(String datasetId, int rowNum) {
		return dataSetMap.get(DEFUALT_DATASET_ID).getBean(HashMap.class, rowNum);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Map<String, String>> getMapList(String datasetId) {
		DataSet dataSet = dataSetMap.get(DEFUALT_DATASET_ID);

		int rowCount = dataSet.getRowCount();

		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < rowCount; ++i) {
			mapList.add(dataSet.getBean(HashMap.class, i));
		}

		return mapList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> GridData<E> getGridData(String datasetId, Class<E> clazz) {
		return new GridDataImpl<E>(dataSetMap.get(DEFUALT_DATASET_ID), clazz, null, null);
	}
}
