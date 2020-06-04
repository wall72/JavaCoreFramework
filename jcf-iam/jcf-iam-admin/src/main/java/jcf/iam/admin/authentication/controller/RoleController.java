package jcf.iam.admin.authentication.controller;

import jcf.iam.admin.authentication.service.RoleService;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authentication.RoleMapping;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <pre>
 * 시스템에 정의된 권한정보를 처리하는 연산을 수행한다.
 *
 *  - Base URL : /jcfiam/admin/roles
 *
 *   - 조회 : /selectRoles
 *           /select
 *   - 추가 : /insert
 *   - 수정 : /update
 *   - 삭제 : /update
 * <pre>
 *
 * @see jcf.iam.core.IamCustomizerFactory
 * @see jcf.iam.core.DefaultCustomizer
 * @see jcf.iam.core.Customizer
 * @see jcf.iam.core.jdbc.authentication.RoleMapping
 * @see jcf.iam.admin.authentication.service.RoleService
 *
 * @author nolang
 *
 */
@Controller
@RequestMapping("/jcfiam/admin/roles")
public class RoleController {

	@Autowired
	private RoleService service;

	@Autowired
	private IamCustomizerFactory factory;

	/**
	 *
	 * 권한리스트 조회
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/selectRoles")
	public void selectRoles(MciRequest request, MciResponse response)	{
		response.setList("roleList", service.getRoleList());
	}

	/**
	 *
	 * 권한 세부정보 조회
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/select")
	public void selectRole(MciRequest request, MciResponse response) {
		response.set("role", service.getRole(request.getParam("roleId")));
	}

	/**
	 *
	 * 권한 추가
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/insert")
	public void insertRole(MciRequest request, MciResponse response) {
		RoleMapping role = request.get("role", factory.getCustomizer().getRoleClass());
		service.insertRole(role);
	}

	/**
	 *
	 * 권한 수정
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/update")
	public void updateRole(MciRequest request, MciResponse response) {
		RoleMapping role = request.get("role", factory.getCustomizer().getRoleClass());
		service.updateRole(role);
	}

	/**
	 *
	 * 권한 삭제
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/delete")
	public void deleteRole(MciRequest request, MciResponse response) {
		String roleId = request.getParam("roleId");
		service.deleteUser(roleId);
	}
}
