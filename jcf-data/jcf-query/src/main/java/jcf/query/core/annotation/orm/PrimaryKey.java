package jcf.query.core.annotation.orm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jcf.query.core.evaluator.definition.KeyType;

/**
 * <pre>
 * Primark key로 사용될 컬럼을 지정한다.
 * <pre>
 *
 * @see    jcf.query.core.evaluator.ObjectRelationMappingQueryEvaluator
 * @see    jcf.query.core.evaluator.definition.KeyType
 *
 * @author nolang
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface PrimaryKey {
	/**
	 * <pre>
	 * KeyType
	 *
	 *  1. NORMAL - PrimaryKey로 지정된 Field는 모든 경우에 where 조건절에 포함된다.
	 *  2. DYNAMIC - PrimaryKey로 지정된 Field는 값을 가지는 경우에 where 조건절에 포함된다.
	 * <pre>
	 *
	 * @return
	 */
	KeyType keyType() default KeyType.NORMAL;

	/**
	 * <pre>
	 * 정의된 키컬럼을 특정 상수값으로 제한할때 사용한다.
	 *
	 * ex)
	 *   아래와 같이 정의된 Field는..
	 *
	 *    &#64;PrimaryKey(defaultValue = "ANY_ROLE")
	 *	  &#64;ColumnDef(columnName = "ROLE_ID")
	 *	  private String roleId;
	 *
	 *    select ... from ... where ROLE_ID = 'ANY_ROLE' 로 evaluation 된다.
	 * <pre>
	 *
	 * @return
	 */
	String defaultValue() default "";

	/**
	 * <pre>
	 * 정의된 키컬럼의 값을 세션에 정의된 값으로 설정한다.
	 * <pre>
	 *
	 * @return
	 */
	String sessionAttribute() default "";

	/**
	 * <pre>
	 * 조회시 함께 사용되어야 할 Function을 정의할 때  suffix와 함께 사용된다.
	 *
	 *   ex) to_char(CREATE_DATE, 'yyyy-mm-dd hh24:mi:ss')
	 *
	 *       -> &#64;ColumnDef(columnName = "CREATE_DATE", columnType = ColumnType.DATE, prefix = "to_char(", suffix = ", 'yyyy-mm-dd hh24:mi:ss')")
	 * <pre>
	 * @return
	 */
	String prefix() default "";
	String suffix() default "";
}
