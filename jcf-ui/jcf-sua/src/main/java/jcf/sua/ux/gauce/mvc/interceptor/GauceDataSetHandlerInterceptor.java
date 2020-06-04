package jcf.sua.ux.gauce.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.interceptor.MciDataSetHandlerInterceptor;
import jcf.sua.ux.gauce.dataset.GauceDataSetReader;
import jcf.sua.ux.gauce.dataset.GauceDataSetStreamWriter;
import jcf.sua.ux.gauce.dataset.GauceDataSetWriter;

import org.springframework.util.StringUtils;

/**
 *
 * {@link MciDataSetHandlerInterceptor}
 *
 * @author nolang
 *
 */
public class GauceDataSetHandlerInterceptor extends MciDataSetHandlerInterceptor {

	private int firstRowSize = 100;

	/**
	 * FirstRow Size 설정 (Default = 100)
	 *
	 * @param firstRowSize
	 */
	public void setFirstRowSize(int firstRowSize) {
		this.firstRowSize = firstRowSize;
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetReader getDataSetReader(HttpServletRequest request, HttpServletResponse response) {
		return new GauceDataSetReader(request);
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new GauceDataSetWriter(response, accessor, firstRowSize);
	}

	/**
	 * {@inheritDoc}
	 */
	protected DataSetStreamWriter getDataSetStreamWriter(HttpServletRequest request, HttpServletResponse response) {
		return new GauceDataSetStreamWriter(response);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean checkMciRequest(HttpServletRequest request) {
		String queryString = request.getQueryString();
		return (StringUtils.hasText(queryString) && queryString.indexOf("X-UIClient") >= 0) || StringUtils.hasText(request.getHeader("X-UIClient")) ;
	}

	/**
	 * {@inheritDoc}
	 */
	protected SuaChannels getChannelType() {
		return SuaChannels.GAUCE;
	}
}
