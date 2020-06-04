package jcf.sua.config;

import java.util.List;

import jcf.sua.SuaChannels;
import jcf.sua.mvc.MciExceptionViewResolver;
import jcf.sua.mvc.MciViewResolver;
import jcf.sua.mvc.MciWebArgumentResolver;
import jcf.sua.mvc.converter.MciHttpMessageConverter;
import jcf.sua.mvc.interceptor.MciDataSetHandlerInterceptor;
import jcf.sua.mvc.view.MciView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.w3c.dom.Element;

/**
 * <pre>
 * SUA Top level Element 인 <sua-config> 태그를 분석하여 SUA 채널을 등록한다.
 *
 *  - 채널 설정의 예
 *
 *    <jcfsua:sua-config auto-config="true/false" use-rest="true/false">
 *        <jcfsua:channel channel-type="JSON" />
 *        <jcfsua:channel channel-type="GAUCE" first-row-size="100" />
 *        <jcfsua:custom-interceptor ref="customInterceptor" />
 *    </jcfsua:sua-config>
 * <pre>
 *
 * @author nolang
 *
 */
class SuaConfigBeanDefinitionParser  implements BeanDefinitionParser  {

	private static final Logger logger = LoggerFactory.getLogger(SuaConfigBeanDefinitionParser.class);

	public BeanDefinition parse(Element element, ParserContext pc) {
		Object source = pc.extractSource(element);

		if(logger.isDebugEnabled()){
			logger.trace("[JCF-SUA] SUA 채널 설정 작업을 시작합니다.");
		}

		Boolean isAutoConfig = Boolean.valueOf(element.getAttribute(SuaChannelNodeConstants.autoConfigAttrName));

		if(isAutoConfig && logger.isDebugEnabled()){
			logger.trace("[JCF-SUA] SUA 채널 자동 설정 모드로 동작합니다.");
		}

		Boolean useRestType = Boolean.valueOf(element.getAttribute(SuaChannelNodeConstants.useRestAttrName));

		if(useRestType && logger.isDebugEnabled()){
			logger.trace("[JCF-SUA] SUA REST 채널 모드로 설정합니다.");
		}

		/*
		 * HandlerMapping 정의 - Default : org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping
		 */
		String handlerMappingRef = element.getAttribute(SuaChannelNodeConstants.handlerMappingAttrName);

		if(logger.isDebugEnabled()){
			logger.trace("[JCF-SUA] Step 1 : HandlerMapping 객체를 설정합니다. - Class={}", new Object[]{handlerMappingRef});
		}

		RootBeanDefinition handlerMapping = new RootBeanDefinition(handlerMappingRef);

		handlerMapping.setSource(source);

		/*
		 * 사용자정의 인터셉터 추가
		 */
		ManagedList<Object> interceptors = getCustomInterceptors(element);

		/*
		 * 채널 인터셉터 추가
		 */
		ManagedList<BeanDefinition> channels = createChannelInterceptors(element, pc, isAutoConfig, useRestType);

		interceptors.addAll(channels);

		handlerMapping.getPropertyValues().addPropertyValue("interceptors", interceptors);

		registerBeanDefinition(handlerMapping, pc);

		/*
		 * HandlerAdapter 정의
		 */
		String adapterClassName = element.getAttribute(SuaChannelNodeConstants.handlerAdapterAttrName);

		if(logger.isDebugEnabled()){
			logger.trace("[JCF-SUA] Step 2 : HandlerAdapter 객체를 설정합니다. - Class={}", new Object[]{adapterClassName});
		}

		RootBeanDefinition handlerAdapter = new RootBeanDefinition(adapterClassName);

		handlerAdapter.setSource(source);

		/*
		 * DataSetConverter 설정
		 */
		String dataSetConverterRef = element.getAttribute(SuaChannelNodeConstants.converterRefAttrName);
		RuntimeBeanReference converter = null;

		if(StringUtils.hasText(dataSetConverterRef))	{
			if(logger.isDebugEnabled()){
				logger.trace("[JCF-SUA] Step 2.0 : 사용자 정의 DataSetConverter 가 정의되었습니다.");
			}

			converter = new RuntimeBeanReference(dataSetConverterRef);
		}

		handlerAdapter.getPropertyValues().addPropertyValue("customArgumentResolvers", createChannelArgumentResolvers(element, pc, isAutoConfig, converter));

		if(useRestType)	{
			handlerAdapter.getPropertyValues().addPropertyValue("messageConverters", createChannelMessageConverters(element, pc, isAutoConfig, null));
		}

		registerBeanDefinition(handlerAdapter, pc);

		/*
		 * ViewResolver 정의
		 */
		ManagedList<Object> viewResolvers = getCustomViewResolvers(element);
		
		RootBeanDefinition pageViewResolver = new RootBeanDefinition(InternalResourceViewResolver.class);

		String webflowPrefix = element.getAttribute(SuaChannelNodeConstants.webflowPrefixAttrName);
		String webflowSuffix = element.getAttribute(SuaChannelNodeConstants.webflowSuffixAttrName);

		pageViewResolver.setSource(source);
		pageViewResolver.getPropertyValues().addPropertyValue("prefix", webflowPrefix);
		pageViewResolver.getPropertyValues().addPropertyValue("suffix", webflowSuffix);

		viewResolvers.add(pageViewResolver);
		
		RootBeanDefinition viewResolver = new RootBeanDefinition(MciViewResolver.class);

		if(logger.isDebugEnabled()){
			logger.trace("[JCF-SUA] Step 4 : ViewResolver 객체를 설정합니다. - Class={}", new Object[]{viewResolver.getBeanClassName()});
		}

		viewResolver.setSource(source);
		viewResolver.getPropertyValues().addPropertyValue("defaultView", new RootBeanDefinition(MciView.class));
		viewResolver.getPropertyValues().addPropertyValue("order", "1");
		viewResolver.getPropertyValues().addPropertyValue("viewResolvers", viewResolvers);

		registerBeanDefinition(viewResolver, pc);

		RootBeanDefinition exceptionViewResolver = new RootBeanDefinition(MciExceptionViewResolver.class);

		if(logger.isDebugEnabled()){
			logger.trace("[JCF-SUA] Step 5 : ExceptionViewResolver 객체를 설정합니다. - Class={}", new Object[]{viewResolver.getBeanClassName()});
		}

		exceptionViewResolver.setSource(source);

		registerBeanDefinition(exceptionViewResolver, pc);

		if(logger.isDebugEnabled()){
			logger.trace("[JCF-SUA] SUA 채널 설정 작업을 종료합니다.");
		}

		return null;
	}

	private BeanDefinition getBeanDefinition(Class<?> clazz, Object source, ParserContext pc) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();

		builder.getRawBeanDefinition().setBeanClass(clazz);
		builder.getRawBeanDefinition().setSource(source);

		BeanDefinition bd = builder.getBeanDefinition();

		registerBeanDefinition(bd, pc);

		return bd;
	}

	private void registerBeanDefinition(BeanDefinition beanDefinition, ParserContext pc)	{
		BeanDefinitionReaderUtils.registerBeanDefinition(
				new BeanDefinitionHolder(beanDefinition, pc.getReaderContext()
						.generateBeanName(beanDefinition)), pc.getRegistry());
	}


	/**
	 *
	 * @param element
	 * @param pc
	 * @return
	 */
	private ManagedList<Object> getCustomInterceptors(Element element) {
		ManagedList<Object> interceptorList = new ManagedList<Object>();

		List<Element> interceptorElementList = DomUtils.getChildElementsByTagName(element, SuaChannelNodeConstants.interceptorElementName);

		for (int i = 0; i < interceptorElementList.size(); ++i) {
			Element interceptorElement = interceptorElementList.get(i);

			BeanReference bd = new RuntimeBeanReference(interceptorElement.getAttribute("ref"));

			if(logger.isDebugEnabled()){
				logger.trace("[JCF-SUA] Step 1.{} : {} 사용자 정의 인터셉터를 추가합니다. - Class={}", new Object[]{i + 1, interceptorElement.getAttribute(SuaChannelNodeConstants.refAttrName), bd.getClass()});
			}

			interceptorList.add(bd);
		}

		return interceptorList;
	}

	/**
	 *
	 * SUA Channel HandlerInterceptor 설정
	 *
	 * @param element
	 * @param pc
	 * @param isAutoConfig
	 * @param isRestMode TODO
	 * @return
	 */
	private ManagedList<BeanDefinition> createChannelInterceptors(Element element, ParserContext pc, boolean isAutoConfig, boolean isRestMode) {
		ManagedList<BeanDefinition> channelList = new ManagedList<BeanDefinition>();

		if(isAutoConfig){

			/*
			 * 자동설정을 사용할 경우 정의된 모든 채널을 등록한다.
			 */
			Object source = pc.extractSource(element);

			SuaChannels[] channels = SuaChannels.values();

			for (int i = 0; i < channels.length; ++i) {
				Class<? extends MciDataSetHandlerInterceptor> channelClass = SuaChannelClassFactory.getHandlerInterceptor(channels[i]);

				if(logger.isDebugEnabled()){
					logger.trace("[JCF-SUA] Step 1.{} : {} 채널 인터셉터를 추가합니다. - Class={}", new Object[]{i + 1, channels[i], channelClass});
				}

				BeanDefinition bd = getBeanDefinition(channelClass, source, pc);

				bd.getPropertyValues().addPropertyValue("isRestMode", isRestMode);

				if(channels[i] == SuaChannels.GAUCE){
					bd.getPropertyValues().addPropertyValue("firstRowSize", 100);
				}

				channelList.add(bd);
			}

		} else {
			SuaChannelInterceptorBeanDefinitionParser channelParser = new SuaChannelInterceptorBeanDefinitionParser(isRestMode);

			List<Element> channelElementList = DomUtils.getChildElementsByTagName(element, SuaChannelNodeConstants.channelElementName);

			for (int i = 0; i < channelElementList.size(); ++i) {
				Element channelElement = channelElementList.get(i);

				BeanDefinition bd = channelParser.parse(channelElement, pc);

				if(logger.isDebugEnabled()){
					logger.trace("[JCF-SUA] Step 1.{} : {} 채널 인터셉터를 추가합니다. - Class={}", new Object[]{i + 1, channelElement.getAttribute(SuaChannelNodeConstants.channelTypeAttrName), bd.getBeanClassName()});
				}

				channelList.add(bd);
			}
		}

		return channelList;
	}

	/**
	 *
	 * SUA Channel WebArguementResolver 설정
	 *
	 * @param element
	 * @param pc
	 * @param isAutoConfig
	 * @param dataSetConverter TODO
	 * @return
	 */
	private ManagedList<BeanDefinition> createChannelArgumentResolvers(Element element, ParserContext pc, boolean isAutoConfig, RuntimeBeanReference dataSetConverter){
		ManagedList<BeanDefinition> channelArgumentResolverList = new ManagedList<BeanDefinition>();

		if(isAutoConfig){

			Object source = pc.extractSource(element);

			SuaChannels[] channels = SuaChannels.values();

			for (int i = 0; i < channels.length; ++i) {
				Class<? extends MciWebArgumentResolver> channelClass = SuaChannelClassFactory.getWebArgumentResolver(channels[i]);

				if(logger.isDebugEnabled()){
					logger.trace("[JCF-SUA] Step 2.{} : {} 채널 아규먼트 리졸버를 추가합니다. - Class={}", new Object[]{i + 1, channels[i], channelClass});
				}

				RootBeanDefinition argumentResolver = new RootBeanDefinition(channelClass);

				argumentResolver.setSource(source);
				argumentResolver.getPropertyValues().addPropertyValue("dataSetConverter", dataSetConverter);
				argumentResolver.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
				registerBeanDefinition(argumentResolver, pc);

				channelArgumentResolverList.add(argumentResolver);
			}

		} else {
			SuaChannelArguemtnResolverBeanDefinitionParser channelParser = new SuaChannelArguemtnResolverBeanDefinitionParser();

			List<Element> channelElementList = DomUtils.getChildElementsByTagName(element, SuaChannelNodeConstants.channelElementName);

			for (int i = 0; i < channelElementList.size(); ++i) {
				Element channelElement = channelElementList.get(i);

				BeanDefinition argumentResolver = channelParser.parse(channelElement, pc);

				argumentResolver.getPropertyValues().addPropertyValue("dataSetConverter", dataSetConverter);

				if(logger.isDebugEnabled()){
					logger.trace("[JCF-SUA] Configuration Setup Step 2.{} : {} 채널 아규먼트 리졸버를 추가합니다. - Class={}", new Object[]{i + 1, channelElement.getAttribute(SuaChannelNodeConstants.channelTypeAttrName),argumentResolver.getBeanClassName()});
				}

				channelArgumentResolverList.add(argumentResolver);
			}
		}

		return channelArgumentResolverList;
	}
	
	/**
	 * SUA Custom ViewResolver 설정
	 * @param element
	 * @return
	 */
	private ManagedList<Object> getCustomViewResolvers(Element element) {
		ManagedList<Object> viewResolverList = new ManagedList<Object>();
		
		List<Element> viewResolverElementList = DomUtils.getChildElementsByTagName(element, SuaChannelNodeConstants.viewResovlerElementName);
		
		for(int i = 0; i < viewResolverElementList.size(); i++){
			Element viewResolverElement = viewResolverElementList.get(i);
			
			RuntimeBeanReference bd = new RuntimeBeanReference(viewResolverElement.getAttribute("ref"));
			
			if(logger.isDebugEnabled()){
				logger.trace("[JCF-SUA] Step 1.{} : {} 사용자 정의 뷰리졸버를 추가합니다. - Class={}", new Object[]{i + 1, viewResolverElement.getAttribute(SuaChannelNodeConstants.refAttrName), bd.getClass()});
			}
			
			viewResolverList.add(bd);
		}
		
		return viewResolverList;
	}

	/**
	 *
	 * SUA REST Channel HttpMessageConverter 설정
	 *
	 * @param element
	 * @param pc
	 * @param isAutoConfig
	 * @param dataSetConverter TODO
	 * @return
	 */
	private Object createChannelMessageConverters(Element element, ParserContext pc, Boolean isAutoConfig, RuntimeBeanReference dataSetConverter) {
		ManagedList<BeanDefinition> channelList = new ManagedList<BeanDefinition>();

		if(isAutoConfig){

			Object source = pc.extractSource(element);

			SuaChannels[] channels = SuaChannels.values();

			for (int i = 0; i < channels.length; ++i) {
				Class<? extends MciHttpMessageConverter> channelClass = SuaChannelClassFactory.getHttpMessageConverter(channels[i]);

				if (channelClass != null) {
					if(logger.isDebugEnabled()){
						logger.trace("[JCF-SUA] Step 3.{} : {} 채널 메세지 컨버터를 추가합니다. - Class={}", new Object[]{i + 1, channels[i], channelClass});
					}

					RootBeanDefinition messageConverter = new RootBeanDefinition(channelClass);

					messageConverter.setSource(source);
					messageConverter.getPropertyValues().addPropertyValue("dataSetConverter", dataSetConverter);
					registerBeanDefinition(messageConverter, pc);

					channelList.add(messageConverter);
				}
			}

		} else {
			SuaChannelMessageConverterBeanDefinitionParser channelParser = new SuaChannelMessageConverterBeanDefinitionParser();

			List<Element> channelElementList = DomUtils.getChildElementsByTagName(element, SuaChannelNodeConstants.channelElementName);

			for (int i = 0; i < channelElementList.size(); ++i) {
				Element channelElement = channelElementList.get(i);

				BeanDefinition messageConverter = channelParser.parse(channelElement, pc);

				if(logger.isDebugEnabled()){
					logger.trace("[JCF-SUA] Step 3.{} : {} 채널 메세지 컨버터를 추가합니다. - Class={}", new Object[]{i + 1, channelElement.getAttribute(SuaChannelNodeConstants.channelTypeAttrName), messageConverter.getBeanClassName()});
				}

				messageConverter.getPropertyValues().addPropertyValue("dataSetConverter", dataSetConverter);

				channelList.add(messageConverter);
			}
		}

		return channelList;
	}

}
