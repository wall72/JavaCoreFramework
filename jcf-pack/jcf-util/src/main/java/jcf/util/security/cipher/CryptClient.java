package jcf.util.security.cipher;

/**
 * 단일 바이트 배열 암복호화 클라이언트.
 * <p>
 *  
 * @author setq
 */
public interface CryptClient {

	/**
	 * 암호화.
	 * @param input (not null)
	 * @return
	 */
	byte[] encrypt(byte[] input);
	
	/**
	 * 복호화.
	 * @param input (not null)
	 * @return
	 */
	byte[] decrypt(byte[] input);
	
}
