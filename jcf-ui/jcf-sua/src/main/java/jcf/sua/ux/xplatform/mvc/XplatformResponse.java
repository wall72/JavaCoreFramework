package jcf.sua.ux.xplatform.mvc;

import jcf.sua.dataset.DataSet;
import jcf.sua.mvc.AbstractMciResponse;
import jcf.sua.ux.xplatform.dataset.XplatformDataSet;

/**
 *
 * {@link MciResponse} 의 Xplatform 구현체
 *
 * @author mina
 *
 */
public class XplatformResponse extends AbstractMciResponse {

	/**
	 * {@inheritDoc}
	 */
	public DataSet createUxDataSet(String datasetId) {
		return new XplatformDataSet(datasetId);
	}
}
