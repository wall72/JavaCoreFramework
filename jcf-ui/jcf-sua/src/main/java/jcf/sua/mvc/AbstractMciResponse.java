package jcf.sua.mvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcf.sua.SuaChannels;
import jcf.sua.SuaConstants;
import jcf.sua.dataset.AbstractDataSetStreamWriterStreamHandlerAdapter;
import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.DataSetConverter;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetStreamWriterFactory;
import jcf.sua.dataset.StreamSource;
import jcf.sua.exception.MciException;
import jcf.upload.FileInfo;
import jcf.upload.handler.DownloadEventHandler;

import org.springframework.web.servlet.ModelAndView;

/**
*
* 하향 채널 사용을 위한 편의 클래스
*
* @author nolang
*
*/
public abstract class AbstractMciResponse implements MciResponse, MciDataSetAccessor {

	protected Map<String, DataSet> dataSetMap = new HashMap<String, DataSet>();
	protected Map<String, String> params= new HashMap<String, String>();
	protected ModelAndView modelAndView = new ModelAndView();
	protected List<String> successMessages = new ArrayList<String>();
	protected String exceptionMessage;
	protected FileInfo fileInfo;
	protected DataSetConverter dataSetConverter;


	/**
	 * {@inheritDoc}
	 */
	public Map<String, DataSet> getDataSetMap() {
		return dataSetMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addSuccessMessage(String message) {
		successMessages.add(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getSuccessMessags() {
		return successMessages;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getExceptionMessage() {
		return exceptionMessage;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setViewName(String viewName) {
		this.modelAndView.setViewName(viewName);
		//throw new UnsupportedOperationException("지원하지 않는 연산");
	}

	/**
	 * {@inheritDoc}
	 */
	public ModelAndView getModelAndView() {
		return this.modelAndView;
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> void set(String datasetId, E bean) {
		set(datasetId, bean, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> void set(String datasetId, E bean, Class<E> type) {

		if(bean != null && Collection.class.isAssignableFrom(bean.getClass()))	{
			throw new MciException("DataSetId-" + datasetId + "는 Collection Type 입니다. - setList 를 사용하십시오.");
		}

		DataSet dataSet = createUxDataSet(datasetId);

		if (dataSetConverter != null && dataSetConverter.support(bean)) {
			dataSetConverter.convert(dataSet, bean);
		} else {
			if (bean != null && Map.class.isAssignableFrom(bean.getClass())) {
				dataSet.addRowMap((Map) bean, type);
			} else {
				dataSet.addRowBean(bean, type);
			}
		}

		addDataSet(datasetId, dataSet);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> void setList(String datasetId, List<E> listOfModel) {
		setList(datasetId, listOfModel, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addParam(String paramName, String paramValue) {
		params.put(paramName, paramValue);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getParams() {
		return params;
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> void setList(String datasetId, List<E> listOfModel, Class<E> type) {
		DataSet dataSet = createUxDataSet(datasetId);

		/*
		 * Column 정보 생성
		 */
		dataSet.addRowBean(null, type);

		if (listOfModel!= null) {
			for (E bean : listOfModel) {
				if (Map.class.isAssignableFrom(bean.getClass())) {
					dataSet.addRowMap((Map) bean, type);
				} else {
					dataSet.addRowBean(bean, type);
				}
			}

			addDataSet(datasetId, dataSet);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	public void setMap(String datasetId, Map<String, ?> map) {
		DataSet dataSet = createUxDataSet(datasetId);

		dataSet.addRowMap(map, null);

		addDataSet(datasetId, dataSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	public void setMapList(String datasetId,
			List<? extends Map<String, ?>> mapList) {
		DataSet dataSet = createUxDataSet(datasetId);

		for (Map<String, ?> map : mapList) {
			dataSet.addRowMap(map, null);
		}

		addDataSet(datasetId, dataSet);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDownStreamChannel(SuaChannels channel) {
		MciRequestContextHolder.get().setMciChannelType(channel);
	}

	protected void addDataSet(String dataSetId, DataSet dataSet) {
		dataSetMap.put(dataSetId, dataSet);
	}

	/**
	 * {@inheritDoc}
	 */
	public FileInfo getDownloadFile() {
		return fileInfo;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDownloadFile(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDownloadFile(DownloadEventHandler eventHandler, FileInfo fileInfo) {
		if(eventHandler != null)	{
			eventHandler.preprocess(fileInfo);
		}

		this.fileInfo = fileInfo;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isFileProcessing() {
		return fileInfo != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDataSetConverter(DataSetConverter dataSetConverter) {
		this.dataSetConverter = dataSetConverter;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> void stream(StreamSource<T> streamSource, AbstractDataSetStreamWriterStreamHandlerAdapter<T> streamHandler) {
		streamSource.read(streamHandler);

		if (MciRequestContextHolder.get().getMciChannelType() == SuaChannels.WEBFLOW) {
			setViewName(SuaConstants.STREAMING);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public DataSetStreamWriter getStreamWriter() {
		if (MciRequestContextHolder.get().getMciChannelType() == SuaChannels.WEBFLOW) {
			setViewName(SuaConstants.STREAMING);
		}

		return MciRequestContextHolder.get().getDataSetStreamWriter();
	}

	/**
	 * {@inheritDoc}
	 */
	public DataSetStreamWriter getStreamWriter(DataSetStreamWriterFactory factory) {
		if (MciRequestContextHolder.get().getMciChannelType() == SuaChannels.WEBFLOW) {
			setViewName(SuaConstants.STREAMING);
		}

		return factory.getStreamWriter(MciRequestContextHolder.get().getHttpServletResponse());
	};

	/**
	 * {@inheritDoc}
	 */
	public abstract DataSet createUxDataSet(String datasetId);

}
