package jcf.util.metadata;

import java.sql.Types;

import org.springframework.util.Assert;

/**
 * 
 * 
 * @author Administrator
 *
 */
public class SQLMetaMapper {

	/**
	 * 
	 * @param fieldTypeName
	 * @return
	 */
	public static int toDBType(String fieldTypeName) {
		Assert.notNull(fieldTypeName);
		int typeValue;
		if( "java.lang.String".equals(fieldTypeName)){
			typeValue = Types.VARCHAR;
		}else if("int".equals(fieldTypeName) || "java.lang.Integer".equals(fieldTypeName) ||
				"double".equals(fieldTypeName) || "java.lang.Double".equals(fieldTypeName) ||
				"long".equals(fieldTypeName) || "java.lang.Long".equals(fieldTypeName) ||
				"java.math.BigDecimal".equals(fieldTypeName)
				){
			typeValue = Types.NUMERIC;
		}else if("java.util.Date".equals(fieldTypeName)){
			typeValue = Types.DATE;
		}else if("java.io.File".equals(fieldTypeName)){
			typeValue = Types.BLOB;
		}else if("[B".equals(fieldTypeName) ){
			typeValue = Types.BLOB;
		}else{
			typeValue = Types.VARCHAR;
		}
		
		return typeValue;
	}
	
	/**
	 * 
	 * @param fieldTypeName
	 * @return
	 */
	public static int toDisplaySize(String fieldTypeName) {
		Assert.notNull(fieldTypeName);
		int displaySize;
		if( "java.lang.String".equals(fieldTypeName)){
			displaySize = 255;
		}else if("int".equals(fieldTypeName) || "java.lang.Integer".equals(fieldTypeName)){
			displaySize = 9;
		}else if("double".equals(fieldTypeName) || "java.lang.Double".equals(fieldTypeName) ||
				"long".equals(fieldTypeName) || "java.lang.Long".equals(fieldTypeName) ||
				"java.math.BigDecimal".equals(fieldTypeName)
				){
			displaySize = 20;
		}else if("java.util.Date".equals(fieldTypeName)){
			displaySize = 8;
		}else if("java.io.File".equals(fieldTypeName)){
			displaySize = 255;
		}else if("[B".equals(fieldTypeName) ){
			displaySize = 255;
		}else{
			displaySize = 255;
		}
		
		return displaySize;
	}

	/**
	 * 
	 * @param fieldTypeName
	 * @return
	 */
	public static int toPrecision(String fieldTypeName) {
		Assert.notNull(fieldTypeName);
		int precision;
		if( "java.lang.String".equals(fieldTypeName)){
			precision = 255;
		}else if("int".equals(fieldTypeName) || "java.lang.Integer".equals(fieldTypeName)){
			precision = 9;
		}else if("double".equals(fieldTypeName) || "java.lang.Double".equals(fieldTypeName) ||
				"long".equals(fieldTypeName) || "java.lang.Long".equals(fieldTypeName) ||
				"java.math.BigDecimal".equals(fieldTypeName)
				){
			precision = 20;
		}else if("java.util.Date".equals(fieldTypeName)){
			precision = 8;
		}else if("java.io.File".equals(fieldTypeName)){
			precision = 255;
		}else if("[B".equals(fieldTypeName) ){
			precision = 255;
		}else{
			precision = 255;
		}
		
		return precision;
	}

	/**
	 * 
	 * @param fieldTypeName
	 * @return
	 */
	public static int toScale(String fieldTypeName) {
		Assert.notNull(fieldTypeName);
		int scale;
		if( "java.lang.String".equals(fieldTypeName)){
			scale = 0;
		}else if("int".equals(fieldTypeName) || "java.lang.Integer".equals(fieldTypeName)){
			scale = 0;
		}else if("double".equals(fieldTypeName) || "java.lang.Double".equals(fieldTypeName) ||
				"long".equals(fieldTypeName) || "java.lang.Long".equals(fieldTypeName) ||
				"java.math.BigDecimal".equals(fieldTypeName)
				){
			scale = 10;
		}else if("java.util.Date".equals(fieldTypeName)){
			scale = 0;
		}else if("java.io.File".equals(fieldTypeName)){
			scale = 0;
		}else if("[B".equals(fieldTypeName) ){
			scale = 0;
		}else{
			scale = 0;
		}
		
		return scale;
	}


	
	
}
