package jcf.sua.ux.webflow.dataset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciDataSetAccessor;
import jcf.sua.mvc.file.operator.FileOperator;

/**
 *
 * {@link DataSetWriter} 의 표준웹 구현체
 *
 * @author nolang
 *
 */
public class WebFlowDataSetWriter implements DataSetWriter {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private FileOperator fileOperator;
	private MciDataSetAccessor accessor;

	public WebFlowDataSetWriter(HttpServletRequest request,  HttpServletResponse response, FileOperator fileOperator, MciDataSetAccessor accessor) {
		this.request = request;
		this.response = response;
		this.fileOperator = fileOperator;
		this.accessor = accessor;
	}

	/**
	 * {@inheritDoc}
	 */
	public void write() {
		if(accessor != null && accessor.isFileProcessing()){
			fileOperator.sendFileStream(request, response, accessor.getDownloadFile());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDataSetAccessor(MciDataSetAccessor accessor) {
		this.accessor = accessor;
	}
}
