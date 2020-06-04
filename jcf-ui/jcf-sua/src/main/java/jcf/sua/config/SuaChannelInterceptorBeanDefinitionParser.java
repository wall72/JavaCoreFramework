package jcf.sua.config;

import jcf.sua.SuaChannels;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 *
 * SUA Channel HandlerInterceptor 를 생성한다.
 *
 * @author nolang
 *
 */
class SuaChannelInterceptorBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	private boolean isRestMode = false;

	public SuaChannelInterceptorBeanDefinitionParser(boolean isRestMode) {
		this.isRestMode = isRestMode;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		SuaChannels channel = SuaChannels.valueOf(element.getAttribute(SuaChannelNodeConstants.channelTypeAttrName));

		if (channel == SuaChannels.GAUCE) {
			int firstRowSize = Integer.valueOf(element.getAttribute(SuaChannelNodeConstants.firstRowSizeAttrName));

			if(firstRowSize <= 0)	{
				firstRowSize = 100;
			}

			builder.addPropertyValue("firstRowSize", firstRowSize);
		}

		builder.addPropertyValue("isRestMode", isRestMode);
	}

	@Override
	protected Class<?> getBeanClass(Element element) {
		return SuaChannelClassFactory.getHandlerInterceptor(SuaChannels.valueOf(element.getAttribute(SuaChannelNodeConstants.channelTypeAttrName)));
	}

	@Override
	protected boolean shouldGenerateId() {
		return true;
	}
}
