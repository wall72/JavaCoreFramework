package jcf.iam.core.authentication.event;

import java.util.List;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

/**
 *
 * 사용자인증 성공시 발생하는 이벤트 리스너 (authenticationManager에 authenticationEventPublisher가 등록되어야함)
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
 * @see jcf.iam.core.authentication.event.AuthenticationSuccessEventProcessor
 * @see jcf.iam.core.authentication.event.AuthenticationFailureEventListener
 * @see jcf.iam.core.authentication.event.AuthenticationFailureEventPostProcessor
 *
 * @author nolang
 *
 */
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

	protected List<AuthenticationSuccessEventProcessor> postProcessors;

	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		postProcess(event);
	}

	protected void postProcess(AuthenticationSuccessEvent event) {
		if (postProcessors != null) {
			for (AuthenticationSuccessEventProcessor processor : postProcessors) {
				processor.execute(event.getAuthentication(), event.getTimestamp());
			}
		}
	}

	/**
	 *
	 * @param postProcessors
	 */
	public void setPostProcessors(
			List<AuthenticationSuccessEventProcessor> postProcessors) {
		this.postProcessors = postProcessors;
	}
}
