package jcf.sua.ux.xplatform.mvc.converter;

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
import jcf.sua.ux.xplatform.dataset.XplatformDataSetReader;
import jcf.sua.ux.xplatform.dataset.XplatformDataSetWriter;
import jcf.sua.ux.xplatform.mvc.XplatformRequest;
import jcf.sua.ux.xplatform.mvc.XplatformResponse;

import org.springframework.http.MediaType;

/**
 *
 * {@link MciHttpMessageConverter} 의 Xplatform 구현체
 *
 * @author nolang
 *
 */
public class XplatformHttpMessageConverter extends MciHttpMessageConverter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return Arrays.asList(new MediaType("application", "xplatform"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciRequest getMciRequest(DataSetReader reader, MciRequestValidator requestValidator) {
		return new XplatformRequest((XplatformDataSetReader) reader, requestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciResponse getMciResponse() {
		return new XplatformResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetReader getDataSetReader(HttpServletRequest request) {
		return new XplatformDataSetReader(request, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new XplatformDataSetWriter(request, response, null, accessor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SuaChannels getChannelType() {
		return SuaChannels.XPLATFORM;
	}


}
