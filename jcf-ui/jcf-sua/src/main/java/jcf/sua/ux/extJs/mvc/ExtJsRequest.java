package jcf.sua.ux.extJs.mvc;

import java.util.List;

import jcf.sua.mvc.AbstractMciRequest;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.extJs.dataset.ExtJsDataSet;
import jcf.sua.ux.extJs.dataset.ExtJsDataSetReader;

/**
 *
 * {@link MciRequest}
 *
 * @author nolang
 *
 */
public final class ExtJsRequest extends AbstractMciRequest {

	public ExtJsRequest(ExtJsDataSetReader reader, MciRequestValidator requestValidator) {

		List<String> dataSetIdList = reader.getDataSetIdList();

		for (String dataSetId : dataSetIdList) {
			dataSetMap.put(dataSetId, new ExtJsDataSet(dataSetId, reader.getDataSetColumns(dataSetId), reader.getDataSetRows(dataSetId)));
		}

		paramMap = reader.getParamMap();

		this.requestValidator = requestValidator;
	}
}
