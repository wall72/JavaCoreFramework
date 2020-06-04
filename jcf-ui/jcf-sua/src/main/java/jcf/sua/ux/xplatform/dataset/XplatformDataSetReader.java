package jcf.sua.ux.xplatform.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jcf.data.RowStatus;
import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.dataset.DataSetRowImpl;
import jcf.sua.exception.MciException;
import jcf.sua.mvc.MciRequestContextHolder;
import jcf.sua.mvc.file.operator.FileOperator;
import jcf.upload.FileInfo;
import jcf.upload.MultiPartInfo;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tobesoft.xplatform.data.DataSet;
import com.tobesoft.xplatform.data.DataSetList;
import com.tobesoft.xplatform.data.DataTypes;
import com.tobesoft.xplatform.data.Debugger;
import com.tobesoft.xplatform.data.PlatformData;
import com.tobesoft.xplatform.data.VariableList;
import com.tobesoft.xplatform.tx.HttpPlatformRequest;
import com.tobesoft.xplatform.tx.PlatformException;
import com.tobesoft.xplatform.tx.PlatformRequest;
import com.tobesoft.xplatform.tx.PlatformType;

/**
 *
 * {@link DataSetReader} 의 Xplatform 구현체
 *
 * @author nolang
 *
 */
public class XplatformDataSetReader implements DataSetReader {

	private Logger logger = LoggerFactory.getLogger(XplatformDataSetReader.class);

	/**
	 * ---------------------------------------------------------------------------------------------------------------------
	 * |            속성명                            |  데이터 형식   |    유효한 값    |  기본값   |                   설 명                                                |
	 * ---------------------------------------------------------------------------------------------------------------------
	 * |  http.getparameter.register   |   String   |  true/false |  false | 데이터 수신시 HTTP GET 데이터의 등록 여부                         |
	 * ---------------------------------------------------------------------------------------------------------------------
     * |  http.getparameter.asvariable |   String   |  true/false |  false | HTTP GET 데이터 등록시 Variable 형식으로의 변환 여부, |
     * |                               |            |             |        | false인 경우 HTTP GET 데이터는 DataSet 형식으로 변환  |
     * ---------------------------------------------------------------------------------------------------------------------
	 */
	private static final String HTTP_GETPARAMETER_REGISTER = "http.getparameter.register";
	private static final String HTTP_GETPARAMETER_ASVARIABLE = "http.getparameter.asvariable";

	private Map<String, DataSet> dataSetMap = new HashMap<String, DataSet>();
	private Map<String, Object> paramMap = new HashMap<String, Object>();
	private List<FileInfo> attachments = new ArrayList<FileInfo>();

	private FileOperator fileOperator;

	public XplatformDataSetReader(HttpServletRequest request, FileOperator fileOperator) {
		this.fileOperator = fileOperator;

		if(fileOperator != null && fileOperator.isMultiPartRequest(request)){
			initMultiPartData(request);
		} else {
			if(ServletFileUpload.isMultipartContent(request)){
				throw new MciException("[JCF-SUA] XplatformDataSetReader - MultiPart 요청을 처리를 위한 FileOperator가 정의되어 있지 않습니다. ");
			}

			initXplatformData(request);
		}
	}

	private void initMultiPartData(HttpServletRequest request) {
		MultiPartInfo multiPartInfo = fileOperator.handleMultiPartRequest(request);

		if (multiPartInfo != null) {
			attachments.addAll(multiPartInfo.getFileInfos());
			paramMap.putAll(multiPartInfo.getAttributes());
		}
	}

	private void initXplatformData(HttpServletRequest request) {
		PlatformRequest platformRequest = new HttpPlatformRequest(request);

		platformRequest.setProperty(HTTP_GETPARAMETER_REGISTER, Boolean.toString(true));
		platformRequest.setProperty(HTTP_GETPARAMETER_ASVARIABLE, Boolean.toString(true));

		try {
			/*
			 * HTTP 요청으로부터 데이터(PlatformData)를 수신받는다. 송수신 형식(contentType)이 설정되지 않은 경우 HTTP 헤더의 ContentType 값으로부터 판단하며, 다음과 같이 적용된다.
			 *
			 * - HTTP 헤더의 ContentType                적용되는 송수신 형식(contentType)
			 *    text/xml                    ->    PlatformType.CONTENT_TYPE_XML
			 *    application/octet-stream    ->    PlatformType.CONTENT_TYPE_BINARY
			 *    그외..                       ->    PlatformType.DEFAULT_CONTENT_TYPE
			 */

			platformRequest.receiveData();

			logger.debug("[SUA XPlatform] ContentType={}", platformRequest.getContentType());

		} catch (PlatformException e) {
			throw new MciException(null, e);
		}

		PlatformData platformData = platformRequest.getData();

		if (logger.isDebugEnabled()) {
			Debugger debugger = new Debugger();
			logger.debug("[SUA XPlatform] PlatformData is " + debugger.detail(platformData));
		}

		/*
		 * Xplatform DataSet 처리
		 */
		DataSetList dataSetList = platformData.getDataSetList();

		for (int i = 0; i < dataSetList.size(); ++i) {
			dataSetMap.put(dataSetList.get(i).getName(), platformData.getDataSet(i));
		}

		/*
		 * Xplatform Variable 처리 (GET PARAMETER 포함)
		 */
		VariableList variableList = platformData.getVariableList();

		for (int i = 0; i < variableList.size(); ++i) {
			paramMap.put(variableList.get(i).getName(), variableList.getObject(i));
		}

		MciRequestContextHolder.get().addNamedParameter("_xplatform_request_content_type", platformRequest.getContentType());
		MciRequestContextHolder.get().addNamedParameter("_xplatform_request_charset", platformRequest.getCharset());

		if(platformRequest.containsProtocolType(PlatformType.PROTOCOL_TYPE_ZLIB))	{
			MciRequestContextHolder.get().addNamedParameter("_xplatform_request_protocol_type_zlib", PlatformType.PROTOCOL_TYPE_ZLIB);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> getParamMap() {
		return paramMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getDataSetIdList() {
		return Collections.unmodifiableList(new ArrayList<String>(dataSetMap.keySet()));
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetRow> getDataSetRows(String dataSetId) {
		DataSet dataSet = dataSetMap.get(dataSetId);
		List<DataSetRow> rows = new ArrayList<DataSetRow>();

		for (int i = 0; i < dataSet.getRemovedRowCount(); i++) {
			DataSetRow dataSetRow = new DataSetRowImpl();

			for (int j = 0; j < dataSet.getColumnCount(); j++) {
				dataSetRow.add(dataSet.getColumn(j).getName(), dataSet.getRemovedData(i, dataSet.getColumn(j).getName()));
			}

			dataSetRow.setRowStatus(RowStatus.DELETE.name());

			rows.add(dataSetRow);
		}

		for (int i = 0; i < dataSet.getRowCount(); i++) {
			DataSetRow dataSetRow = new DataSetRowImpl();

			for (int j = 0; j < dataSet.getColumnCount(); j++) {
				dataSetRow.add(dataSet.getColumn(j).getName(), dataSet.getObject(i, dataSet.getColumn(j).getName()));
			}

			dataSetRow.setRowStatus(getStatusOf(dataSet.getRowType(i)));

			if (dataSet.getRowType(i) == DataSet.ROW_TYPE_UPDATED) {
				DataSetRow savedDataSetRow = null;

				if(i <= dataSet.getSavedRowCount())	{
					savedDataSetRow = new DataSetRowImpl();

					for (int k = 0; k < dataSet.getColumnCount(); k++) {
						savedDataSetRow.add(dataSet.getColumn(k).getName(), dataSet.getSavedData(i, dataSet.getColumn(k).getName()));
					}
				}

				dataSetRow.setOrgDataSetRow(savedDataSetRow);
			}

			rows.add(dataSetRow);
		}

		return rows;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetColumn> getDataSetColumns(String dataSetId) {
		DataSet dataSet = dataSetMap.get(dataSetId);
		List<DataSetColumn> cols = new ArrayList<DataSetColumn>();

		for (int i = 0; i < dataSet.getColumnCount(); i++) {
			cols.add(new DataSetColumn(dataSet.getColumn(i).getName(), getJavaType(dataSet.getColumnDataType(i))));
		}

		return cols;
	}


	/**
	 * xplatform 의 데이터 셋의 rowstatus 추출함( getDataSetRows 메소드에서 호출됨)
	 *
	 * @param jobType
	 * @return
	 */
	private String getStatusOf(int jobType) {
		RowStatus rowStatus = null;

		switch (jobType) {
			case DataSet.ROW_TYPE_INSERTED:
				rowStatus = RowStatus.INSERT;
				break;
			case DataSet.ROW_TYPE_UPDATED:
				rowStatus = RowStatus.UPDATE;
				break;
			case DataSet.ROW_TYPE_DELETED:
				rowStatus = RowStatus.DELETE;
				break;
			default:
				rowStatus = RowStatus.NORMAL;
		}

		return rowStatus.name();
	}

	/**
	 * 받아온 xplatform 데이터의 타입과 맵핑되는 JavaType 을 리턴
	 *
	 * @param colType
	 * @return
	 */
	private Class<?> getJavaType(int colType) {

		Class<?> javaType = null;

		switch (colType) {
			case DataTypes.STRING:
				javaType = String.class;
				break;
			case DataTypes.INT:
				javaType = Integer.class;
				break;
			case DataTypes.LONG:
				javaType = Long.class;
				break;
			case DataTypes.BIG_DECIMAL:
				javaType = Double.class;
				break;
			case DataTypes.DATE:
				javaType = Date.class;
				break;
			default:
				javaType = Object.class;
		}

		return javaType;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<FileInfo> getAttachments() {
		return attachments;
	}
}
