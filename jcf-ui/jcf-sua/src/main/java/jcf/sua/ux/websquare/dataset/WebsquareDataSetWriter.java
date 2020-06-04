package jcf.sua.ux.websquare.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.exception.MciException;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.ux.websquare.mvc.WebsquareResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import websquare.dataset.DataSetLogListener;
import websquare.dataset.DataSetUtil;

/**
 *
 * {@link DataSetWriter} 의 웹스퀘어 구현체
 *
 * @author nolang
 *
 */
public class WebsquareDataSetWriter implements DataSetWriter {

	private static final Logger logger = LoggerFactory.getLogger(WebsquareDataSetWriter.class);

	private HttpServletRequest request;
	private HttpServletResponse response;
	private MciDataSetAccessor accessor;

	public WebsquareDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		this.request = request;
		this.response = response;
		this.accessor = accessor;
	}

	/**
	 * {@inheritDoc}
	 */
	public void write() {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		Map<String, DataSet> dataSetMap = accessor.getDataSetMap();

		for (Map.Entry<String, DataSet> entry : dataSetMap.entrySet()) {
			DataSet dataSet = entry.getValue();

			if(dataSet.getRowCount() == 0)	{
				continue;
			}

			boolean isFromCollection = ((WebsquareResponse) accessor).isFromCollection(dataSet.getId());

			if(isFromCollection)	{
				List<Map<String, ?>> list = new ArrayList<Map<String, ?>>();

				for (int i = 0; i < dataSet.getRowCount(); ++i) {
					list.add(dataSet.getBean(HashMap.class, i));
				}

				resultMap.put(dataSet.getId(), list);
			} else {
				resultMap.put(dataSet.getId(), dataSet.getBean(HashMap.class, 0));
			}
		}


		String exMsg = accessor.getExceptionMessage();

		if(StringUtils.hasText(exMsg))	{
			resultMap.put("msg", exMsg.toString());
			resultMap.put("msgCode", "error");
		}

		StringBuilder succMsg = new StringBuilder();

		for(String success : accessor.getSuccessMessags()){
			if(StringUtils.hasText(succMsg.toString()))	{
				succMsg.append(", ");
			}

			succMsg.append(success);
		}

		if(StringUtils.hasText(succMsg.toString())){
			resultMap.put("success", succMsg.toString());
		}

		response.setContentType("text/xml");
		response.setCharacterEncoding("utf-8");

		try {
			DataSetUtil.mapToXml(request, response, resultMap, true, true,
					new DataSetLogListener() {

						public void log(String msg, Throwable t) {
							logger.error(msg, t);
						}

						public void log(String msg) {
							logger.debug(msg);
						}
					});
		} catch (Exception e) {
			throw new MciException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDataSetAccessor(MciDataSetAccessor accessor) {
		this.accessor = accessor;
	}

}
