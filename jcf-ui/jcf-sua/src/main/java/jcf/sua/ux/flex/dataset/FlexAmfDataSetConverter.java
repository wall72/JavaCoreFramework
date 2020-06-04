package jcf.sua.ux.flex.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.dataset.DataSetRowImpl;
import jcf.sua.ux.flex.FlexAmfDataSet;
import jcf.sua.ux.flex.FlexAmfDataSetColumn;
import jcf.sua.ux.flex.FlexAmfDataSetRow;

import org.springframework.stereotype.Component;

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
@Component
public class FlexAmfDataSetConverter {

	/**
	 *
	 * @param dataSetMap
	 * @return
	 */
	public Map<String, DataSet> toMciDataSet(Map<String, FlexAmfDataSet> dataSetMap) {
		Map<String, DataSet> map = new HashMap<String, DataSet>();

		Iterator<String> it = dataSetMap.keySet().iterator();

		while (it.hasNext()) {
			String dataSetId = it.next();
			map.put(dataSetId, toMciDataSet(dataSetMap.get(dataSetId)));
		}

		return map;
	}

	/**
	 *
	 * @param flexAmfDataSet
	 * @return
	 */
	public FlexDataSet toMciDataSet(FlexAmfDataSet flexAmfDataSet) {
		FlexDataSet dataSet = new FlexDataSet(flexAmfDataSet.getId());

		List<FlexAmfDataSetColumn> amfDataSetColumnList = flexAmfDataSet.getColumns();
		List<DataSetColumn> dataSetColumnList = new ArrayList<DataSetColumn>();

		for (FlexAmfDataSetColumn amfColumn : amfDataSetColumnList) {
			dataSetColumnList.add(new DataSetColumn(amfColumn.getColumnName(), String.class));
		}

		List<FlexAmfDataSetRow> amfDataSetRowList = flexAmfDataSet.getRows();
		List<DataSetRow> dataSetRowList = new ArrayList<DataSetRow>();

		for (FlexAmfDataSetRow amfRow : amfDataSetRowList) {
			DataSetRow dataSetRow = new DataSetRowImpl();

			Iterator<String> columnNames = amfRow.keySet().iterator();

			while (columnNames.hasNext()) {
				String columnName = columnNames.next();
				dataSetRow.add(columnName, amfRow.get(columnName));
			}

			dataSetRow.setRowStatus(amfRow.getRowStatus());

			dataSetRowList.add(dataSetRow);
		}

		dataSet.setColumns(dataSetColumnList);
		dataSet.setRows(dataSetRowList);

		return dataSet;
	}

	/**
	 *
	 * @param dataSetMap
	 * @return
	 */
	public Map<String, FlexAmfDataSet> toFlexAmfDataSet(
			Map<String, DataSet> dataSetMap) {
		Map<String, FlexAmfDataSet> map = new HashMap<String, FlexAmfDataSet>();

		Iterator<String> it = dataSetMap.keySet().iterator();

		while (it.hasNext()) {
			String dataSetId = it.next();
			map.put(dataSetId, toFlexAmfDataSet(dataSetMap.get(dataSetId)));
		}

		return map;
	}

	/**
	 *
	 * @param dataSet
	 * @return
	 */
	public FlexAmfDataSet toFlexAmfDataSet(DataSet dataSet) {
		FlexAmfDataSet flexAmfDataSet = new FlexAmfDataSet();

		List<DataSetColumn> dataSetColumnList = ((FlexDataSet) dataSet).getColumns();
		List<FlexAmfDataSetColumn> amfDataSetColumnList = new ArrayList<FlexAmfDataSetColumn>();

		for (DataSetColumn amfColumn : dataSetColumnList) {
			FlexAmfDataSetColumn column = new FlexAmfDataSetColumn();

			column.setColumnName(amfColumn.getColumnName());

			amfDataSetColumnList.add(column);
		}

		List<FlexAmfDataSetRow> amfDataSetRowList = new ArrayList<FlexAmfDataSetRow>();

		for (int i = 0; i < dataSet.getRowCount(); ++i) {
			FlexAmfDataSetRow amfRow = new FlexAmfDataSetRow();

			amfRow.setRowStatus(dataSet.getDataSetRow(i).getRowStatus());
			amfRow.putAll(dataSet.getBean(HashMap.class, i));

			amfDataSetRowList.add(amfRow);
		}

		flexAmfDataSet.setId(dataSet.getId());
		flexAmfDataSet.setColumns(amfDataSetColumnList);
		flexAmfDataSet.setRows(amfDataSetRowList);

		return flexAmfDataSet;
	}
}
