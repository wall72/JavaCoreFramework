package jcf.iam.admin.authentication.controller;

import jcf.data.GridData;
import jcf.iam.admin.authentication.service.AuthorityService;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authentication.AuthorityMapping;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
* <pre>
* 사용자 권한 매핑 정보를 처리하는 연산을 수행한다.
*
*  - Base URL : /jcfiam/admin/authorities
*
*   - 조회 : /selectAuthorities
*   - 추가 : /insert
*   - 수정 : /update
*   - 삭제 : /update
*   - 저장 : /save
* <pre>
*
* @see jcf.iam.core.IamCustomizerFactory
* @see jcf.iam.core.DefaultCustomizer
* @see jcf.iam.core.Customizer
* @see jcf.iam.core.jdbc.authentication.AuthorityMapping
* @see jcf.iam.admin.authentication.service.AuthorityService
*
* @author nolang
*
*/
@Controller
@RequestMapping("/jcfiam/admin/authorities")
public class AuthorityController {

	@Autowired
	private AuthorityService service;

	@Autowired
	private IamCustomizerFactory factory;


	/**
	 * <pre>
	 * 사용자 권한 매핑 정보 조회
	 * <pre>
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/selectAuthorities")
	public void selectRoles(MciRequest request, MciResponse response)	{
		response.setList("authorityList", service.getAuthorities(request.getParam("username")));
	}

	/**
	 *
	 * 사용자 권한 매핑 정보 추가
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/insert")
	public void insertUserAuthority(MciRequest request, MciResponse response) {
		AuthorityMapping userAuthority = request.get("userAuthority", factory.getCustomizer().getAuthorityClass());
		service.insertUserAuthority(userAuthority);
	}

	/**
	 *
	 * 사용자 권한 매핑 정보 수정
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/update")
	public void updateUserAuthority(MciRequest request, MciResponse response) {
		AuthorityMapping userAuthority = request.get("userAuthority", factory.getCustomizer().getAuthorityClass());
		service.updateUserAuthority(userAuthority);
	}

	/**
	 *
	 * 사용자 권한 매핑 정보 삭제
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/delete")
	public void deleteUserAuthority(MciRequest request, MciResponse response) {
		String username = request.getParam("username");
		String authority = request.getParam("authority");

		service.deleteUserAuthority(username, authority);
	}

	/**
	 *
	 * 사용자 권한 매핑 정보 추가/수정/삭제
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/save")
	public void saveUserAuthorities(MciRequest request, MciResponse response) {
		GridData<? extends AuthorityMapping> gridData = request.getGridData("userAuthorities", factory.getCustomizer().getAuthorityClass());
		service.saveUserAuthorities(gridData);
	}
}
