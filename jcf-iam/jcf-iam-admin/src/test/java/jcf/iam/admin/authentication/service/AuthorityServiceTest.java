package jcf.iam.admin.authentication.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import jcf.iam.core.authentication.userdetails.model.Role;
import jcf.iam.core.authentication.userdetails.model.User;
import jcf.iam.core.authentication.userdetails.model.UserAuthority;
import jcf.iam.core.jdbc.authentication.AuthorityMapping;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext-sua.xml" })
public class AuthorityServiceTest {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private AuthorityService service;

	@Test
	public void 조회추가삭제()	{
		/*
		 * 추가
		 */
		UserAuthority authority = new UserAuthority();

		authority.setAuthority("ROLE_TEST");
		authority.setUsername("testuser");

		service.insertUserAuthority(authority);

		/*
		 * 조회
		 */
		List<? extends AuthorityMapping> authorityList = service.getAuthorities("testuser");

		assertEquals("ROLE_TEST", authorityList.get(0).getAuthority());

		/*
		 * 삭제
		 */
		service.deleteUserAuthority("testuser", "ROLE_TEST");

		/*
		 * 조회
		 */
		authorityList = service.getAuthorities("testuser");

		assertEquals(0, authorityList.size());
	}

	@Before
	public void before() {
		User user = new User();

		user.setUsername("testuser");
		user.setEnabled("Y");
		user.setPassword("비밀번호");
		user.setCreateUserId("nolang");
		user.setLastModifyUserId("nolang");

		userService.insertUser(user);

		Role role = new Role();

		role.setRoleId("ROLE_TEST");
		role.setRoleName("테스트권한");

		roleService.insertRole(role);
	}

	@After
	public void after() {
		userService.deleteUser("testuser");
		roleService.deleteUser("ROLE_TEST");
	}
}
