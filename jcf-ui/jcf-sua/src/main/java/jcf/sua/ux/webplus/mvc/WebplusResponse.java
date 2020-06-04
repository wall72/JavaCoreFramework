package jcf.sua.ux.webplus.mvc;

import jcf.sua.dataset.DataSet;
import jcf.sua.mvc.AbstractMciResponse;
import jcf.sua.ux.webplus.dataset.WebplusDataSet;

/**
 *
 * {@link MciResponse} 의 웹플러스 구현체
 *
 * @author Jeado
 *
 */
public class WebplusResponse extends AbstractMciResponse {

	private String MessageCode;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataSet createUxDataSet(String datasetId) {
		return new WebplusDataSet(datasetId);
	}

	public String getMessageCode() {
		return MessageCode;
	}

	public void setMessageCode(String messageCode) {
		MessageCode = messageCode;
	}
}
