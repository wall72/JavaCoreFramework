package jcf.util.metadata;

import org.springframework.util.Assert;

public class JavaTypes {

	private static final String IS_MAP_FLAG = "IS_MAP";
	private static final String java_util_Map = "java.util.Map";
	
	/**
	 * <p><pre>
	 * true : 
	 * 	- String, int, Integer, double, Double, long, Long, 
	 * 	- java.util.Date, BigDecimal, float, Float
	 * 	- java.io.File, byte[]
	 * 
	 * false : 
	 * 	- All of the other case.
	 * 
	 * </pre></p>
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isNormalType(Class clazz){
		Assert.notNull(clazz);
		return isNormalType(clazz.getName());
	}
	
	/**
	 * <p><pre>
	 * true : 
	 * 	- String, int, Integer, double, Double, long, Long, 
	 * 	- java.util.Date, BigDecimal, float, Float
	 * 	- java.io.File, byte[]
	 * 
	 * false : 
	 * 	- All of the other case.
	 * 
	 * </pre></p>
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isNormalType(String typeName){
		
		if( "java.lang.String".equals(typeName) ||
			"int".equals(typeName) || "java.lang.Integer".equals(typeName) ||
			"double".equals(typeName) || "java.lang.Double".equals(typeName) ||
			"long".equals(typeName) || "java.lang.Long".equals(typeName) ||
			"java.util.Date".equals(typeName) ||
			"java.math.BigDecimal".equals(typeName) ||
			"float".equals(typeName) || "java.lang.Float".equals(typeName) ||
			"java.io.File".equals(typeName) ||
			"[B".equals(typeName)
			){
			
			return true;
		}else{
			return false;
		}		
	}
	
	public static boolean isNumeric(Class clazz){
		Assert.notNull(clazz);
		return isNumeric(clazz.getName());
	}
	
	public static boolean isNumeric(String typeName){		
		if( "int".equals(typeName) || "java.lang.Integer".equals(typeName) ||
			"double".equals(typeName) || "java.lang.Double".equals(typeName) ||
			"long".equals(typeName) || "java.lang.Long".equals(typeName) ||
			"float".equals(typeName) || "java.lang.Float".equals(typeName) ||
			"java.math.BigDecimal".equals(typeName)
			){			
			return true;
		}else{
			return false;
		}		
	}
	
	public static boolean isDate(Class clazz){
		Assert.notNull(clazz);
		return isDate(clazz.getName());
	}
	
	public static boolean isDate(String typeName){
		if( "java.util.Date".equals(typeName) ){			
			return true;
		}else{
			return false;
		}
	}
	
	

	
	
}
