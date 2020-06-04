package jcf.iam.config;

import java.util.List;

import jcf.iam.core.DefaultCustomizer;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.IamMappingClass;
import jcf.iam.core.authentication.userdetails.UserAccessControlService;
import jcf.iam.core.authentication.userdetails.UserAuthenticationService;
import jcf.iam.core.authorization.SecuredResourcesMappingFactoryBean;
import jcf.iam.core.authorization.SecuredResourcesMetadataSource;
import jcf.iam.core.authorization.expression.MethodSecurityExpressionHandler;
import jcf.iam.core.authorization.expression.UrlSecurityExpressionHandler;
import jcf.iam.core.authorization.service.SecuredResourcesService;
import jcf.iam.core.filter.AuthenticationParameterIntegrationFilter;
import jcf.query.TemplateEngineType;
import jcf.query.core.QueryExecutorWrapper;
import jcf.query.core.decorator.QueryEvaluatorChanger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.AntUrlPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 *
 * @author nolang
 *
 */
public class IamBeanDefinitionParser implements BeanDefinitionParser {

	private static final Logger logger = LoggerFactory.getLogger(IamBeanDefinitionParser.class);

	public BeanDefinition parse(Element element, ParserContext pc) {
		Object source = pc.extractSource(element);

		if(logger.isDebugEnabled()){
			logger.debug("[JCF-IAM] JCF-IAM 설정 작업을 시작합니다.");
		}

		RootBeanDefinition customizer = registerIamCustomizer(element, pc);
		RootBeanDefinition customizerFactory = registerCustomizerFactory(element, pc, customizer);

		/*
		 * JCFQUERY Executor 정의
		 */
		RootBeanDefinition queryExecutor = registerQueryExecutorWrapper(element, pc);

		/*
		 * UserDetailsService 정의
		 */
		if(logger.isDebugEnabled()){
			logger.debug("[JCF-IAM] UserDetailsService를 생성합니다. - ServiceID={}", element.getAttribute("user-service-id"));
		}

		registerUserDetailsService(element, pc, customizerFactory, queryExecutor);

		/*
		 * AuthenticationManager 관련 처리 - EventPublish가 필요한 경우 Wrapping 한다.
		 */
		BeanMetadataElement authenticationManager = registerAuthenticationManager(element, pc);

		/*
		 * AccesssDecisionManager 정의
		 */
		registerAclComponent(element, pc, customizerFactory, queryExecutor, authenticationManager);

		/*
		 * AuthenticationParameterIntegrationFilter 정의
		 */
		List<Element> parameterFilterList = DomUtils.getChildElementsByTagName(element, IamNodeNameDefinition.PARAMETER_INTEGRATION_FILTER);

		if (parameterFilterList.size() > 0) {
			if(logger.isDebugEnabled()){
				logger.debug("[JCF-IAM] AuthenticationParameterIntegrationFilter 를 생성합니다. - FilterID={}", parameterFilterList.get(0).getAttribute("id"));
			}

			RootBeanDefinition parameterFilter = new RootBeanDefinition(AuthenticationParameterIntegrationFilter.class);
			parameterFilter.setSource(source);
			parameterFilter.getPropertyValues().addPropertyValue("customizerFactory", customizerFactory);

			BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(parameterFilter, parameterFilterList.get(0).getAttribute("id")), pc.getRegistry());
		}

		/*
		 * Method Expression Handler 정의
		 */
		List<Element> expressionHandlerList = DomUtils.getChildElementsByTagName(element, IamNodeNameDefinition.CUSTOM_METHOD_EXPRESSION_HANDLER);

		if (expressionHandlerList.size() > 0) {
			if(logger.isDebugEnabled()){
				logger.debug("[JCF-IAM] Method Expression Handler 를 생성합니다. - HandlerID={}", expressionHandlerList.get(0).getAttribute("id"));
			}

			RootBeanDefinition expressionHandler = new RootBeanDefinition(MethodSecurityExpressionHandler.class);
			expressionHandler.setSource(source);

			BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(expressionHandler, expressionHandlerList.get(0).getAttribute("id")), pc.getRegistry());
		}

		if(logger.isDebugEnabled()){
			logger.debug("[JCF-IAM] JCF-IAM 설정 작업을 종료합니다.");
		}

		return null;
	}

	/**
	 *
	 * @param element
	 * @param pc
	 * @return
	 */
	private RootBeanDefinition registerQueryExecutorWrapper(Element element, ParserContext pc) {
		Object source = pc.extractSource(element);

		if(logger.isDebugEnabled()){
			logger.debug("[JCF-IAM] QueryExecutorWrapper를 생성합니다.");
		}


		RootBeanDefinition queryExecutor = new RootBeanDefinition(QueryExecutorWrapper.class);

		queryExecutor.setSource(source);
		queryExecutor.getPropertyValues().addPropertyValue("queryExecutor", new RuntimeBeanReference(element.getAttribute("query-executor-id")));

		RootBeanDefinition queryDecorator = new RootBeanDefinition(QueryEvaluatorChanger.class);

		queryDecorator.setSource(source);
		queryDecorator.getPropertyValues().addPropertyValue("templateEngineType", TemplateEngineType.SIMPLE_ORM);

		queryExecutor.getPropertyValues().addPropertyValue("decorator", queryDecorator);

		BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(queryExecutor, "_jcfiam_query_executor"), pc.getRegistry());

		return queryExecutor;
	}

	/**
	 *
	 * @param element
	 * @param pc
	 * @param customizerFactory
	 * @param queryExecutor
	 * @param authenticationManager
	 */
	private void registerAclComponent(Element element, ParserContext pc, RootBeanDefinition customizerFactory,	RootBeanDefinition queryExecutor, BeanMetadataElement authenticationManager) {

		Object source = pc.extractSource(element);

		if (StringUtils.hasText(element.getAttribute("access-decision-manager-id"))) {
			RootBeanDefinition accessDecisionManager = registerAccessDecisionManager(element, pc, authenticationManager);

			/*
			 * AccessDecisionManager가 정의된 경우에만 FilterSecurityInterceptor를 사용할 수 있다.
			 */
			List<Element> filterSecurityInterceptorList = DomUtils.getChildElementsByTagName(element, IamNodeNameDefinition.FILTER_SECURITY_INTERCEPTOR);

			if (filterSecurityInterceptorList.size() > 0) {
				RootBeanDefinition securedResourcesService = new RootBeanDefinition(SecuredResourcesService.class);

				securedResourcesService.setSource(source);
				securedResourcesService.getPropertyValues().addPropertyValue("customizerFactory", customizerFactory);
				securedResourcesService.getPropertyValues().addPropertyValue("queryExecutor", queryExecutor);

				RootBeanDefinition securedResourceFactoryBean = new RootBeanDefinition(SecuredResourcesMappingFactoryBean.class);

				securedResourceFactoryBean.setSource(source);
				securedResourceFactoryBean.setInitMethodName("init");
				securedResourceFactoryBean.getPropertyValues().addPropertyValue("securedResourcesService", securedResourcesService);

				ConstructorArgumentValues metadataSourceConstrutor = new ConstructorArgumentValues();

				metadataSourceConstrutor.addIndexedArgumentValue(0, new RootBeanDefinition(AntUrlPathMatcher.class));
				metadataSourceConstrutor.addIndexedArgumentValue(1, securedResourceFactoryBean);

				if(logger.isDebugEnabled()){
					logger.debug("[JCF-IAM] SecurityMetadataSource를 생성합니다.");
				}

				RootBeanDefinition securityMetadataSource = new RootBeanDefinition(SecuredResourcesMetadataSource.class);

				securityMetadataSource.setSource(source);
				securityMetadataSource.setConstructorArgumentValues(metadataSourceConstrutor);

				Element filterElement = filterSecurityInterceptorList.get(0);

				if(logger.isDebugEnabled()){
					logger.debug("[JCF-IAM] FilterSecurityInterceptor를 생성합니다. - FilterID={}", filterElement.getAttribute("id"));
				}

				/*
				 * FilterSecurityInterceptor 정의
				 */
				RootBeanDefinition filterSecurityInterceptor = new RootBeanDefinition(FilterSecurityInterceptor.class);

				filterSecurityInterceptor.setSource(source);
				filterSecurityInterceptor.getPropertyValues().addPropertyValue("observeOncePerRequest", "false");
				filterSecurityInterceptor.getPropertyValues().addPropertyValue("authenticationManager", authenticationManager);
				filterSecurityInterceptor.getPropertyValues().addPropertyValue("accessDecisionManager", accessDecisionManager);
				filterSecurityInterceptor.getPropertyValues().addPropertyValue("securityMetadataSource", securityMetadataSource);

				BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(filterSecurityInterceptor, filterElement.getAttribute("id")), pc.getRegistry());
			}
		}
	}

	/**
	 *
	 * @param element
	 * @param pc
	 * @return
	 */
	private BeanMetadataElement registerAuthenticationManager(Element element, ParserContext pc) {
		Object source = pc.extractSource(element);

		BeanMetadataElement authenticationManager = null;

		if (Boolean.valueOf(element.getAttribute("use-security-event"))) {
			if(logger.isDebugEnabled()){
				logger.debug("[JCF-IAM] Global Method Security Event를 사용하도록 설정되었습니다.");
				logger.debug("[JCF-IAM] SpringSecurity AuthenticationManager(ID={}) Wrapper를 생성합니다.", element.getAttribute("authentication-manager-id"));
			}

			authenticationManager = new RootBeanDefinition(ProviderManager.class);

			((RootBeanDefinition) authenticationManager).setSource(source);
			((RootBeanDefinition) authenticationManager).getPropertyValues().addPropertyValue("authenticationEventPublisher", new RootBeanDefinition(DefaultAuthenticationEventPublisher.class));
			((RootBeanDefinition) authenticationManager).getPropertyValues().addPropertyValue("parent", new RuntimeBeanReference(element.getAttribute("authentication-manager-id")));

			BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder((RootBeanDefinition) authenticationManager, "_jcfiam_authentication_manager"), pc.getRegistry());
		} else {
			authenticationManager = new RuntimeBeanReference(element.getAttribute("authentication-manager-id"));
		}

		return authenticationManager;
	}

	/**
	 *
	 * AccessDecisionManager 설정
	 *
	 * @param element
	 * @param pc
	 * @param authenticationManager
	 * @return
	 */
	private RootBeanDefinition registerAccessDecisionManager(Element element, ParserContext pc, BeanMetadataElement authenticationManager) {
		if(logger.isDebugEnabled()){
			logger.debug("[JCF-IAM] SpringSecurity AccessDecisionManager(ID={})를 생성합니다.", element.getAttribute("access-decision-manager-id"));
		}

		Object source = pc.extractSource(element);

		ManagedList<Object> decisionVoters = new ManagedList<Object>();

		RootBeanDefinition roleVoter = new RootBeanDefinition(RoleVoter.class);
		roleVoter.setSource(source);
		roleVoter.getPropertyValues().addPropertyValue("rolePrefix", "");

		BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(roleVoter, "_jcfima_role_voter"), pc.getRegistry());

		RootBeanDefinition webExpressionHandler = new RootBeanDefinition(UrlSecurityExpressionHandler.class);
		RootBeanDefinition webExpressionVoter = new RootBeanDefinition(WebExpressionVoter.class);
		webExpressionVoter.setSource(source);
		webExpressionVoter.getPropertyValues().addPropertyValue("expressionHandler", webExpressionHandler);

		BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(webExpressionVoter, "_jcfima_webexpression_voter"), pc.getRegistry());
		BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(webExpressionHandler, "_jcfima_webexpression_handler"), pc.getRegistry());

		RootBeanDefinition authenticationVoter = new RootBeanDefinition(AuthenticatedVoter.class);
		BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(authenticationVoter, "_jcfima_authentication_voter"), pc.getRegistry());

		decisionVoters.add(roleVoter);
		decisionVoters.add(authenticationVoter);
		decisionVoters.add(webExpressionVoter);

		RootBeanDefinition accessDecisionManager = new RootBeanDefinition(AffirmativeBased.class);

		accessDecisionManager.setSource(source);
		accessDecisionManager.getPropertyValues().addPropertyValue("allowIfAllAbstainDecisions", "false");
		accessDecisionManager.getPropertyValues().addPropertyValue("decisionVoters", decisionVoters);

		BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder((RootBeanDefinition) accessDecisionManager, element.getAttribute("access-decision-manager-id")), pc.getRegistry());

		return accessDecisionManager;
	}

	/**
	 *
	 * Customizer Factory 설정
	 *
	 * @param element
	 * @param pc
	 * @param customizer
	 * @return
	 */
	private RootBeanDefinition registerCustomizerFactory(Element element, ParserContext pc, RootBeanDefinition customizer) {
		Object source = pc.extractSource(element);

		RootBeanDefinition customizerFactory = new RootBeanDefinition(IamCustomizerFactory.class);

		customizerFactory.setSource(source);
		customizerFactory.getPropertyValues().addPropertyValue("customizer", customizer);

		BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(customizerFactory, "_jcfiam_customizer_factory"), pc.getRegistry());

		return customizerFactory;
	}

	/**
	 *
	 * UserDetailsService 설정
	 *
	 * @param element
	 * @param pc
	 * @param customizerFactory
	 * @param queryExecutor
	 * @param userServiceId
	 * @return
	 */
	private BeanDefinition registerUserDetailsService(Element element, ParserContext pc, RootBeanDefinition customizerFactory, RootBeanDefinition queryExecutor) {
		Object source = pc.extractSource(element);

		/*
		 * UserAccessControlService 정의
		 */
		RootBeanDefinition userAccessControlService = new RootBeanDefinition(UserAccessControlService.class);
		userAccessControlService.setSource(source);
		userAccessControlService.getPropertyValues().addPropertyValue("customizerFactory", customizerFactory);
		userAccessControlService.getPropertyValues().addPropertyValue("queryExecutor", queryExecutor);

		BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(userAccessControlService, "_jcfiam_user_access_control_service"), pc.getRegistry());

		/*
		 * UserDetailsService 정의
		 */
		RootBeanDefinition userService = new RootBeanDefinition(UserAuthenticationService.class);
		userService.setSource(source);
		userService.getPropertyValues().addPropertyValue("customizerFactory", customizerFactory);
		userService.getPropertyValues().addPropertyValue("queryExecutor", queryExecutor);
		userService.getPropertyValues().addPropertyValue("userAccessControlService", userAccessControlService);

		BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(userService, element.getAttribute("user-service-id")), pc.getRegistry());

		return userService;
	}

	/**
	 *
	 * CUSTOMIZER 설정
	 *
	 * @param element
	 * @param pc
	 */
	private RootBeanDefinition registerIamCustomizer(Element element, ParserContext pc) {
		List<Element> securityModelList = DomUtils.getChildElementsByTagName(element, IamNodeNameDefinition.CUSTOM_SECURITY_MODEL);

		RootBeanDefinition customizer = new RootBeanDefinition(DefaultCustomizer.class);

		Object source = pc.extractSource(element);

		customizer.setSource(source);

		if(securityModelList.size() > 0){
			customizer.getPropertyValues().addPropertyValue("useJcfIamAcl", Boolean.valueOf(securityModelList.get(0).getAttribute("use-acl")));
			customizer.getPropertyValues().addPropertyValue("useDefaultConfiguation", Boolean.valueOf(securityModelList.get(0).getAttribute("use-default-model")));


			if(logger.isDebugEnabled()){
				logger.debug("[JCF-IAM] JCF-IAM Customizer를 생성합니다.");
			}

			if(securityModelList.size() != 0)	{
				List<Element> securityClassList = DomUtils.getChildElementsByTagName(securityModelList.get(0), "security-class");

				for (Element securityClass : securityClassList) {
					IamMappingClass mappingClassType = IamMappingClass.valueOf(securityClass.getAttribute("type"));
					String className = securityClass.getAttribute("class");

					if(logger.isDebugEnabled()){
						logger.debug("[JCF-IAM] Customizer의 기본설정이 변경되었습니다.- SecurityModel={}, Class={}", mappingClassType.toString(), className);
					}

					switch (mappingClassType) {
						case USER :
							customizer.getPropertyValues().addPropertyValue("userClassName", className);
							break;
						case AUTHORITY :
							customizer.getPropertyValues().addPropertyValue("authorityClassName", className);
							break;
						case ROLE :
							customizer.getPropertyValues().addPropertyValue("roleClassName", className);
							break;
						case PERMISSION :
							customizer.getPropertyValues().addPropertyValue("permissionClassName", className);
							break;
						case PERMISSION_ROLES :
							customizer.getPropertyValues().addPropertyValue("permissionByRoleClassName", className);
							break;
						case PERMISSION_USERS :
							customizer.getPropertyValues().addPropertyValue("permissionByUserClassName", className);
							break;
						case SECURED_RESOURCES_ROLES :
							customizer.getPropertyValues().addPropertyValue("securedRoleMappingClassName", className);
							break;
						case SECURED_RESOURCES_USERS :
							customizer.getPropertyValues().addPropertyValue("securedUserMappingClassName", className);
							break;
						case VIEW_RESOURCES :
							customizer.getPropertyValues().addPropertyValue("viewResourceClassName", className);
							break;
					}
				}
			}
		}

		/*
		 * AuthenticationParameterIntegrationFilter 정의
		 */
		List<Element> parameterFilterList = DomUtils.getChildElementsByTagName(element, IamNodeNameDefinition.PARAMETER_INTEGRATION_FILTER);

		ManagedList<String> parameterIdList = new ManagedList<String>();

		if (parameterFilterList.size() > 0) {
			List<Element> parameters = DomUtils.getChildElementsByTagName(parameterFilterList.get(0), "parameter");

			for (Element parameter : parameters) {
				parameterIdList.add(parameter.getAttribute("name"));
			}
		}

		customizer.getPropertyValues().addPropertyValue("customParameterList", parameterIdList);



		BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(customizer, "_jcfiam_customizer"), pc.getRegistry());

		return customizer;
	}

}
