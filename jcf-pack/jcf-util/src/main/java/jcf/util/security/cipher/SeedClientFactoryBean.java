package jcf.util.security.cipher;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Seed 블록 암복호화 클라이언트(SeedClient)를 생성, 공유하기 위한 팩토리 빈.
 * <p>
 * Seed 블록 암호화 클라이언트에는 키(열쇠)가 들어있어 손쉽게 데이터를 암/복호화할 수 있다.
 * <p>
 * 키가 간단한 평문이라면 키와 알고리즘(Seed)만 알게 되면 데이터를 누구나 쉽게 복호화할 수 있으므로 
 * 복잡한 키 값을 얻기 위해서 직접 지정하지 않고
 * 평문과 몇 가지 설정 (소금, 해시반복횟수)를 이용하여 (한번 더) 복잡하게 만들어준다.   
 * <p>
 * 국내 공인인증서에 쓰이는 방법으로 아래 알고리즘을 적용(구현)하여 SeedClient를 생성한다. (keyData와 initializationVector를 제공)
 * <p>
 * <a href="http://www.oid-info.com/get/1.2.410.200004.1.15">Key Generation with SHA1 and Encryption with SEED CBC mode</a>
 * <p>
 * 
 * <h3>팩토리빈 파라미터 값 지정</h3>
 * <ul>
 * <li>비밀번호 (필수 파라미터)</li> 
 * <li>소금 (8자리 문자열)</li>
 * <li>해싱 반복횟수 (자연수)</li>
 * </ul>
 * <p>
 * 참조 : 공인인증서로 전자서명해보기 [<a href="http://blog.kangwoo.kr/49">http://blog.kangwoo.kr/49</a>]
 * 
 * 
 * @author setq
 */
public class SeedClientFactoryBean implements FactoryBean<SeedClient>, InitializingBean {

	private SeedClient client;
	
	private String password;
	private String salt = "01234567"; //.getBytes(); // new byte[8];
	private int iterationCount = 10;
	
	
	/**
	 * 비밀번호 지정.
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 해싱할 때 쓸 8자리 문자열 (소금 치기).
	 * @param salt
	 */
	public void setSalt(String salt) {
		this.salt = salt;
	}

	/**
	 * 해싱 반복 횟수 (자연수).
	 * @param iterationCount
	 */
	public void setIterationCount(int iterationCount) {
		this.iterationCount = iterationCount;
	}
	
	public SeedClient getObject() throws Exception {
		return client;
	}


	public Class<?> getObjectType() {
		return SeedClient.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public void afterPropertiesSet() throws Exception {
		
		Assert.notNull(password, "password must be set.");
		Assert.notNull(salt, "salt must be set.");
		Assert.notNull(iterationCount, "iterationCount must be set.");

		client = new SeedClientBuilder().setPassword(password).setSalt(salt).setIterationCount(iterationCount).build();
	}

}
