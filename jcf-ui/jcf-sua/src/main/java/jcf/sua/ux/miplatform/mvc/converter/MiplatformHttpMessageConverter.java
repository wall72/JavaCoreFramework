package jcf.sua.ux.miplatform.mvc.converter;

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
import jcf.sua.ux.miplatform.dataset.MiplatformDataSetReader;
import jcf.sua.ux.miplatform.dataset.MiplatformDataSetWriter;
import jcf.sua.ux.miplatform.mvc.MiplatformRequest;
import jcf.sua.ux.miplatform.mvc.MiplatformResponse;

import org.springframework.http.MediaType;

/**
 *
 * {@link MciHttpMessageConverter}
 *
 * @author nolang
 *
 */
public class MiplatformHttpMessageConverter extends MciHttpMessageConverter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return Arrays.asList(new MediaType("application", "miplatform"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciRequest getMciRequest(DataSetReader reader, MciRequestValidator requestValidator) {
		return new MiplatformRequest((MiplatformDataSetReader) reader, requestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciResponse getMciResponse() {
		return new MiplatformResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetReader getDataSetReader(HttpServletRequest request) {
		return new MiplatformDataSetReader(request, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new MiplatformDataSetWriter(request, response, null, accessor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SuaChannels getChannelType() {
		return SuaChannels.MIPLATFORM;
	}
}
