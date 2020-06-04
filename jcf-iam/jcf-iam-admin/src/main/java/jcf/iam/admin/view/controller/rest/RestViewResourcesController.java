package jcf.iam.admin.view.controller.rest;

import jcf.iam.admin.RestController;
import jcf.iam.admin.view.service.ViewResourcesService;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authorization.ViewResourcesMapping;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <pre>
 * JCFIAM Admin 콘솔이 활성화 되었을때 동작하며, 화면 처리와 관련한 연산을 수행한다.
 *
 *  - Base URL : /jcfiam/admin/view
 *
*   - 조회 (GET)    : /?parentViewId=''
*                  : /{viewResourceid}
*   - 추가 (POST)   : /
*   - 수정 (PUT)    : /
*   - 삭제 (DELETE) : /{viewResourceid}
 * <pre>
 *
 * @see jcf.iam.core.IamCustomizerFactory
 * @see jcf.iam.core.DefaultCustomizer
 * @see jcf.iam.core.Customizer
 * @see jcf.iam.core.jdbc.authorization.ViewResourcesMapping
 *
 * @author nolang
 *
 */
@RestController
@RequestMapping("/jcfiam/admin/view")
public class RestViewResourcesController {

	@Autowired
	private ViewResourcesService service;

	private static final String ROOT = " ";

	@Autowired
	private IamCustomizerFactory customizer;

	/**
	 *
	 * MENU TREE 조회
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public MciResponse selectMenuTree(@RequestBody MciRequest request, MciResponse response) {
		String parentViewId = request.getParam("parentViewId");

		if(!StringUtils.hasText(parentViewId)){
			parentViewId = ROOT;
		}

		response.setList("menuTree", service.getMenuTree(parentViewId));

		return response;
	}

	/**
	 *
	 * MENU 조회
	 *
	 * @param request
	 * @param response
	 * @param viewResourceId
	 */
	@RequestMapping(value = "/{viewResourceId}", method = RequestMethod.GET)
	@ResponseBody
	public MciResponse selectMenu(@RequestBody MciRequest request, MciResponse response, @PathVariable String viewResourceId) {
		response.set("menu", service.getMenuDetails(viewResourceId));
		return response;
	}

	/**
	 *
	 * MEUN 추가
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	public MciResponse insert(@RequestBody MciRequest request, MciResponse response) {
		ViewResourcesMapping view = request.get("viewResource", customizer.getCustomizer().getViewResourceClass());
		service.insertViewResources(view);
		return response;
	}

	/**
	 *
	 * MEUN 삭제
	 *
	 * @param request
	 * @param response
	 * @param viewResourceId
	 */
	@RequestMapping(value = "/{viewResourceId}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public MciResponse delete(@RequestBody MciRequest request, MciResponse response, @PathVariable String viewResourceId) {
		ViewResourcesMapping view = BeanUtils.instantiate(customizer.getCustomizer().getViewResourceClass());

		view.setViewResourceId(viewResourceId);

		service.deleteViewResources(view);
		return response;
	}

	/**
	 *
	 * MEUN 수정
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public MciResponse update(@RequestBody MciRequest request, MciResponse response) {
		ViewResourcesMapping view = request.get("viewResource", customizer.getCustomizer().getViewResourceClass());
		service.updateViewResources(view);
		return response;
	}
}
