package jcf.sua.mvc.validation;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import jcf.sua.exception.BeanConstraintViolationException;

/**
 *
 * {@link MciRequestValidator} 의 구현체
 *
 * @author nolang
 *
 */
public class DefaultRequestValidator implements MciRequestValidator {

	private Validator validator;

	/**
	 * {@inheritDoc}
	 */
	public void checkValidation(Object bean) {
		if (validator == null) {
			return;
		}

		Set<ConstraintViolation<Object>>  constraintViolations = validator.validate(bean);

		if(!constraintViolations.isEmpty())	{
			Iterator<ConstraintViolation<Object>> it = constraintViolations.iterator();

			StringBuilder builder = new StringBuilder();

//			builder.append("[").append(bean.getClass().getName()).append("] ");

			while (it.hasNext()) {
				ConstraintViolation<Object> c = it.next();

//				builder.append("PropertyPath={").append(c.getPropertyPath()).append("} ");
//				builder.append("Invalid value={").append(c.getInvalidValue()).append("} ");
				builder.append("Message={").append(c.getMessage()).append("} ");
			}

			throw new BeanConstraintViolationException("[JCF-SUA] 유효성 체크 위반 - " + builder.toString(), constraintViolations);
		}
	}

	/**
	 *
	 * 빈 유효성 체크를 위한 Validator 의존성 주입
	 *
	 * @param validator
	 */
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
}
