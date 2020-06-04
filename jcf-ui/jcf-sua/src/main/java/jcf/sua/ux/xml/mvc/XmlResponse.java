package jcf.sua.ux.xml.mvc;

import jcf.sua.dataset.DataSet;
import jcf.sua.mvc.AbstractMciResponse;
import jcf.sua.mvc.MciResponse;
import jcf.sua.ux.xml.dataset.XmlDataSet;

/**
 *
 * {@link MciResponse} 의 XML 구현체
 *
 * @author nolang
 *
 */
public class XmlResponse extends AbstractMciResponse {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataSet createUxDataSet(String datasetId) {
		return new XmlDataSet(datasetId);
	}

}
