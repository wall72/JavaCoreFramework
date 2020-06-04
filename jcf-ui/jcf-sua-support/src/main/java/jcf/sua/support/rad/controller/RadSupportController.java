package jcf.sua.support.rad.controller;

import java.util.List;
import java.util.Map;

import jcf.sua.dataset.DataSetStreamWriterStreamHandlerAdapter;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.support.rad.service.RadSupportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * RAD (Rapid Application Development) 지원을 위한 컨트롤러 클래스
 *
 * @author nolang
 *
 */
@Controller
@RequestMapping("/sql")
public class RadSupportController {

	@Autowired
	private RadSupportService service;

	public void setService(RadSupportService service) {
		this.service = service;
	}

	/**
	 *
	 * 조회
	 *
	 * @param request
	 * @param response
	 * @param dataSetId
	 * @param sqlId
	 */
	@RequestMapping("/select/{dataSetId}")
	public void select(MciRequest request, MciResponse response, @PathVariable String dataSetId, @RequestParam(value = "_sqlId") String sqlId) {
		response.setList(dataSetId, service.select(sqlId, request.getParam()));
	}

	/**
	 *
	 * 대용량 데이터 전송을 위한 스트리밍 서비스
	 *
	 * @param request
	 * @param response
	 * @param dataSetId
	 * @param sqlId
	 * @param bufferSize
	 */
	@RequestMapping("/selectByStream/{dataSetId}")
	public void selectByStream(final MciRequest request, MciResponse response, @PathVariable String dataSetId,  @RequestParam(value = "_sqlId") final String sqlId, @RequestParam(value = "_bufferSize", defaultValue = "1024") int bufferSize) {
//		response.stream(new StreamSource<Object>() {
//
//			public void read(AbstractMciStreamHandler<Object> streamHandler) {
//				service.selectBySream(sqlId, request.getParam(), streamHandler);
//			}
//
//		}, new MciStreamHandler<Object>(dataSetId, bufferSize) {
//
//			public void handleRow(DataSetStreamWriter streamWriter, Object valueObject) {
//				streamWriter.addStreamData(valueObject);
//			}
//
//		});

		service.selectBySream(sqlId, request.getParam(), new DataSetStreamWriterStreamHandlerAdapter<Object>(response.getStreamWriter(), dataSetId, bufferSize));
	}

	/**
	 *
	 * 추가
	 *
	 * @param request
	 * @param response
	 * @param dataSetId
	 * @param sqlId
	 */
	@RequestMapping("/insert/{dataSetId}")
	public void insert(MciRequest request, MciResponse response, @PathVariable String dataSetId, @RequestParam(value = "_sqlId") String sqlId) {
		List<Map<String, String>> parameterList = request.getMapList(dataSetId);

		if(parameterList != null && parameterList.size() > 0){
			service.insert(sqlId, parameterList);
		}
	}

	/**
	 *
	 * 수정
	 *
	 * @param request
	 * @param response
	 * @param dataSetId
	 * @param sqlId
	 */
	@RequestMapping("/update/{dataSetId}")
	public void update(MciRequest request, MciResponse response, @PathVariable String dataSetId, @RequestParam(value = "_sqlId") String sqlId) {
		List<Map<String, String>> parameterList = request.getMapList(dataSetId);

		if(parameterList != null && parameterList.size() > 0){
			service.update(sqlId, parameterList);
		}
	}

	/**
	 *
	 * 삭제
	 *
	 * @param request
	 * @param response
	 * @param dataSetId
	 * @param sqlId
	 */
	@RequestMapping("/delete/{dataSetId}")
	public void delete(MciRequest request, MciResponse response, @PathVariable String dataSetId, @RequestParam(value = "_sqlId") String sqlId) {
		List<Map<String, String>> parameterList = request.getMapList(dataSetId);

		if(parameterList != null && parameterList.size() > 0){
			service.delete(sqlId, parameterList);
		}
	}
}
