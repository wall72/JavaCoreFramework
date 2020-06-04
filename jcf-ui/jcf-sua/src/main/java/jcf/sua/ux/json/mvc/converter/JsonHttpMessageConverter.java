package jcf.sua.ux.json.mvc.converter;

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
import jcf.sua.ux.json.dataset.JsonDataSetReader;
import jcf.sua.ux.json.dataset.JsonDataSetWriter;
import jcf.sua.ux.json.mvc.JsonRequest;
import jcf.sua.ux.json.mvc.JsonResponse;

import org.springframework.http.MediaType;

/**
 *
 * {@link MciHttpMessageConverter}
 *
 * @author nolang
 *
 */
public class JsonHttpMessageConverter extends MciHttpMessageConverter {

	private List<MediaType> mediaTypes = Arrays.asList(new MediaType("application", "json+sua"));

	/**
	 * {@inheritDoc}
	 */
	public List<MediaType> getSupportedMediaTypes() {
		return mediaTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SuaChannels getChannelType() {
		return SuaChannels.JSON;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciRequest getMciRequest(DataSetReader reader, MciRequestValidator requestValidator) {
		return new JsonRequest((JsonDataSetReader) reader, requestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciResponse getMciResponse() {
		return new JsonResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetReader getDataSetReader(HttpServletRequest request) {
		return new JsonDataSetReader(request, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new JsonDataSetWriter(response, accessor);
	}
}
