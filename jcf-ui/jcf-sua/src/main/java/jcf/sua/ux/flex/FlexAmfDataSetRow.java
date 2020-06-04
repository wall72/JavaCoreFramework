package jcf.sua.ux.flex;

import java.util.HashMap;

import jcf.data.RowStatus;
import jcf.sua.exception.MciException;

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
public class FlexAmfDataSetRow extends HashMap<String, Object> {
	private String rowStatus;

	public String getRowStatus() {
		return rowStatus;
	}

	public void setRowStatus(String rowStatus) {
		RowStatus rst = rowStatus == null ? RowStatus.NORMAL : RowStatus.valueOf(RowStatus.class, rowStatus.toUpperCase());

		if(rst == null)	{
			throw new MciException(String.format("지원하지 않는 RowStatus Type 입니다. - RowsStatus={%s}", rowStatus));
		}

		this.rowStatus = rowStatus;
	}

	@Override
	public Object get(Object key) {
		Object value = super.get(key);

		if(value == null)	{
			value = "";
		}

		return value;
	}
}
