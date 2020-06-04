package jcf.iam.admin.authentication.controller.rest;

import jcf.iam.admin.RestController;
import jcf.iam.admin.authentication.service.UserService;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authentication.UserMapping;
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
* 사용자 정보를 처리하는 연산을 수행한다.
*
*  - Base URL : /jcfiam/admin/users
*
*   - 조회 (GET)    : /
*                  : /{username}
*   - 추가 (POST)   : /
*   - 수정 (PUT)    : /
*   - 삭제 (DELETE) : /{username}
* <pre>
*
* @see jcf.iam.core.IamCustomizerFactory
* @see jcf.iam.core.DefaultCustomizer
* @see jcf.iam.core.Customizer
* @see jcf.iam.core.jdbc.authentication.UserMapping
* @see jcf.iam.admin.authentication.service.UserService
*
* @author nolang
*
*/
@RestController
@RequestMapping("/jcfiam/admin/users")
public class RestUserController {

	@Autowired
	private UserService service;

	@Autowired
	private IamCustomizerFactory factory;

	/**
	 *
	 * 사용자 리스트 조회
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public MciResponse selectUsers(@RequestBody MciRequest request, MciResponse response)	{
		response.setList("userList", service.getUserList(""));
		return response;
	}

	/**
	 *
	 * 사용자 정보 조회
	 *
	 * @param request
	 * @param response
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/{username}", method = RequestMethod.GET)
	@ResponseBody
	public MciResponse selectUser(@RequestBody MciRequest request, MciResponse response, @PathVariable String username)	{
		response.set("user", service.getUser(username));
		return response;
	}

	/**
	 *
	 * 사용자 추가
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	public MciResponse insertUser(@RequestBody MciRequest request, MciResponse response)	{
		UserMapping user = request.get("user", factory.getCustomizer().getUserClass());
		service.insertUser(user);
		return response;
	}

	/**
	 *
	 * 사용자 정보 수정
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public MciResponse updateUser(@RequestBody MciRequest request, MciResponse response)	{
		UserMapping user = request.get("user", factory.getCustomizer().getUserClass());
		service.updateUser(user);
		return response;
	}

	/**
	 *
	 * 사용자 삭제
	 *
	 * @param request
	 * @param response
	 * @param username
	 * @return
	 */
	@RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public MciResponse deleteUser(@RequestBody MciRequest request, MciResponse response, @PathVariable String username) {
		service.deleteUser(username);
		return response;
	}
}
