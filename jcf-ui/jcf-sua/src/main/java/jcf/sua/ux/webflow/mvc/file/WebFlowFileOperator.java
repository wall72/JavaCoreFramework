package jcf.sua.ux.webflow.mvc.file;

import javax.servlet.http.HttpServletRequest;

import jcf.sua.mvc.file.operator.AbstractMciFileOperator;
import jcf.sua.mvc.file.operator.FileOperator;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * {@link FileOperator} 의 표준웹 구현체
 *
 * @author nolang
 *
 */
public class WebFlowFileOperator  extends AbstractMciFileOperator {

	/**
	 * {@inheritDoc}
	 */
	public boolean isMultiPartRequest(HttpServletRequest request) {
		return ServletFileUpload.isMultipartContent(request);
	}

}
