package jcf.sua.ux.miplatform.mvc;

import jcf.sua.dataset.DataSet;
import jcf.sua.mvc.AbstractMciResponse;
import jcf.sua.ux.miplatform.dataset.MiplatformDataSet;

/**
*
* {@link MciResponse}
*
* @author mina
*
*/
public class MiplatformResponse extends AbstractMciResponse {

	/**
	 * {@inheritDoc}
	 */
	public DataSet createUxDataSet(String datasetId) {
		return new MiplatformDataSet(datasetId);
	}
}
