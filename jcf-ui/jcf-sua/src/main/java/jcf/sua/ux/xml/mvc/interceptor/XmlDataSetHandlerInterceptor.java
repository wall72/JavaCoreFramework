package jcf.sua.ux.xml.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.interceptor.MciDataSetHandlerInterceptor;
import jcf.sua.ux.xml.dataset.XmlDataSetReader;
import jcf.sua.ux.xml.dataset.XmlDataSetStreamWriter;
import jcf.sua.ux.xml.dataset.XmlDataSetWriter;

import org.springframework.util.StringUtils;

/**
 *
 * {@link DataSetHandlerInterceptor} 의 XML 구현체
 *
 * @author nolang
 *
 */
public class XmlDataSetHandlerInterceptor extends MciDataSetHandlerInterceptor {


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SuaChannels getChannelType() {
		return SuaChannels.XML;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetReader getDataSetReader(HttpServletRequest request, HttpServletResponse response) {
		return new XmlDataSetReader(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new XmlDataSetWriter(response, accessor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetStreamWriter getDataSetStreamWriter(HttpServletRequest request, HttpServletResponse response) {
		return new XmlDataSetStreamWriter(response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean checkMciRequest(HttpServletRequest request) {
		String contentType = request.getContentType();
		String accept = request.getHeader("Accept");

		if ((StringUtils.hasText(contentType) && contentType.indexOf("application/xml+sua") != -1) || (StringUtils.hasText(accept) && accept.indexOf("application/xml+sua") != -1)) {
			return true;
		} else {
			return false;
		}
	}

}
