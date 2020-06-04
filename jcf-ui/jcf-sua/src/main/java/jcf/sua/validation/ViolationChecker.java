package jcf.sua.validation;

/**
 *
 * ViolationChecker 인터페이스
 *
 * @author mina
 *
 * @param <E>
 */
public interface ViolationChecker<E> {

	public void checkViolations(E bean);

}