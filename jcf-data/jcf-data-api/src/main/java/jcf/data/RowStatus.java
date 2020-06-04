package jcf.data;

/**
*
* GRID 각 행의 상태 정보
*
* @author nolang
*
*/
public enum RowStatus {
	INSERT,
	UPDATE,
	DELETE,
	NORMAL,
	/** Using only for Webplus */
	RETRIEVE
}