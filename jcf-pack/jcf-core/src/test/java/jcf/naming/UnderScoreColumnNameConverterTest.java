/**
 * 언더스코어 문자열과 CamelCase 간의 상호변환 규칙 테스트
 */
package jcf.naming;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

/**
 * @author setq
 * 
 */
public class UnderScoreColumnNameConverterTest {

	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private ColumnNameConverter converter = new UnderScoreColumnNameConverter();

	@Test
	public void testEncode() {
		assertEquals("USER_ID", converter.encode("userId"));
	}

	@Test
	public void testDecode() {
		Assert.assertEquals("userId", converter.decode("USER_ID"));
	}

	@Test
	public void 디코드_불가능한_문자열() {
		thrown.expect(ColumnNameConversionException.class);

		roundTripDecodeFirst("USER_1D");
	}

	/**
	 * 소문자 입력도 받아들이도록 한다. 단, 이 경우는 결과가 대문자로 바뀐다.
	 */
	@Test
	public void testRountTripDecodeFirst() {
		roundTripDecodeFirst("USER_ID");
		roundTripDecodeFirst("user_id");
		roundTripDecodeFirst("user__id");
		roundTripDecodeFirst("user_Id");
		roundTripDecodeFirst("_USERID");
		roundTripDecodeFirst("USER__ID");
	}

	private void roundTripDecodeFirst(String encodedString) {
		try {
			String decoded = converter.decode(encodedString);
			String encodedAgain = converter.encode(decoded);

			collector.checkThat(encodedAgain.toLowerCase(),
					is(encodedString.toLowerCase()));

		} catch (Exception e) {
			collector.addError(e);
		}
	}

	@Test
	public void testRountTripEncodeFirst() {
		roundTripEncodeFirst("USER_ID");
		roundTripEncodeFirst("user_id");
		roundTripEncodeFirst("user_Id");
		roundTripEncodeFirst("_USERID");
		roundTripEncodeFirst("USER_1D");
		roundTripEncodeFirst("USER__ID");
	}

	/*
	 * 1. 소문자를 나타낼 수 없는 영역에서 대문자 및 기타 기호로만 표현할 수 있는데 소문자 및 대문자를 표현할 수 있는 문자열을
	 * escape할 수 있는 방법 (공백은 제외한다.)
	 * 
	 * 2. 빈 프로퍼티 이름 규칙과 1의 규칙의 조합
	 * 
	 * AbcDef123_456aBc _ABC_DEF123_456A_BC
	 */
	private void roundTripEncodeFirst(String decodedString) {
		try {
			String encoded = converter.encode(decodedString);
			String decodedAgain = converter.decode(encoded);

			collector.checkThat(decodedAgain, is(decodedString));
			// System.out.println(decodedString.equals(decodedAgain) ? "OK" :
			// "Failed");

		} catch (Exception e) {
			collector.addError(e);
		}
	}

	/**
	 * 디코더 입력에 언더스코어 뒤에는 항상 영문(대문자)가 한 글자 있어야 함.
	 */
	@Test
	public void testSymmetryCheck() {
		thrown.expect(ColumnNameConversionException.class);

		converter.decode("USER_1");
	}

	/**
	 * 빈 문자열에 대해 에러를 발생시키지 않아야 한다.
	 */
	@Test
	public void testEmpty() {
		assertEquals("", converter.encode(""));
		assertEquals("", converter.decode(""));
	}

	/**
	 * NULL에 대해 에러를 발생시키지 않아야 한다.
	 */
	@Test
	public void testNull() {
		assertEquals(null, converter.encode(null));
		assertEquals(null, converter.decode(null));
	}

	@Test
	public void test길게한번에() {
		String origin = "아무거나_걸려라__이런것도 되나 Spring __user_Id_1.what? USER_1D user_1d";

		String enc = converter.encode(origin);

		String dec = converter.decode(enc);

		assertEquals(origin, dec);
	}
}
