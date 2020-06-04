package jcf.query.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 *
 * JCFQUERY Namespace Handler
 *
 * @author nolang
 *
 */
public class DataQueryNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("query-config", new DataQueryBeanDefinitionParser());
	}

}
