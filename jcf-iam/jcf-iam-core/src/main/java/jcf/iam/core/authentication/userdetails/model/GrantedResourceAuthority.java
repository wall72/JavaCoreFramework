package jcf.iam.core.authentication.userdetails.model;

import org.springframework.security.core.GrantedAuthority;

/**
 *
 * 인가자원에 대한 권한을 처리하는 모델
 *
 * @author nolang
 *
 */
public class GrantedResourceAuthority implements GrantedAuthority {

	public static final String resourcePermissionStringFormat = "%s(%s)";

	private String authority;

	private String resourceId;

	private String resourcePermission;

	public GrantedResourceAuthority(String resourceId, String resourcePermission) {
		this.authority = String.format(resourcePermissionStringFormat, resourceId, resourcePermission);
//		this.authority = resourcePermission;
		this.resourceId = resourceId;
		this.resourcePermission = resourcePermission;
	}

	/**
	 * 인가 대상 자원에 대한 접근권한을 반환한다.
	 */
	public String getAuthority() {
		return authority;
	}

	/**
	 *
	 * 인가자원의 ID를 반환한다.
	 *
	 * @return
	 */
	public String getResourceId() {
		return resourceId;
	}

	/**
	 *
	 * 인가자원에 대한 세부 접근권한을 반환한다.
	 *
	 * @return
	 */
	public String getResourcePermission() {
		return resourcePermission;
	}

	public boolean equals(Object obj) {
		boolean result = false;

		if (obj instanceof GrantedResourceAuthority) {
			result = this.getAuthority().equals(
					((GrantedResourceAuthority) obj).getAuthority());
		}

		return result;
	}

	public String toString() {
		return getAuthority();
	}
}
