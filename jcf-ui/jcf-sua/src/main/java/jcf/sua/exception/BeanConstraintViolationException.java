package jcf.sua.exception;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;

/**
*
* 빈 유효성 체크 과정에서 발생하는 예외
*
* @author nolang
*
*/
public class BeanConstraintViolationException extends ValidationException {

	private final Set<ConstraintViolation<Object>> constraintViolations;

	/**
	 * Creates a constraint violation report
	 *
	 * @param message error message
	 * @param constraintViolations <code>Set</code> of <code>ConstraintViolation</code>
	 */
	public BeanConstraintViolationException(String message,
										Set<ConstraintViolation<Object>> constraintViolations) {
		super( message );
		this.constraintViolations = constraintViolations;
	}

	/**
	 * Creates a constraint violation report
	 *
	 * @param constraintViolations <code>Set</code> of <code>ConstraintViolation</code>
	 */
	public BeanConstraintViolationException(Set<ConstraintViolation<Object>> constraintViolations) {
		super();
		this.constraintViolations = constraintViolations;
	}

	/**
	 * Set of constraint violations reported during a validation
	 *
	 * @return <code>Set</code> of <code>ConstraintViolation</code>
	 */
	public Set<ConstraintViolation<Object>> getConstraintViolations() {
		return constraintViolations;
	}
}
