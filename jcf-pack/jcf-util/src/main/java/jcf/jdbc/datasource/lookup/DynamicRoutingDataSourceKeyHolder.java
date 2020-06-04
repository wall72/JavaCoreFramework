package jcf.jdbc.datasource.lookup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DynamicRoutingDataSourceKeyHolder {

	private static final Logger logger = LoggerFactory
			.getLogger(DynamicRoutingDataSourceKeyHolder.class);
	
	private static ThreadLocal<Object> sourceKeyLocal = new ThreadLocal<Object>();
	
	public static Object getKey(){
		Object key = sourceKeyLocal.get();
		if( null == key ) logger.debug("DataSource routing key is null. This will make problem.");
		return key;
	}
	
	public static void setKey(Object key){
		if( null == key ) logger.debug("Given argument for trying to set dynamicdatasource routing key is null.");
		sourceKeyLocal.set(key);
	}
	
	public static void resetKey(){
		sourceKeyLocal.set(null);
	}
	
}
