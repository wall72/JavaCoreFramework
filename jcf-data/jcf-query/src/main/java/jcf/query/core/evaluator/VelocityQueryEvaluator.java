package jcf.query.core.evaluator;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import jcf.query.TemplateEngineType;
import jcf.query.core.annotation.DomainNamespace;
import jcf.query.core.evaluator.support.DynamicQuerySupport;
import jcf.query.core.evaluator.support.MacroSupport;
import jcf.query.core.evaluator.support.velocity.BetweenDirectiveSupport;
import jcf.query.core.evaluator.support.velocity.InDirectiveSupport;
import jcf.query.exception.StatementEvaluateException;
import jcf.query.exception.TemplateEngineInitialingException;
import jcf.query.util.QueryUtils;
import jcf.query.web.CommonVariableHolder;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 *
 * @author nolang
 *
 */
public class VelocityQueryEvaluator implements QueryEvaluator, InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(VelocityQueryEvaluator.class);

	private Map<String, MacroSupport> macros = new HashMap<String, MacroSupport>();

	public VelocityQueryEvaluator() {
		try {
			Properties properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/velocity.properties"));
			Velocity.init(properties);

		} catch (Exception e) {
			logger.error("Velocity 초기화 실패! - {}", ExceptionUtils.getCause(e).getMessage());
			throw new TemplateEngineInitialingException("Velocity 초기화 실패!", e);
		}
	}

	public QueryMetaData evaluate(Object statementTemplate, Object param) {
		StringWriter statement = new StringWriter();

		try {
			boolean result = Velocity.evaluate(getVelocityContext(param), statement, "[jcfquery] " + UUID.randomUUID().toString(), (String) statementTemplate);

			if(!result){
				throw new StatementEvaluateException("Velocity 쿼리 생성 실패!");
			}
		} catch (Exception e) {
			throw new StatementEvaluateException("Velocity 쿼리 생성 실패!", e);
		} finally {
			CommonVariableHolder.clearMacroSupports();
		}

		SqlParameterSource parameterSource = null;

		if(param != null && QueryUtils.isPrimitiveType(param.getClass())) {
			List<String> namedParameters = QueryUtils.getNamedParameters(statement.toString());

			Object[] p = null;

			if(ClassUtils.isPrimitiveArray(param.getClass()) || ClassUtils.isPrimitiveWrapperArray(param.getClass()) || String[].class.isAssignableFrom(param.getClass()))	{
				p = (Object[]) param;
			} else {
				p = new Object[] { param };
			}

			if(namedParameters.size() != p.length)	{
				throw new StatementEvaluateException("Velocity 쿼리 생성 실패! - Parameter의 개수와 Bind변수의 개수가 일치하지 않습니다.");
			}

			Map<String, Object> pMap = new HashMap<String, Object>();

			for (int i = 0; i < namedParameters.size(); ++i) {
				pMap.put(namedParameters.get(i), p[i]);
			}

			parameterSource = SqlParameterSourceBuilder.getSqlParameterSource(pMap);
		} else {
			parameterSource = SqlParameterSourceBuilder.getSqlParameterSource(param);
		}

		CallMetaData callMetaData = null;

		if (CallMetaDataBuilder.isCallStatement(statement.toString().trim())) {
			callMetaData = CallMetaDataBuilder.buildCallMataData(statement.toString().trim());
		}

		return new QueryMetaData(TemplateEngineType.VELOCITY, statement.toString(), parameterSource, callMetaData);
	}

	private VelocityContext getVelocityContext(Object param) {

		VelocityContext context = null;

		if (param != null && param instanceof Map) {
			context = new VelocityContext((Map<?, ?>) param);
		} else {
			context = new VelocityContext();

			if (param != null) {
				if (QueryUtils.isPrimitiveType(param.getClass())) {
					/**
					 * TODO 자바기본형에 대한 처리 검토
					 */
					logger.debug("PrimitiveType Arguement가 입력되었습니다. - Type={}, Value={}", param.getClass(), param);

					if(ClassUtils.isPrimitiveArray(param.getClass()) || ClassUtils.isPrimitiveWrapperArray(param.getClass())
							|| String[].class.isAssignableFrom(param.getClass()))	{

						Object[] o = (Object[]) param;

						for (int i = 0; i < o.length; ++i) {
							context.put("_" + (i + 1), o[i]);
						}
					} else {
						context.put("_1", param);
					}
				} else {
					String namespace = "";

					if (param.getClass().isAnnotationPresent(DomainNamespace.class)) {
						namespace = param.getClass().getAnnotation(DomainNamespace.class).value();
					}

					if(StringUtils.hasText(namespace)){
						context.put(namespace, param);
					} else {
						Field[] fields = param.getClass().getDeclaredFields();

						for (Field field : fields) {
							ReflectionUtils.makeAccessible(field);
							context.put(field.getName(), ReflectionUtils.getField(field, param));
						}
					}
				}
			}
		}

		/**
		 * TODO 글로벌변수(세션등)와 관련한 처리 - Velocity에서 세션변수를 evaluation 하는 경우에만 고려한다.
		 */
		Map<String, Object> commonVariables = CommonVariableHolder.getVariables();

		if(!commonVariables.isEmpty())	{
			for(Map.Entry<String, Object> e: commonVariables.entrySet())	{
				context.put(e.getKey(), e.getValue());
			}
		}

		/**
		 * TODO 매크로 or 서버사이드 지정변수를 이용한 동적 쿼리 지원기능 추가 (WHERE/AND 등..)
		 */
		context.put("_dynamic_condition_support", new DynamicQuerySupport() {

			public String getDirective(String group, String prepend) {
				if(!CommonVariableHolder.getMacroSupports().containsKey(group)){
					CommonVariableHolder.getMacroSupports().put(group, true);
					return "WHERE";
				}

				return prepend;
			}
		});

		/*
		 * 사용자 정의 매크로를 추가한다.
		 */
		for (Map.Entry<String, MacroSupport> e : macros.entrySet()) {
			context.put(e.getKey(), e.getValue());
		}

		return context;
	}

	public void setMacros(Map<String, MacroSupport> macros) {
		this.macros = macros;
	}

	public void afterPropertiesSet() throws Exception {
		synchronized (macros) {
			macros.put("_between_support", new BetweenDirectiveSupport());
			macros.put("_in_support", new InDirectiveSupport());
		}
	}
}
