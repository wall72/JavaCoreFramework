package jcf.sua.ux.xml.dataset;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.dataset.DataSetRowImpl;
import jcf.sua.exception.MciException;
import jcf.sua.ux.UxConstants;
import jcf.sua.ux.xml.NodeConstants;
import jcf.upload.FileInfo;

import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

/**
 *
 * {@link DataSetReader} 의 XML 구현체
 *
 * @author nolang
 *
 */
public class XmlDataSetReader implements DataSetReader {

	private static Logger logger = LoggerFactory.getLogger(XmlDataSetReader.class);

	private static final String xpathOfHeaderNode = "/" + NodeConstants.rootNode + "/" + NodeConstants.headerNode;
	private static final String xpathOfContentNode = "/" + NodeConstants.rootNode + "/" + NodeConstants.contentNode;

	private Map<String, Object> paramMap = new HashMap<String, Object>();
	private Map<String, List<Map<String, String>>> datasetMap = new HashMap<String, List<Map<String, String>>>();

	public XmlDataSetReader(HttpServletRequest request) {
		Document document = null;

		if (!request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())) {
			try {
				document = (new SAXBuilder()).build(request.getInputStream());
			} catch (Exception e) {
				throw new MciException(e);
			}
		}

		if (document != null) {
			try {
				readXmlDataSet(document);
			} catch (Exception e) {
				throw new MciException("[UI-ADAPTER] 메시지 변환 오류 -" + e.getMessage(), e);
			}
		}

		readParameter(request);
	}

	@SuppressWarnings("unchecked")
	private void readParameter(HttpServletRequest request) {
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

	@SuppressWarnings("unchecked")
	private void readXmlDataSet(Document document) throws Exception	{
		/*
		 * Reads DataSet from request body
		 */
		List<Element> datasetList = selectNodes(document, xpathOfContentNode);

		if(datasetList != null){
			for (int i = 0; i < datasetList.size(); ++i) {
				Element dataset = datasetList.get(i);

				List<Map<String, String>> recordList = new ArrayList<Map<String,String>>();

				List<Element> recordElementList = new JDOMXPath(String.format("%s/%s/%s/%s", xpathOfContentNode, dataset.getName(), NodeConstants.dataNode, NodeConstants.recordNode)).selectNodes(document);

				for (Element record : recordElementList) {
					recordList.add(getRecordMap(record.getChildren()));
				}

				datasetMap.put(dataset.getName(), recordList);
			}
		}

		/*
		 * Reads header params from request body
		 */
		List<Element> headerList = selectNodes(document, xpathOfHeaderNode);

		if (headerList != null) {
			for (Element header : headerList) {
				paramMap.put(header.getName(), getNodeValue(header));

				logger.trace("Received Parameter Info : id={}, value={}", header.getName(), getNodeValue(header));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<Element> selectNodes(Document document, String xpath) throws Exception {
		List<Element> contents = new JDOMXPath(xpath).selectNodes(document);

		if(contents != null && contents.size() > 0)	{
			return contents.get(0).getChildren();
		}

		return null;
	}

	private Map<String, String> getRecordMap(List<Element> dataElementList) {
		Map<String, String> map = new LinkedHashMap<String, String>();

		for (Element dataElement : dataElementList) {
			map.put(dataElement.getName(), getNodeValue(dataElement));
		}

		return map;
	}

	private String getNodeValue(Element element) {
		return element.getTextTrim();
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
		return Collections.unmodifiableList(new ArrayList<String>(datasetMap.keySet()));
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetRow> getDataSetRows(String dataSetId) {
		List<DataSetRow> rows = new ArrayList<DataSetRow>();

		if(!datasetMap.isEmpty())	{
			List<Map<String, String>> dataRows = datasetMap.get(dataSetId);

			for(Map<String, String> data : dataRows)	{
				DataSetRow dataSetRow = new DataSetRowImpl();

				for(Map.Entry<String, String> e : data.entrySet())	{
					if (e.getKey().equals(UxConstants.DEFAULT_ROWSTATUS_PROPERTY_NAME)) {
						dataSetRow.setRowStatus(getStatusOf(e.getValue()));
					} else {
						dataSetRow.add(e.getKey(), e.getValue());
					}
				}

				rows.add(dataSetRow);
			}
		}

		return rows;
	}

	private String getStatusOf(String crud) {
		if (crud.equals("C")) {
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
	public List<DataSetColumn> getDataSetColumns(String dataSetId) {
		List<DataSetColumn> cols = new ArrayList<DataSetColumn>();

		if(!datasetMap.isEmpty())	{
			List<Map<String, String>> dataRows = datasetMap.get(dataSetId);

			if(dataRows.size() > 0)	{
				Map<String, String> data = dataRows.get(0);

				for (String columnName : data.keySet()) {
					if (columnName.equals(UxConstants.DEFAULT_ROWSTATUS_PROPERTY_NAME)) {
						continue;
					}

					cols.add(new DataSetColumn(columnName, String.class));
				}
			}
		}

		return cols;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<FileInfo> getAttachments() {
		return null;
	}

}
