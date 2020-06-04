package jcf.iam.admin.authorization.controller;

import jcf.data.GridData;
import jcf.iam.admin.authorization.service.SecuredResourceService;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authorization.ViewResourcesRoleMapping;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
*
* <pre>
* 사용자 및 권한별 인가 자원 처리와 관련한 연산을 수행한다.
*
*  - Base URL : /jcfiam/admin/secure
*
*   - 권한별 인가 자원 조회 : /role/selectResources
*   - 권한별 인가 자원 추가 : /role/insertResource
*   - 권한별 인가 자원 수정 : /role/updateResource
*   - 권한별 인가 자원 삭제 : /role/deleteResource
*   - 권한별 인가 자원 저장 : /role/saveResource
*
*   - 사용자별 인가 자원 조회 : /user/selectResources
*   - 사용자별 인가 자원 추가 : /user/insertResource
*   - 사용자별 인가 자원 수정 : /user/updateResource
*   - 사용자별 인가 자원 삭제 : /user/deleteResource
*   - 사용자별 인가 자원 저장 : /user/saveResource
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
@Controller
@RequestMapping("/jcfiam/admin/secure")
public class SecuredResourceController {

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
	@RequestMapping("/role/selectResources")
	public void selectResourcesByRole(MciRequest request, MciResponse response) {
		response.setList("securedResourceList", service.getSecuredResourceListByRole(request.getParam("roleId")));
	}

	/**
	 *
	 * 권한별 인가 자원 추가
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/role/insertResource")
	public void insertResourcesByRole(MciRequest request, MciResponse response) {
		ViewResourcesRoleMapping resource = request.get("securedResource", factory.getCustomizer().getPermissionByRoleClass());
		service.insertSecuredResourceRole(resource);
	}

	/**
	 *
	 * 권한별 인가 자원 수정 - Default 설정 사용시 비활성화됨
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/role/updateResource")
	public void updateResourcesByRole(MciRequest request, MciResponse response) {
		ViewResourcesRoleMapping resource = request.get("securedResource", factory.getCustomizer().getPermissionByRoleClass());
		service.updateSecuredResourceRole(resource);
	}

	/**
	 *
	 * 권한별 인가 자원 삭제
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/role/deleteResource")
	public void deleteResourcesByRole(MciRequest request, MciResponse response) {
		String viewResourceId = request.getParam("viewResourceId");
		String roldId = request.getParam("roleId");
		String permissionId = request.getParam("permissionId");

		service.deleteSecuredResourceRole(viewResourceId, roldId, permissionId);
	}

	/**
	 *
	 * 권한별 인가 자원 추가/삭제/수정
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/role/saveResources")
	public void saveSecuredRoleResources(MciRequest request, MciResponse response){
		GridData<? extends ViewResourcesRoleMapping> gridData = request.getGridData("securedResources", factory.getCustomizer().getPermissionByRoleClass());
		service.saveSecuredRoleResources(gridData);
	}

	/**
	 *
	 * 사용자별 인가 자원 조회
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/user/selectResources")
	public void selectResourcesByUser(MciRequest request, MciResponse response) {
		response.setList("securedResourceList", service.getSecuredResourceListByUser(request.getParam("username")));
	}

	/**
	 *
	 * 사용자별 인가 자원  추가
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/user/insertResource")
	public void insertResourcesByUser(MciRequest request, MciResponse response) {
		ViewResourcesRoleMapping resource = request.get("securedResource", factory.getCustomizer().getPermissionByUserClass());
		service.insertSecuredResourceUser(resource);
	}

	/**
	 *
	 * 사용자별 인가 자원 수정  - Default 설정 사용시 비활성화됨
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/user/updateResource")
	public void updateResourcesByUser(MciRequest request, MciResponse response) {
		ViewResourcesRoleMapping resource = request.get("securedResource", factory.getCustomizer().getPermissionByUserClass());
		service.updateSecuredResourceUser(resource);
	}

	/**
	 *
	 * 사용자별 인가 자원 삭제
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/user/deleteResource")
	public void deleteResourcesByUser(MciRequest request, MciResponse response) {
		String viewResourceId = request.getParam("viewResourceId");
		String username = request.getParam("userId");
		String permissionId = request.getParam("permissionId");

		service.deleteSecuredResourceUser(viewResourceId, username, permissionId);
	}

	/**
	 *
	 * 사용자별 인가 자원 추가/삭제/수정
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/user/saveResources")
	public void saveSecuredUserResources(MciRequest request, MciResponse response){
		GridData<? extends ViewResourcesRoleMapping> gridData = request.getGridData("securedResources", factory.getCustomizer().getPermissionByUserClass());
		service.saveSecuredUserResources(gridData);
	}

}
