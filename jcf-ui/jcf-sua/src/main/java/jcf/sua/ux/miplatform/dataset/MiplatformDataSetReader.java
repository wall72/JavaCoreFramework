package jcf.sua.ux.miplatform.dataset;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
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
import org.springframework.util.StringUtils;

import com.tobesoft.platform.PlatformConstants;
import com.tobesoft.platform.PlatformRequest;
import com.tobesoft.platform.data.ColumnInfo;
import com.tobesoft.platform.data.Dataset;
import com.tobesoft.platform.data.DatasetList;
import com.tobesoft.platform.data.PlatformData;
import com.tobesoft.platform.data.VariableList;
import com.tobesoft.platform.data.Variant;

/**
 *
 * {@link DataSetReader}
 *
 * @author nolang
 *
 */
public class MiplatformDataSetReader implements DataSetReader {

	private Logger logger = LoggerFactory.getLogger(MiplatformDataSetReader.class);

	private Map<String, Dataset> dataSetMap = new HashMap<String, Dataset>();
	private Map<String, Object> paramMap = new HashMap<String, Object>();
	private List<FileInfo> attachments = new ArrayList<FileInfo>();
	private int requestContentType = PlatformConstants.XML;

	private FileOperator fileOperator;

	public MiplatformDataSetReader(HttpServletRequest request, FileOperator fileOperator) {
		this.fileOperator = fileOperator;

		if(fileOperator != null && fileOperator.isMultiPartRequest(request)){
			initMultiPartData(request);
		} else {
			if(ServletFileUpload.isMultipartContent(request)){
				throw new MciException("[JCF-SUA] MiplatformDataSetReader - MultiPart 요청을 처리를 위한 FileOperator가 정의되어 있지 않습니다. ");
			}

			initMiplatformData(request);
		}
	}

	private void initMultiPartData(HttpServletRequest request) {
		MultiPartInfo multiPartInfo = fileOperator.handleMultiPartRequest(request);

		if(StringUtils.hasText(request.getContentType()) && request.getContentType().contains("application/octet"))	{
			this.requestContentType = PlatformRequest.CONTENT_TYPE_BIN;
		}
		MciRequestContextHolder.get().addNamedParameter("_miplatform_request_content_type",  this.requestContentType);
		if (multiPartInfo != null) {
			attachments.addAll(multiPartInfo.getFileInfos());
			paramMap.putAll(multiPartInfo.getAttributes());
		}
	}




	private void initMiplatformData(HttpServletRequest request) {

		PlatformRequest platformRequest = null;

		try {
			platformRequest = new PlatformRequest(request, request.getCharacterEncoding());
			platformRequest.receiveData();
		}  catch (IOException e) {
			throw new MciException(null, e);
		}

		PlatformData platformData = platformRequest.getPlatformData();
		/*
		 * Xplatform DataSet 처리
		 */
		DatasetList dataSetList = platformData.getDatasetList();

		for (int i = 0; i < dataSetList.size(); ++i) {
			dataSetMap.put(dataSetList.get(i).getId(), platformData.getDataset(i));
		}

		/*
		 * Xplatform Variable 처리 (GET PARAMETER 포함)
		 */
		VariableList variableList = platformData.getVariableList();

		for (int i = 0; i < variableList.size(); ++i) {
			paramMap.put(variableList.get(i).getID(), variableList.getValueAsObject(variableList.get(i).getID()));
		}

		if(StringUtils.hasText(request.getContentType()) && request.getContentType().contains("application/octet"))	{
			this.requestContentType = PlatformRequest.CONTENT_TYPE_BIN;
		}

		MciRequestContextHolder.get().addNamedParameter("_miplatform_request_content_type", this.requestContentType );
		MciRequestContextHolder.get().addNamedParameter("_miplatform_request_charset", platformRequest.getCharset());

		logger.debug("[SUA Miplatform] Request ContentType={}", requestContentType);
		logger.debug("[SUA Miplatform] Request Charset={}", platformRequest.getCharset());
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
		Dataset dataSet = dataSetMap.get(dataSetId);
		List<DataSetRow> rows = new ArrayList<DataSetRow>();

		for (int i = 0; i < dataSet.getDeleteRowCount(); i++) {
			DataSetRow dataSetRow = new DataSetRowImpl();

			for (int j = 0; j < dataSet.getColumnCount(); j++) {
				dataSetRow.add(dataSet.getColumnID(j), getValueFromVariant(dataSet.getDeleteColumn(i, dataSet.getColumnID(j)), dataSet.getColumnInfo(j).getColumnType()));
			}

			dataSetRow.setRowStatus(RowStatus.DELETE.name());

			rows.add(dataSetRow);
		}

		for (int i = 0; i < dataSet.getRowCount(); i++) {
			DataSetRow dataSetRow = new DataSetRowImpl();

			for (int j = 0; j < dataSet.getColumnCount(); j++) {
				dataSetRow.add(dataSet.getColumnID(j), getValueFromVariant(dataSet.getColumn(i, dataSet.getColumnID(j)),  dataSet.getColumnInfo(j).getColumnType()));
			}

			dataSetRow.setRowStatus(getStatusOf(dataSet.getRowType(i)));
			// OrgData  추가
			if (dataSet.getRowType(i) == Dataset.ROWTYPE_UPDATE) {
				DataSetRow savedDataSetRow = null;

				if(i <= dataSet.getUpdateRowCount())	{
					savedDataSetRow = new DataSetRowImpl();

					for (int k = 0; k < dataSet.getColumnCount(); k++) {
						savedDataSetRow.add(dataSet.getColumnID(k), getValueFromVariant(dataSet.getOriginalColumn(i, dataSet.getColumnID(k)),  dataSet.getColumnInfo(k).getColumnType()));
					}
				}

				dataSetRow.setOrgDataSetRow(savedDataSetRow);
			}

			rows.add(dataSetRow);
		}

		return rows;
	}

	/**
	 *
	 * <p>
	 * Variant 에서 Value 추출

	 * </p>
	 * @param variant
	 * @param columnType
	 * @return
	 */
	protected Object getValueFromVariant(Variant variant, int columnType) {
		Object variantValue = null;
		if( ColumnInfo.COLTYPE_INT == columnType ){
			if(StringUtils.hasLength( variant.toString())){
				variantValue = new Integer(Double.valueOf(variant.toString()).intValue());
			}
		}
		else if( ColumnInfo.COLTYPE_DECIMAL == columnType ){
			if(StringUtils.hasLength( variant.toString())){
				variantValue = Double.valueOf(variant.toString());
			}
		}

		if( ColumnInfo.COLTYPE_DATE == columnType ){
			variantValue = variant.getDate();
		}
		else if( ColumnInfo.COLTYPE_BLOB == columnType ){
			variantValue = variant.getBinary();
		}
		else{
			variantValue = variant.toString();
		}

		return variantValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetColumn> getDataSetColumns(String dataSetId) {
		Dataset dataSet = dataSetMap.get(dataSetId);
		List<DataSetColumn> cols = new ArrayList<DataSetColumn>();

		for (int i = 0; i < dataSet.getColumnCount(); i++) {
			cols.add(new DataSetColumn(dataSet.getColumnID(i) , getJavaType(dataSet.getColumnInfo(i).getColumnType())));
		}

		return cols;
	}


	/**
	 * <p>
	 * miplatform 의 데이터 셋의 rowstatus 추출함( getDataSetRows 메소드에서 호출됨)
	 *</p>
	 * @param jobType
	 * @return
	 */
	private String getStatusOf(int jobType) {
		RowStatus rowStatus = null;

		switch (jobType) {
		case Dataset.ROWTYPE_INSERT:
			rowStatus = RowStatus.INSERT;
			break;
		case Dataset.ROWTYPE_UPDATE:
			rowStatus = RowStatus.UPDATE;
			break;
		case Dataset.ROWTYPE_DELETE:
			rowStatus = RowStatus.DELETE;
			break;
		default:
			rowStatus = RowStatus.NORMAL;
		}

		return rowStatus.name();
	}

	/**
	 * 받아온 miplatform 데이터의 타입과 맵핑되는 JavaType 을 리턴
	 *
	 * @param colType
	 * @return
	 */
	private Class<?> getJavaType(int colType) {

		Class<?> javaType = null;
		switch (colType) {
		case ColumnInfo.COLUMN_TYPE_STRING:
			javaType = String.class;
			break;
		case ColumnInfo.COLUMN_TYPE_INT:
			javaType = Integer.class;
			break;
		case ColumnInfo.COLUMN_TYPE_LONG:
			javaType = Long.class;
			break;
		case ColumnInfo.COLUMN_TYPE_DECIMAL:
			javaType = BigDecimal.class;
			break;
		case ColumnInfo.COLUMN_TYPE_DATE:
			javaType = Date.class;
			break;
		case ColumnInfo.COLUMN_TYPE_BLOB:
			javaType = Blob.class;
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
