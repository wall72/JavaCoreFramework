package jcf.query.core.decorator;

/**
 *
 * @author nolang
 *
 */
public interface QueryDecorator {

	void beforeExecution(Object... args);

	void afterExecution(Object... args);

}
