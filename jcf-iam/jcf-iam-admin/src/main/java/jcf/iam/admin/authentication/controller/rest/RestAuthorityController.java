package jcf.iam.admin.authentication.controller.rest;

import jcf.iam.admin.RestController;
import jcf.iam.admin.authentication.service.AuthorityService;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authentication.AuthorityMapping;
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
* 사용자 권한 매핑 정보를 처리하는 연산을 수행한다.
*
*  - Base URL : /jcfiam/admin/authorities
*
*   - 조회 (GET)    : /
*                  : /{username}
*   - 추가 (POST)   : /
*   - 수정 (PUT)    : /
*   - 삭제 (DELETE) : /{username}?authority='ROLE_USER'
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
@RestController
@RequestMapping("/jcfiam/admin/authorities")
public class RestAuthorityController {

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
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public MciResponse selectRoles(@RequestBody MciRequest request, MciResponse response)	{
		response.setList("authorityList", service.getAuthorities(""));
		return response;
	}

	/**
	 * <pre>
	 * 사용자 권한 매핑 정보 조회
	 * <pre>
	 *
	 * @param request
	 * @param response
	 * @param username
	 */
	@RequestMapping(value = "/{username}", method = RequestMethod.GET)
	@ResponseBody
	public MciResponse selectRole(@RequestBody MciRequest request, MciResponse response, @PathVariable String username)	{
		response.setList("authorityList", service.getAuthorities(username));
		return response;
	}

	/**
	 *
	 * 사용자 권한 매핑 정보 추가
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	public MciResponse insertUserAuthority(@RequestBody MciRequest request, MciResponse response) {
		AuthorityMapping userAuthority = request.get("userAuthority", factory.getCustomizer().getAuthorityClass());
		service.insertUserAuthority(userAuthority);
		return response;
	}

	/**
	 *
	 * 사용자 권한 매핑 정보 수정
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public MciResponse updateUserAuthority(@RequestBody MciRequest request, MciResponse response) {
		AuthorityMapping userAuthority = request.get("userAuthority", factory.getCustomizer().getAuthorityClass());
		service.updateUserAuthority(userAuthority);
		return response;
	}

	/**
	 *
	 * 사용자 권한 매핑 정보 삭제
	 *
	 * @param request
	 * @param response
	 * @param username
	 */
	@RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public MciResponse deleteUserAuthority(@RequestBody MciRequest request, MciResponse response, @PathVariable String username) {
		String authority = request.getParam("authority");

		service.deleteUserAuthority(username, authority);

		return response;
	}
}
