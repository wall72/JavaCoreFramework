/**
 * 
 */
package jcf.naming;



/**
 * CamelCase 원본 데이터를 언더스코어(밑줄, "_")이 들어간 대문자 이름으로 인코딩하거나 반대로 디코딩 한다.
 * 
 * @author setq
 *
 */
public class UnderScoreColumnNameConverter implements ColumnNameConverter {

	/* (non-Javadoc)
	 * @see gov.mnd.dmobis3.system.mvc.param.ColumnnNameConverter#decode(java.lang.String)
	 */
	public String decode(String encodedString) throws ColumnNameConversionException {
		if (encodedString == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();

		boolean underscoreFlag = false;
		for (int i = 0; i < encodedString.length(); i++) {
			char c = encodedString.charAt(i);

			if (underscoreFlag) {
				underscoreFlag = false;

				if (isLowerCase(c)) {
					sb.append(toUpper(c));
					
				} else if (isUpperCase(c) || c == '_') {
					sb.append(c);
					
				} else {
					throw new ColumnNameConversionException("변환 불가능한 문자 시퀀스 '_" +c + "' 가 있습니다. 전체 문자열은 '" + encodedString + "'");
				}
				
			} else {
				if (c == '_') {
					underscoreFlag = true;

				} else {
					sb.append(toLower(c));
				}
			}
		}
		return sb.toString();
	}

	/**
	 * @param c
	 * @return
	 */
	private char toLower(char c) {
		if ('A' <= c && c <= 'Z') {
			return (char) (c + ('a' - 'A'));

		} else {
			return c;
		}
	}

	/* (non-Javadoc)
	 * @see gov.mnd.dmobis3.system.mvc.param.ColumnnNameConverter#encode(java.lang.String)
	 */
	public String encode(String decodedString) throws ColumnNameConversionException {

		if (decodedString == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();

		boolean underscoreFlag = false;
		for (int i = 0; i < decodedString.length(); i++) {
			char c = decodedString.charAt(i);
			if (underscoreFlag) {
				underscoreFlag = false;
				if (isUpperCase(c) || c == '_') {
					sb.append('_');
				} 
				sb.append(toUpper(c));
				
			} else {
				if (c=='_') {
					underscoreFlag = true;
					sb.append("__");
					
				} else {
					if (isUpperCase(c)) {
						sb.append('_').append(c);

					} else {
						sb.append(toUpper(c));
					}
				}
			}
		}

		return sb.toString();
	}

	private char toUpper(char c) {
		if (isLowerCase(c)) {
			return (char) (c + ('A' - 'a'));

		} else {
			return c;
		}
	}

	private boolean isUpperCase(char c) {
		return 'A' <= c && c <= 'Z';
	}

	private boolean isLowerCase(char c) {
		return 'a' <= c && c <= 'z';
	}
	
}
