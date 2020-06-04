package jcf.sua.ux.webplus.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.interceptor.MciDataSetHandlerInterceptor;
import jcf.sua.ux.webplus.dataset.WebplusDataSetReader;
import jcf.sua.ux.webplus.dataset.WebplusDataSetWriter;

import org.springframework.util.StringUtils;

/**
 *
 * {@link MciDataSetHandlerInterceptor} 의 웹플러스 구현체
 *
 * @author Jeado
 *
 */
public class WebplusDataSetHandlerInterceptor extends MciDataSetHandlerInterceptor {

	/**
	 * {@inheritDoc}
	 */
	protected DataSetReader getDataSetReader(HttpServletRequest request,
			HttpServletResponse response) {
		return new WebplusDataSetReader(request);
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetWriter getDataSetWriter(HttpServletRequest request,
			HttpServletResponse response, MciDataSetAccessor accessor) {
		return new WebplusDataSetWriter(response, accessor);
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetStreamWriter getDataSetStreamWriter(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean checkMciRequest(HttpServletRequest request) {
		String accept = request.getHeader("Accept");

		if (StringUtils.hasText(accept) && accept.indexOf("text/xml") != -1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected SuaChannels getChannelType() {
		return SuaChannels.WEBPLUS;
	}
}
