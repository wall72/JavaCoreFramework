package jcf.sua.config;

import jcf.sua.SuaChannels;
import jcf.sua.mvc.MciWebArgumentResolver;
import jcf.sua.mvc.converter.MciHttpMessageConverter;
import jcf.sua.mvc.interceptor.MciDataSetHandlerInterceptor;
import jcf.sua.ux.extJs.mvc.ExtJsWebArgumentResolver;
import jcf.sua.ux.extJs.mvc.converter.ExtJsHttpMessageConverter;
import jcf.sua.ux.extJs.mvc.interceptor.ExtJsDataSetHandlerInterceptor;
import jcf.sua.ux.gauce.mvc.GauceWebArgumentResolver;
import jcf.sua.ux.gauce.mvc.converter.GauceHttpMessageConverter;
import jcf.sua.ux.gauce.mvc.interceptor.GauceDataSetHandlerInterceptor;
import jcf.sua.ux.json.mvc.JsonWebArgumentResolver;
import jcf.sua.ux.json.mvc.converter.JsonHttpMessageConverter;
import jcf.sua.ux.json.mvc.interceptor.JsonDataSetHandlerInterceptor;
import jcf.sua.ux.miplatform.mvc.MiplatformWebArgumentResolver;
import jcf.sua.ux.miplatform.mvc.converter.MiplatformHttpMessageConverter;
import jcf.sua.ux.miplatform.mvc.interceptor.MiplatformDataSetHandlerInterceptor;
import jcf.sua.ux.mybuilder.mvc.MybuilderWebArgumentResolver;
import jcf.sua.ux.mybuilder.mvc.interceptor.MybuilderDataSetHandlerInterceptor;
import jcf.sua.ux.nexacro.mvc.NexacroWebArgumentResolver;
import jcf.sua.ux.nexacro.mvc.converter.NexacroHttpMessageConverter;
import jcf.sua.ux.nexacro.mvc.interceptor.NexacroDataSetHandlerInterceptor;
import jcf.sua.ux.webflow.mvc.WebFlowArgumentResolver;
import jcf.sua.ux.webflow.mvc.converter.WebFlowHttpMessageConverter;
import jcf.sua.ux.webflow.mvc.interceptor.WebFlowDataSetHandlerInterceptor;
import jcf.sua.ux.webplus.mvc.WebplusWebArgumentResolver;
import jcf.sua.ux.webplus.mvc.converter.WebplusHttpMessageConverter;
import jcf.sua.ux.webplus.mvc.interceptor.WebplusDataSetHandlerInterceptor;
import jcf.sua.ux.websquare.mvc.WebsquareWebArgumentResolver;
import jcf.sua.ux.websquare.mvc.interceptor.WebsquareDataSetHandlerInterceptor;
import jcf.sua.ux.xml.mvc.XmlWebArgumentResolver;
import jcf.sua.ux.xml.mvc.interceptor.XmlDataSetHandlerInterceptor;
import jcf.sua.ux.xplatform.mvc.XplatformWebArgumentResolver;
import jcf.sua.ux.xplatform.mvc.converter.XplatformHttpMessageConverter;
import jcf.sua.ux.xplatform.mvc.interceptor.XplatformDataSetHandlerInterceptor;

/**
 *
 * SUA Channel Class 를 생성한다.
 *
 * @author nolang
 *
 */
class SuaChannelClassFactory {

	/**
	 *
	 * SUA Channel HandlerInterceptor 클래스 반환
	 *
	 * @param channel
	 * @return
	 */
	static Class<? extends MciDataSetHandlerInterceptor> getHandlerInterceptor(SuaChannels channel){
		Class<? extends MciDataSetHandlerInterceptor> channelType = null;

		if (channel == SuaChannels.JSON) {
			channelType = JsonDataSetHandlerInterceptor.class;
		} else if (channel == SuaChannels.EXTJS) {
			channelType = ExtJsDataSetHandlerInterceptor.class;
		} else if (channel == SuaChannels.MIPLATFORM) {
			channelType = MiplatformDataSetHandlerInterceptor.class;
		} else if (channel == SuaChannels.XPLATFORM) {
			channelType = XplatformDataSetHandlerInterceptor.class;
		} else if (channel == SuaChannels.NEXACRO) {
			channelType = NexacroDataSetHandlerInterceptor.class;
		} else if (channel == SuaChannels.GAUCE) {
			channelType = GauceDataSetHandlerInterceptor.class;
		} else if (channel == SuaChannels.MYBUILDER){
			channelType = MybuilderDataSetHandlerInterceptor.class;
		} else if (channel == SuaChannels.WEBPLUS) {
			channelType = WebplusDataSetHandlerInterceptor.class;
		} else if (channel == SuaChannels.WEBFLOW) {
			channelType = WebFlowDataSetHandlerInterceptor.class;
		} else if (channel == SuaChannels.WEBSQUARE){
			channelType = WebsquareDataSetHandlerInterceptor.class;
		} else if (channel == SuaChannels.XML){
			channelType = XmlDataSetHandlerInterceptor.class;
		} else {
			throw new UnsupportedOperationException("지원하지 않는 채널 - " + channel);
		}

		return channelType;
	}

	/**
	 *
	 * SUA Channel HttpMessageConverter 클래스 반환
	 *
	 * @param channel
	 * @return
	 */
	static Class<? extends MciHttpMessageConverter> getHttpMessageConverter(SuaChannels channel) 	{
		Class<? extends MciHttpMessageConverter> channelType = null;

		if (channel == SuaChannels.JSON) {
			channelType = JsonHttpMessageConverter.class;
		} else if (channel == SuaChannels.EXTJS) {
			channelType = ExtJsHttpMessageConverter.class;
		} else if (channel == SuaChannels.MIPLATFORM) {
			channelType = MiplatformHttpMessageConverter.class;
		} else if (channel == SuaChannels.XPLATFORM) {
			channelType = XplatformHttpMessageConverter.class;
		} else if (channel == SuaChannels.NEXACRO){
			channelType = NexacroHttpMessageConverter.class;
		} else if (channel == SuaChannels.GAUCE) {
			channelType = GauceHttpMessageConverter.class;
		} else if (channel == SuaChannels.WEBPLUS) {
			channelType = WebplusHttpMessageConverter.class;
		} else if (channel == SuaChannels.WEBFLOW) {
			channelType = WebFlowHttpMessageConverter.class;
		} else {
//			throw new UnsupportedOperationException("지원하지 않는 채널 - " + channel);
		}

		return channelType;
	}

	/**
	 *
	 * SUA Channel WebArgumentResolver 클래스 반환
	 *
	 * @param channel
	 * @return
	 */
	static Class<? extends MciWebArgumentResolver> getWebArgumentResolver(SuaChannels channel){
		Class<? extends MciWebArgumentResolver> channelType = null;

		if (channel == SuaChannels.JSON) {
			channelType = JsonWebArgumentResolver.class;
		} else if (channel == SuaChannels.EXTJS) {
			channelType = ExtJsWebArgumentResolver.class;
		} else if (channel == SuaChannels.MIPLATFORM) {
			channelType = MiplatformWebArgumentResolver.class;
		} else if (channel == SuaChannels.XPLATFORM) {
			channelType = XplatformWebArgumentResolver.class;
		} else if (channel == SuaChannels.NEXACRO){
			channelType = NexacroWebArgumentResolver.class;
		} else if (channel == SuaChannels.GAUCE) {
			channelType = GauceWebArgumentResolver.class;
		} else if (channel == SuaChannels.MYBUILDER){
			channelType = MybuilderWebArgumentResolver.class;
		} else if (channel == SuaChannels.WEBPLUS) {
			channelType = WebplusWebArgumentResolver.class;
		} else if (channel == SuaChannels.WEBFLOW) {
			channelType = WebFlowArgumentResolver.class;
		} else if (channel == SuaChannels.WEBSQUARE){
			channelType = WebsquareWebArgumentResolver.class;
		} else if (channel == SuaChannels.XML){
			channelType = XmlWebArgumentResolver.class;
		}else {
			throw new UnsupportedOperationException("지원하지 않는 채널 - " + channel);
		}

		return channelType;
	}
}
