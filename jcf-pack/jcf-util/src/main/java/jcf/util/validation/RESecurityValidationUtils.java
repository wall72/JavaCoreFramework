package jcf.util.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RESecurityValidationUtils {

	private static final Log log = LogFactory
			.getLog(RESecurityValidationUtils.class);

	// 같은 문자가 3회 이상 반복되는지 검증
	public final static boolean validateCharRepeatation(String[] input) {
		boolean result = false;

		for (int i = 0; i < input.length; i++) {
			String temp = input[i];
			result = validateCharRepeatation(temp);
			if (result)
				return result;
		}
		return result;
	}

	public final static boolean validateCharRepeatation(String input) {
		boolean result = false;

		for (int j = 0; j < input.length(); j++) {
			char subChar = input.charAt(j);
			String pattern = "([:alnum:]" + subChar + "{3,}[:alnum:])";
			result = Pattern.matches(pattern, input);
			log.debug("result: " + result + " char: " + input);
		}
		return result;
	}

	// 문자, 숫자 조합 6자 이상이면 false 반환
	public final static boolean validateCharDigit(String[] input) {
		boolean result = false;

		for (int i = 0; i < input.length; i++) {
			String temp = input[i];

			result = validateCharDigit(temp);
			log.debug(temp + "(function): " + result);
			if (result)
				return true;
		}
		return result;
	}

	public final static boolean validateCharDigit(String input) {
		return !(Pattern.matches(RESecurityPattern.ALNUM_DIGITCONTOL_PATTERN,
				input)
				&& Pattern.matches(RESecurityPattern.ALPHA_PATTERN, input) && Pattern
				.matches(RESecurityPattern.DIGIT_PATTERN, input));
	}

	// ID와 PW가 동일하지 않음
	public final static boolean validateSameIdPwComparison(String id, String pw) {
		boolean result = id.equals(pw);
		log.debug("ID: " + id + " PW: " + pw + " result: " + result);
		return result;
	}

	// 사용자 입력 문자열이 오름차순으로 구성되지 않음
	public final static boolean validateCharAscString(String[] input) {
		boolean result = false;

		for (int i = 0; i < input.length; i++) {
			result = true;
			String temp = input[i];

			result = validateCharAscString(temp);
			if (result)
				return true;
		}
		return result;
	}

	public final static boolean validateCharAscString(String input) {
		boolean result = true;

		System.out
				.println("------------------- Asc Test ----------------------"
						+ input);

		for (int j = 0; j < input.length() - 1 && result; j++) {
			int frontChar = input.charAt(j);
			int endChar = input.charAt(j + 1);
			if ((frontChar + 1) == endChar)
				result = true;
			else
				result = false;

			log.debug("frontChar: " + frontChar + " - endChar: " + endChar
					+ " => result: " + result);
		}
		return result;
	}

	// 사용자 입력 문자열이 내림차순으로 구성되지 않음
	public final static boolean validateCharDescString(String[] input) {
		boolean result = false;

		for (int i = 0; i < input.length; i++) {
			result = true;
			String temp = input[i];

			result = validateCharDescString(temp);
			if (result)
				return true;
		}
		return result;
	}

	public final static boolean validateCharDescString(String input) {
		boolean result = true;

		System.out
				.println("------------------- Desc Test ----------------------"
						+ input);

		for (int j = 0; j < input.length() - 1 && result; j++) {
			int frontChar = input.charAt(j);
			int endChar = input.charAt(j + 1);
			if ((frontChar - 1) == endChar)
				result = true;
			else
				result = false;

			log.debug("frontChar: " + frontChar + " - endChar: " + endChar
					+ " => result: " + result);
		}
		return result;
	}

	// Cross Site Scripting 검증
	public final static boolean validateCrossSiteScripting(String[] input) {
		Matcher matcher = Pattern.compile(
				RESecurityPattern.CROSS_SITE_SCRIPTING_PATTERN).matcher("");

		boolean result = false;
		System.out
				.println("--------------- Cross Site Scripting ----------------");

		for (int i = 0; i < input.length; i++) {
			String temp = input[i];
			result = matcher.reset(temp).matches();
			log.debug(temp + " result: " + result);
			if (result)
				return true;
		}
		return result;
	}

	public final static boolean validateCrossSiteScripting(String input) {
		Matcher matcher = Pattern.compile(
				RESecurityPattern.CROSS_SITE_SCRIPTING_PATTERN).matcher(input);

		boolean result = false;
		System.out
				.println("--------------- Cross Site Scripting ----------------");

		result = matcher.matches();
		log.debug(input + " result: " + result);
		return result;
	}

	// E-mail pattern 검증
	public final static boolean validateEmailAddress(String[] input) {
		Matcher matcher = Pattern.compile(
				RESecurityPattern.EMAIL_ADDRESS_PATTERN).matcher("");

		boolean result = false;
		System.out
				.println("--------------- E-mail Address Filtering ----------------");

		for (int i = 0; i < input.length; i++) {
			String temp = input[i];
			result = matcher.reset(temp).matches();
			log.debug(temp + " result: " + result);
		}
		return result;
	}

	public final static boolean validateEmailAddress(String input) {
		Matcher matcher = Pattern.compile(
				RESecurityPattern.EMAIL_ADDRESS_PATTERN).matcher(input);

		boolean result = false;
		System.out
				.println("--------------- E-mail Address Filtering ----------------");

		result = matcher.matches();
		log.debug(input + " result: " + result);
		return result;
	}

	// SQL injection 검증
	public final static boolean validateSQLInjection(String[] input) {
		Matcher matcher = Pattern.compile(
				RESecurityPattern.SQL_INJECTION_PATTERN_01).matcher("");

		boolean result = false;
		log.debug("--------------- SQL Injection 01 ----------------");

		for (int i = 0; i < input.length; i++) {
			String temp = input[i];
			result = matcher.reset(temp).matches();
			log.debug(temp + " result: " + result);
		}

		matcher = Pattern.compile(RESecurityPattern.SQL_INJECTION_PATTERN_02)
				.matcher("");
		result = false;
		log.debug("--------------- SQL Injection 02 ----------------");

		for (int i = 0; i < input.length; i++) {
			String temp = input[i];
			result = matcher.reset(temp).matches();
			log.debug(temp + " result: " + result);
		}
		return result;
	}

	public final static boolean validateSQLInjection(String input) {
		Matcher matcher = Pattern.compile(
				RESecurityPattern.SQL_INJECTION_PATTERN_01).matcher(input);
		boolean result = false;
		log.debug("--------------- SQL Injection 01 ----------------");

		result = matcher.matches();
		log.debug(input + " result: " + result);
		if (result)
			return true;

		matcher = Pattern.compile(RESecurityPattern.SQL_INJECTION_PATTERN_02)
				.matcher(input);
		log.debug("--------------- SQL Injection 02 ----------------");

		result = matcher.matches();
		log.debug(input + " result: " + result);
		return result;
	}

	// Server Side Include 검증
	public final static boolean validateServerSideInclude(String[] input) {
		Matcher matcher = Pattern.compile(
				RESecurityPattern.SERVER_SIDE_INCLUDE_PATTERN).matcher("");

		boolean result = false;
		System.out
				.println("--------------- Server Side Include ----------------");

		for (int i = 0; i < input.length; i++) {
			String temp = input[i];
			result = matcher.reset(temp).matches();
			log.debug(temp + " result: " + result);
		}
		return result;
	}

	public final static boolean validateServerSideInclude(String input) {
		Matcher matcher = Pattern.compile(
				RESecurityPattern.SERVER_SIDE_INCLUDE_PATTERN).matcher(input);

		boolean result = false;
		System.out
				.println("--------------- Server Side Include ----------------");

		result = matcher.matches();
		log.debug(input + " result: " + result);
		return result;
	}

	// 공통 특수문자 포함 문자열 검증
	public final static boolean validateSpecialCharacter(String[] input) {
		Matcher matcher = Pattern.compile(
				RESecurityPattern.SPECIAL_CHARACTER_FILTERING_PATTERN).matcher(
				"");

		boolean result = false;
		System.out
				.println("--------------- Special Character Filtering ----------------");

		for (int i = 0; i < input.length; i++) {
			String temp = input[i];
			result = matcher.reset(temp).matches();
			log.debug(temp + " result: " + result);
		}
		return result;
	}

	public final static boolean validateSpecialCharacter(String input) {
		Matcher matcher = Pattern.compile(
				RESecurityPattern.SPECIAL_CHARACTER_FILTERING_PATTERN).matcher(
				input);

		boolean result = false;
		System.out
				.println("--------------- Special Character Filtering ----------------");

		result = matcher.matches();
		log.debug(input + " result: " + result);
		return result;
	}
}
