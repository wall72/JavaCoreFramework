package jcf.sua.ux.mybuilder.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import jcf.data.RowStatus;
import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.dataset.DataSetRowImpl;
import jcf.sua.mvc.file.operator.FileOperator;
import jcf.sua.ux.UxConstants;
import jcf.sua.ux.mybuilder.MybuilderConstants;
import jcf.upload.FileInfo;
import jcf.upload.MultiPartInfo;

import org.springframework.util.StringUtils;

/**
 *
 * {@link DataSetReader}
 *
 * @author Jeado
 *
 */
public class MybuilderDataSetReader implements DataSetReader {

	private Map<String,Object> parameterMap = new HashMap<String, Object>();
	private Map<String,String> rowStatusMapper = new HashMap<String, String>();
	private List<FileInfo> attachments = new ArrayList<FileInfo>();

	public MybuilderDataSetReader(HttpServletRequest request, FileOperator fileOperator) {
		if(fileOperator != null && fileOperator.isMultiPartRequest(request))	{
			MultiPartInfo info = fileOperator.handleMultiPartRequest(request);
			attachments.addAll(info.getFileInfos());
			parameterMap = info.getAttributes();
		}else{
			@SuppressWarnings("unchecked")
			Set<String> keySet = request.getParameterMap().keySet();

			for (String key : keySet) {
				parameterMap.put(key, request.getParameter(key));
			}
		}

		rowStatusMapper.put("+", RowStatus.INSERT.toString());
		rowStatusMapper.put("*", RowStatus.UPDATE.toString());
		rowStatusMapper.put("-", RowStatus.DELETE.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> getParamMap() {
		return this.parameterMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<FileInfo> getAttachments() {
		return attachments;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getDataSetIdList() {
		List<String> datasetIdList = new ArrayList<String>();
		Set<String> keySet = parameterMap.keySet();

		for (String dataSetId : keySet) {
			String value = (String) parameterMap.get(dataSetId);
			int indexOf = value.indexOf(MybuilderConstants.MSV_LINE_SEP);
			if (indexOf > 0) {
				datasetIdList.add(dataSetId);
			}
		}

		return datasetIdList;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetRow> getDataSetRows(String dataSetId) {
		List<DataSetRow> rows = new ArrayList<DataSetRow>();

		String feildAndFullRow = (String) parameterMap.get(dataSetId);
		String[] columnAndRowsArray = feildAndFullRow.split(MybuilderConstants.MSV_LINE_SEP);
		String[] columnNamesArray = columnAndRowsArray[0].split(MybuilderConstants.MSV_COL_SEP);

		//It starts from 1 because first value of Array(Array[0]) has ColumnNames
		for (int i = 1; i < columnAndRowsArray.length; i++) {
			String[] columnValues = StringUtils.delimitedListToStringArray(columnAndRowsArray[i], MybuilderConstants.MSV_COL_SEP);
			DataSetRow DataSetRow = buildDataSetRow(columnValues, columnNamesArray);
			rows.add(DataSetRow);
		}

		return rows;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetColumn> getDataSetColumns(String dataSetId) {
		ArrayList<DataSetColumn> arrayList = new ArrayList<DataSetColumn>();

		String feildAndFullRow = (String) parameterMap.get(dataSetId);
		String[] columnAndRowsArray = feildAndFullRow.split(MybuilderConstants.MSV_LINE_SEP);
		String[] columnArray = columnAndRowsArray[0].split(MybuilderConstants.MSV_COL_SEP);

		for (String columnName : columnArray) {
			arrayList.add(new DataSetColumn(columnName, String.class));
		}

		return arrayList;
	}

	private DataSetRow buildDataSetRow(String[] columnValues, String[] columnNames) {
		DataSetRow row = new DataSetRowImpl();
		int columnValuesLength = columnValues.length;

		for (int i = 0; i < columnNames.length; i++) {
			if (columnNames[i].equals(UxConstants.MYBUILDER_ROWSTATUS_PROPERTY_NAME)) {
				row.setRowStatus(nomalizeRowstatus((String) columnValues[i]));
			} else {
				if(columnValuesLength==i){
					//화면에서 파라미터 값을 공백문자로 주게되면 null로 넣는다.
					row.add(columnNames[i], null);
				}else{
					row.add(columnNames[i], columnValues[i]);
				}

			}
		}
		return row;
	}

	private String nomalizeRowstatus(String orginal){
		return rowStatusMapper.get(orginal);
	}

}
