package jcf.query.core.annotation.orm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * Group by 절에 사용될 컬럼을 지정한다.
 * <pre>
 *
 * @see    jcf.query.core.evaluator.ObjectRelationMappingQueryEvaluator
 *
 * @author nolang
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface GroupBy {
	/**
	 * Group by 절에 사용되는 컬럼의 순서를 지정한다.
	 * @return
	 */
	int sortOrder();
}
