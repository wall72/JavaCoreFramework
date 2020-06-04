package jcf.sua.ux.xml.mvc;

import java.util.List;

import jcf.sua.dataset.DataSetReader;
import jcf.sua.mvc.AbstractMciRequest;
import jcf.sua.mvc.MciRequest;
import jcf.sua.ux.xml.dataset.XmlDataSet;

/**
 *
 * {@link MciRequest} 의 XML 구현체
 *
 * @author nolang
 *
 */
public class XmlRequest extends AbstractMciRequest {

	public XmlRequest(DataSetReader reader) {
		List<String> dataSetIdList = reader.getDataSetIdList();

		for (String dataSetId : dataSetIdList) {
			this.dataSetMap.put(dataSetId, new XmlDataSet(dataSetId, reader.getDataSetColumns(dataSetId), reader.getDataSetRows(dataSetId)));
		}

		this.paramMap = reader.getParamMap();
	}

}
