package jcf.util.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * SGML 부류의 마크업 언어 (HTML 등)에서 주어진 인코딩으로 표현하지 못하는 문자를 Unicode Charset(UCS)로 매핑하기 위한 
 * escaping 표현식인  <a href="http://en.wikipedia.org/wiki/Numeric_character_reference">Numeric character reference</a>(NCR)
 * 을 이용하여 charset 표현 범위 이탈 문제를 처리하기 위한 유틸리티 클래스.
 * <p>
 * 한국에서는 주로 UTF-8 입력을 EUC-KR 백엔드에서 처리하는 등의 경우에 필요한데,
 * MS Internet explorer에서는 euc-kr에서 표현하지 못하는 문자만 NCR로 표기하는 경제적인 방법을 사용하고 있다.
 * 이를 서버에서 구현하기 위한 클래스.
 * <p>
 * 다른 레퍼런스에서는 iso-8859-1 외의 문자들을 모두 NCR로 바꾸어서 저장공간의 낭비가 있을 수 있으므로
 * CharsetDecoder/Encoder의 unmappable character handling 체계를 이용하여 euc-kr에서 표현할 수 없는 문자만 NCR로
 * 변환하도록 구현한다.
 * 
 * @author setq
 *
 */
public class NCRTest {

	@Test
	public void testNCR() {
		String testData = "숖이건 뭐야, &#48577; 똠뷁스럽군샾 &#48577 ;";
		
		System.out.println("입력 : " + testData);
		String escape = NumericCharacterReferenceEUCKRConverter.escape(testData);
		
		System.out.println("결과 : " + escape);
		
		String unescape = NumericCharacterReferenceEUCKRConverter.unescape(escape);
		
		System.out.println("다시 : " + unescape);
	}
	
}
