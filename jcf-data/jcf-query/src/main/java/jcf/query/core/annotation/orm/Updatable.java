package jcf.query.core.annotation.orm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 수정가능한 (Update statement 에서 set 절에 표현되는..) 컬럼을 지정한다.
 * <pre>
 *
 * @author nolang
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Updatable {

}
