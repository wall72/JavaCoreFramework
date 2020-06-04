package jcf.sua.ux.xplatform.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.interceptor.MciDataSetHandlerInterceptor;
import jcf.sua.ux.xplatform.dataset.XplatformDataSetReader;
import jcf.sua.ux.xplatform.dataset.XplatformDataSetStreamWriter;
import jcf.sua.ux.xplatform.dataset.XplatformDataSetWriter;

/**
 *
 * {@link MciDataSetHandlerInterceptor} 의 Xplatform 구현체
 *
 * @author mina
 *
 */
public class XplatformDataSetHandlerInterceptor extends MciDataSetHandlerInterceptor {

	/**
	 * {@inheritDoc}
	 */
	protected DataSetReader getDataSetReader(HttpServletRequest request, HttpServletResponse response) {
		return new XplatformDataSetReader(request, getFileOperator());
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new XplatformDataSetWriter(request, response, getFileOperator(), accessor);
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetStreamWriter getDataSetStreamWriter(HttpServletRequest request, HttpServletResponse response) {
		return new XplatformDataSetStreamWriter(response);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean checkMciRequest(HttpServletRequest request) {
		String agent = request.getHeader("User-Agent");

		if( agent != null && ( agent.indexOf("XPLATFORM") != -1 ) ){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected SuaChannels getChannelType() {
		return SuaChannels.XPLATFORM;
	}
}
