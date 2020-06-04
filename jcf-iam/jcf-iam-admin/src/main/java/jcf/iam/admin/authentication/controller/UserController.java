package jcf.iam.admin.authentication.controller;

import jcf.iam.admin.authentication.service.UserService;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.jdbc.authentication.UserMapping;
import jcf.sua.mvc.MciRequest;
import jcf.sua.mvc.MciResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
* <pre>
* 사용자 정보를 처리하는 연산을 수행한다.
*
*  - Base URL : /jcfiam/admin/users
*
*    - 조회 : /selectUsers
*            /select
*    - 추가 : /insert
*    - 수정 : /update
*    - 삭제 : /update
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
@Controller
@RequestMapping("/jcfiam/admin/users")
public class UserController {

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
	 */
	@RequestMapping("/selectUsers")
	public void selectUsers(MciRequest request, MciResponse response)	{
		UserMapping user = request.get("user", factory.getCustomizer().getUserClass());
		response.setList("userList", service.getUserList(user));
	}

	/**
	 *
	 * 사용자 정보 조회
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/select")
	public void selectUser(MciRequest request, MciResponse response)	{
		response.set("user", service.getUser(request.getParam("username")));
	}

	/**
	 *
	 * 사용자 추가
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/insert")
	public void insertUser(MciRequest request, MciResponse response)	{
		UserMapping user = request.get("user", factory.getCustomizer().getUserClass());
		service.insertUser(user);
	}

	/**
	 *
	 * 사용자 정보 수정
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/update")
	public void updateUser(MciRequest request, MciResponse response)	{
		UserMapping user = request.get("user", factory.getCustomizer().getUserClass());
		service.updateUser(user);
	}

	/**
	 *
	 * 사용자 삭제
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/delete")
	public void deleteUser(MciRequest request, MciResponse response) {
		String username = request.getParam("username");
		service.deleteUser(username);
	}
}
