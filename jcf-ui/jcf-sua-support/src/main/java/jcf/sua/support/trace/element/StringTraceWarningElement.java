package jcf.sua.support.trace.element;


/**
 * 경고 상황을 표현하기 위해 {@link StringTraceElement}를 상속하여 {@link WarningTraceElement}를 구현함
 *
 */
public class StringTraceWarningElement extends StringTraceElement implements WarningTraceElement {

	public StringTraceWarningElement(String name, String contents) {
		super(name, contents);
	}

}
