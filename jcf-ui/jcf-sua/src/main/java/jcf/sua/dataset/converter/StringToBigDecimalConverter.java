package jcf.sua.dataset.converter;

import java.math.BigDecimal;

import jcf.sua.exception.MciException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 *
 * 데이터 컨버터 (String -> BigDecimal)
 *
 * @author alsdkzz
 *
 */
public class StringToBigDecimalConverter implements Converter<String, BigDecimal>{


	/**
	 *  String  을 방아서 BigDecimal 형태로 컴버팅함.
	 *  데이터가 없을 경우 초기값 0으로 바인딩
	 */
	public BigDecimal convert(String source) {
		BigDecimal data =new BigDecimal(0);

		if(StringUtils.hasText(source)){
			try {
					data=new BigDecimal(source);
				} catch (Exception e) {
						throw new MciException("[" +source + "] 는 BigDecimal  유형으로  컨버팅 할 수 없습니다. ");
					}
		}
		return data;
	 }

}