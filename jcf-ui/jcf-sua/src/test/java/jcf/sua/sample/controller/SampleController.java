package jcf.sua.sample.controller;

import java.util.List;
import java.util.Map;

import jcf.data.GridData;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;
import jcf.sua.sample.model.SampleModel;
import jcf.sua.sample.service.SampleService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SampleController {

	private SampleService sampleService;

	/**
	 * @param mciRequest
	 * @param mciResponse
	 *
	 *  Client 요청으로 부터 "DS_01"에 해당하는 데이터셋의 첫번째 행을 SampleModel의 인스턴스로 받아온다.
	 */
	@RequestMapping("/sua/sample/get")
	public void getMethodUsage(MciRequest mciRequest, MciResponse mciResponse){
		SampleModel sampleModel = mciRequest.get("DS_01", SampleModel.class);

		if (sampleModel.getId() == null) {
			throw new RuntimeException("failed to get id");
		}
	}

	/**
	 * @param mciRequest
	 * @param mciResponse
	 *
	 * Client 요청으로 부터 id를 key로 하는 파라미터를 받아온다.
	 */
	@RequestMapping("/sua/sample/getParam")
	public void getParamMethodUsage(MciRequest mciRequest, MciResponse mciResponse){
		Map<String, Object> param = mciRequest.getParam();

		if (param.get("id") == null) {
			throw new RuntimeException("failed to get id");
		}
	}



	/**
	 * @param mciRequest
	 * @param mciResponse
	 *
	 * Client 요청으로 부터 id를 key로 하는 파라미터를 받아온다.
	 */
	@RequestMapping("/sua/sample/getParamModel")
	public void getParamModel(MciRequest mciRequest, MciResponse mciResponse){
		SampleModel model = mciRequest.getParam(SampleModel.class);
		if (model == null) {
			throw new RuntimeException("failed to get model");
		}


	}


	/**
	 * @param mciRequest
	 * @param mciResponse
	 *
	 *  Client 요청으로 부터 "DS_01"에 해당하는 그리드데이타를 받아온다.
	 */
	@RequestMapping("/sua/sample/getGridData")
	public void getGridDataMethodUsage(MciRequest mciRequest, MciResponse mciResponse){
		GridData<SampleModel> gridData = mciRequest.getGridData("DS_01", SampleModel.class);

		for (SampleModel sampleModel : gridData.getList()) {
			if (sampleModel.getId() == null) {
				throw new RuntimeException("failed to get id");
			}
		}
	}

	/**
	 * @param mciRequest
	 * @param mciResponse
	 *
	 *  SampleSerivce로 부터 sampleData 한건을 받아와 화면에 전달한다.
	 */
	@RequestMapping("/sua/sample/set")
	public void setMethodUsage(MciRequest mciRequest, MciResponse mciResponse){
		SampleModel sample = sampleService.getSample();
		mciResponse.set("DS_01", sample);
	}

	/**
	 * @param mciRequest
	 * @param mciResponse
	 *
	 *  SampleSerivce로 부터 sampleData 다건을 받아와 화면에 전달한다.
	 */
	@RequestMapping("/sua/sample/setList")
	public void setListMethodUsage(MciRequest mciRequest, MciResponse mciResponse){
		List<SampleModel> samples = sampleService.getSamples();
		mciResponse.setList("DS_01", samples);
	}

	public void setSampleService(SampleService sampleService) {
		this.sampleService = sampleService;
	}
}
