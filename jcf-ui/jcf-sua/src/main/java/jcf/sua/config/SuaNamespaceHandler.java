package jcf.sua.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 *
 * SUA Namespace Handler
 *
 * @author nolang
 *
 */
public class SuaNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser(SuaChannelNodeConstants.suaConfigElementName, new SuaConfigBeanDefinitionParser());
	}

}
