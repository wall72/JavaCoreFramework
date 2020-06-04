package jcf.sua.ux.nexacro.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.interceptor.MciDataSetHandlerInterceptor;
import jcf.sua.ux.nexacro.dataset.NexacroDataSetReader;
import jcf.sua.ux.nexacro.dataset.NexacroDataSetStreamWriter;
import jcf.sua.ux.nexacro.dataset.NexacroDataSetWriter;

public class NexacroDataSetHandlerInterceptor extends MciDataSetHandlerInterceptor {

	protected DataSetReader getDataSetReader(HttpServletRequest request, HttpServletResponse response) {
		return new NexacroDataSetReader(request, getFileOperator());
	}

	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new NexacroDataSetWriter(request, response, getFileOperator(), accessor);
	}

	protected DataSetStreamWriter getDataSetStreamWriter(HttpServletRequest request, HttpServletResponse response) {
		return new NexacroDataSetStreamWriter(response);
	}

	protected boolean checkMciRequest(HttpServletRequest request) {
		String agent = request.getHeader("JCF-Channel-Type");

		if( agent != null && ( agent.indexOf("nexacroplatform") != -1 ) ){
			return true;
		}else{
			return false;
		}
	}

	protected SuaChannels getChannelType() {
		return SuaChannels.NEXACRO;
	}
	
}
