package jcf.sua.ux.webflow.mvc;

import java.util.List;
import java.util.Map;

import jcf.sua.dataset.DataSet;
import jcf.sua.mvc.AbstractMciResponse;
import jcf.sua.ux.webflow.dataset.WebFlowDataSet;

/**
 *
 * {@link MciReponse} 의 표준웹 구현체
 *
 * @author nolang
 *
 */
public class WebFlowResponse extends AbstractMciResponse {


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addParam(String paramName, String paramValue) {
		super.addParam(paramName, paramValue);
		modelAndView.addObject(paramName, paramValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> void set(String datasetId, E bean) {
		super.set(datasetId, bean);
		modelAndView.addObject(datasetId, bean);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> void set(String datasetId, E bean, java.lang.Class<E> type) {
		super.set(datasetId, bean, type);
		modelAndView.addObject(datasetId, bean);
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> void setList(String datasetId, List<E> listOfModel) {
		super.setList(datasetId, listOfModel);
		modelAndView.addObject(datasetId, listOfModel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> void setList(String datasetId, List<E> listOfModel, Class<E> clazz) {
		super.setList(datasetId, listOfModel, clazz);
		modelAndView.addObject(datasetId, listOfModel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataSet createUxDataSet(String datasetId) {
		return new WebFlowDataSet(datasetId);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void setMap(String datasetId, Map<String, ?> map) {
		super.setMap(datasetId, map);
		modelAndView.addObject(datasetId, map);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void setMapList(String datasetId,
			List<? extends Map<String, ?>> mapList) {
		super.setMapList(datasetId, mapList);
		modelAndView.addObject(datasetId, mapList);
	}
}
