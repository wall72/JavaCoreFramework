package jcf.sua.ux.json.dataset;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.dataset.DataSetRowImpl;
import jcf.sua.exception.MciException;
import jcf.sua.mvc.file.operator.FileOperator;
import jcf.sua.ux.UxConstants;
import jcf.upload.FileInfo;
import jcf.upload.MultiPartInfo;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/**
 *
 * {@link DataSetReader}
 *
 * @author nolang
 *
 */
public class JsonDataSetReader implements DataSetReader {

	private ObjectMapper mapper = new ObjectMapper();
	
	private Map<String, Object> dataSetMap = new HashMap<String, Object>();
	Map<String, Object> paramMap = new HashMap<String, Object>();
	private List<FileInfo> attachments = new ArrayList<FileInfo>();
	
	public JsonDataSetReader(HttpServletRequest request, FileOperator operator) {
		if(operator != null && operator.isMultiPartRequest(request)){
			MultiPartInfo multiPartInfo = operator.handleMultiPartRequest(request);

			if (multiPartInfo != null) {
				attachments.addAll(multiPartInfo.getFileInfos());
				paramMap.putAll(multiPartInfo.getAttributes());
			}
		} else {
			this.mapper.getSerializationConfig().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
	
			try {
				Reader reader = request.getReader();
	
				if (reader != null) {
					String jsonContent = FileCopyUtils.copyToString(request.getReader());
	
					if (StringUtils.hasText(jsonContent)) {
						dataSetMap = mapper.readValue(jsonContent, HashMap.class);
					}
				}
			} catch (IOException e) {
				throw new MciException("[JsonDataSetReader] JsonDataSetReader - " + e.getMessage(), e);
			}
			
			readParams(request);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> getParamMap() {
		return paramMap;
	}
	
	protected void readParams(HttpServletRequest request)	{
		@SuppressWarnings("unchecked")
		Map<String, String[]> requestParams = request.getParameterMap();

		Iterator<String> it = requestParams.keySet().iterator();

		while (it.hasNext()) {
			String paramName = it.next();
			String[] paramValue = requestParams.get(paramName);

			if (paramValue != null) {
				try {
					if (paramValue != null) {
						if(paramValue.length > 1){
							paramMap.put(paramName, paramValue);
						} else {
							paramMap.put(paramName, URLDecoder.decode(paramValue[0],"UTF-8"));
						}
					}
				} catch (UnsupportedEncodingException e) {
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getDataSetIdList() {
		return Collections
				.unmodifiableList(new ArrayList<String>(dataSetMap.keySet()));
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetRow> getDataSetRows(String dataSetId) {
		List<DataSetRow> rows = new ArrayList<DataSetRow>();

		Object root = dataSetMap.get(dataSetId);

		if (Collection.class.isAssignableFrom(root.getClass())) {
			List<Object> list = (List<Object>) root;

			for (Object node : list) {
				if (!Map.class.isAssignableFrom(node.getClass())) {
					throw new MciException(
							"[JsonDataSetReader] getDataSetRows - Invalid DataSet Format");
				}

				rows.add(map2DataSetRow(((Map<String, Object>) node)));
			}
		} else {
			rows.add(map2DataSetRow(((Map<String, Object>) root)));
		}

		return rows;
	}

	protected DataSetRow map2DataSetRow(Map<String, Object> map) {
		DataSetRow row = new DataSetRowImpl();

		Iterator<String> it = ((Map) map).keySet().iterator();

		while (it.hasNext()) {
			String columnName = it.next();
			Object columnValue = ((Map) map).get(columnName);

			if (columnName.equals(UxConstants.DEFAULT_ROWSTATUS_PROPERTY_NAME)) {
				row.setRowStatus((String) columnValue);
			} else {
				row.add(columnName, columnValue);
			}
		}

		return row;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetColumn> getDataSetColumns(String dataSetId) {
		List<DataSetColumn> cols = new ArrayList<DataSetColumn>();

		Object root = dataSetMap.get(dataSetId);

		if (Collection.class.isAssignableFrom(root.getClass())) {
			root = ((List<Object>) root).get(0);

		}

		Iterator<String> it = ((Map<String, Object>) root).keySet().iterator();

		while (it.hasNext()) {
			String colunmName = it.next();
			Object colunmValue = ((Map<String, Object>) root).get(colunmName);

			cols.add(new DataSetColumn(colunmName, colunmValue != null ? colunmValue.getClass() : String.class));
		}

		return cols;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<FileInfo> getAttachments() {
		return attachments;
	}
}
