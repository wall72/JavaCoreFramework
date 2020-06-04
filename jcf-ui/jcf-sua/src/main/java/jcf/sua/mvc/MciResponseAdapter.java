package jcf.sua.mvc;

import java.util.List;
import java.util.Map;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.AbstractDataSetStreamWriterStreamHandlerAdapter;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetStreamWriterFactory;
import jcf.sua.dataset.StreamSource;
import jcf.upload.FileInfo;
import jcf.upload.handler.DownloadEventHandler;

/**
 *
 * {@link MciResponse}
 *
 * @author nolang
 *
 */
public class MciResponseAdapter implements MciResponse {

	private MciResponse response;

	public MciResponseAdapter(MciResponse response) {
		this.response = response;
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> void set(String datasetId, E bean) {
		response.set(datasetId, bean);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> void set(String datasetId, E bean, Class<E> type) {
		response.set(datasetId, bean, type);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> void setList(String datasetId, List<E> listOfModel) {
		response.setList(datasetId, listOfModel);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> void setList(String datasetId, List<E> listOfModel, Class<E> type) {
		response.setList(datasetId, listOfModel, type);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMapList(String datasetId, List<? extends Map<String, ?>> mapList) {
		response.setMapList(datasetId, mapList);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMap(String datasetId, Map<String, ?> map) {
		response.setMap(datasetId, map);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addSuccessMessage(String message) {
		response.addSuccessMessage(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addParam(String paramName, String paramValue) {
		response.addParam(paramName, paramValue);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setViewName(String viewName) {
		response.setViewName(viewName);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDownStreamChannel(SuaChannels channel) {
		response.setDownStreamChannel(channel);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDownloadFile(FileInfo fileInfo) {
		response.setDownloadFile(fileInfo);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDownloadFile(DownloadEventHandler eventHandler, FileInfo fileInfo) {
		response.setDownloadFile(eventHandler, fileInfo);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> void stream(StreamSource<T> streamSource, AbstractDataSetStreamWriterStreamHandlerAdapter<T> streamHandler) {
		response.stream(streamSource, streamHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	public DataSetStreamWriter getStreamWriter() {
		return response.getStreamWriter();
	}

	/**
	 * {@inheritDoc}
	 */
	public DataSetStreamWriter getStreamWriter(DataSetStreamWriterFactory factory) {
		return response.getStreamWriter(factory);
	}
}
