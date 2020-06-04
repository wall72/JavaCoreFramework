package jcf.query.core.evaluator;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import jcf.query.TemplateEngineType;
import jcf.query.exception.StatementEvaluateException;
import jcf.query.web.CommonVariableHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Template;

/**
 *
 * @author nolang
 *
 */
public class FreeMarkerQueryEvaluator implements QueryEvaluator	{

	private static final Logger logger = LoggerFactory.getLogger(FreeMarkerQueryEvaluator.class);

	public QueryMetaData evaluate(Object statementTemplate, Object param) {
		StringWriter statement = new StringWriter();

		try {
			Template template = new Template(UUID.randomUUID().toString(), new StringReader((String) statementTemplate), null);

			/**
			 * TODO bean/map/기본형 별 parameter 처리로직 검토
			 */
			template.process(param, statement);
		} catch (Exception e) {
			throw new StatementEvaluateException("FreeMarker 쿼리 생성 실패!", e);
		} finally	{
			CommonVariableHolder.clearMacroSupports();
		}

		return new QueryMetaData(TemplateEngineType.FREEMARKER, statement.toString(), SqlParameterSourceBuilder.getSqlParameterSource(param), null);
	}
}
