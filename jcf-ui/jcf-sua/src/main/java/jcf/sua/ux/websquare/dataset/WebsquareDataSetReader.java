package jcf.sua.ux.websquare.dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import websquare.dataset.DataSetLogListener;
import websquare.dataset.DataSetUtil;

/**
 * 
 * {@link DataSetReader} 의 웹스퀘어 구현체
 * 
 * @author nolang
 * 
 */
public class WebsquareDataSetReader implements DataSetReader {

	private static final Logger logger = LoggerFactory
			.getLogger(WebsquareDataSetReader.class);

	private Map<String, ?> map = new HashMap<String, Object>();
	private Map<String, Object> paramMap = new HashMap<String, Object>();

	@SuppressWarnings("unchecked")
	public WebsquareDataSetReader(HttpServletRequest request) {
		try {
			if (!request.getMethod().equals("GET")
					&& request.getInputStream() != null) {
				map = DataSetUtil.read(request, new DataSetLogListener() {
					public void log(String msg, Throwable t) {
						logger.error(msg, t);
					}

					public void log(String msg) {
						logger.debug(msg);
					}
				});
			} 
		} catch (Exception e) {
//			throw new MciException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> getParamMap() {
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			if (ClassUtils.isPrimitiveOrWrapper(entry.getValue().getClass())) {
				paramMap.put(entry.getKey(), String.valueOf(entry.getValue()));
			}
		}

		return paramMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getDataSetIdList() {
		List<String> list = new ArrayList<String>();

		for (Map.Entry<String, ?> entry : map.entrySet()) {
			if (!ClassUtils.isPrimitiveOrWrapper(entry.getValue().getClass())) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetRow> getDataSetRows(String dataSetId) {
		List<DataSetRow> rows = new ArrayList<DataSetRow>();

		Object root = map.get(dataSetId);

		if (Collection.class.isAssignableFrom(root.getClass())) {
			List<Map<String, ?>> list = (List<Map<String, ?>>) root;

			for (Map<String, ?> node : list) {
				if (!Map.class.isAssignableFrom(node.getClass())) {
					throw new MciException(
							"[WebsquareDataSetReader] getDataSetRows - Invalid DataSet Format");
				}

				rows.add(map2DataSetRow(((Map<String, ?>) node)));
			}
		} else {
			rows.add(map2DataSetRow(((Map<String, ?>) root)));
		}

		return rows;
	}

	protected DataSetRow map2DataSetRow(Map<String, ?> map) {
		DataSetRow row = new DataSetRowImpl();

		for (Map.Entry<String, ?> entry : map.entrySet()) {
			if (entry.getKey().equals(
					UxConstants.DEFAULT_ROWSTATUS_PROPERTY_NAME)) {
				row.setRowStatus((String) entry.getValue());
			} else {
				row.add(entry.getKey(), entry.getValue());
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
			if (((List<Map<String, ?>>) root).size() > 0) {
				root = ((List<Map<String, ?>>) root).get(0);
			} else {
				root = null;
			}
		}

		if (root != null) {
			for (Map.Entry<String, ?> entry : ((Map<String, ?>) root)
					.entrySet()) {
				cols.add(new DataSetColumn(entry.getKey(),
						entry.getValue() != null ? entry.getValue().getClass()
								: String.class));
			}
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
