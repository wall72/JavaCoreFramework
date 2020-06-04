package jcf.query.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 *
 * @author nolang
 *
 */
public abstract class QueryUtils {

	/**
	 * Set of characters that qualify as parameter separators,
	 * indicating that a parameter name in a SQL String has ended.
	 */
	private static final char[] PARAMETER_SEPARATORS =
			new char[] {'"', '\'', ':', '&', ',', ';', '(', ')', '|', '=', '+', '-', '*', '%', '/', '\\', '<', '>', '^'};

	/**
	 * Set of characters that qualify as comment or quotes starting characters.
	 */
	private static final String[] START_SKIP =
			new String[] {"'", "\"", "--", "/*"};

	/**
	 * Set of characters that at are the corresponding comment or quotes ending characters.
	 */
	private static final String[] STOP_SKIP =
			new String[] {"'", "\"", "\n", "*/"};


	/**
	 * Parse the SQL statement and locate any placeholders or named parameters.
	 * Named parameters are substituted for a JDBC placeholder.
	 * @param sql the SQL statement
	 * @return the parsed statement, represented as ParsedSql instance
	 */
	public static List<String> getNamedParameters(String sql) {
		Assert.notNull(sql, "SQL must not be null");

		List<String> namedParameters = new ArrayList<String>();
		char[] statement = sql.toCharArray();

		int i = 0;
		while (i < statement.length) {
			int skipToPosition = skipCommentsAndQuotes(statement, i);

			if (i != skipToPosition) {
				if (skipToPosition >= statement.length) {
					break;
				}
				i = skipToPosition;
			}

			char c = statement[i];

			if (c == ':' || c == '&') {
				int j = i + 1;

				if (j < statement.length && statement[j] == ':' && c == ':') {
					i = i + 2;
					continue;
				}

				while (j < statement.length	&& !isParameterSeparator(statement[j])) {
					j++;
				}

				if (j - i > 1) {
					String parameter = sql.substring(i + 1, j);

					if (!namedParameters.contains(parameter)) {
						namedParameters.add(parameter);
					}
				}
				i = j - 1;
			} else {
				if (c == '?') {
				}
			}
			i++;
		}

		return namedParameters;
	}

	/**
	 * Skip over comments and quoted names present in an SQL statement
	 * @param statement character array containing SQL statement
	 * @param position current position of statement
	 * @return next position to process after any comments or quotes are skipped
	 */
	private static int skipCommentsAndQuotes(char[] statement, int position) {
		for (int i = 0; i < START_SKIP.length; i++) {
			if (statement[position] == START_SKIP[i].charAt(0)) {
				boolean match = true;
				for (int j = 1; j < START_SKIP[i].length(); j++) {
					if (!(statement[position + j] == START_SKIP[i].charAt(j))) {
						match = false;
						break;
					}
				}
				if (match) {
					int offset = START_SKIP[i].length();
					for (int m = position + offset; m < statement.length; m++) {
						if (statement[m] == STOP_SKIP[i].charAt(0)) {
							boolean endMatch = true;
							int endPos = m;
							for (int n = 1; n < STOP_SKIP[i].length(); n++) {
								if (m + n >= statement.length) {
									// last comment not closed properly
									return statement.length;
								}
								if (!(statement[m + n] == STOP_SKIP[i].charAt(n))) {
									endMatch = false;
									break;
								}
								endPos = m + n;
							}
							if (endMatch) {
								// found character sequence ending comment or quote
								return endPos + 1;
							}
						}
					}
					// character sequence ending comment or quote not found
					return statement.length;
				}

			}
		}
		return position;
	}

	/**
	 * Determine whether a parameter name ends at the current position,
	 * that is, whether the given character qualifies as a separator.
	 */
	private static boolean isParameterSeparator(char c) {
		if (Character.isWhitespace(c)) {
			return true;
		}
		for (char separator : PARAMETER_SEPARATORS) {
			if (c == separator) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAssignableFromMap(Class<?> clazz) {
		return Map.class.isAssignableFrom(clazz);
	}

	public static boolean isPrimitiveType(Class<?> clazz) {
		return String.class.isAssignableFrom(clazz)
				|| String[].class.isAssignableFrom(clazz)
				|| ClassUtils.isPrimitiveOrWrapper(clazz)
				|| ClassUtils.isPrimitiveArray(clazz)
				|| ClassUtils.isPrimitiveWrapperArray(clazz);
	}
}
