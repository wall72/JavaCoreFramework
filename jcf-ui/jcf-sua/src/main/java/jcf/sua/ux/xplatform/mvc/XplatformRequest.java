package jcf.sua.ux.xplatform.mvc;

import java.util.List;

import jcf.sua.mvc.AbstractMciRequest;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.xplatform.dataset.XplatformDataSet;
import jcf.sua.ux.xplatform.dataset.XplatformDataSetReader;

/**
 *
 * {@link MciRequest} 의 Xplatform 구현체
 *
 * @author mina
 *
 */
public class XplatformRequest extends AbstractMciRequest {

	public XplatformRequest( XplatformDataSetReader reader, MciRequestValidator requestValidator) {
		List<String> dataSetIdList = reader.getDataSetIdList();

		for (String dataSetId : dataSetIdList) {
			this.dataSetMap.put(dataSetId, new  XplatformDataSet(dataSetId, reader.getDataSetColumns(dataSetId), reader.getDataSetRows(dataSetId)));
		}

		this.paramMap = reader.getParamMap();
		this.attachments = reader.getAttachments();
		this.requestValidator = requestValidator;
	}
}
