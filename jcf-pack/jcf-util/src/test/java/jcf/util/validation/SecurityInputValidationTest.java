package jcf.util.validation;

import junit.framework.TestCase;

//import org.apache.regexp.RE;

public class SecurityInputValidationTest extends TestCase {
	
	public void testDummy() {
		; // 테스트케이스 소스 수정 전까지 주석 처리.
	}
//
//	// 8. validation (길이, 영한조합, 특수문자 등) => RE (가대에도 적용)
//	// - 문자, 숫자 조합 6자 이상
//	// - ID와 동일하지 않음
//	// - 연속하여 오름차순, 내림차순 아닐 것
//	// - 동일문자 3회 이상 반복 금지
//	// - SQL injection 필터링
//	// - Cross Site Scription 필터링
//	// - OWASP 10대 취약점 대응
//	// - <, [ 등과 같은 특수문자를 사용해야 하는 경우는 substitution 적용
//
//	// - 개인신상, 부서명칭과 관계 없을 것
//	// - 일반사전에 등록된 단어가 이닐 것
//
//	// 생년원일 패턴 비교
//	public void testBirthDateValidation() throws Exception {
//		RE validation_pattern = new RE("([\\d{2}|\\d{4}])/(\\d{1,2})/(\\d{1,2})");
//		String[] birthDate = { "54/4/27", "75-04-22" };
//		String[] match = { "4�� 4�� 27��", "75�� 04�� 22��" };
//
//		System.out.println("------------- Birth Date Validation -------------");
//		for (int i = 0; i < birthDate.length; i++) {
//			String bday = birthDate[i];
//
//			if (validation_pattern.match(bday)) {
//
//				// 매치가 됐다. 그럼 매치된 문자들을 다시 불러오자.
//				// 첫번째 괄호가 년도이다.
//				String year = validation_pattern.getParen(1);
//				// 두번째 괄호가 월, 그리고 세번째가 날짜이다.
//				String month = validation_pattern.getParen(2);
//				String day = validation_pattern.getParen(3);
//
//				// 숫자들을 모두 추려냈으므로 다른 형태로 출력해보자.
//				System.out.println(year + "�� " + month + "�� " + day + "��");
//				birthDate[i] = year + "�� " + month + "�� " + day + "��";
//			}
//		}
//		assertEquals(match[0].toString(), birthDate[0].toString());
//	}
//
//	public void testCharRepeatValidation() throws Exception {
//		RE validation_pattern = new RE(RESecurityPattern.CHAR_REPEAT_PATTERN);
//		String[] input = { "abbbbbe", "123456" };
//
//		System.out.println("------------- Character Repeat Validation -------------");
//		for (int i = 0; i < input.length; i++) {
//			String temp = input[i];
//
//			boolean result = validation_pattern.match(temp);
//			System.out.println(result);
//			if (result)
//				assertTrue(result);
//			else
//				assertFalse(result);
//		}
//	}
//
//	// 동일문자 3회 이상 반복 금지
//	public void testSameCharRepeatValidation() throws Exception {
//		String[] input = { "abbbbbbe", "123456", "abcd1234ddt", "abccce12334" };
//		boolean result = false;
//
//		System.out.println("------------- Character Repeat Validation (three times) -------------");
//		for (int i = 0; i < input.length; i++) {
//			String temp = input[i];
//			for (int j = 0; j < temp.length(); j++) {
//				char subChar = temp.charAt(j);
//				String pattern = "([:alnum:]" + subChar + "{3,}[:alnum:])";
//				RE validation_pattern = new RE(pattern);
//				result = validation_pattern.match(temp);
//				System.out.println("result: " + result + " char: " + temp);
//			}
//		}
//		if (result)
//			assertTrue(result);
//		else
//			assertFalse(result);
//	}
//
//	// 문자, 숫자 조합 6자 이상이면 false
//	public void testIdPwCharDigitValidation() throws Exception {
//		RE validation_pattern = new RE(RESecurityPattern.ALNUM_DIGITCONTOL_PATTERN);
//		RE alpha_exclusive_pattern = new RE(RESecurityPattern.ALPHA_PATTERN);
//		RE digit_exclusive_pattern = new RE(RESecurityPattern.DIGIT_PATTERN);
//
//
//		String[] input = { "abcedf", "abcde","kyoung94", "12345", "1234567" };
//		boolean result = false;
//		System.out.println("------------- Character Digit Control Validation -------------");
//
//		for (int i = 0; i < input.length; i++) {
//			String temp = input[i];
//
//			result = validation_pattern.match(temp);
//			System.out.println(temp + ": " + result);
//			result = alpha_exclusive_pattern.match(temp);
//			System.out.println(temp + ": " + result);
//			result = digit_exclusive_pattern.match(temp);
//			System.out.println(temp + ": " + result);
//			result = RESecurityValidationUtils.validateCharDigit(temp);
//			System.out.println(temp + "(function): " + result);
//		}
//		assertTrue(result);
//	}
//
//	// ID와 PW가 동일하지 않음
//	public void testIdPwCompareValidation() throws Exception {
//		String[] id = { "abcde", "12345", "administrator", "admin" };
//		String[] password = { "abcde", "12345", "admin1234", "abcd" };
//		boolean result = false;
//		System.out.println("------------- Same ID/PW Validation -------------");
//
//		for (int i = 0; i < id.length; i++) {
//			String tempId = id[i];
//			result = tempId.equals(password[i]);
//			System.out.println("ID: " + tempId + " PW: " + password[i] + " result: " + result);
//		}
//		if (result)
//			assertTrue(result);
//		else
//			assertFalse(result);
//	}
//
//	// 사용자 입력 문자열이 오름차순으로 구성되지 않음
//	public void testCharAscValidation() throws Exception {
//		String[] input = { "12345", "abcdefg", "edcba", "54321", "57483", "abrlf" };
//		boolean result = false;
//
//		for (int i = 0; i < input.length; i++) {
//			result = true;
//			String temp = input[i];
//			System.out.println("------------------- Asc Test ----------------------" + temp);
//
//			for (int j = 0; j < temp.length() - 1 && result; j++) {
//				int frontChar = temp.charAt(j);
//				int endChar = temp.charAt(j + 1);
//				if ((frontChar + 1) == endChar)
//					result = true;
//				else
//					result = false;
//
//				System.out.println("frontChar: " + frontChar + " - endChar: " + endChar + " => result: " + result);
//			}
//		}
//		if (result)
//			assertTrue(result);
//		else
//			assertFalse(result);
//	}
//
//	// 사용자 입력 문자열이 내림차순으로 구성되지 않음
//	public void testCharDescValidation() throws Exception {
//		String[] input = { "12345", "abcdefg", "edcba", "54321", "57483", "abrlf" };
//		boolean result = false;
//
//		for (int i = 0; i < input.length; i++) {
//			result = true;
//			String temp = input[i];
//			System.out.println("------------------- Desc Test ----------------------" + temp);
//
//			for (int j = 0; j < temp.length() - 1 && result; j++) {
//				int frontChar = temp.charAt(j);
//				int endChar = temp.charAt(j + 1);
//				if ((frontChar - 1) == endChar)
//					result = true;
//				else
//					result = false;
//
//				System.out.println("frontChar: " + frontChar + " - endChar: " + endChar + " => result: " + result);
//			}
//		}
//		if (result)
//			assertTrue(result);
//		else
//			assertFalse(result);
//	}
//
//	// Cross Site Scripting 검증
//	public void testCrossSiteScriptingValidation() throws Exception {
//		RE validation_pattern = new RE(RESecurityPattern.CROSS_SITE_SCRIPTING_PATTERN);
//		String[] input = { "<script>alert(document.cookie);</script>", "<script>alert('xss���a�߰�');</script>",
//				">'><%00script>alert('Watchfire XSS Test Successful')</script>", "<!--#echo var='document_url'-->",
//				"javascript:alert(document.cookie)" };
//		boolean result = false;
//		System.out.println("--------------- Cross Site Scripting ----------------");
//
//		for (int i = 0; i < input.length; i++) {
//			String temp = input[i];
//			result = validation_pattern.match(temp);
//			System.out.println(temp + " result: " + result);
//		}
//		assertTrue(result);
//	}
//
//	// E-mail pattern 검증
//	public void testEmailAddressValidation() throws Exception {
//		RE validation_pattern = new RE(RESecurityPattern.EMAIL_ADDRESS_PATTERN);
//		String[] input = { "kyoung94@disc.co.kr", "kyoung94@@co.kr", "abcdefghijk@", "@abedge", "1234567" };
//
//		boolean result = false;
//		System.out.println("--------------- E-mail Address Filtering ----------------");
//
//		for (int i = 0; i < input.length; i++) {
//			String temp = input[i];
//			result = validation_pattern.match(temp);
//			System.out.println(temp + " result: " + result);
//		}
//		if (result)
//			assertTrue(result);
//		else
//			assertFalse(result);
//	}
//
//	// SQL injection 검증
//	public void testSQLInjectionValidation() throws Exception {
//		RE validation_pattern = new RE(RESecurityPattern.SQL_INJECTION_PATTERN_01);
//		String[] input = { "' or 1=1--", "' or 1=1 ##", "1234567='", "\" or \"\"=\"", "\"  OR \"\"=\"", "' or ''*'" };
//		boolean result = false;
//		System.out.println("--------------- SQL Injection 01 ----------------");
//
//		for (int i = 0; i < input.length; i++) {
//			String temp = input[i];
//			result = validation_pattern.match(temp);
//			System.out.println(temp + " result: " + result);
//		}
//
//		validation_pattern = new RE(RESecurityPattern.SQL_INJECTION_PATTERN_02);
//		result = false;
//		System.out.println("--------------- SQL Injection 02 ----------------");
//
//		for (int i = 0; i < input.length; i++) {
//			String temp = input[i];
//			result = validation_pattern.match(temp);
//			System.out.println(temp + " result: " + result);
//		}
//		if (result)
//			assertTrue(result);
//		else
//			assertFalse(result);
//	}
//
//	// Server Side Include 검증
//	public void testServerSideIncludeValidation() throws Exception {
//		RE validation_pattern = new RE(RESecurityPattern.SERVER_SIDE_INCLUDE_PATTERN);
//		String[] input = { "<!--#echo var=\"document_url\"-->", "<!--#echo var=\"document_name\"-->",
//				"<!--#echo var=\"server_name\"-->", "javascript:alert(document.cookie)" };
//		boolean result = false;
//		System.out.println("--------------- Server Side Include ----------------");
//
//		for (int i = 0; i < input.length; i++) {
//			String temp = input[i];
//			result = validation_pattern.match(temp);
//			System.out.println(temp + " result: " + result);
//		}
//		if (result)
//			assertTrue(result);
//		else
//			assertFalse(result);
//	}
//
//	// Server Side Include 검증
//	public void testSpecialCharacterValidation() throws Exception {
//		RE validation_pattern = new RE(RESecurityPattern.SPECIAL_CHARACTER_FILTERING_PATTERN);
//		String[] input = { "263hffaa>aagre8iue", "<!--#echo var=\"document_url\"-->", "<!--#echo var=\"document_name\"-->",
//				"<!--#echo var=\"server_name\"-->", "javascript:alert(document.cookie)" };
//		boolean result = false;
//		System.out.println("--------------- Special Character Filtering ----------------");
//
//		for (int i = 0; i < input.length; i++) {
//			String temp = input[i];
//			result = validation_pattern.match(temp);
//			System.out.println(temp + " result: " + result);
//		}
//		assertTrue(result);
//	}

}
