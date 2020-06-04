package jcf.sua.dataset;

/**
*
* 데이터셋의 컬럼 정보
*
* @author nolang
*/
public class DataSetColumn {

	private String columnName;
	private Class<?> columnType;

	/**
	 *
	 * Method description : Constructor
	 */
	public DataSetColumn() {
	}

	/**
	 *
	 * Method description : Constructor
	 */
	public DataSetColumn(String columnName, Class<?> columnType) {
		this.columnName = columnName;
		this.columnType = columnType;
	}

	/**
	 * column 이름 반환
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * column 이름 지정
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * column type 반환
	 */
	public Class<?> getColumnType() {
		return columnType;
	}

	/**
	 * column type 지정
	 */
	public void setColumnType(Class<?> columnType) {
		this.columnType = columnType;
	}
}