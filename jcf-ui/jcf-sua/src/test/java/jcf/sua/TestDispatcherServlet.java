package jcf.sua;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Ignore;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 *
 * @author 고강민
 *
 */
@Ignore
public class TestDispatcherServlet extends DispatcherServlet	{
	/**
	 *
	 */
	private static final long serialVersionUID = -868007902565242363L;

	private String location;

	public TestDispatcherServlet(String location) {
		this.location = location;

		try {
			this.init(new MockServletConfig());
		} catch (ServletException e) {
		}
	}

	@Override
	protected WebApplicationContext createWebApplicationContext(
			ApplicationContext parent) {
		AbstractRefreshableWebApplicationContext ctx = new AbstractRefreshableWebApplicationContext() {

			@Override
			protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
					throws BeansException, IOException {
				XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
				reader.loadBeanDefinitions(location);
			}
		};

		ctx.setServletContext(getServletContext());
		ctx.setServletConfig(getServletConfig());
		ctx.refresh();

		return ctx;
	}
}