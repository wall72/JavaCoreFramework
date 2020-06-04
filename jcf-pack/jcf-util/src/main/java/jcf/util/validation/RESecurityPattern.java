package jcf.util.validation;

public class RESecurityPattern {

	// ----------------------- Common Pattern --------------------
	public static final String DATE_PATTERN_01 = "([\\d{4}|\\d{4}])/(\\d{1,2})/(\\d{1,2})";

	public static final String DATE_PATTERN_02 = "([\\d{2}|\\d{4}])/(\\d{1,2})/(\\d{1,2})";

	public static final String DATE_PATTERN_03 = "([\\d{4}|\\d{4}])-(\\d{1,2})-(\\d{1,2})";

	public static final String DATE_PATTERN_04 = "([\\d{2}|\\d{4}])-(\\d{1,2})-(\\d{1,2})";

	public static final String ALNUM_PATTERN = "([:alnum:])";

	public static final String ALPHA_PATTERN = "([:alpha:])";

	public static final String DIGIT_PATTERN = "([:digit:])";

	public static final String GRAPH_PATTERN = "([:graph:])";

	public static final String LOWER_PATTERN = "([:lower:])";

	public static final String UPPER_PATTERN = "([:upper:])";

	public static final String PRINT_PATTERN = "([:print:])";

	public static final String HEXADECIMAL_DIGITS_PATTERN = "([:xdigit:])";

	public static final String EMAIL_ADDRESS_PATTERN = "(\\w[:print:]@[:alnum:].[:alpha:])";

	// ----------------------- Vulnerability Pattern -----------------
	public static final String CHAR_REPEAT_PATTERN = "[:alnum:]b{2,}[:alnum:]";

	public static final String CROSS_SITE_SCRIPTING_PATTERN = "([:print:]script[:print:])";

	public static final String SQL_INJECTION_PATTERN_01 = "'[:space:](or)|(OR)[:space:][:alnum:]=[:alnum:]";

	public static final String SQL_INJECTION_PATTERN_02 = "\"[:space:](or)|(OR)[:space:]";

	public static final String SERVER_SIDE_INCLUDE_PATTERN = "<!--#[:alnum:]";

	public static final String SPECIAL_CHARACTER_FILTERING_PATTERN = "(['\"<>()&$#@*!~|;%])";

	// ----------------------- ID/PW Pattern ----------------------
	public static final String ALNUM_DIGITCONTOL_PATTERN = "[:alnum:]{5,}[:alnum:]";

}
