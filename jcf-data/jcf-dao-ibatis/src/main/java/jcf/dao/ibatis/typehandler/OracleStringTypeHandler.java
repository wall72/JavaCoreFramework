package jcf.dao.ibatis.typehandler;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.driver.OracleParameterMetaData;

import com.ibatis.sqlmap.engine.type.StringTypeHandler;

/**
 * <p>
 * {@link OracleParameterMetaData} class can help
 * conversion of charset between database and view.
 * To use this class, first,
 * </p>
 * <p>
 * <pre>
 * you have to define charset of database and view at app.properties file.
 * for example,
 * 	dbEnc=ISO-8859-1
 * 	viewEnc=EUC-KR 
 * Next, you have to define callback property of typeHandler tag at sqlmapconfig file.
 * for example,
 * 	&lt;typeHandler callback="jcf.dao.ibatis.typehandler.OracleStringTypeHandler" javaType="java.lang.String" /&gt;
 * Finally, you have to register bean of typeHandler at applicationContext file.
 * And you need to define property and value of <i>dbEnc</i> and <i>viewEnc</i>. 
 *  </pre>
 * </p>
 * <p>
 * This type handler help conversion for all string type of model.
 * It can apply for VARCHAR2, CHAR, and CLOB types of oracle database
 * which can convert string java type.
 * </p>
 * 
 * @author Dust
 */

public class OracleStringTypeHandler extends StringTypeHandler {
	
	private static String dbEnc;
	private static String viewEnc;
	
	public OracleStringTypeHandler() {
		super();
	}
	
	public void setParameter(PreparedStatement ps, int i, Object parameter,
			String jdbcType) throws SQLException {
		if(parameter != null) {
			try {
				parameter = (Object) (new String(parameter.toString().getBytes(viewEnc), dbEnc));
			} catch (UnsupportedEncodingException e) {
				throw new CharsetConversionException(e);
			}
		}
		
		super.setParameter(ps, i, parameter, jdbcType);
	}
	
	public Object getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return getResult(super.getResult(cs, columnIndex));
	}
	
	public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
		return getResult(super.getResult(rs, columnIndex));
	}
	
	public Object getResult(ResultSet rs, String columnName)
			throws SQLException {
		return getResult(super.getResult(rs, columnName));
	}
	
	public Object getResult(Object obj) {
		if(obj == null) 
			return obj;
		
		try {
			return new String((obj.toString()).getBytes(dbEnc), viewEnc);
		} catch (UnsupportedEncodingException e) {
			throw new CharsetConversionException(e);
		}
	}

	public void setDbEnc(String dbEnc) {
		OracleStringTypeHandler.dbEnc = dbEnc;
	}

	public void setViewEnc(String viewEnc) {
		OracleStringTypeHandler.viewEnc = viewEnc;
	}
}
