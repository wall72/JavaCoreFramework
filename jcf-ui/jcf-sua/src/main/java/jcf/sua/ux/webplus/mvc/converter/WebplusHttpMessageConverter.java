package jcf.sua.ux.webplus.mvc.converter;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.mvc.converter.MciHttpMessageConverter;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.webplus.dataset.WebplusDataSetReader;
import jcf.sua.ux.webplus.dataset.WebplusDataSetWriter;
import jcf.sua.ux.webplus.mvc.WebplusRequest;
import jcf.sua.ux.webplus.mvc.WebplusResponse;

import org.springframework.http.MediaType;

/**
 *
 * {@link MciHttpMessageConverter} 의 웹플러스 구현체
 *
 * @author nolang
 *
 */
public class WebplusHttpMessageConverter extends MciHttpMessageConverter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return Arrays.asList(new MediaType("application", "webplus"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciRequest getMciRequest(DataSetReader reader, MciRequestValidator requestValidator) {
		return new WebplusRequest((WebplusDataSetReader) reader, requestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciResponse getMciResponse() {
		return new WebplusResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetReader getDataSetReader(HttpServletRequest request) {
		return new WebplusDataSetReader(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new WebplusDataSetWriter(response, accessor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SuaChannels getChannelType() {
		return SuaChannels.WEBPLUS;
	}
}
