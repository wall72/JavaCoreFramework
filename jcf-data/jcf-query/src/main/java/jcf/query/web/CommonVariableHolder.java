package jcf.query.web;

import java.util.Map;

import jcf.data.web.context.RequestContextHolder;
import jcf.query.TemplateEngineType;

/**
 *
 * @author nolang
 *
 */
public class CommonVariableHolder {
	private static final String CONTEXT_NAME = "JCF-QUERY";


	public static CommonContext getContext()	{
		CommonContext context = RequestContextHolder.getContext().getContext(CONTEXT_NAME, CommonContext.class);

		if(context == null)	{
			context = new CommonContext();
			RequestContextHolder.getContext().setContext(CONTEXT_NAME, context);
		}

		return context;
	}

	/**
	 *
	 * @return
	 */
	public static Map<String, Object> getVariables()	{
		return getContext().getCommonVariables();
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	public static Object get(String key){
		return getContext().getCommonVariable(key);
	}

	/**
	 *
	 * @param key
	 * @param value
	 */
	public static void addVariable(String key, String value) {
		getContext().getCommonVariables().put(key, value);
	}

	/**
	 *
	 * @param variables
	 */
	public static void addVariables(Map<String, Object> variables) {
		getContext().getCommonVariables().putAll(variables);
	}

	/**
	 *
	 * @param key
	 */
	public static void remove(String key) {
		getContext().getCommonVariables().remove(key);
	}

	/**
	 *
	 * @return
	 */
	public static Map<String, Object> getMacroSupports()	{
		return getContext().getMacroSupports();
	}

	/**
	 *
	 * @return
	 */
	public static TemplateEngineType getTemplateType()	{
		return getContext().getTemplateType();
	}

	/**
	 *
	 * @param templateEngineType
	 */
	public static void setTemplateType(TemplateEngineType templateEngineType){
		getContext().setTemplateType(templateEngineType);
	}

	/**
	 *
	 * @return
	 */
	public static String getCacheKey()	{
		return getContext().getCacheKey();
	}

	/**
	 *
	 * @param cacheKey
	 */
	public static void setCacheKey(String cacheKey) {
		getContext().setCacheKey(cacheKey);
	}

	/**
	 *
	 * @return
	 */
	public static String getQueryHint()	{
		return getContext().getQueryHint();
	}

	/**
	 *
	 * @param hint
	 */
	public static void setQueryHint(String hint){
		getContext().setQueryHint(hint);
	}

	/**
	 *
	 * @return
	 */
	public static boolean isWithoutEvent()	{
		return getContext().isWithoutEvent();
	}

	/**
	 *
	 * @param isWithoutEvent
	 */
	public static void setWithoutEvent(boolean isWithoutEvent){
		getContext().setWithoutEvent(isWithoutEvent);
	}

	/**
	 *
	 */
	public static void clear()	{
		RequestContextHolder.getContext().clearContext(CONTEXT_NAME);
	}

	/**
	 *
	 */
	public static void clearMacroSupports()	{
		getContext().getMacroSupports().clear();
	}

	/**
	 *
	 */
	public static void clearCommand()	{
		getContext().getCommands().clear();
	}
}
