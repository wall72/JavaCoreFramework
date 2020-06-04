package jcf.sua.ux.mybuilder.mvc;

import javax.validation.ValidatorFactory;

import jcf.sua.SuaChannels;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.mvc.MciWebArgumentResolver;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.mybuilder.dataset.MybuilderDataSetReader;

import org.springframework.web.context.request.NativeWebRequest;

/**
 *
 * {@link MciWebArgumentResolver}
 *
 * @author Jeado
 *
 */
public class MybuilderWebArgumentResolver extends MciWebArgumentResolver {

	/**
	 * {@inheritDoc}
	 */
	protected MciRequest getMciRequest(NativeWebRequest webRequest, MciRequestValidator requestValidator) {
		return new MybuilderRequest((MybuilderDataSetReader) getDataSetReader(webRequest), requestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected MciResponse getMciResponse(NativeWebRequest webRequest) {
		return new MybuilderResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	protected SuaChannels getChannelType() {
		return SuaChannels.MYBUILDER;
	}
}
