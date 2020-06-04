package jcf.sua.ux.flex.mvc;

import jcf.sua.dataset.DataSetReader;
import jcf.sua.mvc.AbstractMciRequest;
import jcf.sua.ux.flex.dataset.FlexAmfDataSetReader;

/**
 *
 * @author nolang
 *
 */
public final class FlexRequest extends AbstractMciRequest {

	public FlexRequest(DataSetReader reader) {
		this.dataSetMap = ((FlexAmfDataSetReader) reader).getDataSetMap();
		this.paramMap = ((FlexAmfDataSetReader) reader).getParamMap();
	}
}
