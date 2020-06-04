package jcf.sua.ux.json.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.interceptor.MciDataSetHandlerInterceptor;
import jcf.sua.ux.json.dataset.JsonDataSetReader;
import jcf.sua.ux.json.dataset.JsonDataSetStreamWriter;
import jcf.sua.ux.json.dataset.JsonDataSetWriter;

/**
 *
 * {@link MciDataSetHandlerInterceptor}
 *
 * @author nolang
 *
 */
public class JsonDataSetHandlerInterceptor extends MciDataSetHandlerInterceptor {

	/**
	 * {@inheritDoc}
	 */
	protected SuaChannels getChannelType() {
		return SuaChannels.JSON;
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetReader getDataSetReader(HttpServletRequest request, HttpServletResponse response) {
		return new JsonDataSetReader(request, getFileOperator());
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new JsonDataSetWriter(response, accessor);
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetStreamWriter getDataSetStreamWriter(HttpServletRequest request, HttpServletResponse response) {
		return new JsonDataSetStreamWriter(response);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean checkMciRequest(HttpServletRequest request) {
		String contentType = request.getHeader("Content-Type");
		String accept = request.getHeader("Accept");

		if ((StringUtils.hasText(contentType) && contentType.indexOf("application/json+sua") != -1)
				|| (StringUtils.hasText(accept) && accept.indexOf("application/json+sua") != -1)) {
			return true;
		} else {
			return false;
		}
	}
}
