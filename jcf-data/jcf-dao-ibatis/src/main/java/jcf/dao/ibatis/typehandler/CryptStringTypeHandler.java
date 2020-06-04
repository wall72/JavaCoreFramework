package jcf.dao.ibatis.typehandler;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jcf.util.ByteUtils;
import jcf.util.security.cipher.CryptClient;

import com.ibatis.sqlmap.engine.type.StringTypeHandler;
import com.ibatis.sqlmap.engine.type.TypeHandler;

/**
 * TODO:테스트 중. 주석 첨부필요.
 * @author setq
 */
public class CryptStringTypeHandler extends StringTypeHandler implements TypeHandler {

	private static CryptClient cryptClient;
	private static String charsetName;
	
	public void setCryptClient(CryptClient cryptClient) {
		CryptStringTypeHandler.cryptClient = cryptClient;
	}
	public void setCharsetName(String charsetName) {
		CryptStringTypeHandler.charsetName = charsetName;
	}
	
	public void setParameter(PreparedStatement ps, int i, Object parameter, String jdbcType) throws SQLException {
		super.setParameter(ps, i, encrypt(parameter), jdbcType);
	}

	public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return decrypt(super.getResult(cs, columnIndex));
	}
	
	public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
		return decrypt(super.getResult(rs, columnIndex));
	}
	
	public Object getResult(ResultSet rs, String columnName) throws SQLException {
		return decrypt(super.getResult(rs, columnName));
	}
	
	/**
	 * 
	 * @param parameter
	 * @return 암호화된 문자열. (파라미터가 NULL이면 NULL.)
	 */
	private Object encrypt(Object parameter) {
		if (parameter == null) {
			return null;
		}
		try {
			return ByteUtils.toHexString(cryptClient.encrypt(parameter.toString().getBytes(charsetName)));
			
		} catch (UnsupportedEncodingException e) {
			throw new CharsetConversionException(e);
		}
	}

	private Object decrypt(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			return new String(cryptClient.decrypt(ByteUtils.toBytesFromHexString(obj.toString())), charsetName);
			
		} catch (UnsupportedEncodingException e) {
			throw new CharsetConversionException(e);
		}
	}


}
