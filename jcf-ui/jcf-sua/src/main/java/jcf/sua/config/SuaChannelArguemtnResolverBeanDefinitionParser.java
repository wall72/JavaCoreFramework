package jcf.sua.config;

import jcf.sua.SuaChannels;

import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 *
 * SUA Channel WebArgumentResolver 를 생성한다.
 *
 * @author nolang
 *
 */
class SuaChannelArguemtnResolverBeanDefinitionParser extends
		AbstractSingleBeanDefinitionParser {
	@Override
	protected Class<?> getBeanClass(Element element) {
		return SuaChannelClassFactory.getWebArgumentResolver(SuaChannels.valueOf(element.getAttribute(SuaChannelNodeConstants.channelTypeAttrName)));
	}

	@Override
	protected boolean shouldGenerateId() {
		return true;
	}
}
