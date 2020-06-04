package jcf.sua.ux.websquare.mvc;

import java.util.List;

import jcf.sua.dataset.DataSetReader;
import jcf.sua.mvc.AbstractMciRequest;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.websquare.dataset.WebsquareDataSet;

/**
 *
 * {@link MciRequest} 의 웹스퀘어 구현체
 *
 * @author nolang
 *
 */
public class WebsquareRequest extends AbstractMciRequest {

	public WebsquareRequest(DataSetReader reader, MciRequestValidator requestValidator) {
		List<String> dataSetIdList = reader.getDataSetIdList();

		for (String dataSetId : dataSetIdList) {
			dataSetMap.put(dataSetId, new WebsquareDataSet(dataSetId, reader.getDataSetColumns(dataSetId), reader.getDataSetRows(dataSetId)));
		}

		this.requestValidator = requestValidator;
	}

}
