package jcf.dao.ibatis.util;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

/**
 * @author mina
 * HashMap으로 가져온 DB ColumnName 에 CamelCase를 적용하는 클래스
 */
public class ColumnNameCamelCaseMap extends AbstractColumnNameConvertMap {

    private static final long serialVersionUID = 1L;

    public String columnNameConvert(String columnName) {
        String newColumnName = null;

        if (columnName.indexOf("_") == -1)
            newColumnName = columnName.toLowerCase();
        else {
            StringBuffer sb = new StringBuffer();
            boolean isFirst = true;
            StringTokenizer tokenizer = new StringTokenizer(columnName, "_");
            while (tokenizer.hasMoreTokens()) {
                if (isFirst)
                    sb.append(tokenizer.nextToken().toLowerCase());
                else {
                    sb.append(StringUtils.capitalize(tokenizer.nextToken()
                            .toLowerCase()));
                }
                isFirst = false;
            }

            newColumnName = sb.toString();
        }
        return newColumnName;
    }
    
}
