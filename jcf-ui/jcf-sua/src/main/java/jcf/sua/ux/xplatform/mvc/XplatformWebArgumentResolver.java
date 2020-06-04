package jcf.sua.ux.xplatform.mvc;

import jcf.sua.SuaChannels;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.mvc.MciWebArgumentResolver;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.xplatform.dataset.XplatformDataSetReader;

import org.springframework.web.context.request.NativeWebRequest;

/**
 *
 * {@link MciWebArgumentResolver} 의 Xplatform 구현체
 *
 * @author mina
 *
 */
public class XplatformWebArgumentResolver extends MciWebArgumentResolver {

	/**
	 * {@inheritDoc}
	 */
	protected MciRequest getMciRequest(NativeWebRequest webRequest, MciRequestValidator requestValidator)	{
		return new XplatformRequest((XplatformDataSetReader) getDataSetReader(webRequest), requestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected MciResponse getMciResponse(NativeWebRequest webRequest) {
		return new XplatformResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	protected SuaChannels getChannelType() {
		return SuaChannels.XPLATFORM;
	}
}
