package jcf.sua.ux.miplatform.mvc.file;

import javax.servlet.http.HttpServletRequest;

import jcf.sua.mvc.file.operator.AbstractMciFileOperator;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * {@link AbstractMciFileOperator}
 *
 * @author nolang
 *
 */
public class MiplatformFileOperator  extends AbstractMciFileOperator {

	/**
	 * {@inheritDoc}
	 */
	public boolean isMultiPartRequest(HttpServletRequest request) {
		String agent = request.getHeader("User-Agent");

		if (agent != null && (agent.indexOf("MiPlatform") != -1)) {
			return ServletFileUpload.isMultipartContent(request);
		} else {
			return false;
		}
	}
}
