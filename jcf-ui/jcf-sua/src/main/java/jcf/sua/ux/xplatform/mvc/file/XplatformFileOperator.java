package jcf.sua.ux.xplatform.mvc.file;

import javax.servlet.http.HttpServletRequest;

import jcf.sua.mvc.file.operator.AbstractMciFileOperator;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * {@link FileOperator} 의 Xplatform 구현체
 *
 * @author nolang
 *
 */
public class XplatformFileOperator extends AbstractMciFileOperator {

	/**
	 * {@inheritDoc}
	 */
	public boolean isMultiPartRequest(HttpServletRequest request) {
		String agent = request.getHeader("User-Agent");

		if (agent != null && (agent.indexOf("XPLATFORM") != -1)) {
			return ServletFileUpload.isMultipartContent(request);
		} else {
			return false;
		}
	}
}
