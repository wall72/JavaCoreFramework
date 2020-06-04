package jcf.iam.admin.view.controller;

import jcf.data.GridData;
import jcf.iam.admin.view.service.ViewResourcesService;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authorization.ViewResourcesMapping;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <pre>
 * JCFIAM Admin 콘솔이 활성화 되었을때 동작하며, 화면 처리와 관련한 연산을 수행한다.
 *
 *  - Base URL : /jcfiam/admin/view
 *
 *   - 조회 : /selectMenuTree
 *           /selectMenus
 *           /selectMenu
 *   - 추가 : /insert
 *   - 수정 : /update
 *   - 삭제 : /update
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
@Controller
@RequestMapping("/jcfiam/admin/view")
public class ViewResourcesController {

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
	@RequestMapping("/selectMenuTree")
	public void selectMenuTree(MciRequest request, MciResponse response) {
		String parentViewId = request.getParam("parentViewId");

		if(!StringUtils.hasText(parentViewId)){
			parentViewId = ROOT;
		}

		response.setList("menuTree", service.getMenuTree(parentViewId));
	}

	/**
	 *
	 * MENU LIST 조회
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/selectMenus")
	public void selectMenus(MciRequest request, MciResponse response) {
		ViewResourcesMapping view = request.get("viewResource", customizer.getCustomizer().getViewResourceClass());
		response.setList("menu", service.getMenuList(view));
	}

	/**
	 *
	 * MENU 조회
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/selectMenu")
	public void selectMenu(MciRequest request, MciResponse response) {
		response.set("menu", service.getMenuDetails(request.getParam("viewResourceId")));
	}

	/**
	 *
	 * MEUN 추가
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/insert")
	public void insert(MciRequest request, MciResponse response) {
		ViewResourcesMapping view = request.get("viewResource", customizer.getCustomizer().getViewResourceClass());
		service.insertViewResources(view);
	}

	/**
	 *
	 * MEUN 삭제
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/delete")
	public void delete(MciRequest request, MciResponse response) {
		String viewResourceId = request.getParam("viewResourceId");

		ViewResourcesMapping view = BeanUtils.instantiate(customizer.getCustomizer().getViewResourceClass());

		view.setViewResourceId(viewResourceId);

		service.deleteViewResources(view);
	}

	/**
	 *
	 * MEUN 수정
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/update")
	public void update(MciRequest request, MciResponse response) {
		ViewResourcesMapping view = request.get("viewResource", customizer.getCustomizer().getViewResourceClass());
		service.updateViewResources(view);
	}

	/**
	 *
	 * MENU 추가/수정/삭제
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/save")
	public void save(MciRequest request, MciResponse response) {
		GridData<? extends ViewResourcesMapping> gridData = request.getGridData("viewResources", customizer.getCustomizer().getViewResourceClass());
		service.saveViewResources(gridData);
	}
}
