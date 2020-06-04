package jcf.sua.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.SuaChannels;
import jcf.sua.SuaConstants;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.MciRequestContextHolder;
import jcf.sua.mvc.file.MciFileHandler;
import jcf.sua.mvc.file.operator.FileOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
*
* 사용자의 요청을 처리하는 채널의 생명주기를 관리한다.
*
* @author nolang
*
*/
public abstract class MciDataSetHandlerInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(MciDataSetHandlerInterceptor.class);

	@Autowired(required = false)
	protected MciFileHandler fileHandler;

	protected boolean isRestMode = false;

	/**
	 *
	 * 요청을 처리하는 컨트롤러의 메소드가 호출되는 이전 시점에 요청 처리에 적합한 채널 및 부가 정보를 생성한다.
	 *
	 * @param request
	 * @param response
	 * @param handler
	 *
	 * @return Boolean
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		MciRequestContextHolder.get().setHttpServletRequest(request);
		MciRequestContextHolder.get().setHttpServletResponse(response);

		if(MciRequestContextHolder.get().getMciChannelType() == null && checkMciRequest(request)){
			if(logger.isDebugEnabled()){
				logger.trace("[JCF-SUA] {} 데이터셋 리더를 생성합니다. : Class={}", new Object[]{getChannelType(), this.getClass()});
			}

			MciRequestContextHolder.get().setMciRequest(checkMciRequest(request));
			MciRequestContextHolder.get().setDataSetReader(getDataSetReader(request, response));

			/**
			 * @Todo Excel 출력 기능이 추가될 경우 아래 두개의 Writer를 교체해주는 로직이 필요.. or 채널 타입을 바꾸는것도 방법일듯....
			 */
			MciRequestContextHolder.get().setDataSetWriter(getDataSetWriter(request, response, null));
			MciRequestContextHolder.get().setDataSetStreamWriter(getDataSetStreamWriter(request, response));
			MciRequestContextHolder.get().setMciChannelType(getChannelType());
		}

		return true;
	}

	/**
	 *
	 * 모든 요청이 처리되었으므로 생성된 채널를 해제한다.
	 *
	 * @param request
	 * @param response
	 * @param handler
	 * @param ex
	 */
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		if(MciRequestContextHolder.get().isMciRequest()  && MciRequestContextHolder.get().getMciChannelType() == getChannelType())	{
			if(logger.isDebugEnabled()){
				logger.trace("[JCF-SUA] {} 요청 처리를 위해 생성된 데이터를 초기화합니다. : Class={}", new Object[]{getChannelType(), this.getClass()});
			}

			MciRequestContextHolder.clear();
		}
	}

	/**
	 *
	 * 컨트롤러에서 반환된 정보를 클라이언트로 전송하기 위한 하향 채널을 생성한다.
	 *
	 * @param request
	 * @param response
	 * @param handler
	 * @param modelAndView
	 */
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		/*
		 * HandlerInterceptor 과 HttpMessageConverter 가 동시에 적용되었을 경우, 그 실행순서는..
		 *
		 * 1. HandlerInterceptor.preHandler
		 * 2. HttpMessageConverter.read
		 * 3. HttpMessageConverter.write
		 * 4. HandlerInterceptor.postHanlder
		 * 5. HandlerInterceptor.afterCompletion
		 *
		 * 의 순서로 동작한다.
		 *
		 */
		if(MciRequestContextHolder.get().isMciRequest() && MciRequestContextHolder.get().getMciChannelType() == getChannelType())	{
			if(logger.isDebugEnabled()){
				logger.trace("[JCF-SUA] {} 데이터셋 라이터를 생성합니다. : Class={}", new Object[]{getChannelType(), this.getClass()});
			}

			MciDataSetAccessor accessor = getDataSetAccessor(request);

			if(accessor.isFileProcessing()){
				modelAndView.setViewName(SuaConstants.STREAMING);
			}

			MciRequestContextHolder.get().setDataSetWriter(getDataSetWriter(request, response, accessor));
		}
	}

	public void setIsRestMode(boolean isRestMode) {
		this.isRestMode = isRestMode;
	}

	/**
	 *
	 * 파일처리를 위한 핸들러를 등록한다.
	 *
	 * @param fileHandler
	 */
	public void setFileHandler(MciFileHandler fileHandler) {
		this.fileHandler = fileHandler;
	}

	protected FileOperator getFileOperator() {
		return fileHandler == null ? null : fileHandler.getFileOperator(getChannelType());
	}

	/**
	 *
	 * 컨트롤러에서 반환한 처리 결과를 ThreadLocal 범위의 변수에 저장한다.
	 *
	 * @param request
	 * @return MciDataSetAccessor
	 */
	protected MciDataSetAccessor getDataSetAccessor(HttpServletRequest request) {
		return MciRequestContextHolder.get().getDataSetAccessor();
	}

	/**
	 *
	 * 채널의 타입을 반환한다.
	 *
	 * @return SuaChannels
	 */
	protected abstract SuaChannels getChannelType();


	/**
	 *
	 * 클라이언트로 부터 전송된 데이터를 처리할 상향 채널을 반환한다.
	 *
	 * @param request
	 * @param response
	 * @return DataSetReader
	 */
	protected abstract DataSetReader getDataSetReader(HttpServletRequest request, HttpServletResponse response);

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

	/**
	 *
	 * 대용량 데이터를 처리할 스트리밍 채널을 반환한다.
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	protected abstract DataSetStreamWriter getDataSetStreamWriter(HttpServletRequest request, HttpServletResponse response);

	/**
	 *
	 * 사용자의 요청 처리에 적합한 채널을 식별한다.
	 *
	 * @param request
	 * @return Boolean
	 */
	protected abstract boolean checkMciRequest(HttpServletRequest request);
}
