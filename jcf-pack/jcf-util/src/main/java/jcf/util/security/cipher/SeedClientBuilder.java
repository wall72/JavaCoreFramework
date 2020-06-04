package jcf.util.security.cipher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Seed 블록 암복호화 클라이언트(SeedClient)를 생성, 공유하기 위한 Client Builder.
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
public class SeedClientBuilder {

	private String password;
	private String salt = "01234567"; //.getBytes(); // new byte[8];
	private int iterationCount = 10;
	
	
	/**
	 * 비밀번호 지정.
	 * @param password
	 */
	public SeedClientBuilder setPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * 해싱할 때 쓸 8자리 문자열 (소금 치기).
	 * @param salt
	 */
	public SeedClientBuilder setSalt(String salt) {
		this.salt = salt;
		return this;
	}

	/**
	 * 해싱 반복 횟수 (자연수).
	 * @param iterationCount
	 */
	public SeedClientBuilder setIterationCount(int iterationCount) {
		this.iterationCount = iterationCount;
		return this;
	}
	
	public SeedClient build() throws Exception {
		
		if (password == null) {
			throw new InstantiationException("password must be set.");
		}
		
		// 추출키(DK) 생성
		byte[] dk = pbkdf1(password, salt, iterationCount);

		byte[] keyData = getKeyData(dk);

		byte[] iv = getInitVector(dk);

		return new SeedClient(keyData, iv);
	}

	private byte[] getInitVector(byte[] dk) {
		byte[] iv;
		// 추출키(DK)에서 암호화 키(K)를 제외한 나머지 4바이트를 SHA-1
		// 으로 해쉬하여 20바이트의 값(DIV)을 생성하고, 그 중 처음 16바이트를 초기
		// 벡터(IV)로 정의한다.
		byte[] div; // = new byte[20];
		byte[] tmp4Bytes = new byte[4];
		System.arraycopy(dk, 16, tmp4Bytes, 0, 4);
		div = SHA1Utils.getHash(tmp4Bytes);

		iv = getKeyData(div);
		return iv;
	}

	private byte[] getKeyData(byte[] dk) {
		byte[] keyData;
		/*
		 * 암호화 키
		 */
		keyData = new byte[16];
		System.arraycopy(dk, 0, keyData, 0, 16);
		return keyData;
	}

	/**
	 * RFC2898(http://www.ietf.org/rfc/rfc2898.txt
	 * (귀찮으므로 20byte로만 되도록 구현함)
	 * 
	 * @param password 임의의 비밀번호
	 * @param salt 8자리 문자열
	 * @param iterationCount 해시 반복 횟수. 자연수
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private byte[] pbkdf1(String password, String salt, int iterationCount) {
		byte[] dk; // = new byte[20]; // 생성이 의미가 없지만 한눈에 알아보라고 20바이트로 초기화
		MessageDigest md;
		
		try {
			md = MessageDigest.getInstance("SHA1");
			
		} catch (NoSuchAlgorithmException e) {
			throw new CipherException(e);
		}
		
		md.update(password.getBytes());
		md.update(salt.getBytes());
		
		dk = md.digest();
		
		for (int i = 1; i < iterationCount; i++) {
			dk = md.digest(dk);
		}
		
		return dk;
	}

}
