/**
 * 
 */
package jcf.naming;


/**
 * @author setq
 *
 */
public class LowerCaseColumnNameConverter implements ColumnNameConverter {

	/* (non-Javadoc)
	 * @see gov.mnd.dmobis3.system.mvc.param.ColumnNameConverter#decode(java.lang.String)
	 */
	public String decode(String encodedString) throws ColumnNameConversionException {
		return encodedString.toLowerCase();
	}

	/* (non-Javadoc)
	 * @see gov.mnd.dmobis3.system.mvc.param.ColumnNameConverter#encode(java.lang.String)
	 */
	public String encode(String decodedString) throws ColumnNameConversionException {
		return decodedString.toLowerCase();
	}

}
