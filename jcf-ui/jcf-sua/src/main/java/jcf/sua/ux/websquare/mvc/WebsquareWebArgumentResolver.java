package jcf.sua.ux.websquare.mvc;

import jcf.sua.SuaChannels;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.mvc.MciWebArgumentResolver;
import jcf.sua.mvc.validation.MciRequestValidator;

import org.springframework.web.context.request.NativeWebRequest;

/**
 *
 * {@link MciWebArgumentResolver} 의 웹스퀘어 구현체
 *
 * @author nolang
 *
 */
public class WebsquareWebArgumentResolver extends MciWebArgumentResolver {

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
	protected MciRequest getMciRequest(NativeWebRequest webRequest, MciRequestValidator requestValidator) {
		return new WebsquareRequest(getDataSetReader(webRequest), requestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciResponse getMciResponse(NativeWebRequest webRequest) {
		return new WebsquareResponse();
	}

}
