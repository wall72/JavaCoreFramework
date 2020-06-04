package jcf.sua.mvc;

import java.util.List;
import java.util.Map;

import jcf.data.GridData;
import jcf.sua.mvc.file.MciPersistenceManager;
import jcf.upload.FileInfo;
import jcf.upload.handler.UploadEventHandler;

/**
 *
 * {@link MciRequest}
 *
 * @author nolang
 *
 */
public class MciRequestAdapter implements MciRequest {

	protected MciRequest request;

	public MciRequestAdapter(MciRequest request) {
		this.request = request;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRequestURI() {
		return request.getRequestURI();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getDataSetIds() {
		return request.getDataSetIds();
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> E get(String datasetId, Class<E> clazz) {
		return request.get(datasetId, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> E get(String datasetId, Class<E> clazz, String filter) {
		return request.get(datasetId, clazz, filter);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> E get(String datasetId, int rowNum, Class<E> clazz) {
		return request.get(datasetId, rowNum, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> E get(String datasetId, int rowNum, Class<E> clazz, String filter) {
		return request.get(datasetId, rowNum, clazz, filter);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> GridData<E> getGridData(String datasetId, Class<E> clazz) {
		return request.getGridData(datasetId, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> GridData<E> getGridData(String datasetId, Class<E> clazz, String filter) {
		return request.getGridData(datasetId, clazz, filter);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, ?> getMap(String datasetId) {
		return request.getMap(datasetId);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, ?> getMap(String datasetId, String filter) {
		return request.getMap(datasetId, filter);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, ?> getMap(String datasetId, int rowNum) {
		return request.getMap(datasetId, rowNum);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Map<String, String>> getMapList(String datasetId) {
		return request.getMapList(datasetId);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Map<String, String>> getMapList(String datasetId, String filter) {
		return request.getMapList(datasetId, filter);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getParameterNames() {
		return request.getParameterNames();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> getParam() {
		return request.getParam();
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T getParam(Class<T> type) {
		return request.getParam(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T getParam(Class<T> type, String filter) {
		return request.getParam(type, filter);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getParam(String paramName) {
		return request.getParam(paramName);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getParam(String paramName, String defaultValue) {
		return request.getParam(paramName, defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getParamArray(String paramName) {
		return request.getParamArray(paramName);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<FileInfo> getAttachments() {
		return request.getAttachments();
	}

	/**
	 * {@inheritDoc}
	 */
	public void handleIfMultipart(UploadEventHandler dispatcher, MciPersistenceManager persistence) {
		request.handleIfMultipart(dispatcher, persistence);
	}

}
