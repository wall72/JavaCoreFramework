package jcf.sua.ux.webplus.mvc;

import jcf.sua.SuaChannels;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.mvc.MciWebArgumentResolver;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.webplus.dataset.WebplusDataSetReader;

import org.springframework.web.context.request.NativeWebRequest;

/**
 *
 * {@link MciWebArgumentResolver} 의 웹플러스 구현체
 *
 * @author Jeado
 *
 */
public class WebplusWebArgumentResolver extends MciWebArgumentResolver {

	/**
	 * {@inheritDoc}
	 */
	protected MciRequest getMciRequest(NativeWebRequest webRequest, MciRequestValidator requestValidator) {
		return new WebplusRequest((WebplusDataSetReader) getDataSetReader(webRequest), requestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected MciResponse getMciResponse(NativeWebRequest webRequest) {
		return new WebplusResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	protected SuaChannels getChannelType() {
		return SuaChannels.WEBPLUS;
	}
}
