package jcf.sua.ux.xml.mvc.converter;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.mvc.converter.MciHttpMessageConverter;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.xml.mvc.XmlRequest;
import jcf.sua.ux.xml.mvc.XmlResponse;

/**
 *
 * {@link MciHttpMessageConverter} 의 XML 구현체
 *
 * @author nolang
 *
 */
public class XmlHttpMessageConverter extends MciHttpMessageConverter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SuaChannels getChannelType() {
		return SuaChannels.XML;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return Arrays.asList(new MediaType("application", "xml+sua"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciRequest getMciRequest(DataSetReader reader, MciRequestValidator requestValidator) {
		return new XmlRequest(reader);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MciResponse getMciResponse() {
		return new XmlResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetReader getDataSetReader(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSetWriter getDataSetWriter(HttpServletRequest request,
			HttpServletResponse response, MciDataSetAccessor accessor) {
		// TODO Auto-generated method stub
		return null;
	}

}
