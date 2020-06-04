package jcf.iam.admin.authentication.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import jcf.iam.core.authentication.userdetails.model.Role;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext-sua.xml" })
public class RoleServiceTest {

	@Autowired
	private RoleService service;

	@Test
	public void 권한조회추가수정삭제() {
		Role role = new Role();

		role.setRoleId("ROLE_TEST");
		role.setRoleName("테스트권한");
		role.setDescription("테스트권한");
		role.setEnabled("Y");
		role.setCreateUserId("admin");
		role.setLastModifyUserId("admin");

		/*
		 * 추가
		 */
		service.insertRole(role);

		/*
		 * 조회
		 */
		Role actual = (Role) service.getRole("ROLE_TEST");

		assertRole(role, actual);

		/*
		 * 수정
		 */
		role.setDescription("수정됨");

		service.updateRole(role);

		/*
		 * 조회
		 */
		actual = (Role) service.getRole("ROLE_TEST");

		assertRole(role, actual);

		/*
		 * 삭제
		 */
		service.deleteUser("ROLE_TEST");

		/*
		 * 조회
		 */
		actual = (Role) service.getRole("ROLE_TEST");

		assertNull(actual);
	}

	private void assertRole(Role expected, Role actual) {
		assertEquals(expected.getRoleId(), actual.getRoleId());
		assertEquals(expected.getRoleName(), actual.getRoleName());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(expected.getEnabled(), actual.getEnabled());
	}
}
