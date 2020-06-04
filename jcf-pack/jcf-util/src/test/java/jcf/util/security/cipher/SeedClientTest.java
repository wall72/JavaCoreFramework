package jcf.util.security.cipher;

import jcf.util.ByteUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SeedClientTest {

	private static final Logger logger = LoggerFactory
			.getLogger(SeedClientTest.class);
	
	@Autowired
	private SeedClient seedClient;
	
	@Test
	public void 시드암복호화테스트() {
		byte[] bytes;
		bytes = "이건 뭔가".getBytes();
//		bytes = null;
		
		byte[] encryptedBytes = seedClient.encrypt(bytes);
		logger.debug("encrypted bytes : {}", ByteUtils.toHexString(encryptedBytes));
		
		byte[] decryptedBytes = seedClient.decrypt(encryptedBytes);
		logger.debug("decrypted string : {}", new String(decryptedBytes));
	}
}
