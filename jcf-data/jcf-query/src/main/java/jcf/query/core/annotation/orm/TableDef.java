package jcf.query.core.annotation.orm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 테이블 정보를 지정한다.
 * <pre>
 *
 * @see    jcf.query.core.evaluator.ObjectRelationMappingQueryEvaluator
 *
 * @author nolang
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface TableDef {
	/**
	 * Table Name
	 * @return
	 */
	String tableName();

	/**
	 * Table Alias
	 * @return
	 */
	String alias() default "";
}
