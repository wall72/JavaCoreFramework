package jcf.sua.mvc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcf.data.GridData;
import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.GridDataImpl;
import jcf.sua.dataset.converter.StringToBigDecimalConverter;
import jcf.sua.dataset.converter.StringToDateConverter;
import jcf.sua.dataset.converter.StringToIntConverter;
import jcf.sua.exception.MciException;
import jcf.sua.mvc.file.MciPersistenceManager;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.upload.FileInfo;
import jcf.upload.MultiPartInfo;
import jcf.upload.handler.UploadEventHandler;

import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 *
 * 상향 채널 사용을 위한 편의 클래스
 *
 * @author nolang
 */
public abstract class AbstractMciRequest implements MciRequest {

	protected Map<String, DataSet> dataSetMap = new HashMap<String, DataSet>();
	protected Map<String, Object> paramMap = new HashMap<String, Object>();
	protected List<FileInfo> attachments = new ArrayList<FileInfo>();
	protected MciRequestValidator requestValidator;
	private GenericConversionService conversionService = ConversionServiceFactory.createDefaultConversionService();
	/**
	 * {@inheritDoc}
	 */
	public String getRequestURI() {
		return MciRequestContextHolder.get().getHttpServletRequest().getRequestURI();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getDataSetIds() {
		return Collections.unmodifiableList(new ArrayList<String>(dataSetMap.keySet()));
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> E get(String datasetId, Class<E> clazz) {
		return get(datasetId, 0, clazz, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> E get(String datasetId, Class<E> clazz, String filter) {
		return get(datasetId, 0, clazz, filter);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> E get(String datasetId, int rowNum, Class<E> clazz) {
		return get(datasetId, rowNum, clazz, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> E get(String datasetId, int rowNum, Class<E> clazz, String filter) {
		E bean = null;

		try {
			if (dataSetMap.get(datasetId).getRowCount() >= rowNum) {
				bean = dataSetMap.get(datasetId).getBean(clazz, rowNum, filter);
			}
		} catch (Exception e) {
		}

		/*
		 * Bean validation 체크
		 */
		if(requestValidator != null)	{
			requestValidator.checkValidation(bean);
		}

		return bean;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, ?> getMap(String datasetId) {
		return get(datasetId, 0, HashMap.class, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, ?> getMap(String datasetId, String filter) {
		return get(datasetId, 0, HashMap.class, filter);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, ?> getMap(String datasetId, int rowNum) {
		return get(datasetId, rowNum, HashMap.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Map<String, String>> getMapList(String datasetId) {
		return getMapList(datasetId, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Map<String, String>> getMapList(String datasetId, String filter) {
		DataSet dataSet = dataSetMap.get(datasetId);

		int rowCount = dataSet.getRowCount();

		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();

		for (int i = 0; i < rowCount; ++i) {
			mapList.add(dataSet.getBean(HashMap.class, i, filter));
		}

		return mapList;
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> GridData<E> getGridData(String datasetId, Class<E> clazz) {
		return new GridDataImpl<E>(dataSetMap.get(datasetId), clazz, requestValidator, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> GridData<E> getGridData(String datasetId, Class<E> clazz, String filter) {
		return new GridDataImpl<E>(dataSetMap.get(datasetId), clazz, requestValidator, filter);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getParameterNames() {
		return  Collections.unmodifiableList(new ArrayList<String>(paramMap.keySet()));
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> getParam()	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.putAll(paramMap);
		return params;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T getParam(Class<T> type) {
		return getParam(type, null);
	}


	protected final <E> E newInstance(Class<E> clazz) {
		E bean;

		try {
			bean = clazz.newInstance();
		} catch (InstantiationException e) {
			throw new MciException("InstantiationException", e);
		} catch (IllegalAccessException e) {
			throw new MciException("IllegalAccessException", e);
		}

		return bean;
	}
	/**
	 * {@inheritDoc}
	 */
	public <T> T getParam(Class<T> type, String filter) {
		final T bean = newInstance(type);
		Field[] fields = type.getDeclaredFields();

		ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(bean);

		conversionService.addConverter(new StringToDateConverter());
		conversionService.addConverter(new StringToIntConverter());
		conversionService.addConverter(new StringToBigDecimalConverter());

		propertyAccessor.setConversionService(conversionService);

		for(Field field : fields){
			Object value = null;

			if(String[].class.isAssignableFrom(field.getType())){
				value = getParamArray(field.getName());
			} else{
				value = getParam(field.getName());
			}

			if(StringUtils.hasText(filter))	{
				if(!isSelectedProperty(filter, field.getName()))	{
					continue;
				}

				if(isRequiredProperty(filter, field.getName()))	{
					if(value == null || (value instanceof String && !StringUtils.hasText((String) value)))	{
						throw new MciException("Column[" + field.getName() + "] 는 요청[" + filter + "] 에 의해 필수값으로 설정되었습니다.");
					}
				}
			}

			try {
				if (!Modifier.isStatic(field.getModifiers())) {
					if (isPrimitiveType(field.getType())) {
						propertyAccessor.setPropertyValue(field.getName(),value);
					}
				}
			} catch (Exception e) {
				throw new MciException("[AbstractMciRequest] AbstractMciRequest - " + e.getMessage(), e);
			}
		}

		/*
		 * Bean validation 체크
		 */
		if(requestValidator != null)	{
			requestValidator.checkValidation(bean);
		}

		return bean;
	}

	protected boolean isSelectedProperty(String expr, String property)	{
		boolean result = true;

		if(expr != null)	{
			result = expr.contains(property);
		}

		return result;
	}

	protected boolean isRequiredProperty(String expr, String property)	{
		return expr.contains(String.format("%s+", property));
	}

	/**
	 * {@inheritDoc}
	 */
	public String getParam(String paramName) {
		return getParam(paramName, "");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getParam(String paramName, String defaultValue) {
		Object paramValue = paramMap.get(paramName);
		String retValue = null;

		if (paramValue != null) {
			if (String[].class.isAssignableFrom(paramValue.getClass())) {
				retValue = ((String[]) paramValue)[0];
			} else
				retValue =  (String) paramValue;
		}

		return !StringUtils.hasText(retValue) ? defaultValue : retValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getParamArray(String paramName) {
		Object value = paramMap.get(paramName);

		if (value != null) {
			if(String[].class.isAssignableFrom(value.getClass()))	{
				return (String[]) value;
			} else {
				return new String[]{(String)value};
			}
		}

		return null;
	}

	public boolean isPrimitiveType(Class<?> clazz) {
		return String.class.isAssignableFrom(clazz)
			|| String[].class.isAssignableFrom(clazz)
			|| Number.class.isAssignableFrom(clazz)
			|| ClassUtils.isPrimitiveArray(clazz)
			|| ClassUtils.isPrimitiveOrWrapper(clazz)
			|| ClassUtils.isPrimitiveWrapper(clazz)
			|| ClassUtils.isPrimitiveWrapperArray(clazz);
	}

	@Deprecated
	public List<FileInfo> getAttachments() {
		return attachments;
	}

	/**
	 * {@inheritDoc}
	 */
	public void handleIfMultipart(UploadEventHandler eventHandler, MciPersistenceManager persistence) {

		if (eventHandler == null) {
			throw new MciException("UploadEventHandler 가 정의되어 있지 않습니다.");
		}

		List<FileInfo> serverFileInfos = new ArrayList<FileInfo>();

		String serverFolder = eventHandler.getFolder(MciRequestContextHolder.get().getHttpServletRequest());

		eventHandler.prepareStorage(persistence, serverFolder);

		for (FileInfo fileInfo : attachments) {
			if (fileInfo.getLength() > eventHandler.getMaxUploadSize()) {
				throw new MciException("허용 가능한 파일 크기를 초과하였습니다. - maxsize={" + eventHandler.getMaxUploadSize() + "} filesize={" + fileInfo.getLength() + "}");
			}

			String fileName = eventHandler.createFileNameIfAccepted(serverFolder, fileInfo);

			try {
				serverFileInfos.add(persistence.moveFile(fileInfo.getFolder(), fileInfo.getCallName(), serverFolder, fileName));
			} catch(Exception e){
				throw new MciException("error moving uploaded file " + fileInfo.getCallName(), e);
			} finally {
				/*
				 * 한번더 지워줘야하나... ?
				 */
				persistence.deleteFile(fileInfo.getFolder(), fileInfo.getCallName());
			}
		}

		eventHandler.postprocess(serverFolder, new MultiPartInfo(getParam(), serverFileInfos), persistence);
	}

}
