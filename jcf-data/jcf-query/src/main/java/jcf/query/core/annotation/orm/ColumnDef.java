package jcf.query.core.annotation.orm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jcf.query.core.evaluator.definition.ColumnType;

/**
 * <pre>
 * 클래스 변수와 매핑되는 테이블의 컬럼 및 부가 정보를 지정한다.
 * <pre>
 *
 * @see    jcf.query.core.evaluator.definition.ColumnType
 * @see    jcf.query.core.annotation.orm.PrimaryKey
 * @see    jcf.query.core.annotation.orm.ReferenceKey
 * @see    jcf.query.core.annotation.orm.Updatable
 * @see    jcf.query.core.annotation.orm.Unused
 *
 * @author nolang
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ColumnDef {
	/**
	 * Column Name 지정
	 * @return
	 */
	String columnName();

	/**
	 * Column Alias 지정
	 * @return
	 */
	String alias() default "";

	/**
	 * Column Type 지정
	 *  - ColumnType.NUMBER : 숫자타입의 컬럼
	 *  - ColumnType.DATE : 날짜타입의 컬럼
	 *  - ColumnType.ANY : 컬럼의 타입을 지정하지 않음 - default
	 * @return
	 */
	ColumnType columnType() default ColumnType.ANY;

	/**
	 * 조회시 함께 사용되어야 할 Function을 정의할 때  suffix와 함께 사용된다.
	 *
	 *   ex) to_char(CREATE_DATE, 'yyyy-mm-dd hh24:mi:ss')
	 *
	 *       -> @ColumnDef(columnName = "CREATE_DATE", columnType = ColumnType.DATE, prefix = "to_char(", suffix = ", 'yyyy-mm-dd hh24:mi:ss')")
	 *
	 * @return
	 */
	String prefix() default "";
	String suffix() default "";
}
