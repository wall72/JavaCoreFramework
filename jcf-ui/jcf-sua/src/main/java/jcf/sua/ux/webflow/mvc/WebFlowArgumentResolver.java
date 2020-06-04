package jcf.sua.ux.webflow.mvc;

import jcf.sua.SuaChannels;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.mvc.MciWebArgumentResolver;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.webflow.dataset.WebFlowDataSetReader;

import org.springframework.web.context.request.NativeWebRequest;

/**
 *
 * {@link MciWebArgumentResolver} 의 표준웹 구현체
 *
 * @author nolang
 *
 */
public class WebFlowArgumentResolver extends MciWebArgumentResolver {

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
	protected MciRequest getMciRequest(NativeWebRequest webRequest, MciRequestValidator requestValidator) {
		return new WebFlowRequest((WebFlowDataSetReader) getDataSetReader(webRequest), requestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciResponse getMciResponse(NativeWebRequest webRequest) {
		return new WebFlowResponse();
	}

}
