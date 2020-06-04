package jcf.sua.mvc.validation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 * Bean 유효성 체크를 수행하지 않기 위하여 컨트롤러에 매개변수로 전달되는 {@link MciRequest}와 함께 사용된다.
 *
 * @author nolang
 *
 */
@Target({ PARAMETER })
@Retention(RUNTIME)
public @interface SkipValidation {

}
