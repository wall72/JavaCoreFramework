package jcf.sua.mvc.validation;

/**
 *
 * 클라이언트로부터 전달된 데이터의 유효성을 확인한다.
 *
 * @author nolang
 *
 */
public interface MciRequestValidator {

	/**
	 *
	 * 객체화된 데이터의 유효성을 체크한다.
	 *
	 * @param bean
	 */
	void checkValidation(Object bean);

}
