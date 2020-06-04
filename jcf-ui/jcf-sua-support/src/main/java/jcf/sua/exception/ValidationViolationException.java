package jcf.sua.exception;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import jcf.sua.support.validation.ViolationInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model JSR303을 이용한  Validation 위반 에 대한 Exception
 * @author mina
 *
 */
public class ValidationViolationException extends RuntimeException {

	private static final long serialVersionUID = 8737632253185272562L;
	private static final Logger logger = LoggerFactory.getLogger(ValidationViolationException.class);
	protected String message;
	protected List<ViolationInfo>  violationMsg;

	public ValidationViolationException() {
		super();
	}

	public ValidationViolationException(String msg) {
		super(msg);
		this.message=msg;
	}


	/**
	 * 위반된 모델의 property, 메시지 등을 정보를 ViolationInfo 클래스에 담아 ArrayList 에 더한다.
	 * 전역 변수인 message 에서 ||| 구분자로 에러 메시지 담는다.
	 */
	public <E> ValidationViolationException(Set<ConstraintViolation<E>> constraintViolations) {
		message="";
		violationMsg= new ArrayList<ViolationInfo>();
		Iterator<ConstraintViolation<E>> iterator=constraintViolations.iterator();
		logger.error("Violation Number is ", constraintViolations.size());
		while (	iterator.hasNext()) {
			ConstraintViolation<E> constraintViolation = iterator.next();
			logger.error("invalid value : {}", constraintViolation.getInvalidValue());
			logger.error("message : {} {}", constraintViolation.getPropertyPath() ,constraintViolation.getMessage());
			ViolationInfo violationInfo= new ViolationInfo();
			violationInfo.setViolationProperty(constraintViolation.getPropertyPath());
			violationInfo.setInvalidValue(constraintViolation.getInvalidValue());
			violationInfo.setViolationMsg(constraintViolation.getMessage());
			violationMsg.add(violationInfo);
			message=message+"||| "+constraintViolation.getPropertyPath() +"  "+constraintViolation.getMessage()+"  ";
		}

		message=message.substring(3);
		logger.info("message : "+ message );
	}

	public ValidationViolationException(String msg, Exception e) {
		super(msg, e);
	}

	public String getMessage() {
		return message;
	}

	public List<ViolationInfo> getViolationMsg() {
		return violationMsg;
	}

}
