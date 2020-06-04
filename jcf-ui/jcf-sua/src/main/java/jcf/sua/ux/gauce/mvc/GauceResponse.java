package jcf.sua.ux.gauce.mvc;

import jcf.sua.dataset.DataSet;
import jcf.sua.mvc.AbstractMciResponse;
import jcf.sua.ux.gauce.dataset.GauceDataSet;

/**
 *
 * {@link MciResponse}
 *
 * @author nolang
 *
 */
public class GauceResponse extends AbstractMciResponse {

	/**
	 * {@inheritDoc}
	 */
	public DataSet createUxDataSet(String datasetId) {
		return new GauceDataSet(datasetId);
	}
}
