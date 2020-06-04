package jcf.sua.ux.mybuilder.mvc;

import java.util.List;

import jcf.sua.mvc.AbstractMciRequest;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.mybuilder.dataset.MybuilderDataSet;
import jcf.sua.ux.mybuilder.dataset.MybuilderDataSetReader;

/**
 *
 * {@link MciRequest}
 *
 * @author Jeado
 *
 */
public class MybuilderRequest extends AbstractMciRequest{

	public MybuilderRequest(MybuilderDataSetReader reader, MciRequestValidator requestValidator) {
		List<String> dataSetIdList = reader.getDataSetIdList();

		for (String dataSetId : dataSetIdList) {
			dataSetMap.put(dataSetId, new MybuilderDataSet(dataSetId, reader.getDataSetColumns(dataSetId), reader.getDataSetRows(dataSetId)));
		}

		attachments = reader.getAttachments();
		paramMap = reader.getParamMap();

		this.requestValidator = requestValidator;
	}
}
