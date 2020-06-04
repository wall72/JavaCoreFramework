package jcf.iam.admin.authorization.service;

import java.util.List;

import jcf.iam.core.jdbc.authorization.PermissionMapping;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext-sua.xml" })
public class PermissionServiceTest {

	@Autowired
	private PermissionService service;

	@Test
	public void 조회추가수정삭제() {
		/*
		 * 추가
		 */
		PermissionMapping permission = new PermissionMapping();

		permission.setPermissionId("T");
		permission.setDescription("TEST");
		permission.setCreateUserId("admin");
		permission.setLastModifyUserId("admin");

		service.insertPermission(permission);

		/*
		 * 조회
		 */
		List<? extends PermissionMapping> permissionList = service.getPermissions();

		assertPermission(permissionList, "T", "TEST", true);

		/*
		 * 수정
		 */
		permission.setDescription("TEST1111");

		service.updatePermission(permission);

		/*
		 * 조회
		 */
		permissionList = service.getPermissions();

		assertPermission(permissionList, "T", "TEST1111", true);

		/*
		 * 삭제
		 */
		service.deletePermission("T");

		/*
		 * 조회
		 */
		permissionList = service.getPermissions();

		assertPermission(permissionList, "T", "TEST1111", false);
	}

	private void assertPermission(List<? extends PermissionMapping> permissionList, String permissionId, String description, boolean condition) {
		boolean result = false;

		for(PermissionMapping permission : permissionList)	{
			if((result = permission.getPermissionId().equals(permissionId) && permission.getDescription().equals(description)))	{
				break;
			}
		}

		Assert.assertTrue(result == condition);
	}
}
