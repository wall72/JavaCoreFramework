package jcf.sua.ux.flex;

import java.util.List;

/**
 * * * <p>
 * 참고사항
 * <nl>
 * <li>Su 프레임워크와 RPC 통신으로 인한 Mapping이 되어있습니다. 변경시 꼭 SuFramework 담당자에게 연락하세요.</li>
 * </nl>
 * </p>
 * <br/>
 *
 * @author nolang
 *
 */
public class FlexAmfDataSet {
	private String id;
	private List<FlexAmfDataSetColumn> columns;
	private List<FlexAmfDataSetRow> rows;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<FlexAmfDataSetRow> getRows() {
		return rows;
	}

	public void setRows(List<FlexAmfDataSetRow> rows) {
		this.rows = rows;
	}

	public List<FlexAmfDataSetColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<FlexAmfDataSetColumn> columns) {
		this.columns = columns;
	}
}
