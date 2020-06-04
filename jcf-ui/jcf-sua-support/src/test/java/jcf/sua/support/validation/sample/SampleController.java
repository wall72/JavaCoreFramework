package jcf.sua.support.validation.sample;

import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * 테스트를 위한 SUA 요청을 받아 결과를 반환한다.
 *
 * @author nolang
 *
 */
@Controller
@RequestMapping("/sample")
public class SampleController {

	/*
	 * JSR 303 벨리테이션 테스트
	 *
	 * @param request
	 *
	 * @param response
	 */
	@RequestMapping("/validation")
	public void validation(MciRequest request, MciResponse response) {

		SampleModel sample = request.get("ds_grid", SampleModel.class);
		response.set("jcfsuaDataSet", sample);
	}

}
