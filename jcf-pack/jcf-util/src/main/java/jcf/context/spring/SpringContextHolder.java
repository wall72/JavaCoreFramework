package jcf.context.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <p>Holder for delegating the BeanFactory's getBean() method.<br/>
 * After initiating, this perform the method delegation to spring context.<br/>
 * For using this holder class, this should be defined in spring context as following :</p>
 * 
 * <pre code="class">
 * &lt;bean class="jcf.context.spring.SpringContextHolder"/&gt;
 * </pre>
 * @author purple
 * @deprecated This instance method writes to a static field. This is tricky to get correct if multiple instances are being manipulated, and generally bad practice. 
 *
 */
public class SpringContextHolder implements ApplicationContextAware{

	private static ApplicationContext ctx;
	
	/**
	 * 
	 * 
	 * @param name
	 * @return
	 */
    public static Object getBean(String name) {
    	if( ctx == null) throw new RuntimeException("SpringContextHolder may not be initiated properly. " +
    			"For using the SpringContextHolder, it should be declared in context by bean definition(<bean class=\"jcf.context.spring.SpringContextHolder\"/>)");
    	return ctx.getBean(name);
    }

    /**
     * 
     * @override
     */
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		SpringContextHolder.ctx = ctx;
		
	}	
}
