package jcf.sua.support.excel;

import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetStreamWriterFactory;
import jcf.sua.ux.excel.dataset.ExcelRadStreamWriter;

/**
 *
 * @author nolang
 *
 */
public class ExcelStreamWriterFactory implements DataSetStreamWriterFactory {

	public DataSetStreamWriter getStreamWriter(HttpServletResponse response) {
		return new ExcelRadStreamWriter(response);
	}

}
