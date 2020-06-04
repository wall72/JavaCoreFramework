package jcf.sua.ux.webplus.dataset;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import jcf.sua.ux.webplus.DeliminatedReader;
import jcf.upload.FileInfo;

import org.springframework.util.StringUtils;

/**
 *
 * {@link DataSetReader} 의 웹플러스 구현체
 *
 * @author nolang
 *
 */
public class WebplusDataSetReader implements DataSetReader {

	private HttpServletRequest request;

	private Map<String, String> oneRowDataSetMap = new HashMap<String, String>();

	public WebplusDataSetReader(HttpServletRequest request) {
		this.request = request;

		DeliminatedReader deliminatedReader = null;

		try {
			InputStream in = request.getInputStream();

			if(in != null){
				deliminatedReader = new DeliminatedReader(request.getInputStream(), UxConstants.DEFAULT_CHARSET, UxConstants.WEBPLUS_DELIMETER);
			}

			readMessage(deliminatedReader);
		} catch (IOException e) {
			throw new MciException("DeliminatedReader : "+e.getMessage());
		}
	}

	private void readMessage(DeliminatedReader deliminatedReader) {
		while(deliminatedReader != null && deliminatedReader.isAvailable()){
			String read = deliminatedReader.read();
			String[] split = StringUtils.split(read, "=");
			oneRowDataSetMap.put(split[0], split[1]);
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
				paramMap.put(paramName, paramValue[0]);
			}
		}
		return paramMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getDataSetIdList() {
		return Collections.unmodifiableList(new ArrayList<String>(oneRowDataSetMap
				.keySet()));
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetRow> getDataSetRows(String dataSetId) {
		List<DataSetRow> rows = new ArrayList<DataSetRow>();
		DataSetRow dataSetRow = new DataSetRowImpl();

		if(!oneRowDataSetMap.isEmpty())	{
			for (String fieldName : oneRowDataSetMap.keySet()) {
				if (fieldName.equals("crud")) {
					continue;
				}

				dataSetRow.add(fieldName, oneRowDataSetMap.get(fieldName));
			}

			dataSetRow.setRowStatus(getStatusOf(oneRowDataSetMap.get("crud")));
			rows.add(dataSetRow);
		}

		return rows;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetColumn> getDataSetColumns(String dataSetId) {
		List<DataSetColumn> cols = new ArrayList<DataSetColumn>();

		for (String columnName : oneRowDataSetMap.keySet()) {
			if (columnName.equals("crud")) {
				continue;
			}
			//UI로부터 타입정보를 가져올 수 없다.
			cols.add(new DataSetColumn(columnName, String.class));
		}

		return cols;
	}

	private String getStatusOf(String crud) {
		if (crud.equals("I")) {
			return "INSERT";
		} else if (crud.equals("U")) {
			return "UPDATE";
		} else if (crud.equals("D")) {
			return "DELETE";
		} else {
			return "NORMAL";
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<FileInfo> getAttachments() {
		return Collections.emptyList();
	}
}
