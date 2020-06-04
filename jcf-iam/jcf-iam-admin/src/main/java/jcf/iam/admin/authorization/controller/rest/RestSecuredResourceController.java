package jcf.iam.admin.authorization.controller.rest;

import jcf.iam.admin.RestController;
import jcf.iam.admin.authorization.service.SecuredResourceService;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authorization.ViewResourcesRoleMapping;
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
* 사용자 및 권한별 인가 자원 처리와 관련한 연산을 수행한다.
*
*  - Base URL : /jcfiam/admin/secure

*   - 권한별 인가 자원 조회 (GET)    : /role/{roleId}
*   - 권한별 인가 자원 추가 (POST)   : /role/
*   - 권한별 인가 자원 수정 (PUT)    : /role/
*   - 권한별 인가 자원 삭제 (DELETE) : /role/{roleId}?viewResourceId=''&permissionId=''
*
*   - 사용자별 인가 자원 조회 (GET)  : /user/{username}
*   - 사용자별 인가 자원 추가 (POST) : /user/
*   - 사용자별 인가 자원 수정 (PUT)  : /user/
*   - 사용자별 인가 자원 삭제 (DELETE): /user/{username}?viewResourceId=''&permissionId=''
* <pre>
*
* @see jcf.iam.core.IamCustomizerFactory
* @see jcf.iam.core.DefaultCustomizer
* @see jcf.iam.core.Customizer
* @see jcf.iam.core.jdbc.authorization.ViewResourcesRoleMapping
*
* @author nolang
*
*/
@RestController
@RequestMapping("/jcfiam/admin/secure")
public class RestSecuredResourceController {

	@Autowired
	private SecuredResourceService service;

	@Autowired
	private IamCustomizerFactory factory;


	/**
	 *
	 * 권한별 인가 자원 조회
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/role/{roleId}", method = RequestMethod.GET)
	@ResponseBody
	public MciResponse selectResourcesByRole(@RequestBody MciRequest request, MciResponse response, @PathVariable String roleId) {
		response.setList("securedResourceList", service.getSecuredResourceListByRole(roleId));
		return response;
	}

	/**
	 *
	 * 권한별 인가 자원 추가
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/role/", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	public MciResponse insertResourcesByRole(@RequestBody MciRequest request, MciResponse response) {
		ViewResourcesRoleMapping resource = request.get("securedResource", factory.getCustomizer().getPermissionByRoleClass());
		service.insertSecuredResourceRole(resource);
		return response;
	}

	/**
	 *
	 * 권한별 인가 자원 수정 - Default 설정 사용시 비활성화됨
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/role/", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public MciResponse updateResourcesByRole(@RequestBody MciRequest request, MciResponse response) {
		ViewResourcesRoleMapping resource = request.get("securedResource", factory.getCustomizer().getPermissionByRoleClass());
		service.updateSecuredResourceRole(resource);
		return response;
	}

	/**
	 *
	 * 권한별 인가 자원 삭제
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/role/{roleId}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public MciResponse deleteResourcesByRole(@RequestBody MciRequest request, MciResponse response, @PathVariable String roleId) {
		String viewResourceId = request.getParam("viewResourceId");
		String permissionId = request.getParam("permissionId");

		service.deleteSecuredResourceRole(viewResourceId, roleId, permissionId);

		return response;
	}

	/**
	 *
	 * 사용자별 인가 자원 조회
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
	@ResponseBody
	public MciResponse selectResourcesByUser(@RequestBody MciRequest request, MciResponse response, @PathVariable String username) {
		response.setList("securedResourceList", service.getSecuredResourceListByUser(username));
		return response;
	}

	/**
	 *
	 * 사용자별 인가 자원  추가
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/user/", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	public MciResponse insertResourcesByUser(@RequestBody MciRequest request, MciResponse response) {
		ViewResourcesRoleMapping resource = request.get("securedResource", factory.getCustomizer().getPermissionByUserClass());
		service.insertSecuredResourceUser(resource);

		return response;
	}

	/**
	 *
	 * 사용자별 인가 자원 수정  - Default 설정 사용시 비활성화됨
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/user/", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public MciResponse updateResourcesByUser(@RequestBody MciRequest request, MciResponse response) {
		ViewResourcesRoleMapping resource = request.get("securedResource", factory.getCustomizer().getPermissionByUserClass());
		service.updateSecuredResourceUser(resource);
		return response;
	}

	/**
	 *
	 * 사용자별 인가 자원 삭제
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/user/{username}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public MciResponse deleteResourcesByUser(@RequestBody MciRequest request, MciResponse response, @PathVariable String username) {
		String viewResourceId = request.getParam("viewResourceId");
		String permissionId = request.getParam("permissionId");

		service.deleteSecuredResourceUser(viewResourceId, username, permissionId);

		return response;
	}
}
