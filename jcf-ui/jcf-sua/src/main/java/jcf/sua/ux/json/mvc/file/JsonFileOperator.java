package jcf.sua.ux.json.mvc.file;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import jcf.sua.mvc.file.operator.AbstractMciFileOperator;
import jcf.sua.mvc.file.operator.FileOperator;

/**
*
* {@link FileOperator} 의 JSON 채널 구현체
*
* @author nolang
*
*/
public class JsonFileOperator extends AbstractMciFileOperator {

	@Override
	public boolean isMultiPartRequest(HttpServletRequest request) {
		return ServletFileUpload.isMultipartContent(request);
	}

}
