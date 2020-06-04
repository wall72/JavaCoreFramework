package jcf.sua.ux.nexacro.mvc;

import org.springframework.web.context.request.NativeWebRequest;

import jcf.sua.SuaChannels;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.mvc.MciWebArgumentResolver;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.nexacro.dataset.NexacroDataSetReader;

public class NexacroWebArgumentResolver extends MciWebArgumentResolver {

	protected MciRequest getMciRequest(NativeWebRequest webRequest, MciRequestValidator requestValidator) {
		return new NexacroRequest( (NexacroDataSetReader)getDataSetReader(webRequest), requestValidator);
	}

	protected MciResponse getMciResponse(NativeWebRequest webRequest) {
		return new NexacroResponse();
	}

	protected SuaChannels getChannelType() {
		return SuaChannels.NEXACRO;
	}
}
