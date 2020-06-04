package jcf.sua.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.exception.SuaBusinessException;
import jcf.sua.mvc.view.MciView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * 시스템에서 발생한 예외를 통합하여 처리한다.
 *
 * @author 고강민
 *
 */
public class MciExceptionViewResolver implements HandlerExceptionResolver, Ordered {

	private static final Logger logger = LoggerFactory.getLogger(MciExceptionViewResolver.class);

	@Autowired(required = false)
	private MessageSourceAccessor messageSourceAccessor;

	@Autowired(required = false)
	private MciExceptionTraceHandler traceHandler;

	private List<MciExceptionMessageWriter> exceptionMessageWriters;

	private MciExceptionMessageWriter defaultExceptionMessageWriter = new MciExceptionMessageWriter() {

		public String buildExceptionMessage(MessageSourceAccessor messageSourceAccessor, Exception exception) {
			String code = "";
			String defaultMessage = "";
			Object[] arguments = null;
			String message = exception.getMessage();

			if(exception instanceof SuaBusinessException)	{
				code = ((SuaBusinessException) exception).getCode();
				defaultMessage = ((SuaBusinessException) exception).getDefaultMessage();
				message = ((SuaBusinessException) exception).getDefaultMessage();
				arguments = ((SuaBusinessException) exception).getArguments();
			}

			if(messageSourceAccessor != null){
				try	{
					message = messageSourceAccessor.getMessage(code, arguments);
				}catch(Exception mex){
					message = defaultMessage;
				}

				if (!StringUtils.hasText(message)) {
					message = exception.getMessage();
				}
			}

			return message;
		}

		public boolean accept(Exception exception) {
			return true;
		}
	};

	private HttpStatus exceptionStatusCode = HttpStatus.METHOD_FAILURE;

	private int order = Integer.MAX_VALUE - 1000;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.servlet.HandlerExceptionResolver#resolveException(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
	 */
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

		if(logger.isDebugEnabled())	{
			logger.debug(ex.getMessage());
			ex.printStackTrace();
		}

		if(ex.getClass().isAnnotationPresent(ResponseStatus.class))	{
			response.setStatus(ex.getClass().getAnnotation(ResponseStatus.class).value().value());
		} else {
			response.setStatus(exceptionStatusCode.value());
		}

		MciDataSetAccessor mciDataSetAccesor = MciRequestContextHolder.get().getDataSetAccessor();

		if(!MciRequestContextHolder.get().isMciRequest()){
			return null;
		}

		mciDataSetAccesor.setExceptionMessage(buildExceptionMessage(messageSourceAccessor, ex));

		MciRequestContextHolder.get().getDataSetWriter().setDataSetAccessor(mciDataSetAccesor);

		/*
		 * 예외를 이용한 후처리 (ex. 이력처리등..)
		 */
		if(traceHandler != null)	{
			traceHandler.handler(request, response, ex);
		}

		return new ModelAndView(new MciView());
	}

	/**
	 *
	 * 발생한 예외에 대한 적절한 예외 메세지를 생성하여 반환한다.
	 *
	 * @param ex
	 * @return
	 */
	private String buildExceptionMessage(MessageSourceAccessor messageSourceAccessor, Exception ex) {
		MciExceptionMessageWriter exceptionWriter = defaultExceptionMessageWriter;

		if(exceptionMessageWriters != null)	{
			for(MciExceptionMessageWriter w : exceptionMessageWriters)	{
				if(w.accept(ex))	{
					exceptionWriter = w;
					break;
				}
			}
		}

		String exceptionMessage = exceptionWriter.buildExceptionMessage(messageSourceAccessor, ex);

		logger.debug(exceptionMessage);
		return exceptionMessage;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public void setExceptionStatusCode(HttpStatus exceptionStatusCode) {
		this.exceptionStatusCode = exceptionStatusCode;
	}

	public void setExceptionMessageWriters(List<MciExceptionMessageWriter> exceptionMessageWriters) {
		this.exceptionMessageWriters = exceptionMessageWriters;
	}

	public void setDefaultExceptionMessageWriter(MciExceptionMessageWriter defaultExceptionMessageWriter) {
		this.defaultExceptionMessageWriter = defaultExceptionMessageWriter;
	}
}
