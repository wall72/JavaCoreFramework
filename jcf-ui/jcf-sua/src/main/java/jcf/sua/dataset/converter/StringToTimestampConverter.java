package jcf.sua.dataset.converter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.core.convert.converter.Converter;

/**
*
* 데이터 컨버터 (String -> Timestamp)
*
* @author nolang
*
*/
public class StringToTimestampConverter implements Converter<String, Timestamp>{

	private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	/*
	 * (non-Javadoc)
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	public Timestamp convert(String source) {
		Timestamp date = null;

		try {
			date = new Timestamp(format.parse(source).getTime());
		} catch (ParseException e) {
		}

		return date;
	}
}