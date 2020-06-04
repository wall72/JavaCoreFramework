package jcf.dao.ibatis.util;

import jcf.dao.ibatis.util.AbstractColumnNameConvertMap;

/**
 * @author mina
 * HashMap으로 가져온 DB ColumnName 를 소문자로 바꾸는  클래스
 */

public class ColumnNameToLowerCaseMap extends AbstractColumnNameConvertMap {

	private static final long serialVersionUID = 1L;

	@Override
	public String columnNameConvert(String columnName) {
		 String newColumnName = columnName.toLowerCase();

	        return newColumnName;
	}

}
