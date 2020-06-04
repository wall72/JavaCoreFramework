package jcf.sua.ux.json.mvc;

import java.util.List;

import jcf.sua.mvc.AbstractMciRequest;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.json.dataset.JsonDataSet;
import jcf.sua.ux.json.dataset.JsonDataSetReader;

/**
 *
 * {@link MciRequest}
 *
 * @author nolang
 *
 */
public final class JsonRequest extends AbstractMciRequest {

	public JsonRequest(JsonDataSetReader reader, MciRequestValidator requestValidator) {
		List<String> dataSetIdList = reader.getDataSetIdList();

		for (String dataSetId : dataSetIdList) {
			this.dataSetMap.put(dataSetId, new JsonDataSet(dataSetId, reader.getDataSetColumns(dataSetId), reader.getDataSetRows(dataSetId)));
		}

		this.paramMap = reader.getParamMap();
		this.attachments = reader.getAttachments();
		this.requestValidator = requestValidator;
	}
}
