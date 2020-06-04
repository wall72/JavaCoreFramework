package jcf.sua.support.validation;

import javax.validation.Path;

/**
 * ValidationViolation 정보
 *
 * @author mina
 *
 */
public class ViolationInfo {

	private Path violationProperty; // 위반된 모델의 property 이름
	private String violationMsg; // 위반 메시지
	private Object invalidValue; // 유효하지 않은 값 (int 값이 대한 자리 수가 틀린 경우 등에 사용 )

	public Path getViolationProperty() {
		return violationProperty;
	}

	public void setViolationProperty(Path violationProperty) {
		this.violationProperty = violationProperty;
	}

	public String getViolationMsg() {
		return violationMsg;
	}

	public void setViolationMsg(String violationMsg) {
		this.violationMsg = violationMsg;
	}

	public Object getInvalidValue() {
		return invalidValue;
	}

	public void setInvalidValue(Object invalidValue) {
		this.invalidValue = invalidValue;
	}

}
