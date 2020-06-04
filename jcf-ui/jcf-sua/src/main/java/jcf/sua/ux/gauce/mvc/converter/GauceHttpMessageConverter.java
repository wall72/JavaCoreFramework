package jcf.sua.ux.gauce.mvc.converter;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.mvc.converter.MciHttpMessageConverter;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.gauce.dataset.GauceDataSetReader;
import jcf.sua.ux.gauce.dataset.GauceDataSetWriter;
import jcf.sua.ux.gauce.mvc.GauceRequest;
import jcf.sua.ux.gauce.mvc.GauceResponse;

import org.springframework.http.MediaType;

/**
 *
 * {@link MciHttpMessageConverter}
 *
 * @author nolang
 *
 */
public class GauceHttpMessageConverter extends MciHttpMessageConverter {

	private List<MediaType> mediaTypes = Arrays.asList(new MediaType("application", "gauce+sua"));
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
	public List<MediaType> getSupportedMediaTypes() {
		return mediaTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciRequest getMciRequest(DataSetReader reader, MciRequestValidator requestValidator) {
		return new GauceRequest((GauceDataSetReader) reader, requestValidator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciResponse getMciResponse() {
		return new GauceResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetReader getDataSetReader(HttpServletRequest request) {
		return new GauceDataSetReader(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new GauceDataSetWriter(response, accessor, firstRowSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SuaChannels getChannelType() {
		return SuaChannels.GAUCE;
	}
}
