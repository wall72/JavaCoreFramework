package jcf.sua;

import java.util.List;

/**
 *
 * * <p>
 * 참고사항
 * <nl>
 * <li>Su 프레임워크와 RPC 통신으로 인한 Mapping이 되어있습니다. 변경시 꼭 SuFramework 담당자에게 연락하세요.</li>
 * </nl>
 * </p>
 * <br/>
 *
 * @author Jeado
 *
 */
public class MessageHeaders {

	private List<String> succesMessages;

	public void setSuccesMessages(List<String> succesMessages) {
		this.succesMessages = succesMessages;
	}

	public List<String> getSuccesMessages() {
		return succesMessages;
	}
}
