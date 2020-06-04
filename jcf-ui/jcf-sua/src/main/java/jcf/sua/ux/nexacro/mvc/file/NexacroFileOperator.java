package jcf.sua.ux.nexacro.mvc.file;

import javax.servlet.http.HttpServletRequest;

import jcf.sua.mvc.file.operator.AbstractMciFileOperator;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class NexacroFileOperator extends AbstractMciFileOperator {

	@Override
	public boolean isMultiPartRequest(HttpServletRequest request) {
		String agent = request.getHeader("JCF-Channel-Type");
		
		if(agent != null && (agent.indexOf("nexacroplatform") != -1)){
			return ServletFileUpload.isMultipartContent(request);
		} else
			return false;
	}

}
