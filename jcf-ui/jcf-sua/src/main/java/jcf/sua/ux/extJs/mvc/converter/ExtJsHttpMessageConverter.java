package jcf.sua.ux.extJs.mvc.converter;

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
import jcf.sua.ux.extJs.dataset.ExtJsDataSetReader;
import jcf.sua.ux.extJs.dataset.ExtJsDataSetWriter;
import jcf.sua.ux.extJs.mvc.ExtJsRequest;
import jcf.sua.ux.extJs.mvc.ExtJsResponse;

import org.springframework.http.MediaType;


/**
 *
 * {@link MciHttpMessageConverter}
 *
 * @author Jeado
 *
 */
public class ExtJsHttpMessageConverter extends MciHttpMessageConverter {

	private List<MediaType> mediaTypes = Arrays.asList(new MediaType("application", "extJs+sua"));

	public List<MediaType> getSupportedMediaTypes() {
		return mediaTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SuaChannels getChannelType() {
		return SuaChannels.EXTJS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciRequest getMciRequest(DataSetReader reader, MciRequestValidator requestValidator) {
		return new ExtJsRequest((ExtJsDataSetReader) reader, requestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciResponse getMciResponse() {
		return new ExtJsResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetReader getDataSetReader(HttpServletRequest request) {
		return new ExtJsDataSetReader(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new ExtJsDataSetWriter(response, accessor);
	}
}
