package jcf.dao.jdbctemplate;

import java.util.List;

/**
 * Spring의 {@link org.springframework.jdbc.core.JdbcTemplate}의 {@link org.springframework.jdbc.core.ResultSetExtractor}를 이용할 때 행별 데이터를 스트림 등에 쓰기 위한 인터페이스.
 * 
 * @author setq
 *
 */
public interface RowStreamWriter {
	
	/**
	 * 스트림을 열고, 조회 결과 헤더 정보를 스트림으로 보낸다.
	 * 
	 * @param columnNames
	 */
	void open(List<? extends String> columnNames);

	/**
	 * 스트림을 닫는다. 버퍼에 쓰지 못한 데이터들을 플러시해준다.
	 */
	void close();

	/**
	 * 조회 데이터의 한 행을 스트림으로 보낸다.
	 * <p>
	 * 구현 클래스에서는 클라이언트가 연결을 끊는 경우를 체크하기 위해서 버퍼를 자주 flush 해주도록 한다.
	 * <p>
	 * 주: Writer를 통해서는 체크할 수 없고, OutputStream을 flush해줘야 체크가 된다. 
	 *  
	 * @param list
	 */
	void writeRow(List<? extends Object> list);
	
}
