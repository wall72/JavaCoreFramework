package jcf.sua.ux.extJs.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.interceptor.MciDataSetHandlerInterceptor;
import jcf.sua.ux.extJs.dataset.ExtJsDataSetReader;
import jcf.sua.ux.extJs.dataset.ExtJsDataSetStreamWriter;
import jcf.sua.ux.extJs.dataset.ExtJsDataSetWriter;

import org.springframework.util.StringUtils;


/**
 *
 * {@link MciDataSetHandlerInterceptor}
 *
 * @author Jeado
 *
 */
public class ExtJsDataSetHandlerInterceptor extends MciDataSetHandlerInterceptor {

	/**
	 * {@inheritDoc}
	 */
	protected SuaChannels getChannelType() {
		return SuaChannels.EXTJS;
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetReader getDataSetReader(HttpServletRequest request, HttpServletResponse response) {
		return new ExtJsDataSetReader(request);
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new ExtJsDataSetWriter(response, accessor);
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetStreamWriter getDataSetStreamWriter(HttpServletRequest request, HttpServletResponse response) {
		return new ExtJsDataSetStreamWriter(response);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean checkMciRequest(HttpServletRequest request) {
		String contentType = request.getHeader("Content-Type");
		String accept = request.getHeader("Accept");

		if ((StringUtils.hasText(contentType) && contentType.indexOf("application/extJs+sua") != -1)
				|| (StringUtils.hasText(accept) && accept.indexOf("application/extJs+sua") != -1)) {
			return true;
		} else {
			return false;
		}
	}
}
