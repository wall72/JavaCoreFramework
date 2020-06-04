package jcf.sua.ux.mybuilder.dataset;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.exception.MciException;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.file.operator.FileOperator;
import jcf.sua.ux.mybuilder.MybuilderConstants;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

/**
 * {@link DataSetWriter}
 *
 * @author Jeado
 *
 */
public class MybuilderDataSetWriter implements DataSetWriter {

	private static final Logger logger = LoggerFactory.getLogger(MybuilderDataSetWriter.class);

	private HttpServletRequest request;
	private HttpServletResponse response;
	private MciDataSetAccessor accessor;
	private FileOperator fileOperator;

	private String charset = "utf-8";
	private String contentType = "text/html; charset=utf-8";

	public MybuilderDataSetWriter(HttpServletRequest request, HttpServletResponse response,
			MciDataSetAccessor accessor, FileOperator fileOperator) {
		this.response = response;
		this.accessor = accessor;
		this.fileOperator = fileOperator;
	}

	/**
	 * {@inheritDoc}
	 */
	public void write() {
		if(accessor != null && accessor.isFileProcessing()){
			logger.debug("send file...");
			fileOperator.sendFileStream(request, response, accessor.getDownloadFile());
			return;
		}

		response.setContentType(contentType);
		response.setCharacterEncoding(charset);

		try {
			response.getWriter().write(buildResponseStringData(accessor.getDataSetMap(),accessor.getExceptionMessage(),accessor.getSuccessMessags(),accessor.getParams()));
		} catch (Exception e) {
			throw new MciException("[Mybuilder DataSetWriter] write - "
					+ e.getMessage(), e);
		}
	}

	private String buildResponseStringData(Map<String, DataSet> dataSetMap,
			String exceptions, List<String> successMessags, Map<String, String> paramMap) {
		StringBuilder sb = new StringBuilder();

		if(exceptions != null){
			//예외처리
			sb.append(exceptions);
		}else if(!successMessags.isEmpty()){
			//성공메시지 전달. 단 dataset은 전달하지 않고 성공메시지가 있으면 무조건 성공메시지만 보내야 된다.
			for (String string : successMessags) {
				sb.append(string);
			}
		}else if(dataSetMap.size() != 0){
			String dataSetId = dataSetMap.keySet().iterator().next();
			DataSet dataSet = dataSetMap.get(dataSetId);
			appendDatasetToString(sb, dataSet);
		}else{
			//파라미터로 보내는 경우이다.
			if(!paramMap.isEmpty()){
				for (Entry<String, String> entry : paramMap.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					sb.append(key);
					sb.append(MybuilderConstants.MSV_LINE_SEP);
					sb.append(value);
					sb.append(MybuilderConstants.MSV_LINE_SEP);
				}
			}
		}
		return sb.toString();
	}

	private void appendDatasetToString(StringBuilder sb, DataSet dataset) {
		// Seperator Value를 만들기 위해 컬럼 이름들을 우선 작성한다.
		for (int i = 0; i < dataset.getColumnCount(); i++) {
			DataSetColumn dataSetColumn = dataset.getDataSetColumn(i);
			sb.append(dataSetColumn.getColumnName());
			if( isNumeralType(dataSetColumn.getColumnType()) ) {
				sb.append("#"); // 숫자 컬럼은 컬럼명뒤에 '#'을 붙여준다.
			}
			if(dataset.getColumnCount() != 1) sb.append(MybuilderConstants.MSV_COL_SEP);
		}
		sb.append(MybuilderConstants.MSV_LINE_SEP);
		for (int i = 0; i < dataset.getRowCount(); i++) {
			DataSetRow dataSetRow = dataset.getDataSetRow(i);
			for (int j = 0; j < dataset.getColumnCount(); j++) {
				Object valueObj = dataSetRow.get(dataset.getDataSetColumn(j).getColumnName());
				appendPropertyToStringBuilder(sb, valueObj);
				if(dataset.getColumnCount() != 1) sb.append(MybuilderConstants.MSV_COL_SEP);
			}
			sb.append(MybuilderConstants.MSV_LINE_SEP);
		}
	}

	private void appendPropertyToStringBuilder(StringBuilder sb, Object value) {
		if( value instanceof String) {
			if( value == null ) value = "";
			sb.append(value);
		}else if( value instanceof Date){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MybuilderConstants.DEFAULT_DATE_FORMAT);
			sb.append(simpleDateFormat.format((Date)value));
		}else if( value instanceof Byte[]){
			if( null == value ) sb.append("");
			else sb.append(Base64.encodeBase64((byte[])value));
		}else{
			if( null == value ) value = "";
			sb.append(String.valueOf(value));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDataSetAccessor(MciDataSetAccessor accessor) {
		this.accessor = accessor;
	}

	private boolean isNumeralType(Class<?> type){
		return ClassUtils.isAssignable(type, Integer.class) ||
				ClassUtils.isAssignable(type, Double.class) ||
				ClassUtils.isAssignable(type, Float.class)	||
				ClassUtils.isAssignable(type, BigDecimal.class) ||
				ClassUtils.isAssignable(type, BigInteger.class)	||
				ClassUtils.isAssignable(type, Number.class) ||
				ClassUtils.isAssignable(type, Long.class) ||
				ClassUtils.isAssignable(type, Short.class);
	}
}
