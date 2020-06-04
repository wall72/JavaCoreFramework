package jcf.iam.admin.authentication.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import jcf.iam.core.authentication.userdetails.model.User;
import jcf.iam.core.jdbc.authentication.UserMapping;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext-sua.xml" })
public class UserServiceTest   {

	@Autowired(required=false)
	private UserService service;

	@Test
	@Ignore
	public void 사용자리스트조회() throws Exception {
		List<? extends UserMapping> userList = service.getUserList("");

		UserMapping user = userList.get(0);

		assertEquals("nolang", user.getUsername());
	}

	@Test
	@Ignore
	public void 사용자조회() throws Exception {
		UserMapping user = service.getUser("nolang");

		assertEquals("nolang", user.getUsername());
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void 없는놈조회하기() {
		UserMapping user = service.getUser("없는놈");

		/*
		 * 없는놈을 일단 출력해봄.. NullPointerException 발생
		 */
		System.out.println(user.getUsername());
	}

	@Test
	@Rollback(true)
	public void 사용자추가수정삭제조회() {
		User user = new User();

		user.setUsername("테스트유저");
		user.setEnabled("Y");
		user.setPassword("비밀번호");
		user.setCreateUserId("nolang");
		user.setLastModifyUserId("nolang");

		service.insertUser(user);

		UserMapping searchUser = service.getUser("테스트유저");

		assertEquals(user.getUsername(), searchUser.getUsername());
		assertEquals(user.getPassword(), searchUser.getPassword());

		user.setPassword("비밀");

		service.updateUser(user);

		searchUser = service.getUser("테스트유저");

		assertEquals(user.getUsername(), searchUser.getUsername());
		assertEquals(user.getPassword(), searchUser.getPassword());

		service.deleteUser("테스트유저");

		searchUser = service.getUser("테스트유저");

		assertNull(searchUser);
	}
}
