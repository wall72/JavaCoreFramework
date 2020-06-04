package jcf.util.security.cipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * 국산 Seed 블록 암호화 알고리즘을 이용한 데이터 암복호화 서비스 빈. 
 * <p>
 * 생성시 키 값을 지정한다.
 * 
 * @author setq
 */
public class SeedClient implements CryptClient {
		
		private static final BouncyCastleProvider BOUNCY_CASTLE_PROVIDER = new BouncyCastleProvider();
		
		private SecretKeySpec secretKeySpec;
		private IvParameterSpec iv;
		
		public SeedClient(byte[] keyData, byte[] iv) {
			this.secretKeySpec = new SecretKeySpec(keyData, "SEED");
			this.iv = new IvParameterSpec(iv);
		}

		public byte[] encrypt(byte[] input) {
			try {
				Cipher encCipher = Cipher.getInstance("SEED/CBC/PKCS5Padding", BOUNCY_CASTLE_PROVIDER);
				encCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

				return encCipher.doFinal(input);
				
			} catch (IllegalBlockSizeException e) {
				throw new CipherException("error encrypting bytes", e);
			} catch (BadPaddingException e) {
				throw new CipherException("error encrypting bytes", e);
			} catch (NoSuchAlgorithmException e) {
				throw new CipherException("error encrypting bytes", e);
			} catch (NoSuchPaddingException e) {
				throw new CipherException("error encrypting bytes", e);
			} catch (InvalidKeyException e) {
				throw new CipherException("error encrypting bytes", e);
			} catch (InvalidAlgorithmParameterException e) {
				throw new CipherException("error encrypting bytes", e);
			}
		}
		
		public byte[] decrypt(byte[] input) {
			try {
				Cipher decCipher = Cipher.getInstance("SEED/CBC/PKCS5Padding", BOUNCY_CASTLE_PROVIDER);
				decCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
				
				return decCipher.doFinal(input);
				
			} catch (IllegalBlockSizeException e) {
				throw new CipherException("error decrypting bytes", e);
			} catch (BadPaddingException e) {
				throw new CipherException("error decrypting bytes", e);
			} catch (NoSuchAlgorithmException e) {
				throw new CipherException("error decrypting bytes", e);
			} catch (NoSuchPaddingException e) {
				throw new CipherException("error decrypting bytes", e);
			} catch (InvalidKeyException e) {
				throw new CipherException("error decrypting bytes", e);
			} catch (InvalidAlgorithmParameterException e) {
				throw new CipherException("error decrypting bytes", e);
			}
		}

	}