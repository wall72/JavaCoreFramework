package jcf.sua.ux.xplatform.dataset;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.exception.MciException;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.MciRequestContextHolder;
import jcf.sua.mvc.file.operator.FileOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.tobesoft.xplatform.data.DataSetList;
import com.tobesoft.xplatform.data.DataTypes;
import com.tobesoft.xplatform.data.PlatformData;
import com.tobesoft.xplatform.data.VariableList;
import com.tobesoft.xplatform.tx.HttpPlatformResponse;
import com.tobesoft.xplatform.tx.PlatformException;
import com.tobesoft.xplatform.tx.PlatformResponse;
import com.tobesoft.xplatform.tx.PlatformType;

/**
 *
 * {@link DataSetWriter}의 Xplatform 구현체
 * @author mina
 *
 */

public class XplatformDataSetWriter implements DataSetWriter {

	private Logger logger = LoggerFactory.getLogger(XplatformDataSetWriter.class);

	public static final String ERROR_CODE_PARAMETER_NAME = "ErrorCode";
	public static final String ERROR_MSG_PARAMETER_NAME = "ErrorMsg";

	private HttpServletRequest request;
	private HttpServletResponse response;
	private MciDataSetAccessor accessor;
	private FileOperator fileOperator;

	/**
	 * 생성자 생성
	 *
	 * @param response
	 * @param fileOperator
	 * @param accessor
	 */
	public XplatformDataSetWriter(HttpServletRequest request, HttpServletResponse response, FileOperator fileOperator, MciDataSetAccessor accessor) {
		this.request = request;
		this.response = response;
		this.accessor = accessor;
		this.fileOperator = fileOperator;
	}

	/**
	 * {@inheritDoc}
	 */
	public void write() {
		/**
		 * TODO 파일다운로드 구현..
		 */
		if(accessor != null && accessor.isFileProcessing()){
			fileOperator.sendFileStream(request, response, accessor.getDownloadFile());
			return;
		}

		PlatformResponse platformResponse = new HttpPlatformResponse(response, getRequetContentType(), getRequetCharset());

		String protocolType = getRequestProtocolType();

		if(StringUtils.hasText(protocolType))	{
			platformResponse.addProtocolType(protocolType);
		}

		PlatformData platformData = new PlatformData();

		platformData.setDataSetList(getDataSetList()); // DataSetList 를 받아와  platformData 에 셋팅
		platformData.setVariableList(getVariableList()); // VariableList 를 받아와  platformData에 셋팅(에러 및 메서지)

		platformResponse.setData(platformData); // platformData PlatformResponse에 보냄

		try {
			platformResponse.sendData();
		} catch (PlatformException e) {
			throw new MciException(e);
		}
	}

	private String getRequestProtocolType() {
		String protocolType = (String) MciRequestContextHolder.get().getNamedParameter("_xplatform_request_protocol_type_zlib");

		logger.debug("[SUA XPlatform] Response ProtocolType={}", protocolType);

		return protocolType;
	}

	private String getRequetContentType()	{
		String requestContentType = (String) MciRequestContextHolder.get().getNamedParameter("_xplatform_request_content_type");

		if(!StringUtils.hasText(requestContentType))	{
			requestContentType = PlatformType.CONTENT_TYPE_XML;
		}

		logger.debug("[SUA XPlatform] Response ContentType={}", requestContentType);

		return requestContentType;
	}

	private String getRequetCharset()	{
		String requestCharset = (String) MciRequestContextHolder.get().getNamedParameter("_xplatform_request_charset");

		if(!StringUtils.hasText(requestCharset))	{
			requestCharset = "utf-8";
		}

		logger.debug("[SUA XPlatform] Response Charset={}", requestCharset);

		return requestCharset;
	}

	private DataSetList getDataSetList() {
		Map<String, DataSet> dataSetMap = accessor.getDataSetMap();

		/**
		 * dataSetMap 을 루프를 돌면서 xplatform의 DataSet에 담음
		 */
		DataSetList datasetList = new DataSetList();

		Iterator<String> it = dataSetMap.keySet().iterator();

		while (it.hasNext()) {
			com.tobesoft.xplatform.data.DataSet xplatformDataSet = createXplatformDataSet(dataSetMap.get(it.next()));
			datasetList.add(xplatformDataSet);

		}

		return datasetList;
	}

	/**
	 * 에러 및 메시지 variableList 에 셋팅
	 *
	 * @param variableList
	 * @return
	 */
	private VariableList getVariableList() {
		VariableList variableList = new VariableList();

		List<String> successMessages = accessor.getSuccessMessags();

		/**
		 * 에러도 없고 따로 설정한 메시지가 없으면 코드는 0 메시지는 SUCCESS 에러가 없고 따로 설정한 메시지가 있으면 코드는 0 에러 발생 시 코드는 -1, 메시지는 에러내용
		 */
		if (successMessages.size() == 0 && !StringUtils.hasText(accessor.getExceptionMessage())) {
			variableList.add(ERROR_CODE_PARAMETER_NAME, 0);
			variableList.add(ERROR_MSG_PARAMETER_NAME, "SUCCESS");
		} else {
			for (String message : successMessages) {
				variableList.add(ERROR_CODE_PARAMETER_NAME, 0);
				variableList.add(ERROR_MSG_PARAMETER_NAME, message);
			}

			if(StringUtils.hasText(accessor.getExceptionMessage())) {
				variableList.add(ERROR_CODE_PARAMETER_NAME, -1);
				variableList.add(ERROR_MSG_PARAMETER_NAME, accessor.getExceptionMessage());
			}
		}

        // dataset이 아닌 변수를 넘기기 위한 처리
		Map<String, String> paramData = accessor.getParams();

		int max = paramData.size();
		Set<String> keySet = paramData.keySet();
		Iterator<String> keys = keySet.iterator();
		Object value;
		String id;

		for (int i = 0; i < max; i++) {
			id = (String) keys.next();

			value = paramData.get(id);
			if (value instanceof String) {
				variableList.add(id, (String) value);
			} else if (value instanceof Integer) {
				variableList.add(id, (Integer) value);
			} else if (value instanceof Double) {
				variableList.add(id, (Double) value);
			} else if (value instanceof byte[]) {
				variableList.add(id, (byte[]) value);
			} else if (value instanceof Date) {
				variableList.add(id, (Date) value);
			} else if (value instanceof Currency) {
				variableList.add(id, (Currency) value);
			} else {
				variableList.add(id, (String) value);
			}
		}

		return variableList;
	}

	/**
	 * dataSetMap 을 가지고 XplatformDataSet 을 만듬 (getDataSetList 에서 호출 )
	 *
	 * @param dataSet
	 * @return
	 */
	private com.tobesoft.xplatform.data.DataSet createXplatformDataSet(DataSet dataSet) {
		com.tobesoft.xplatform.data.DataSet xplatformDataSet = new com.tobesoft.xplatform.data.DataSet(dataSet.getId());

		int rowCount = dataSet.getRowCount();
		int columnCount = dataSet.getColumnCount();

		/*
		 * xplatform 의 DataSet에 컬럼 타입 셋팅
		 */
		for (int col = 0; col < columnCount; ++col) {
			String columnName = dataSet.getDataSetColumn(col).getColumnName();
			Class<?> columnType = dataSet.getDataSetColumn(col).getColumnType();

			xplatformDataSet.addColumn(columnName, getXPlatformType(columnType));
		}

		int rownum = 0;

		/**
		 * platform 의 DataSet에 데이터 셋팅
		 */
		for (int row = 0; row < rowCount; ++row) {
			DataSetRow dataSetRow = dataSet.getDataSetRow(row);
			rownum = xplatformDataSet.newRow();

			for (int col = 0; col < columnCount; ++col) {
				String columnName = dataSet.getDataSetColumn(col)
						.getColumnName();
				Object colunmValue = dataSetRow.get(columnName);

				xplatformDataSet.set(rownum, columnName, colunmValue);

			}
		}

		return xplatformDataSet;
	}

	/**
	 * dataSetMap 안의 데이터의 타입과 맵핑되는 xplatformType 을 리턴
	 *
	 * @param colType
	 * @return
	 */
	private int getXPlatformType(Class<?> clazz) {
		int xplatformType = DataTypes.BLOB;

		if (clazz.isAssignableFrom(String.class)) {
			xplatformType = DataTypes.STRING;
		} else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
			xplatformType = DataTypes.INT;
		} else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
			xplatformType = DataTypes.LONG;
		} else if (clazz.isAssignableFrom(Double.class)	|| clazz.isAssignableFrom(double.class) || clazz.isAssignableFrom(BigDecimal.class)) {
			xplatformType = DataTypes.BIG_DECIMAL;
		} else if (clazz.isAssignableFrom(Date.class)) {
			xplatformType = DataTypes.DATE;
		}

		return xplatformType;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDataSetAccessor(MciDataSetAccessor accessor) {
		this.accessor = accessor;
	}
}
