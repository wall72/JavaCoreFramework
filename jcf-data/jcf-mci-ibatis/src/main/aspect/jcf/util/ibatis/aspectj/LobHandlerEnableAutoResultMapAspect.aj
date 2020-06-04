package jcf.util.ibatis.aspectj;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibatis.sqlmap.engine.mapping.result.ResultMapping;
import com.ibatis.sqlmap.engine.mapping.result.AutoResultMap;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;

/**
 * 
 * 
 */
public aspect LobHandlerEnableAutoResultMapAspect {
	
	private static final Log log = LogFactory.getLog(LobHandlerEnableAutoResultMapAspect.class);
	
	public pointcut initializeMapResults(ResultSet rs)
		: execution(void com.ibatis.sqlmap.engine.mapping.result.AutoResultMap.initializeMapResults(..))
		&& args(rs);
	
	void around (ResultSet rs)
		: initializeMapResults(rs) {
		if (log.isInfoEnabled()){
    		log.info("Start overrided AutoResultMap.initializeMapResults(..) method.");
    	}
		
	    try {
	    	AutoResultMap _target = ((AutoResultMap)thisJoinPoint.getTarget());
	    	
	        List resultMappingList = new ArrayList();
	        ResultSetMetaData rsmd = rs.getMetaData();
	        for (int i = 0, n = rsmd.getColumnCount(); i < n; i++) {
	          String columnName = (_target.getDelegate().isUseColumnLabel())?
	        		  					rsmd.getColumnLabel(i+1) :
	        		  					rsmd.getColumnName(i+1);
	          ResultMapping resultMapping = new ResultMapping();
	          resultMapping.setPropertyName(columnName);
	          resultMapping.setColumnName(columnName);
	          resultMapping.setColumnIndex(i + 1);
	          
	          String columnTypeName = rsmd.getColumnTypeName(i + 1);
	          if( "CLOB".equals(columnTypeName) ){
	        	  resultMapping.setTypeHandler(_target.getDelegate().getTypeHandlerFactory().getTypeHandler(String.class));
	          }
	          else if( "BLOB".equals(columnTypeName) ){
	        	  resultMapping.setTypeHandler(_target.getDelegate().getTypeHandlerFactory().getTypeHandler(byte[].class));
	          }
	          else{
	        	  resultMapping.setTypeHandler(_target.getDelegate().getTypeHandlerFactory().getTypeHandler(Object.class));
	          }
	          resultMappingList.add(resultMapping);
	          
		  		if (log.isDebugEnabled()){
		    		log.debug("resultMapping info for Map type - columnName : " + resultMapping.getColumnName() +
		    				", JdbcTypeName : " + columnTypeName +
		    				", TypeHandler : " + resultMapping.getTypeHandler()
		    				);
		    	}
	        }
	        
	        _target.setResultMappingList(resultMappingList);
	    } catch (SQLException e) {
	    	throw new RuntimeException("Error in LobHandlerEnableAutoResultMapAspect. Cause: " + e);
	    }
	    
		if (log.isInfoEnabled()){
    		log.info("End overrided AutoResultMap.initializeMapResults(..) method.");
    	}
	}
	
}