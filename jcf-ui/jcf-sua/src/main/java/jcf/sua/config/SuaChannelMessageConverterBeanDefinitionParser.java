package jcf.sua.config;

import jcf.sua.SuaChannels;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 *
 * SUA Channel MessageConverter 를 생성한다.
 *
 * @author nolang
 *
 */
class SuaChannelMessageConverterBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		SuaChannels channel = SuaChannels.valueOf(element.getAttribute(SuaChannelNodeConstants.channelTypeAttrName));

		if (channel == SuaChannels.GAUCE) {
			builder.addPropertyValue("firstRowSize", Integer.valueOf(element.getAttribute(SuaChannelNodeConstants.firstRowSizeAttrName)));
		}
	}

	@Override
	protected Class<?> getBeanClass(Element element) {
		return SuaChannelClassFactory.getHttpMessageConverter(SuaChannels.valueOf(element.getAttribute(SuaChannelNodeConstants.channelTypeAttrName)));
	}

	@Override
	protected boolean shouldGenerateId() {
		return true;
	}
}
