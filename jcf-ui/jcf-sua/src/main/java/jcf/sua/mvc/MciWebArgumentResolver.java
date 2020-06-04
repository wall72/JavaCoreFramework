package jcf.sua.mvc;

import jcf.sua.SuaChannels;
import jcf.sua.dataset.DataSetConverter;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.mvc.validation.SkipValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

/**
*
* 컨트롤러에 매개변수로 등록된 채널 접근자 ({@link MciRequest}, {@link MciResponse}) 를 생성하여 컨트롤러에 전달한다.
*
* @author nolang
*
*/
public abstract class MciWebArgumentResolver implements WebArgumentResolver {

	protected static final Logger logger = LoggerFactory.getLogger(MciWebArgumentResolver.class);

	protected DataSetConverter dataSetConverter;

	@Autowired(required = false)
	protected MciRequestValidator requestValidator;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.bind.support.WebArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.context.request.NativeWebRequest)
	 */
	public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {

		if (MciRequestContextHolder.get().getMciChannelType() == getChannelType()) {
			if (MciRequestAdapter.class.isAssignableFrom(methodParameter.getParameterType())) {
				return BeanUtils.instantiateClass(methodParameter.getParameterType().getConstructor(MciRequest.class), buildMciRequest(methodParameter, webRequest));
			}

			if (MciRequest.class.isAssignableFrom(methodParameter.getParameterType())) {
				return buildMciRequest(methodParameter, webRequest);
			}

			if (MciResponseAdapter.class.isAssignableFrom(methodParameter.getParameterType())) {
				return BeanUtils.instantiateClass(methodParameter.getParameterType().getConstructor(MciResponse.class), buildMciResponse(webRequest));
			}

			if (MciResponse.class.isAssignableFrom(methodParameter.getParameterType())) {
				return buildMciResponse(webRequest);
			}

			if (DataSetStreamWriter.class.isAssignableFrom(methodParameter.getParameterType())) {
				return MciRequestContextHolder.get().getDataSetStreamWriter();
			}
		}

		return UNRESOLVED;
	}

	/**
	 *
	 * 하향 채널 접근에 필요한 편의 클래스를 생성하여 반환한다.
	 *
	 * @param webRequest
	 * @return
	 */
	protected Object buildMciResponse(NativeWebRequest webRequest) {
		MciResponse mciResponse = getMciResponse(webRequest);

		/*
		 * 사용자 정의 객체를 처리하기 위한 컨버터 등록
		 */
		((MciDataSetAccessor) mciResponse).setDataSetConverter(dataSetConverter);

		MciRequestContextHolder.get().setDataSetAccessor((MciDataSetAccessor) mciResponse);

		if(logger.isDebugEnabled()){
			logger.trace("[JCF-SUA] 파라미터 변환 작업을 수행합니다. : ChannelType={}, Class={}", new Object[]{getChannelType(), this.getClass()});
		}

		return mciResponse;
	}

	/**
	 *
	 * 상향 채널 접근에 필요한 편의 클래스를 생성하여 반환한다.
	 *
	 * @param methodParameter
	 * @param webRequest
	 * @return
	 */
	protected Object buildMciRequest(MethodParameter methodParameter, NativeWebRequest webRequest) {
		if(logger.isDebugEnabled()){
			logger.trace("[JCF-SUA] 파라미터 변환 작업을 수행합니다. : ChannelType={}, Class={}", new Object[]{getChannelType(), this.getClass()});
		}

		MciRequestValidator validator = null;

		if(methodParameter.getParameterAnnotation(SkipValidation.class) == null)	{
			validator = requestValidator;
		}

		return getMciRequest(webRequest, validator);
	}

	/**
	 *
	 * 채널의 타입을 반환한다.
	 *
	 * @return
	 */
	protected abstract SuaChannels getChannelType();

	/**
	 *
	 * 상향 채널 접근에 필요한 편의 클래스를 생성하여 반환한다.
	 *
	 * @param webRequest
	 * @return
	 */
	protected abstract MciRequest getMciRequest(NativeWebRequest webRequest, MciRequestValidator requestValidator);

	/**
	 *
	 * 하향 채널 접근에 필요한 편의 클래스를 생성하여 반환한다.
	 *
	 * @param webRequest
	 * @return
	 */
	protected abstract MciResponse getMciResponse(NativeWebRequest webRequest);

	/**
	 *
	 * 요청을 처리할 상향 채널을 반환한다.
	 *
	 * @param webRequest
	 * @return
	 */
	protected DataSetReader getDataSetReader(NativeWebRequest webRequest) {
		return MciRequestContextHolder.get().getDataSetReader();
	}

	/**
	 *
	 * 데이터셋 컨버터의 의존성을 주입한다.
	 *
	 * @param dataSetConverter
	 */
	public void setDataSetConverter(DataSetConverter dataSetConverter) {
		this.dataSetConverter = dataSetConverter;
	}

	/**
	 *
	 * 유효성 체크에 필요한 Validator의 의존성을 주입한다.
	 *
	 * @param requestValidator
	 */
	public void setRequestValidator(MciRequestValidator requestValidator) {
		this.requestValidator = requestValidator;
	}
}
