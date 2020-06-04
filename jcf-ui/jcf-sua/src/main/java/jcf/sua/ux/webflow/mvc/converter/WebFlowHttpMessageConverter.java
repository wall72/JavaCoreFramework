package jcf.sua.ux.webflow.mvc.converter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
import jcf.sua.ux.webflow.dataset.WebFlowDataSetReader;
import jcf.sua.ux.webflow.mvc.WebFlowRequest;
import jcf.sua.ux.webflow.mvc.WebFlowResponse;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * {@link MciHttpMessageConverter} 의 표준웹 구현체
 *
 * @author nolang
 *
 */
public class WebFlowHttpMessageConverter extends MciHttpMessageConverter {

	private FormHttpMessageConverter converter = new FormHttpMessageConverter();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(Object accessor, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		ModelAndView modelAndView = ((MciDataSetAccessor) accessor).getModelAndView();

		MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<String, Object>((Map) modelAndView.getModelMap());

		converter.write(multiValueMap, contentType, outputMessage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SuaChannels getChannelType() {
		return SuaChannels.WEBFLOW;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return converter.getSupportedMediaTypes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciRequest getMciRequest(DataSetReader reader, MciRequestValidator requestValidator) {
		return new WebFlowRequest((WebFlowDataSetReader) reader, requestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciResponse getMciResponse() {
		return new WebFlowResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetReader getDataSetReader(HttpServletRequest request) {
		return new WebFlowDataSetReader(request, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return null;
	}

}
