package jcf.sua.ux.flex;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * * <p>
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
public final class FlexMessagePayload {
	private Map<String, FlexAmfDataSet> dataSetMap;
	private Map<String, String> params;

	public Map<String, FlexAmfDataSet> getDataSetMap() {
		return dataSetMap;
	}

	public void setDataSetMap(Map<String, FlexAmfDataSet> dataSetMap) {
		this.dataSetMap = dataSetMap;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
}