package jcf.sua.ux.webflow.mvc;

import jcf.sua.mvc.AbstractMciRequest;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.webflow.dataset.WebFlowDataSetReader;

/**
 *
 * {@link MciRequest} 의 표준웹 구현체
 *
 * @author nolang
 *
 */
public class WebFlowRequest extends AbstractMciRequest {

	public WebFlowRequest(WebFlowDataSetReader reader, MciRequestValidator requestValidator) {
		attachments = reader.getAttachments();
		paramMap = reader.getParamMap();

		this.requestValidator = requestValidator;
	}
}
