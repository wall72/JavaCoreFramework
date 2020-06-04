package jcf.sua.ux.webflow.dataset;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.exception.MciException;
import jcf.sua.mvc.file.operator.FileOperator;
import jcf.upload.FileInfo;
import jcf.upload.MultiPartInfo;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.StringUtils;

/**
 *
 * {@link DataSetReader} 의 표준웹 구현체
 *
 * @author nolang
 *
 */
public class WebFlowDataSetReader implements DataSetReader {

	private Map<String, Object> paramMap = new HashMap<String, Object>();
	private List<FileInfo> attachments = new ArrayList<FileInfo>();

	public WebFlowDataSetReader(HttpServletRequest request, FileOperator fileHandler) {
		paramMap.putAll(getParameters(request.getQueryString()));

		try {
			if(fileHandler != null && fileHandler.isMultiPartRequest(request))	{
				MultiPartInfo info = fileHandler.handleMultiPartRequest(request);

				paramMap.putAll(info.getAttributes());
				attachments.addAll(info.getFileInfos());

			} else {
				@SuppressWarnings("unchecked")
				Map<String, Object> parameterMap = request.getParameterMap();
				Enumeration paramNames = request.getParameterNames();

				while(paramNames.hasMoreElements()) {
					String key = paramNames.nextElement().toString();
					Object value = parameterMap.get(key);

					if(String[].class.isAssignableFrom(value.getClass())){
						String[] paramValue = (String[])value;
						if(paramValue.length == 1){
							paramMap.put(key, paramValue[0]);
						}else{
							paramMap.put(key, paramValue);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new MciException("[WebFlowDataSetReader] WebFlowDataSetReader - " + e.getMessage(), e);
		}
	}

	private Map<String, Object> getParameters(String queryString) {
		Map<String, Object> map = new HashMap<String, Object>();

		if(StringUtils.hasText(queryString)){
			try {
				//한글 decode추가
				queryString = URLDecoder.decode(queryString.toString(), "utf-8");
				String[] paramValues = queryString.split("&");

				for (int i = 0; i < paramValues.length; ++i) {
					String[] paramValue = paramValues[i].split("=", 2);

					if(map.containsKey(paramValue[0])){
						Object fromParamValue = map.get(paramValue[0]);
						String[] toParamValue = null;

						if(String[].class.isAssignableFrom(fromParamValue.getClass())){
							toParamValue = (String[]) ArrayUtils.add((Object[]) fromParamValue, paramValue[1]);
						} else {
							toParamValue = (String[]) ArrayUtils.add(new String[]{(String)fromParamValue}, paramValue[1]);
						}

						map.put(paramValue[0], toParamValue);
					} else {
						map.put(paramValue[0], paramValue[1]);
					}
				}
			} catch (UnsupportedEncodingException e1) {
				throw new MciException(e1);
			}
		}

		return map;
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
	public List<FileInfo> getAttachments() {
		return attachments;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getDataSetIdList() {
		throw new UnsupportedOperationException("[WebFlowDataSetReader] WebFlow 채널에서는 DataSet과 관련한 연산을 지원하지 않습니다.");
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetRow> getDataSetRows(String dataSetId) {
		throw new UnsupportedOperationException("[WebFlowDataSetReader] WebFlow 채널에서는 DataSet과 관련한 연산을 지원하지 않습니다.");
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DataSetColumn> getDataSetColumns(String dataSetId) {
		throw new UnsupportedOperationException("[WebFlowDataSetReader] WebFlow 채널에서는 DataSet과 관련한 연산을 지원하지 않습니다.");
	}
}
