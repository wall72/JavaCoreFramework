package jcf.query.web;

import java.util.LinkedHashMap;
import java.util.Map;

import jcf.query.TemplateEngineType;

/**
 *
 * @author nolang
 *
 */
public class CommonContext {

	private Map<String, Object> commonVariables = new LinkedHashMap<String, Object>();
	private Map<String, Object> supports = new LinkedHashMap<String, Object>();
	private Map<String, Object> commands = new LinkedHashMap<String, Object>();

	class QueryCommand {
		static final String _TEMPLATE_ENGINE = "_TEMPLATE_ENGINE";
		static final String _CACHE_KEY = "_CACHE_KEN";
		static final String _QUERY_HINT = "_QUERY_HINT";
		static final String _TRACEID = "_TRACE_ID";
		static final String _WITHOUT_EVENT = "_WITHOUT_EVENT";
	}

	/**
	 *
	 * @return
	 */
	public Map<String, Object> getCommonVariables() {
		return commonVariables;
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	public Object getCommonVariable(String key)	{
		return commonVariables.get(key);
	}

	/**
	 *
	 * @param commonVariables
	 */
	public void addCommonVariables(Map<String, Object> commonVariables) {
		this.commonVariables.putAll(commonVariables);
	}

	/**
	 *
	 * @param key
	 * @param value
	 */
	public void addCommonVariable(String key, String value) {
		this.commonVariables.put(key, value);
	}

	/**
	 *
	 * @return
	 */
	public Map<String, Object> getMacroSupports() {
		return supports;
	}

	/**
	 *
	 * @return
	 */
	public Map<String, Object> getCommands() {
		return commands;
	}

	/**
	 *
	 * @return
	 */
	public TemplateEngineType getTemplateType() {
		return (TemplateEngineType) commands.get(QueryCommand._TEMPLATE_ENGINE);
	}

	/**
	 *
	 * @param templateType
	 */
	public void setTemplateType(TemplateEngineType templateType) {
		commands.put(QueryCommand._TEMPLATE_ENGINE, templateType);
	}

	/**
	 *
	 * @return
	 */
	public String getCacheKey() {
		return (String) commands.get(QueryCommand._CACHE_KEY);
	}

	/**
	 *
	 * @param cacheKey
	 */
	public void setCacheKey(String cacheKey) {
		commands.put(QueryCommand._CACHE_KEY, cacheKey);
	}

	/**
	 *
	 * @return
	 */
	public String getQueryHint() {
		return (String) commands.get(QueryCommand._QUERY_HINT);
	}

	/**
	 *
	 * @param queryHint
	 */
	public void setQueryHint(String queryHint) {
		commands.put(QueryCommand._QUERY_HINT, queryHint);
	}

	/**
	 *
	 * @return
	 */
	public String getTraceId() {
		return (String) commands.get(QueryCommand._TRACEID);
	}

	/**
	 *
	 * @param traceId
	 */
	public void setTraceId(String traceId) {
		commands.put(QueryCommand._TRACEID, traceId);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWithoutEvent() {
		Boolean result = false;

		if(commands.containsKey(QueryCommand._WITHOUT_EVENT))	{
			result = (Boolean) commands.get(QueryCommand._WITHOUT_EVENT);
		}

		return result;
	}

	/**
	 *
	 * @param withoutEvent
	 */
	public void setWithoutEvent(boolean withoutEvent) {
		commands.put(QueryCommand._WITHOUT_EVENT, withoutEvent);
	}
}
