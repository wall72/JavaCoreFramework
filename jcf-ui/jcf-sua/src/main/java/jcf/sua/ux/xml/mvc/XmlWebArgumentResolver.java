/**
 *
 */
package jcf.sua.ux.xml.mvc;

import jcf.sua.SuaChannels;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.mvc.MciWebArgumentResolver;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.xml.dataset.XmlDataSetReader;

import org.springframework.web.context.request.NativeWebRequest;

/**
 *
 * {@link MciWebArgumentResolver} 의 XML 구현체
 *
 * @author nolang
 *
 */
public class XmlWebArgumentResolver extends MciWebArgumentResolver {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SuaChannels getChannelType() {
		return SuaChannels.XML;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciRequest getMciRequest(NativeWebRequest webRequest, MciRequestValidator requestValidator) {
		return new XmlRequest((XmlDataSetReader) getDataSetReader(webRequest));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciResponse getMciResponse(NativeWebRequest webRequest) {
		return new XmlResponse();
	}

}
