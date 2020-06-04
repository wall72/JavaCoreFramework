package jcf.sua.mvc.converter;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetConverter;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciRequestContextHolder;
import jcf.sua.mvc.MciResponse;
import jcf.sua.mvc.validation.MciRequestValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;

/**
 *
 * REST 통신을 위한 메세지컨버터<br/>
 * XML이나 JSON을 이용한 통신에 이용할 수 있다.<br/>
 * request를 model에 바인딩하고 다시 view를 이용해 클라이언트로 보내는 대신 request와 response 자체를 메시지로 다룰 때 사용.<br/>
 * 파라미터에 RequestBody, 메소드에 ResponseBody annotation을 붙여서 사용한다.<br/>
 * annotation이 없으면 WebArgumentResolver, ViewResolver에서 파라미터를 변환하고 model과 view를 돌려준다.
 *
 * @author nolang
 *
 */
public abstract class MciHttpMessageConverter implements HttpMessageConverter<Object> {

	private static final Logger logger = LoggerFactory.getLogger(MciHttpMessageConverter.class);

	private static final Object UNRESOLVED = null;

	private DataSetConverter dataSetConverter;

	@Autowired(required = false)
	protected MciRequestValidator requestValidator;

	public void setDataSetConverter(DataSetConverter dataSetConverter) {
		this.dataSetConverter = dataSetConverter;
	}

	public void setRequestValidator(MciRequestValidator requestValidator) {
		this.requestValidator = requestValidator;
	}

	/**
	 *
	 * 처리가능한 요청(Read)인지 판단한다.
	 *
	 * @param clazz
	 * @param mediaType
	 */
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		boolean canRead = (MciRequest.class.isAssignableFrom(clazz) || MciResponse.class.isAssignableFrom(clazz)) && isSupportedChannel(mediaType);

		if(canRead && logger.isDebugEnabled()){
			logger.trace("[JCF-SUA] {} 객체 읽기를 위한 메세지 컨버터를 초기화합니다. : Class={}", new Object[] { getChannelType(), this.getClass() });
		}

		return canRead;
	}

	/**
	 *
	 * 처리가능한 요청(Write)인지 판단한다.
	 *
	 * @param clazz
	 * @param mediaType
	 */
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		boolean canWrite = MciResponse.class.isAssignableFrom(clazz) && isSupportedChannel(mediaType);

		if(canWrite && logger.isDebugEnabled()){
			logger.trace("[JCF-SUA] {} 객체 쓰기를 위한 메세지 컨버터를 초기화합니다. : Class={}", new Object[] { getChannelType(), this.getClass() });
		}

		return canWrite;
	}

	/**
	 *
	 * @param mediaType
	 * @return
	 */
	protected boolean isSupportedChannel(MediaType mediaType) {
		return getSupportedMediaTypes().contains(mediaType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#read(java.lang.Class, org.springframework.http.HttpInputMessage)
	 */
	public Object read(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

		if(MciRequest.class.isAssignableFrom(clazz)){
			if(logger.isDebugEnabled()){
				logger.trace("[JCF-SUA] {} 메세지 컨버터를 이용하여 요청 데이터를 조회합니다. : Class={}", new Object[] { getChannelType(), this.getClass() });
			}

			DataSetReader reader = MciRequestContextHolder.get().getDataSetReader();

			if (reader == null) {
				MciRequestContextHolder.get().setHttpServletRequest(getHttpServletRequest(inputMessage));
				MciRequestContextHolder.get().setMciChannelType(getChannelType());

				if(logger.isDebugEnabled()){
					logger.trace("[JCF-SUA] {} 데이터셋 리더를 생성합니다. : Class={}", new Object[] { getChannelType(), this.getClass() });
				}

				reader = getDataSetReader(MciRequestContextHolder.get().getHttpServletRequest());

				MciRequestContextHolder.get().setDataSetReader(reader);
			}

//			MciRequestContextHolder.get().setHttpServletRequest(getHttpServletRequest(inputMessage));
//			MciRequestContextHolder.get().setMciChannelType(getChannelType());
//
//			if(logger.isDebugEnabled()){
//				logger.trace("[JCF-SUA] {} 데이터셋 리더를 생성합니다. : Class={}", new Object[] { getChannelType(), this.getClass() });
//			}
//
//			DataSetReader reader = getDataSetReader(MciRequestContextHolder.get().getHttpServletRequest());
//
//			MciRequestContextHolder.get().setDataSetReader(reader);

			if(logger.isDebugEnabled()){
				logger.trace("[JCF-SUA] 파라미터 변환 작업을 수행합니다. : ChannelType={}, Class={}, ArguemntType={}", new Object[]{getChannelType(), this.getClass(), clazz});
			}

			return getMciRequest(reader, requestValidator);
		}

		if(MciResponse.class.isAssignableFrom(clazz)){
			if(logger.isDebugEnabled()){
				logger.trace("[JCF-SUA] 파라미터 변환 작업을 수행합니다. : ChannelType={}, Class={}, ArguemntType={}", new Object[]{getChannelType(), this.getClass(), clazz});
			}

			MciResponse mciResponse = getMciResponse();

			/*
			 * 사용자 정의 객체를 처리하기 위한 컨버터 등록
			 */
			((MciDataSetAccessor) mciResponse).setDataSetConverter(dataSetConverter);

			return mciResponse;
		}

		return UNRESOLVED;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#write(java.lang.Object, org.springframework.http.MediaType, org.springframework.http.HttpOutputMessage)
	 */
	public void write(Object accessor, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
			if(logger.isDebugEnabled()){
				logger.trace("[JCF-SUA] {} 데이터셋 라이터를 생성합니다. : Class={}", new Object[]{getChannelType(), this.getClass()});
			}

			HttpServletRequest request = MciRequestContextHolder.get().getHttpServletRequest();
			HttpServletResponse response = getHttpServletResponse(outputMessage);

			DataSetWriter writer = getDataSetWriter(request, response, (MciDataSetAccessor) accessor);

			if(logger.isDebugEnabled()){
				logger.trace("[JCF-SUA] {} 메세지 컨버터를 이용하여 응답 데이터를 렌더링합니다. : Class={}", new Object[]{getChannelType(), this.getClass()});
			}

			writer.write();

			if(logger.isDebugEnabled()){
				logger.trace("[JCF-SUA] {} 요청 처리를 위해 생성된 데이터를 초기화합니다. : Class={}", new Object[]{getChannelType(), this.getClass()});
			}

			MciRequestContextHolder.clear();
	}

	protected HttpServletRequest getHttpServletRequest(HttpInputMessage inputMessage) {
		ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess((ServletServerHttpRequest) inputMessage);
		return (HttpServletRequest) propertyAccessor.getPropertyValue("servletRequest");
	}

	protected HttpServletResponse getHttpServletResponse(HttpOutputMessage outputMessage) {
		ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess((ServletServerHttpResponse) outputMessage);
		return (HttpServletResponse) propertyAccessor.getPropertyValue("servletResponse");
	}

	/**
	 *
	 * 채널의 타입을 반환한다.
	 *
	 * @return Channels
	 */
	protected abstract SuaChannels getChannelType();

	/**
	 * 본 채널에서 처리가능한 요청의 목록 (MediaTypes)
	 */
	public abstract List<MediaType> getSupportedMediaTypes();

	/**
	 *
	 * 상향 채널 접근에 필요한 편의 클래스를 생성하여 반환한다.
	 *
	 * @param reader
	 * @param requestValidator
	 * @return
	 */
	protected abstract MciRequest getMciRequest(DataSetReader reader, MciRequestValidator requestValidator);

	/**
	 *
	 * 하향 채널 접근에 필요한 편의 클래스를 생성하여 반환한다.
	 *
	 * @return
	 */
	protected abstract MciResponse getMciResponse();

	/**
	 *
	 * 클라이언트로 부터 전송된 데이터를 처리할 상향 채널을 반환한다.
	 *
	 * @param request
	 * @param response
	 * @return DataSetReader
	 */
	protected abstract DataSetReader getDataSetReader(HttpServletRequest request);

	/**
	 *
	 * 클라이언트로 데이터를 전송할 하향 채널을 반환한다.
	 *
	 * @param request
	 * @param response
	 * @param accessor
	 * @return DataSetWriter
	 */
	protected abstract DataSetWriter getDataSetWriter(HttpServletRequest request, HttpServletResponse response, MciDataSetAccessor accessor);

}