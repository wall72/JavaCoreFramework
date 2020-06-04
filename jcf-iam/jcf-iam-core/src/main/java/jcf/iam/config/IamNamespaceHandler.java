package jcf.iam.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 *
 * @author nolang
 *
 */
public class IamNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("iam-config", new IamBeanDefinitionParser());
	}

}
