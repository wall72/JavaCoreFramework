package jcf.sua.ux.flex.dataset;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcf.sua.dataset.DataSet;
import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetReader;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.exception.MciException;
import jcf.sua.ux.flex.FlexMessage;
import jcf.sua.ux.flex.FlexMessagePayload;
import jcf.upload.FileInfo;
import flex.messaging.messages.RemotingMessage;

/**
 *
 * SU Framework에서 전송된 데이터를 추출
 *
 * @author nolang
 *
 */
public class FlexAmfDataSetReader implements DataSetReader {

	private Map<String, Object> paramMap = new HashMap<String, Object>();
	private Map<String, DataSet> dataSetMap = new HashMap<String, DataSet>();

	private FlexAmfDataSetConverter converter = new FlexAmfDataSetConverter();

	@SuppressWarnings("unchecked")
	public FlexAmfDataSetReader(RemotingMessage remoteMessage) {
		List<?> messages = remoteMessage.getParameters();

		if( messages.size() > 1){
			throw new MciException("Flex AMF message parameter's count should be 1");
		}

		FlexMessage<FlexMessagePayload> flexMessage = null;

		if (messages.size() == 1) {
			if (messages.get(0) instanceof FlexMessage) {
				flexMessage = (FlexMessage<FlexMessagePayload>) messages.get(0);
			} else {
				throw new MciException("화면으로 부터 MCIMessage를 받을 수 없습니다.");
			}
		}

		if(flexMessage.getPayload().getDataSetMap() != null){
			dataSetMap.putAll(converter.toMciDataSet(flexMessage.getPayload().getDataSetMap()));
		}

		if(flexMessage.getPayload().getParams() != null){
			paramMap.putAll(flexMessage.getPayload().getParams());
		}
	}

	public Map<String, Object> getParamMap() {
		return paramMap;
	}

	public  Map<String, DataSet> getDataSetMap()	{
		return dataSetMap;
	}

	public List<String> getDataSetIdList() {
		throw new UnsupportedOperationException();
	}

	public List<DataSetRow> getDataSetRows(String dataSetId) {
		throw new UnsupportedOperationException();
	}

	public List<DataSetColumn> getDataSetColumns(String dataSetId) {
		throw new UnsupportedOperationException();
	}

	public List<FileInfo> getAttachments() {
		return Collections.emptyList();
	}
}
