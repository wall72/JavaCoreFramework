package jcf.iam.core.authentication.event;

import java.util.List;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;

/**
*
* 사용자인증 실패시 발생하는 이벤트 리스너 (authenticationManager에 authenticationEventPublisher가 등록되어야함)
*
* <pre>
*  <bean id="authenticationManager" class="org.springframework.security.authentication.ProviderManager">
*    <property name="authenticationEventPublisher">
*      <bean class="org.springframework.security.authentication.DefaultAuthenticationEventPublisher" />
*    </property>
*    <property name="parent" ref="innerAuthenticationManager" />
*  </bean>
* <pre>
*
* @see jcf.iam.core.authentication.event.AuthenticationFailureEventPostProcessor
* @see jcf.iam.core.authentication.event.AuthenticationSuccessEventListener
* @see jcf.iam.core.authentication.event.AuthenticationSuccessEventProcessor
*
* @author nolang
*
*/
public class AuthenticationFailureEventListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	private List<AuthenticationFailureEventPostProcessor> postProcessors;

	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		if (postProcessors != null) {
			for(AuthenticationFailureEventPostProcessor processor : postProcessors)	{
				processor.execute(event.getAuthentication(), event.getTimestamp(), event.getException());
			}
		}
	};

	/**
	 *
	 * @param postProcessors
	 */
	public void setPostProcessors(List<AuthenticationFailureEventPostProcessor> postProcessors) {
		this.postProcessors = postProcessors;
	}
}
