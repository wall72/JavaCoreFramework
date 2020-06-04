package jcf.util.metadata;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.lang.SystemUtils;

/**
 * <p>
 * This is a mock object for ResultSetMetaData.<br/>
 * 
 * Note followings : 
 * <nl>
 * <li>In different with original ResultSetMetaData, columnName will take the shape of java's traditional local private variable notation(lowerCase start and capitalized).</li>
 * </nl>
 * </p>
 * @author Administrator
 *
 */
public class ResultSetMetadataHolder implements ResultSetMetaData {
	private int columnCount;

	private String[] catalogNameArray;
	private String[] columnClassNameArray;
	private int[] columnDisplaySizeArray;
	private String[] columnLabelArray;
	private String[] columnNameArray;
	private int[] columnTypeArray;
	private String[] columnTypeNameArray;
	private int[] precisionArray;
	private int[] scaleArray;
	private String[] schemaNameArray;
	private String[] tableNameArray;
	
	public int getColumnCount() {
		return columnCount;
	}
	
	public ResultSetMetadataHolder(int columnCount) {
		this.columnCount = columnCount;
		
		catalogNameArray = new String[columnCount];
		columnClassNameArray = new String[columnCount];
		columnDisplaySizeArray = new int[columnCount];
		columnLabelArray = new String[columnCount];
		columnNameArray = new String[columnCount];
		columnTypeArray = new int[columnCount];
		columnTypeNameArray = new String[columnCount];
		precisionArray = new int[columnCount];
		scaleArray = new int[columnCount];
		schemaNameArray = new String[columnCount];
		tableNameArray = new String[columnCount];
	}	
	public String getCatalogName(int column) {
		return catalogNameArray[column - 1];
	}
	public void setCatalogName(String catalogName, int column) {
		this.catalogNameArray[column - 1] = catalogName;
	}
	public String getColumnClassName(int column) {
		return columnClassNameArray[column - 1];
	}
	public void setColumnClassName(String columnClassName, int column) {
		this.columnClassNameArray[column - 1] = columnClassName;
	}
	public int getColumnDisplaySize(int column) {
		return columnDisplaySizeArray[column - 1];
	}
	public void setColumnDisplaySize(int columnDisplaySize, int column) {
		this.columnDisplaySizeArray[column - 1] = columnDisplaySize;
	}
	public String getColumnLabel(int column) {
		return columnLabelArray[column - 1];
	}
	public void setColumnLabel(String columnLabel, int column) {
		this.columnLabelArray[column - 1] = columnLabel;
	}
	public String getColumnName(int column) {
		return columnNameArray[column - 1];
	}
	public void setColumnName(String columnName, int column) {
		this.columnNameArray[column - 1] = columnName;
	}
	public int getColumnType(int column) {
		return columnTypeArray[column - 1];
	}
	public void setColumnType(int columnType, int column) {
		this.columnTypeArray[column - 1] = columnType;
	}
	public String getColumnTypeName(int column) {
		return columnTypeNameArray[column - 1];
	}
	public void setColumnTypeName(String columnTypeName, int column) {
		this.columnTypeNameArray[column - 1] = columnTypeName;
	}
	public int getPrecision(int column) {
		return precisionArray[column - 1];
	}
	public void setPrecision(int precision, int column) {
		this.precisionArray[column - 1] = precision;
	}
	public int getScale(int column) {
		return scaleArray[column - 1];
	}
	public void setScale(int scale, int column) {
		this.scaleArray[column - 1] = scale;
	}
	public String getSchemaName(int column) {
		return schemaNameArray[column - 1];
	}
	public void setSchemaName(String schemaName, int column) {
		this.schemaNameArray[column - 1] = schemaName;
	}
	public String getTableName(int column) {
		return tableNameArray[column - 1];
	}
	public void setTableName(String tableName, int column) {
		this.tableNameArray[column - 1] = tableName;
	}

	public boolean isAutoIncrement(int column) throws SQLException {
		return false;
	}

	public boolean isCaseSensitive(int column) throws SQLException {
		return false;
	}

	public boolean isCurrency(int column) throws SQLException {
		return false;
	}

	public boolean isDefinitelyWritable(int column) throws SQLException {
		return false;
	}

	public int isNullable(int column) throws SQLException {
		return 0;
	}

	public boolean isReadOnly(int column) throws SQLException {
		return false;
	}

	public boolean isSearchable(int column) throws SQLException {
		return false;
	}

	public boolean isSigned(int column) throws SQLException {
		return false;
	}

	public boolean isWritable(int column) throws SQLException {
		return false;
	}

	public boolean isWrapperFor(Class iface) throws SQLException {
		return false;
	}

	public Object unwrap(Class iface) throws SQLException {
		return null;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		String columnNameStr = "columnName";
		String columnTypeStr = "columnType";
		String columnTypeNameStr = "columnTypeName";
		String columnDisplaySizeStr = "columnDisplaySize";
		String precisionStr = "precision";
		String scaleStr = "scale";
		String space = " ";
		String colon = ":";
		String lineSaperator = (SystemUtils.LINE_SEPARATOR != null) ? SystemUtils.LINE_SEPARATOR : "\n";
		
		sb.append(lineSaperator);
		sb.append("ResultSetMetadataHolder's message.");
		for (int i = 0; i < columnCount; i++) {
			sb.append(lineSaperator);
			sb.append(columnNameStr).append(colon).append(columnNameArray[i]).append(space);
			sb.append(columnTypeStr).append(colon).append(columnTypeArray[i]).append(space);
			sb.append(columnTypeNameStr).append(colon).append(columnTypeNameArray[i]).append(space);
			sb.append(columnDisplaySizeStr).append(colon).append(columnDisplaySizeArray[i]).append(space);
			sb.append(precisionStr).append(colon).append(precisionArray[i]).append(space);
			sb.append(scaleStr).append(colon).append(scaleArray[i]).append(space);			
		}
		
		return sb.toString();
	}
	
}
