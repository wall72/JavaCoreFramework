package jcf.sua.support.trace.recorder;

import java.util.Date;
import java.util.Iterator;

import jcf.sua.exception.SuaBusinessException;
import jcf.sua.support.trace.LogConstant;
import jcf.sua.support.trace.Trace;
import jcf.sua.support.trace.element.ErrorTraceElement;
import jcf.sua.support.trace.element.TraceElement;
import jcf.sua.support.trace.element.WarningTraceElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * 트레이스 정보를 처리하는 역할을 해주는 {@link TraceRecorder}를 구현하여 트레이스 정보를 콘솔 로그로 남김
 *
 * @author nolang
 */
public class DefaultLogTraceRecorder implements TraceRecorder, InitializingBean {
	/**
	 * 기본 메시지 포맷
	 */
	private static final String DEFAULT_BASE_MESSAGE = "[서비스 처리 이력 정보] 트랜잭션 ID:{}, 요청 처리 시작 시간:{}, 요청 처리 종료 시간:{}, 수행 시간:{}ms";

	/**
	 * 기본 로거
	 */
	private static Logger logger = LoggerFactory.getLogger(LogConstant.TRACE_LOGGER);

	@Autowired(required = false)
	private MessageSourceAccessor messageSourceAccessor;

	public void setMessageSourceAccessor(
			MessageSourceAccessor messageSourceAccessor) {
		this.messageSourceAccessor = messageSourceAccessor;
	}

	public void recordTraceElement(Trace t) {
		logger.info(DEFAULT_BASE_MESSAGE, getBaseMessageArgument(t));

		if (hasTraceElement(t)) {
			for (Iterator<TraceElement> it = t.getElements().iterator(); it
					.hasNext();) {
				TraceElement element = it.next();

				if (element instanceof ErrorTraceElement) {
					logger.error("\t[에러 이력 정보]" + element.getElementContents() + "\n", ((ErrorTraceElement) element).getCause());
				} else {
					if (element instanceof WarningTraceElement) {
						logger.warn("\t[경고 이력 정보]{}: {}", new Object[] { element.getElementName(), element.getElementContents() });
					} else {
						logger.trace("\t[상세 이력 정보] {}: {}", new Object[] { element.getElementName(), element.getElementContents() });
					}
				}
			}
		}
	}

	public void recorErrors(Exception ex) {
		if (ex != null) {
			String errorMessage = ex.getMessage();

			if (messageSourceAccessor != null && ex instanceof SuaBusinessException) {
				try	{
					errorMessage = messageSourceAccessor.getMessage(((SuaBusinessException) ex).getCode());
				}catch (Exception e) {
				}
			}

			logger.error("\t[에러 이력 정보]" + errorMessage, ex);
		}
	}

	public void afterPropertiesSet() throws Exception {
	}

	/**
	 * 해당 트레이스 정보가 있는지 점검
	 *
	 * @param t
	 * @return
	 */
	private boolean hasTraceElement(Trace t) {
		return t.getElements() != null && !t.getElements().isEmpty();
	}

	/**
	 * 트레이스에서 기본 정보 추출
	 *
	 * @param t
	 * @return
	 */
	private Object[] getBaseMessageArgument(Trace t) {
		return new Object[] { t.getTransactionId(), new Date(t.getStartTime()),
				new Date(t.getEndTime()), t.calculateDuration() };
	}

}
