package jcf.iam.admin.authorization.controller.rest;

import jcf.iam.admin.RestController;
import jcf.iam.admin.authorization.service.PermissionService;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authorization.PermissionMapping;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
*
* <pre>
* 리소스 세부 접근 권한(READ/CREATE/UPDATE/DELETE)과 관련한 연산을 수행한다.
*
*  - Base URL : /jcfiam/admin/permissions
*
*   - 조회 (GET)    : /
*   - 추가 (POST)   : /
*   - 수정 (PUT)    : /
*   - 삭제 (DELETE) : /{permissionId}
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
@RestController
@RequestMapping("/jcfiam/admin/permissions")
public class RestPermissionController {

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
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public MciResponse selectpermissions(@RequestBody MciRequest request, MciResponse response) {
		response.setList("permissions", service.getPermissions());
		return response;
	}

	/**
	 *
	 * 리소스 세부 접근 권한 정의 추가
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	public MciResponse insertPermission(@RequestBody MciRequest request, MciResponse response) {
		PermissionMapping permission = request.get("permission", factory.getCustomizer().getPermissionClass());
		service.insertPermission(permission);
		return response;
	}

	/**
	 *
	 * 리소스 세부 접근 권한 정의 수정
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public MciResponse updatePermission(@RequestBody MciRequest request, MciResponse response) {
		PermissionMapping permission = request.get("permission", factory.getCustomizer().getPermissionClass());
		service.updatePermission(permission);
		return response;
	}

	/**
	 *
	 * 리소스 세부 접근 권한 정의 삭제
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/{permissionId}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public MciResponse deletePermission(@RequestBody MciRequest request, MciResponse response, @PathVariable String permissionId) {
		service.deletePermission(permissionId);
		return response;
	}
}
