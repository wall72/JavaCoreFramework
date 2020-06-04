package jcf.naming;
/**
 * 네이밍 룰. 양방향성을 가지도록 지원.
 * <p>
 * 함수관계가 성립하지 않는 집합에 적용된 경우 예외 발생.
 * <p>
 * encode/decode의 구분은 유저 인터페이스에 가까운 쪽을 원본 데이터로 보았을 때의 방향 기준.
 * 
 * @author setq
 */
/**
 * @author setq
 *
 */
public interface ColumnNameConverter {

	/**
	 * 인코드된 이름으로부터 원본 이름을 꺼낸다.
	 * 
	 * @param encodedString
	 * @return
	 * @throws ColumnNameConversionException
	 */
	String decode(String encodedString) throws ColumnNameConversionException;

	/**
	 * 원본 이름을 인코드한다.
	 * 
	 * @param decodedString
	 * @return
	 * @throws ColumnNameConversionException
	 */
	String encode(String decodedString) throws ColumnNameConversionException;

}