package jcf.iam.admin.authorization.controller;

import jcf.iam.admin.authorization.service.PermissionService;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authorization.PermissionMapping;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
*
* <pre>
* 리소스 세부 접근 권한(READ/CREATE/UPDATE/DELETE)과 관련한 연산을 수행한다.
*
*  - Base URL : /jcfiam/admin/permissions
*
*   - 조회 : /select
*   - 추가 : /insert
*   - 수정 : /update
*   - 삭제 : /update
* <pre>
*
* @see jcf.iam.core.IamCustomizerFactory
* @see jcf.iam.core.DefaultCustomizer
* @see jcf.iam.core.Customizer
* @see jcf.iam.core.jdbc.authorization.PermissionMapping
*
* @author nolang
*
*/
@Controller
@RequestMapping("/jcfiam/admin/permissions")
public class PermissionController {

	@Autowired
	private PermissionService service;

	@Autowired
	private IamCustomizerFactory factory;


	/**
	 *
	 * 리소스 세부 접근 권한 정의 조회
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/select")
	public void selectpermissions(MciRequest request, MciResponse response) {
		response.setList("permissions", service.getPermissions());
	}

	/**
	 *
	 * 리소스 세부 접근 권한 정의 추가
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/insert")
	public void insertPermission(MciRequest request, MciResponse response) {
		PermissionMapping permission = request.get("permission", factory.getCustomizer().getPermissionClass());
		service.insertPermission(permission);
	}

	/**
	 *
	 * 리소스 세부 접근 권한 정의 수정
	 * @param request
	 * @param response
	 */
	@RequestMapping("/update")
	public void updatePermission(MciRequest request, MciResponse response) {
		PermissionMapping permission = request.get("permission", factory.getCustomizer().getPermissionClass());
		service.updatePermission(permission);
	}

	/**
	 *
	 * 리소스 세부 접근 권한 정의 삭제
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/delete")
	public void deletePermission(MciRequest request, MciResponse response) {
		service.deletePermission(request.getParam("permissionId"));
	}
}
