package jcf.jdbc.datasource.lookup;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 
 * 
 * @author purple
 *
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource{

	private static final Logger logger = LoggerFactory
			.getLogger(DynamicRoutingDataSource.class);
	/**
	 * Overridden abstract method to determine current lookup key. 
	 * 
	 */
	protected Object determineCurrentLookupKey() {
		Object key = DynamicRoutingDataSourceKeyHolder.getKey();
		logger.debug("Key value for determining current DataSource is {}", key);
		return key;
	}
	
	/**
	 * Overridden method to log the debug message.
	 * 
	 */
	public Connection getConnection() throws SQLException {
		Connection conn = super.getConnection();
		
		if( conn != null){
			logger.debug("connection's url is {}", conn.getMetaData().getURL());
			logger.debug("connection's username is {}", conn.getMetaData().getUserName());
			
		} else {
			logger.warn("connection is null. Please Check lookup key or default datasource setting value.");
		}
		
		return conn;
	}

	public java.util.logging.Logger getParentLogger()
			throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("the data source does not use java.util.logging");
	}
	
}
