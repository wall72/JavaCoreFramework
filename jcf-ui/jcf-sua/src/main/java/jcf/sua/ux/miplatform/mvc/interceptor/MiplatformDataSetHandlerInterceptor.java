package jcf.sua.ux.miplatform.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.interceptor.MciDataSetHandlerInterceptor;
import jcf.sua.ux.miplatform.dataset.MiplatformDataSetReader;
import jcf.sua.ux.miplatform.dataset.MiplatformDataSetStreamWriter;
import jcf.sua.ux.miplatform.dataset.MiplatformDataSetWriter;

/**
*
* {@link MciDataSetHandlerInterceptor}
*
* @author mina
*
*/
public class MiplatformDataSetHandlerInterceptor extends MciDataSetHandlerInterceptor {

	/**
	 * {@inheritDoc}
	 */
	protected DataSetReader getDataSetReader(HttpServletRequest request, HttpServletResponse response) {
		return new MiplatformDataSetReader(request, getFileOperator());
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new MiplatformDataSetWriter(request, response, getFileOperator(), accessor);
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetStreamWriter getDataSetStreamWriter(HttpServletRequest request, HttpServletResponse response) {
		return new MiplatformDataSetStreamWriter(response);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean checkMciRequest(HttpServletRequest request) {
		String agent = request.getHeader("User-Agent");

		if (agent != null && (agent.indexOf("MiPlatform") != -1)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected SuaChannels getChannelType() {
		return SuaChannels.MIPLATFORM;
	}

}
