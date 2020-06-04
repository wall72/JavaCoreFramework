package jcf.sua.ux.mybuilder.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.interceptor.MciDataSetHandlerInterceptor;
import jcf.sua.ux.mybuilder.dataset.MyBuilderDataSetStreamWriter;
import jcf.sua.ux.mybuilder.dataset.MybuilderDataSetReader;
import jcf.sua.ux.mybuilder.dataset.MybuilderDataSetWriter;

/**
 *
 * {@link MciDataSetHandlerInterceptor}
 *
 * @author Jeado
 *
 */
public class MybuilderDataSetHandlerInterceptor extends MciDataSetHandlerInterceptor {

	/**
	 * {@inheritDoc}
	 */
	protected SuaChannels getChannelType() {
		return SuaChannels.MYBUILDER;
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetReader getDataSetReader(HttpServletRequest request, HttpServletResponse response) {
		return new MybuilderDataSetReader(request, getFileOperator());
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new MybuilderDataSetWriter(request, response, accessor, getFileOperator());
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetStreamWriter getDataSetStreamWriter(HttpServletRequest request, HttpServletResponse response) {
		return new MyBuilderDataSetStreamWriter(response);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean checkMciRequest(HttpServletRequest request) {
		String agent = request.getHeader("User-Agent");
		if( agent != null && ( agent.indexOf("MyBuilder") != -1 )){
			return true;
		}else{
			return false;
		}
	}
}
