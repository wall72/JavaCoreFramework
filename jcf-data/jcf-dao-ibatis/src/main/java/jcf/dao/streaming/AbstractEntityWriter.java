package jcf.dao.streaming;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * iBATIS 대용량 처리시 사용하는 컬럼 라이터.
 * 
 * @author setq
 *
 */
public abstract class AbstractEntityWriter implements EntityWriter {

	/**
	 * {@inheritDoc}
	 * <p>
	 * map을 인자로 받아서 한 행을 스트림으로 보낸다.
	 */
	@SuppressWarnings("unchecked")
	public void write(Object valueObject) {
		LinkedHashMap<? extends String, ? extends Object> map = (LinkedHashMap<? extends String, ? extends Object>)valueObject;
		
		for (Entry<? extends String, ? extends Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			write(entry.getKey(), value == null? "" : value.toString());
		}
		
	}

	public void reset() {
		
	}

}
