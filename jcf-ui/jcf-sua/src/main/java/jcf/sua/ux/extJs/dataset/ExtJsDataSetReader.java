package jcf.sua.ux.extJs.dataset;

import java.io.IOException;
import java.io.Reader;
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
import jcf.sua.ux.UxConstants;
import jcf.upload.FileInfo;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/**
 *
 * {@link DataSetReader}
 *
 * @author Jeado
 *
 */
public class ExtJsDataSetReader implements DataSetReader {

	private ObjectMapper mapper = new ObjectMapper();
	private HttpServletRequest request;
	private Map<String, Object> map = new HashMap<String, Object>();;

	public ExtJsDataSetReader(HttpServletRequest request) {
		this.mapper.getSerializationConfig().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
		this.request = request;

		try {
			Reader reader = request.getReader();

			if (reader != null) {
				String jsonContent = FileCopyUtils.copyToString(request.getReader());

				if (StringUtils.hasText(jsonContent)) {
					map = mapper.readValue(jsonContent, HashMap.class);
				}
			}
		} catch (IOException e) {
			throw new MciException("[ExtJsDataSetReader] ExtJsDataSetReader - "
					+ e.getMessage(), e);
		}
	}

	public ExtJsDataSetReader(String jsonContent) {
		try {
			map = mapper.readValue(jsonContent, HashMap.class);
		} catch (IOException e) {
			throw new MciException("[ExtJsDataSetReader] ExtJsDataSetReader - "
					+ e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> getParamMap() {
		Map<String, Object> paramMap = new HashMap<String, Object>();

		@SuppressWarnings("unchecked")
		Map<String, String[]> requestParams = request.getParameterMap();

		Iterator<String> it = requestParams.keySet().iterator();

		while (it.hasNext()) {
			String paramName = it.next();
			String[] paramValue = requestParams.get(paramName);

			if (paramValue != null) {
				if(paramValue.length > 1){
					paramMap.put(paramName, paramValue);
				}
				else {
					paramMap.put(paramName, paramValue[0]);
				}
			}
		}

		return paramMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getDataSetIdList() {
		return Collections
				.unmodifiableList(new ArrayList<String>(map.keySet()));
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetRow> getDataSetRows(String dataSetId) {
		List<DataSetRow> rows = new ArrayList<DataSetRow>();

		Object root = map.get(dataSetId);

		if (Collection.class.isAssignableFrom(root.getClass())) {
			List<Object> list = (List<Object>) root;

			for (Object node : list) {
				if (!Map.class.isAssignableFrom(node.getClass())) {
					throw new MciException(
							"[ExtJsDataSetReader] getDataSetRows - Invalid DataSet Format");
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

		Object root = map.get(dataSetId);

		if (Collection.class.isAssignableFrom(root.getClass())) {
			if(((List<Object>) root).isEmpty()){
				return cols;
			}else{
				root = ((List<Object>) root).get(0);
			}

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
		return Collections.emptyList();
	}
}
