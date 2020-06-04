package jcf.sua.ux.miplatform.dataset;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.exception.MciException;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.MciRequestContextHolder;
import jcf.sua.mvc.file.operator.FileOperator;
import jcf.sua.ux.UxConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.tobesoft.platform.PlatformResponse;
import com.tobesoft.platform.data.ColumnInfo;
import com.tobesoft.platform.data.Dataset;
import com.tobesoft.platform.data.DatasetList;
import com.tobesoft.platform.data.PlatformData;
import com.tobesoft.platform.data.VariableList;
import com.tobesoft.platform.data.Variant;


/**
 *<p>
 *   데이터를 Miplatform 형태로  response에 보낸다.
 *</p>
 * @author mina
 *
 */
public class MiplatformDataSetWriter implements DataSetWriter {

	private static final Logger logger = LoggerFactory.getLogger(MiplatformDataSetWriter.class);

	private HttpServletRequest request;
	private HttpServletResponse response;
	private MciDataSetAccessor accessor;
	private FileOperator fileOperator;

	private Map<String, DataSet> dataSetMap; // Xplatform DataSet 담는 Map


	/**
	 *
	 *<p>
	 * MiplatformDataSetWriter 생성자 <br/>
	 * MciDataSetAccessor 으로 부터  MciData  받아와  dataSetMap 에 넣는다.<br/>
	 * HttpServletResponse  셋팅한다.
	 *</p>
	 * @param response
	 * @param accessor
	 */
	public MiplatformDataSetWriter(HttpServletRequest request, HttpServletResponse response, FileOperator fileOperator, MciDataSetAccessor accessor) {
		this.accessor = accessor;
		this.request = request;
		this.response =  response;
		this.fileOperator = fileOperator;
	}
	/*
	 * <p>
	 * 서버에서 조회한 데이터인 dataSetMap 를   Miplatform 의 platformData 으로 만들어 UI 에 전달한다.
	 * </p>
	 */
	public void write()	{
		/**
		 * TODO 파일다운로드 구현..
		 */
		if(accessor != null && accessor.isFileProcessing()){
			fileOperator.sendFileStream(request, response, accessor.getDownloadFile());
			return;
		}

		this.dataSetMap = accessor.getDataSetMap(); // 조회한 데이터를 dataSetMap에 받음

		PlatformResponse platformResponse;

		try {
			platformResponse = new PlatformResponse(this.response, getRequetContentType(), getRequetCharset());

			// RD 등의 연계로 인해서 Chunked 상태로 응답을 보내면 안되는 경우에만, 처리해 준다.
			// 요청 시 MiResponse.RESPONSE_CHUNKED_PARAM 파라미터가 "false" 인 경우에만 Chunked를 false로 처리한다.
			String chunkedParam =	request.getParameter("_chunked_enable");

			if("false".equalsIgnoreCase(chunkedParam) ){
				platformResponse.setBase64Chunked(false);
			}

			PlatformData platformData = new PlatformData();

			platformData.setDatasetList(getDataSetList()); // DataSetList 를 받아와  platformData 에 셋팅
			platformData.setVariableList(getVariableList()); // VariableList 를 받아와  platformData에 셋팅(에러 및 메서지)

			platformResponse.sendData(platformData);

			logger.debug(platformData.toString());

		} catch (IOException e) {
			throw new MciException(e);
		}
	}

	private int getRequetContentType()	{
		int requestContentType = (Integer) MciRequestContextHolder.get().getNamedParameter("_miplatform_request_content_type");

		logger.debug("[SUA Miplatform] Response ContentType={}", requestContentType);

		return requestContentType;
	}

	private String getRequetCharset()	{
		String requestCharset = (String) MciRequestContextHolder.get().getNamedParameter("_miplatform_request_charset");

		if(!StringUtils.hasText(requestCharset))	{
			requestCharset = "utf-8";
		}

		logger.debug("[SUA Miplatform] Response Charset={}", requestCharset);

		return requestCharset;
	}

	/**
	 * dataSetMap 을 루프를 돌면서 Mipliatform 의 DataSet에 담음
	 */
	private DatasetList getDataSetList() {
		DatasetList datasetList = new DatasetList();
		Iterator<String> it = dataSetMap.keySet().iterator();
		while (it.hasNext()) {
			Dataset dataset = createMiplatformDataSet(dataSetMap.get(it.next()));
			datasetList.add(dataset);
		}
		return datasetList;
	}


	/**
	 * 	<p>
	 * 에러 및 메시지 variableList 에 셋팅한다.
	 * 기본은 에러코드는 O, 에러메시지는 SUCCESS
	 * </P>
	 * @param variableList
	 * @return
	 */
	public VariableList getVariableList() {
		VariableList variableList = new VariableList();

		List<String> successMessages = accessor.getSuccessMessags();

		Map<String, String> params = accessor.getParams();

		// 파라메터 받음
		Iterator<String> iter = params.keySet().iterator();

		while (iter.hasNext()) {
			String key = iter.next();
			String value = params.get(key);

			variableList.add(key, value);
		}

		/**
		 * 에러도 없고 따로 설정한 메시지가 없으면 코드는 0 메시지는 SUCCESS 에러가 없고 따로 설정한 메시지가 있으면 코드는 0 에러 발생 시 코드는 -1, 메시지는 에러내용
		 */
		if (successMessages.size() == 0 && !StringUtils.hasText(accessor.getExceptionMessage())) {
			variableList.add(UxConstants.ERROR_CODE_PARAMETER_NAME, UxConstants.ERROR_CODE_SUCC_VALUE);
			variableList.add(UxConstants.ERROR_MSG_PARAMETER_NAME, UxConstants.ERROR_MSG_DEFULT_VALUE);
		}

		for (String message : successMessages) {
			variableList.add(UxConstants.ERROR_CODE_PARAMETER_NAME, UxConstants.ERROR_CODE_SUCC_VALUE);
			variableList.add(UxConstants.ERROR_MSG_PARAMETER_NAME, message);
		}

		if (StringUtils.hasText(accessor.getExceptionMessage())) {
			variableList.add(UxConstants.ERROR_CODE_PARAMETER_NAME, UxConstants.ERROR_CODE_FAIL_VALUE);
			variableList.add(UxConstants.ERROR_MSG_PARAMETER_NAME, accessor.getExceptionMessage());
		}


		return variableList;
	}

	/**
	 * <p>
	 *  dataSetMap 을 가지고 Miplatform DataSet 을 만든다.
	 * </P>
	 *
	 *
	 * @param dataSet MciDataSet
	 * @return miplatformDataSet
	 */
	private Dataset createMiplatformDataSet(
			DataSet dataSet) {
		Dataset miplatformDataSet = new Dataset(dataSet.getId());
		int rowCount = dataSet.getRowCount();  //데이터 갯수
		int columnCount = dataSet.getColumnCount();  //컬럼 갯수
		/*
		 * xplatform 의 DataSet에 컬럼 타입 셋팅
		 */
		for (int col = 0; col < columnCount; ++col) {

			Class<?> columnType = dataSet.getDataSetColumn(col).getColumnType();
			miplatformDataSet.addColumn(dataSet.getDataSetColumn(col).getColumnName(), getMiPlatformType(columnType), getMiPlatformColSize(columnType));
		}
		/**
		 * platform 의 DataSet에 데이터 셋팅
		 */
		for (int row = 0; row < rowCount; ++row) {
			DataSetRow dataSetRow = dataSet.getDataSetRow(row);
			miplatformDataSet.appendRow();
			for (int col = 0; col < columnCount; ++col) {
				Object colunmValue = dataSetRow.get(dataSet.getDataSetColumn(col).getColumnName());
				Variant valueVariant = new Variant();
				valueVariant.setType( getMiPlatformType(dataSet.getDataSetColumn(col).getColumnType()));
				valueVariant.setObject(colunmValue);

				miplatformDataSet.setColumn(row, dataSet.getDataSetColumn(col).getColumnName(),valueVariant);

			}
		}

		return miplatformDataSet;
	}

	private short getMiPlatformColSize(Class<?> clazz){
		short miplatformColSize = 255;
		if (clazz.isAssignableFrom(byte[].class)) {
			miplatformColSize=20000;
		}
		return miplatformColSize;
	}
	/**
	 * dataSetMap 안의 데이터의 타입과 맵핑되는MiPlatform Type 을 리턴한다.
	 * @param colType
	 * @return
	 */
	private short getMiPlatformType(Class<?> clazz) {
		short miplatformType = ColumnInfo.COLUMN_TYPE_BLOB;
		if (clazz.isAssignableFrom(String.class)) {
			miplatformType =ColumnInfo.COLUMN_TYPE_STRING;
		} else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
			miplatformType = ColumnInfo.COLUMN_TYPE_INT;
		} else if (clazz.isAssignableFrom(BigDecimal.class) || clazz.isAssignableFrom(Double.class)||clazz.isAssignableFrom(double.class)) {
			miplatformType = ColumnInfo.COLUMN_TYPE_DECIMAL;
		} else if (clazz.isAssignableFrom(Long.class)||clazz.isAssignableFrom(long.class)) {
			miplatformType = ColumnInfo.COLUMN_TYPE_LONG;
		} else if (clazz.isAssignableFrom(Date.class) || clazz.isAssignableFrom(Timestamp.class)) {
			miplatformType = ColumnInfo.COLUMN_TYPE_DATE;
		}
		return miplatformType;
	}

	public void setDataSetAccessor(MciDataSetAccessor accessor) {
		this.accessor = accessor;
	}
}
