package jcf.sua.support.trace.element;



/**
 * 이력 추적 로그 기능이 활성화되어 있지 않을 시에 불필요한 문자열 파싱을 피하기 위해 로그 문자열 파싱 로직을 캡슐화 해주는 인터페이스
 *
 */
public interface ElementBuilder {

	/**
	 * {@link TraceElement}의 내부 정보를 기반으로 로깅 정보를 생성
	 *
	 * @return 생성된 로깅 정보
	 */
	String build();
}
