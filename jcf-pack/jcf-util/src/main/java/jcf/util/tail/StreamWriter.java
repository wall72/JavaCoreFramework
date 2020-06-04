package jcf.util.tail;

import java.io.IOException;

/**
 * 출력 포매팅 및 출력 스트림에 대한 연결 끊김 방지 등의 기능을 하는 인터페이스.
 * 
 * @see JavaTail
 * 
 * @author setq
 *
 */
public interface StreamWriter {

	/**
	 * 입력이 없을 경우 이 메소드를 자주 불러주면 여기서 출력 스트림이 끊기는 것을 방지하는 등의 동작을 할 수 있다. 
	 * @throws IOException
	 */
	void heartBeat() throws IOException;

	/**
	 * 주어진 문자열을 한 라인 출력.
	 * @param line
	 * @throws IOException
	 */
	void println(String line) throws IOException;

	/**
	 * 출력 스트림에 대한 초기 작업.
	 */
	void open() throws IOException;

	/**
	 * 출력 스트림에 대한 마무리 작업.
	 */
	void close() throws IOException;

}
