package jcf.sua.ux.nexacro.mvc.converter;

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
import jcf.sua.ux.nexacro.dataset.NexacroDataSetReader;
import jcf.sua.ux.nexacro.dataset.NexacroDataSetWriter;
import jcf.sua.ux.nexacro.mvc.NexacroRequest;
import jcf.sua.ux.nexacro.mvc.NexacroResponse;

public class NexacroHttpMessageConverter extends MciHttpMessageConverter {

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return Arrays.asList(new MediaType("application", "nexacroplatform"));
	}

	@Override
	protected MciRequest getMciRequest(DataSetReader reader, MciRequestValidator requestValidator) {
		return new NexacroRequest( (NexacroDataSetReader)reader, requestValidator);
	}

	@Override
	protected MciResponse getMciResponse() {
		return new NexacroResponse();
	}

	@Override
	protected DataSetReader getDataSetReader(HttpServletRequest request) {
		return new NexacroDataSetReader(request, null);
	}

	@Override
	protected DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor) {
		return new NexacroDataSetWriter(request, response, null, accessor);
	}

	@Override
	protected SuaChannels getChannelType() {
		return SuaChannels.NEXACRO;
	}
	
}
