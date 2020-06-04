package jcf.sua.ux.nexacro.mvc;

import java.util.List;

import jcf.sua.mvc.AbstractMciRequest;
import jcf.sua.mvc.validation.MciRequestValidator;
import jcf.sua.ux.nexacro.dataset.NexacroDataSet;
import jcf.sua.ux.nexacro.dataset.NexacroDataSetReader;

public class NexacroRequest extends AbstractMciRequest {

	public NexacroRequest(NexacroDataSetReader reader, MciRequestValidator requestValidator){
		List<String> dataSetIdList = reader.getDataSetIdList();
		
		for (String dataSetId : dataSetIdList) {
			this.dataSetMap.put(dataSetId, new NexacroDataSet(dataSetId, reader.getDataSetColumns(dataSetId), reader.getDataSetRows(dataSetId)));
		}
		
		this.paramMap = reader.getParamMap();
		this.attachments = reader.getAttachments();
		this.requestValidator = requestValidator;
	}
}
