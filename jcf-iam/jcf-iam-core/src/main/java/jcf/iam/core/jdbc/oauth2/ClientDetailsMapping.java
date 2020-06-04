package jcf.iam.core.jdbc.oauth2;

import jcf.iam.core.jdbc.SecurityStatement;

import org.springframework.security.oauth2.provider.ClientDetails;

/**
 *
 * OAuth Client 정의 처리 모델 객체를 정의하기 위해 구현한다.
 *
 * @author nolang
 *
 */
public interface ClientDetailsMapping extends SecurityStatement, ClientDetails {
}
