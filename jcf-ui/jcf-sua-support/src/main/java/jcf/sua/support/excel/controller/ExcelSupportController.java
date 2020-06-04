package jcf.sua.support.excel.controller;

import jcf.sua.dataset.AbstractDataSetStreamWriterStreamHandlerAdapter;
import jcf.sua.dataset.DataSetStreamWriter;
import jcf.sua.dataset.DataSetStreamWriterStreamHandlerAdapter;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.support.excel.ExcelStreamWriterFactory;
import jcf.sua.support.rad.service.RadSupportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author nolang
 *
 */
@Controller
@RequestMapping("/sua/excel")
public class ExcelSupportController {

	@Autowired
	private RadSupportService service;

	/**
	 *
	 * 시스템에서 전송된 SQLID를 실행하여 조회된 결과를 엑셀파일로 생성하여 클라이언트로 전송한다.
	 *
	 * @param request
	 * @param response
	 * @param fileName
	 * @param sqlId
	 * @param bufferSize
	 */
	@RequestMapping("/download/{fileName}")
	public void selectByStream(final MciRequest request, MciResponse response, @PathVariable final String fileName, @RequestParam(value = "_sqlId") final String sqlId, @RequestParam(value = "_bufferSize", defaultValue = "1024") final int bufferSize) {
		service.selectBySream(sqlId, request.getParam(), new DataSetStreamWriterStreamHandlerAdapter<Object>(
				response.getStreamWriter(new ExcelStreamWriterFactory()), fileName, bufferSize));
	}
}
