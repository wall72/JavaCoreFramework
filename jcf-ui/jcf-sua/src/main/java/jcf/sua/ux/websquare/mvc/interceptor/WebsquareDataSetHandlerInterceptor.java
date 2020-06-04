package jcf.sua.ux.websquare.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.interceptor.MciDataSetHandlerInterceptor;
import jcf.sua.ux.websquare.dataset.WebsquareDataSetReader;
import jcf.sua.ux.websquare.dataset.WebsquareDataSetStreamWriter;
import jcf.sua.ux.websquare.dataset.WebsquareDataSetWriter;

/**
 *
 * {@link MciDataSetHandlerInterceptor} 의 웹스퀘어 구현체
 *
 * @author nolang
 *
 */
public class WebsquareDataSetHandlerInterceptor extends MciDataSetHandlerInterceptor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SuaChannels getChannelType() {
		return SuaChannels.WEBSQUARE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetReader getDataSetReader(HttpServletRequest request,
			HttpServletResponse response) {
		return new WebsquareDataSetReader(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetWriter getDataSetWriter(HttpServletRequest request,
			HttpServletResponse response, MciDataSetAccessor accessor) {
		return new WebsquareDataSetWriter(request, response, accessor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetStreamWriter getDataSetStreamWriter(
			HttpServletRequest request, HttpServletResponse response) {
		return new WebsquareDataSetStreamWriter(response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean checkMciRequest(HttpServletRequest request) {
		String contentType = request.getHeader("Content-Type");

		return StringUtils.hasText(contentType) && contentType.indexOf("application/xml") > -1;
	}

}
