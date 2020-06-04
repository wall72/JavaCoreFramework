package jcf.sua.support.validation;

import javax.validation.groups.Default;
/**
 *
 * @author mina
 *Valdiation Group 지정
 *	@NotEmpty(groups={ Group.U.class}) 처럼 사용하면 rowstatus 가 해당조건일 경우에만  벨리데이션 체크함
 */
public class Group {


	/**
	 *
	 * Valdiation Group 지정 INSERT 일 경우
	 *
	 */
	public static interface INSERT extends Default {

	};

	/**
	 *
	 * Valdiation Group 지정 UPDATE 일 경우
	 *
	 */
	public static interface UPDATE extends Default {

	}

	/**
	 *
	 * Valdiation Group 지정 DELETE 일 경우
	 *
	 */
	public static interface DELETE extends Default {

	}

	/**
	 *
	 * Valdiation Group 지정 항상 체크할 경우
	 *
	 */
	public static interface ALWAYS extends Default {
	}

}
