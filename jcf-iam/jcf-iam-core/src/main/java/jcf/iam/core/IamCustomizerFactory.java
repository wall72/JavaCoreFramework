package jcf.iam.core;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * <pre>
 * JCF IAM 커스터마이저를 생성/반환한다.
 * <pre>
 *
 * @see    jcf.iam.core.Customizer
 * @see    jcf.iam.core.DefaultCustomizer
 *
 * @author nolang
 *
 */
public class IamCustomizerFactory {

	@Autowired (required = false)
	private Customizer customizer = new DefaultCustomizer();

	/**
	 * <pre>
	 * 정의된 커스터마이저를 생성하여 리턴한다.
	 * <pre>
	 *
	 * @return
	 */
	public Customizer getCustomizer() {
		return customizer;
	}

	/**
	 * <pre>
	 * 별도 생성한 커스터마이저를 설정한다.
	 * <pre>
	 *
	 * @param customizer
	 */
	public void setCustomizer(Customizer customizer) {
		this.customizer = customizer;
	}
}
