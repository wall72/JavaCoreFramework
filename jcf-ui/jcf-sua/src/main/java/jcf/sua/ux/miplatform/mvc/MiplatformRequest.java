package jcf.sua.ux.miplatform.mvc;

import java.util.List;

import jcf.sua.mvc.AbstractMciRequest;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.miplatform.dataset.MiplatformDataSet;
import jcf.sua.ux.miplatform.dataset.MiplatformDataSetReader;

/**
*
* {@link MciRequest}
*
* @author mina
*
*/
public class MiplatformRequest extends AbstractMciRequest {

	public MiplatformRequest() {

	}

	public MiplatformRequest(MiplatformDataSetReader reader, MciRequestValidator requestValidator) {
		List<String> dataSetIdList = reader.getDataSetIdList();

		for (String dataSetId : dataSetIdList) {
			dataSetMap.put(dataSetId, new MiplatformDataSet(dataSetId, reader.getDataSetColumns(dataSetId), reader.getDataSetRows(dataSetId)));
		}

		this.paramMap = reader.getParamMap();
		this.attachments = reader.getAttachments();
		this.requestValidator = requestValidator;
	}
}
