package jcf.sua.ux.gauce.mvc;

import java.util.List;

import jcf.sua.mvc.AbstractMciRequest;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.gauce.dataset.GauceDataSet;
import jcf.sua.ux.gauce.dataset.GauceDataSetReader;

/**
 *
 * {@link MciRequest}
 *
 * @author nolang
 *
 */
public class GauceRequest extends AbstractMciRequest {

	public GauceRequest() {

	}

	public GauceRequest(GauceDataSetReader reader, MciRequestValidator requestValidator) {
		List<String> dataSetIdList = reader.getDataSetIdList();

		for (String dataSetId : dataSetIdList) {
			this.dataSetMap.put(dataSetId, new GauceDataSet(dataSetId, reader.getDataSetColumns(dataSetId), reader.getDataSetRows(dataSetId)));
		}

		this.paramMap = reader.getParamMap();
		this.requestValidator = requestValidator;
	}
}
