package jcf.sua.support.trace.element;


/**
 * <pre>
 * 관리하고자 하는 사용자의 이력 정보 처리 지점을 추상화한 인터페이스.
 * 각 지점별로 이 인터페이스를 구현하여 관련 정보를 구성하면 됨.
 * </pre>
 *
 */
public interface TraceElement {

	String getElementName();

	String getElementContents();
}
