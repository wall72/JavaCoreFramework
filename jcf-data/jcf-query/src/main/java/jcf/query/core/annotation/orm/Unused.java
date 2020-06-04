package jcf.query.core.annotation.orm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * SELECT 절에 표현되지 않는 컬럼을 지정한다.
 * <pre>
 *
 * @see    jcf.query.core.annotation.orm.ColumnDef
 * @see    jcf.query.core.evaluator.ObjectRelationMappingQueryEvaluator
 *
 * @author nolang
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Unused {

}
