package jcf.dao.streaming;

import java.util.LinkedHashMap;


/**
 * iBATIS 대용량 처리시 이용하는 {@link AbstractCsvStreamingRowHandler}나 {@link AbstractXmlStreamingRowHandler}를 이용할 때 
 * 사용하는 요소별 스트림 라이터. 
 * @author setq
 *
 */
public interface EntityWriter {
	
	/**
	 * 한 행을 시작하기 위해 상태를 리셋함. 줄바꿈 등에 이용된다.
	 */
	void reset();
	
	/**
	 * 한 행중 한 컬럼의 라벨과 값을 스트림에 보낸다. 
	 * @param name 컬럼 라벨
	 * @param value 컬럼 값
	 */
	void write(String name, Object value);
	
	/**
	 * 한 행 단위로 스트림에 보낸다.
	 * @param valueObject {@link LinkedHashMap} 타입의 행 데이터(여러 개의 컬럼으로 구성된)
	 */
	void write(Object valueObject);
}