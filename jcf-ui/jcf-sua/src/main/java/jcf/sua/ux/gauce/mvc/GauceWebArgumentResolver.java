package jcf.sua.ux.gauce.mvc;

import jcf.sua.SuaChannels;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.mvc.MciWebArgumentResolver;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.gauce.dataset.GauceDataSetReader;

import org.springframework.web.context.request.NativeWebRequest;

/**
 *
 * {@link MciWebArgumentResolver}
 *
 * @author nolang
 *
 */
public class GauceWebArgumentResolver extends MciWebArgumentResolver {

	/**
	 * {@inheritDoc}
	 */
	protected MciRequest getMciRequest(NativeWebRequest webRequest, MciRequestValidator requestValidator)	{
		return new GauceRequest((GauceDataSetReader) getDataSetReader(webRequest), requestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected MciResponse getMciResponse(NativeWebRequest webRequest) {
		return new GauceResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	protected SuaChannels getChannelType() {
		return SuaChannels.GAUCE;
	}
}
