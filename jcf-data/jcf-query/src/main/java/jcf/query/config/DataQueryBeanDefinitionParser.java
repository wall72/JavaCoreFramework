package jcf.query.config;

import java.util.List;

import jcf.query.TemplateEngineType;
import jcf.query.core.QueryExecutor;
import jcf.query.core.QueryTemplate;
import jcf.query.core.evaluator.DefaultQueryEvaluator;
import jcf.query.core.evaluator.FreeMarkerQueryEvaluator;
import jcf.query.core.evaluator.IBatisQueryEvaluator;
import jcf.query.core.evaluator.SimpleORMQueryEvaluator;
import jcf.query.core.evaluator.VelocityQueryEvaluator;
import jcf.query.core.evaluator.support.MacroSupport;
import jcf.query.core.handler.PrimitiveTypeParameterExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 *
 * @author nolang
 *
 */
public class DataQueryBeanDefinitionParser implements BeanDefinitionParser {

	private static final Logger logger = LoggerFactory.getLogger(DataQueryBeanDefinitionParser.class);

	public BeanDefinition parse(Element element, ParserContext pc) {
		Object source = pc.extractSource(element);

		if(logger.isDebugEnabled()){
			logger.trace("[JCF-QUERY] JCF-QUERY 설정 작업을 시작합니다.");
		}

		/*
		 * QueryEvaluator 설정 (iBatis / Custom Evaluator을 제외한 나머지는 기본설정으로 포함시킴)
		 */
		ManagedMap<TemplateEngineType, Object> queryEvaluators = new ManagedMap<TemplateEngineType, Object>();

		for(TemplateEngineType engineType : TemplateEngineType.values())	{

			BeanMetadataElement queryEvaluator = null;

			try	{
				switch (engineType){
					case DEFAULT :
						queryEvaluator = new RootBeanDefinition(DefaultQueryEvaluator.class);
						break;
					case VELOCITY :
						if(ClassUtils.isPresent("org.apache.velocity.context.Context", this.getClass().getClassLoader()))	{
							queryEvaluator = getVelocityQueryEvaluator(element, source);
						}

						break;
					case FREEMARKER :
						queryEvaluator = new RootBeanDefinition(FreeMarkerQueryEvaluator.class);
						break;
					case SIMPLE_ORM :
						queryEvaluator = new RootBeanDefinition(SimpleORMQueryEvaluator.class);
						break;
				}
			} catch (Exception e) {
				logger.warn("EngineType={} ErrorMessage={}", engineType.toString(), e.getMessage());
			}

			if (queryEvaluator != null) {
				queryEvaluators.put(engineType, queryEvaluator);
			}
		}

		String customTemplateRef = element.getAttribute("custom-template-engine-ref");

		if(StringUtils.hasText(customTemplateRef))	{
			queryEvaluators.put(TemplateEngineType.CUSTOM, new RuntimeBeanReference(customTemplateRef));
		}

		/*
		 * QueryTemplate 생성
		 */
		String defaultTemplateType = element.getAttribute("template-engine-type");

		if (TemplateEngineType.valueOf(defaultTemplateType) == TemplateEngineType.IBATIS) {
			queryEvaluators.put(TemplateEngineType.IBATIS, getIbatisQueryEvaluator(element, source));
		}

		RootBeanDefinition queryTemplate = new RootBeanDefinition(QueryTemplate.class);

		queryTemplate.getPropertyValues().addPropertyValue("queryEvaluators", queryEvaluators);
		queryTemplate.getPropertyValues().addPropertyValue("defaultTemplate", TemplateEngineType.valueOf(defaultTemplateType));

		if(logger.isDebugEnabled()){
			logger.trace("[JCF-QUERY] {}를 기본 TemplateEngine으로 사용합니다.", defaultTemplateType);
		}

		BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(queryTemplate, "_jcfquery_query_template"), pc.getRegistry());

		/*
		 * DataSource 설정
		 */
		String dataSource = element.getAttribute("datasource");

		if (!StringUtils.hasText(dataSource)) {
			throw new BeanDefinitionValidationException("[JCF-QUERY] DATASOURCE가 정의되지 않았습니다");
		}

		BeanReference dataSourceBean = new RuntimeBeanReference(dataSource);

		/*
		 * StreamHandler 설정
		 */

		List<Element> streamHandler = DomUtils.getChildElementsByTagName(element, "stream-handler");
		BeanReference streamHandlerBean = null;

		if(streamHandler.size() > 0)	{
			String ref = streamHandler.get(0).getAttribute("ref");

			if(StringUtils.hasText(ref))	{
				streamHandlerBean = new RuntimeBeanReference(ref);

				if(streamHandlerBean != null && logger.isDebugEnabled()){
					logger.trace("[JCF-QUERY] STREAM HANDLER가 등록되었습니다. - BeanID={}", ref);
				}
			}
		}

		/*
		 * ParameterExceptionHandler 설정
		 */
		List<Element> exceptionHandler = DomUtils.getChildElementsByTagName(element, "parameter-exception-handler");
		BeanMetadataElement exceptionHandlerBean = null;

		if(exceptionHandler.size() > 0)	{
			String ref = exceptionHandler.get(0).getAttribute("ref");

			if(StringUtils.hasText(ref)){
				exceptionHandlerBean = new RuntimeBeanReference(exceptionHandler.get(0).getAttribute("ref"));
			} else {
				exceptionHandlerBean =  new RootBeanDefinition(PrimitiveTypeParameterExceptionHandler.class);
			}

			if(exceptionHandlerBean != null && logger.isDebugEnabled()){
				logger.trace("[JCF-QUERY] PARAMETER EXCEPTION HANDLER가 등록되었습니다. - BeanID={}", ref);
			}
		}

		/*
		 * QueryExecutor 설정
		 */
		String beanId = element.getAttribute("id");
		String fetchSize = element.getAttribute("fetchSize");

		RootBeanDefinition queryExecutor = new RootBeanDefinition(QueryExecutor.class);

		queryExecutor.setSource(source);
		queryExecutor.getPropertyValues().addPropertyValue("dataSource", dataSourceBean);
		queryExecutor.getPropertyValues().addPropertyValue("fetchSize", Integer.parseInt(fetchSize));
		queryExecutor.getPropertyValues().addPropertyValue("queryTemplate", queryTemplate);
		queryExecutor.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

		if(exceptionHandlerBean != null){
			queryExecutor.getPropertyValues().addPropertyValue("exceptionHandler", exceptionHandlerBean);
		}

		if(streamHandlerBean != null){
			queryExecutor.getPropertyValues().addPropertyValue("streamHandler", streamHandlerBean);
		}

		BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(queryExecutor, beanId), pc.getRegistry());

		if(exceptionHandlerBean != null && logger.isDebugEnabled()){
			logger.trace("[JCF-QUERY] QueryExecutor를 생성합니다.");
		}

		if(logger.isDebugEnabled()){
			logger.trace("[JCF-QUERY] JCF-QUERY 설정 작업을 종료합니다.");
		}

		return queryExecutor;
	}

	private RootBeanDefinition getIbatisQueryEvaluator(Element element,
			Object source) {
		String ibatisSqlMapClient = element.getAttribute("ibatis-sqlmap-client");

		if(!StringUtils.hasText(ibatisSqlMapClient)){
			throw new BeanDefinitionValidationException("iBATIS sqlMapClient가 정의되어 있지 않습니다.");
		}

		RootBeanDefinition ibatisQueryEvaluator = new RootBeanDefinition(IBatisQueryEvaluator.class);

		ibatisQueryEvaluator.setSource(source);
		ibatisQueryEvaluator.getPropertyValues().addPropertyValue("sqlMapClient", new RuntimeBeanReference(ibatisSqlMapClient));

		return ibatisQueryEvaluator;
	}

	private RootBeanDefinition getVelocityQueryEvaluator(Element element, Object source) throws LinkageError {
		RootBeanDefinition velocityQueryEvaluator = new RootBeanDefinition(VelocityQueryEvaluator.class);

		/*
		 * Marco 설정
		 */
		ManagedMap<String, RootBeanDefinition> macroDefinitions = new ManagedMap<String, RootBeanDefinition>();

		List<Element> macros = DomUtils.getChildElementsByTagName(element, "macros");

		if(macros != null && macros.size() > 0)	{
			List<Element> macroSupportList = DomUtils.getChildElementsByTagName(macros.get(0), "macro");

			for (Element macro : macroSupportList) {
				String macroId = macro.getAttribute("id");
				String clazz = macro.getAttribute("class");

				try {
//					macroDefinitions.put(macroId, (MacroSupport) ClassUtils.forName(clazz, this.getClass().getClassLoader()).newInstance());
					macroDefinitions.put(macroId, new RootBeanDefinition(ClassUtils.forName(clazz, this.getClass().getClassLoader())));
				} catch (Exception e) {
					throw new BeanCreationException("[Class] " + clazz + " 가 없습니다.", e);
				}
			}
		}

		velocityQueryEvaluator.setSource(source);
		velocityQueryEvaluator.getPropertyValues().addPropertyValue("macros", macroDefinitions);

		return velocityQueryEvaluator;
	}

}
