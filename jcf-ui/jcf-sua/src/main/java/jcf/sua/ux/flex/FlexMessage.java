package jcf.sua.ux.flex;

import jcf.sua.Message;
import jcf.sua.MessageHeaders;

/**
 *
 * SU Framework으로 부터 전송된 데이터의 자료 구조를 추상화한 DTO 클래스
 *
 * * <p>
 * 참고사항
 * <nl>
 * <li>Su 프레임워크와 RPC 통신으로 인한 Mapping이 되어있습니다. 변경시 꼭 SuFramework 담당자에게 연락하세요.</li>
 * </nl>
 * </p>
 * <br/>
 *
 * @author nolang
 *
 * @param <T>
 */
public final class FlexMessage<T> implements Message<T> {

	private MessageHeaders messageHeaders;
	private T payload;

	public T getPayload() {
		return payload;
	}

	public void setPayload(T payload) {
		this.payload = payload;
	}

	public void setMessageHeaders(MessageHeaders messageHeaders) {
		this.messageHeaders = messageHeaders;
	}

	public MessageHeaders getMessageHeaders() {
		return messageHeaders;
	}
}
