package jcf.iam.admin.authentication.controller.rest;

import jcf.iam.admin.RestController;
import jcf.iam.admin.authentication.service.RoleService;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authentication.RoleMapping;
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
 * <pre>
 * 시스템에 정의된 권한정보를 처리하는 연산을 수행한다.
 *
 *  - Base URL : /jcfiam/admin/roles
 *
*   - 조회 (GET)    : /
*                  : /{roleId}
*   - 추가 (POST)   : /
*   - 수정 (PUT)    : /
*   - 삭제 (DELETE) : /{roleId}

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
@RestController
@RequestMapping("/jcfiam/admin/roles")
public class RestRoleController {

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
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public MciResponse selectRoles(@RequestBody MciRequest request, MciResponse response)	{
		response.setList("roleList", service.getRoleList());
		return response;
	}

	/**
	 *
	 * 권한 세부정보 조회
	 *
	 * @param request
	 * @param response
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/{roleId}", method = RequestMethod.GET)
	@ResponseBody
	public MciResponse selectRole(@RequestBody MciRequest request, MciResponse response, @PathVariable String roleId) {
		response.set("role", service.getRole(roleId));
		return response;
	}

	/**
	 *
	 * 권한 추가
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	public MciResponse insertRole(@RequestBody MciRequest request, MciResponse response) {
		RoleMapping role = request.get("role", factory.getCustomizer().getRoleClass());
		service.insertRole(role);
		return response;
	}

	/**
	 *
	 * 권한 수정
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public MciResponse updateRole(@RequestBody MciRequest request, MciResponse response) {
		RoleMapping role = request.get("role", factory.getCustomizer().getRoleClass());
		service.updateRole(role);
		return response;
	}

	/**
	 *
	 * 권한 삭제
	 *
	 * @param request
	 * @param response
	 * @param roleid
	 * @return
	 */
	@RequestMapping(value = "/{roleId}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public MciResponse deleteRole(@RequestBody MciRequest request, MciResponse response, @PathVariable String roleId) {
		service.deleteUser(roleId);
		return response;
	}
}
