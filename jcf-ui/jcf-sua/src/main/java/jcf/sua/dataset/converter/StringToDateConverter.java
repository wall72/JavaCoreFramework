package jcf.sua.dataset.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

/**
*
* 데이터 컨버터 (String -> Date)
*
* @author nolang
*
*/
public class StringToDateConverter implements Converter<String, Date>{

	private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	/*
	 * (non-Javadoc)
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	public Date convert(String source) {
		Date date = null;

		try {
			date = new Date(format.parse(source).getTime());
		} catch (ParseException e) {
		}

		return date;
	}
}