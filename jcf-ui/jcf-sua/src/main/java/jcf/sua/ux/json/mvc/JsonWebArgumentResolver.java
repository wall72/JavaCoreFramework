package jcf.sua.ux.json.mvc;

import jcf.sua.SuaChannels;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.mvc.MciWebArgumentResolver;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.json.dataset.JsonDataSetReader;

import org.springframework.web.context.request.NativeWebRequest;

/**
 *
 * {@link MciWebArgumentResolver}
 *
 * @author nolang
 *
 */
public class JsonWebArgumentResolver extends MciWebArgumentResolver {

	/**
	 * {@inheritDoc}
	 */
	protected MciRequest getMciRequest(NativeWebRequest webRequest, MciRequestValidator requestValidator) {
		return new JsonRequest((JsonDataSetReader) getDataSetReader(webRequest), requestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected MciResponse getMciResponse(NativeWebRequest webRequest) {
		return new JsonResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	protected SuaChannels getChannelType() {
		return SuaChannels.JSON;
	}
}
