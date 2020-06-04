package jcf.query.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jcf.data.handler.StreamHandler;
import jcf.query.core.decorator.QueryDecorator;
import jcf.query.web.CommonVariableHolder;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.HashCodeCacheKeyGenerator;

/**
 *
 * @author nolang
 *
 */
public class QueryExecutorWrapper {

	private static final Logger logger = LoggerFactory.getLogger(QueryExecutorWrapper.class);

	@Autowired
	private QueryExecutor queryExecutor;

	private QueryDecorator decorator = new QueryDecorator() {

		public void beforeExecution(Object... args) {

		}

		public void afterExecution(Object... args) {
			CommonVariableHolder.clear();
		}
	};

	private CacheManager cacheManager;

	private String defaultCacheName = "testCache";

	private CacheKeyGenerator<?> cacheKeyGenerator = new HashCodeCacheKeyGenerator();

	private long keepAliveTime = 30000;

	private Map<String, List<Object>> cacheKeyHolder = new LinkedHashMap<String, List<Object>>();

	private boolean usePropertyName = true;

	public void setQueryExecutor(QueryExecutor queryExecutor) {
		this.queryExecutor = queryExecutor;
	}

	public void setDecorator(QueryDecorator decorator) {
		this.decorator = decorator;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void setDefaultCacheName(String defaultCacheName) {
		this.defaultCacheName = defaultCacheName;
	}

	public void setCacheKeyGenerator(CacheKeyGenerator<?> cacheKeyGenerator) {
		this.cacheKeyGenerator = cacheKeyGenerator;
	}

	public void setKeepAliveTime(long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public QueryExecutorWrapper() {
		((HashCodeCacheKeyGenerator) cacheKeyGenerator).setUseReflection(true);
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 */
	public void queryForStream(Object statementTemplate, Object parameter) {
		decorator.beforeExecution(statementTemplate, parameter);

		try {
			queryExecutor.queryForStream(statementTemplate, parameter);
		} finally {
			decorator.afterExecution();
		}
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @param streamHandler
	 */
	public void queryForStream(Object statementTemplate, Object parameter, @SuppressWarnings("rawtypes") final StreamHandler streamHandler) {
		decorator.beforeExecution(statementTemplate, parameter, streamHandler);

		try {
			queryExecutor.queryForStream(statementTemplate, parameter, streamHandler);
		} finally {
			decorator.afterExecution();
		}
	}

	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> queryForList(Object statementTemplate, Object parameter, Class<T> clazz) {
		decorator.beforeExecution(statementTemplate, parameter, clazz);

		List<T> list = null;

		try {
			if(StringUtils.hasText(CommonVariableHolder.getCacheKey()))	{
				Object cacheKey = generateKey(statementTemplate, parameter, clazz);

				list = (List<T>) getCachedObject(cacheKey);

				if (list == null) {
					list = queryExecutor.queryForList(statementTemplate, parameter, clazz);
					cacheManager.getCache(defaultCacheName).put(new Element(cacheKey, list));
				}

				addCacheKey(CommonVariableHolder.getCacheKey(), cacheKey);

			} else {
				list = queryExecutor.queryForList(statementTemplate, parameter, clazz);
			}

			return list;

		} finally {
			decorator.afterExecution(list);
		}
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	@Deprecated
	public List<Object> queryForList(Object statementTemplate, Object parameter) {
		decorator.beforeExecution(statementTemplate, parameter);

		List<Object> list = null;

		try {
			if(StringUtils.hasText(CommonVariableHolder.getCacheKey()))	{
				Object cacheKey = generateKey(statementTemplate, parameter);

				list = (List<Object>) getCachedObject(cacheKey);

				if (list == null) {
					list = queryExecutor.queryForList(statementTemplate, parameter);
					cacheManager.getCache(defaultCacheName).put(new Element(cacheKey, list));
				}

				addCacheKey(CommonVariableHolder.getCacheKey(), cacheKey);

			} else {
				list = queryExecutor.queryForList(statementTemplate, parameter);
			}

			return list;

		} finally {
			decorator.afterExecution(list);
		}
	}

	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param skipRows
	 * @param maxRows
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> queryForList(Object statementTemplate, Object parameter, final int skipRows, final int maxRows, Class<T> clazz) {
		decorator.beforeExecution(statementTemplate, parameter, skipRows, maxRows, clazz);

		List<T> list = null;

		try {
			if(StringUtils.hasText(CommonVariableHolder.getCacheKey()))	{
				Object cacheKey = generateKey(CommonVariableHolder.getCacheKey(), statementTemplate, parameter, skipRows, maxRows, clazz);

				list = (List<T>) getCachedObject(cacheKey);

				if (list == null) {
					list = queryExecutor.queryForList(statementTemplate, parameter, skipRows, maxRows, clazz);
					cacheManager.getCache(defaultCacheName).put(new Element(cacheKey, list));
				}

				addCacheKey(CommonVariableHolder.getCacheKey(), cacheKey);

			} else {
				list = queryExecutor.queryForList(statementTemplate, parameter, skipRows, maxRows, clazz);
			}

			return list;

		} finally {
			decorator.afterExecution(list);
		}
	}

	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param skipRows
	 * @param maxRows
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public List<Object> queryForList(Object statementTemplate, Object parameter, final int skipRows, final int maxRows) {
		decorator.beforeExecution(statementTemplate, parameter, skipRows, maxRows);

		List<Object> list = null;

		try {
			if(StringUtils.hasText(CommonVariableHolder.getCacheKey()))	{
				Object cacheKey = generateKey(CommonVariableHolder.getCacheKey(), statementTemplate, parameter, skipRows, maxRows);

				list = (List<Object>) getCachedObject(cacheKey);

				if (list == null) {
					list = queryExecutor.queryForList(statementTemplate, parameter, skipRows, maxRows);
					cacheManager.getCache(defaultCacheName).put(new Element(cacheKey, list));
				}

				addCacheKey(CommonVariableHolder.getCacheKey(), cacheKey);

			} else {
				list = queryExecutor.queryForList(statementTemplate, parameter, skipRows, maxRows);
			}

			return list;

		} finally {
			decorator.afterExecution(list);
		}
	}

	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param rowMapper
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> queryForList(Object statementTemplate, Object parameter, RowMapper<T> rowMapper){
		decorator.beforeExecution(statementTemplate, parameter, rowMapper);

		List<T> list = null;

		try {
			if(StringUtils.hasText(CommonVariableHolder.getCacheKey()))	{
				Object cacheKey = generateKey(CommonVariableHolder.getCacheKey(), statementTemplate, parameter, rowMapper);

				list = (List<T>) getCachedObject(cacheKey);

				if (list == null) {
					list = queryExecutor.queryForList(statementTemplate, parameter, rowMapper);
					cacheManager.getCache(defaultCacheName).put(new Element(cacheKey, list));
				}

				addCacheKey(CommonVariableHolder.getCacheKey(), cacheKey);

			} else {
				list = queryExecutor.queryForList(statementTemplate, parameter, rowMapper);
			}

			return list;

		} finally {
			decorator.afterExecution(list);
		}
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	public List<Map<String, Object>> queryForMapList(Object statementTemplate, Object parameter) {
		return queryForMapList(statementTemplate, parameter, usePropertyName);
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @param usePropertyName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public List<Map<String, Object>> queryForMapList(Object statementTemplate, Object parameter, boolean usePropertyName) {
		decorator.beforeExecution(statementTemplate, parameter, usePropertyName);

		List<Map<String, Object>> list = null;

		try {
			if(StringUtils.hasText(CommonVariableHolder.getCacheKey()))	{
				Object cacheKey = generateKey(CommonVariableHolder.getCacheKey(), statementTemplate, parameter, usePropertyName);

				list = (List<Map<String, Object>>) getCachedObject(cacheKey);

				if (list == null) {
					list = queryExecutor.queryForMapList(statementTemplate, parameter, usePropertyName);
					cacheManager.getCache(defaultCacheName).put(new Element(cacheKey, list));
				}

				addCacheKey(CommonVariableHolder.getCacheKey(), cacheKey);

			} else {
				list = queryExecutor.queryForMapList(statementTemplate, parameter, usePropertyName);
			}

			return list;

		} finally {
			decorator.afterExecution(list);
		}
	}



	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @param skipRows
	 * @param maxRows
	 * @param usePropertyName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryForMapList(Object statementTemplate, Object parameter, final int skipRows, final int maxRows, boolean usePropertyName) {
		decorator.beforeExecution(statementTemplate, parameter, skipRows, maxRows, usePropertyName);

		List<Map<String, Object>> list = null;

		try {
			if(StringUtils.hasText(CommonVariableHolder.getCacheKey()))	{
				Object cacheKey = generateKey(CommonVariableHolder.getCacheKey(), statementTemplate, parameter, skipRows, maxRows, usePropertyName);

				list = (List<Map<String, Object>>) getCachedObject(cacheKey);

				if (list == null) {
					list = queryExecutor.queryForMapList(statementTemplate, parameter, skipRows, maxRows, usePropertyName);
					cacheManager.getCache(defaultCacheName).put(new Element(cacheKey, list));
				}

				addCacheKey(CommonVariableHolder.getCacheKey(), cacheKey);

			} else {
				list = queryExecutor.queryForMapList(statementTemplate, parameter, skipRows, maxRows, usePropertyName);
			}

			return list;

		} finally {
			decorator.afterExecution(list);
		}
	}

	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T queryForObject(Object statementTemplate, Object parameter,	Class<T> clazz) {
		decorator.beforeExecution(statementTemplate, parameter, clazz);

		T result = null;

		try {
			if(StringUtils.hasText(CommonVariableHolder.getCacheKey()))	{
				Object cacheKey = generateKey(CommonVariableHolder.getCacheKey(), statementTemplate, parameter, clazz);

				result = (T) getCachedObject(cacheKey);

				if (result == null) {
					result = queryExecutor.queryForObject(statementTemplate, parameter, clazz);
					cacheManager.getCache(defaultCacheName).put(new Element(cacheKey, result));
				}

				addCacheKey(CommonVariableHolder.getCacheKey(), cacheKey);

			} else {
				result = queryExecutor.queryForObject(statementTemplate, parameter, clazz);
			}

			return result;

		} finally {
			decorator.afterExecution(result);
		}
	}

	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param rowMapper
	 * @return
	 */
	public <T> T queryForObject(Object statementTemplate, Object parameter,	RowMapper<T> rowMapper) {
		decorator.beforeExecution(statementTemplate, parameter);

		T result = null;

		try {
			if(StringUtils.hasText(CommonVariableHolder.getCacheKey()))	{
				Object cacheKey = generateKey(CommonVariableHolder.getCacheKey(), statementTemplate, parameter, rowMapper);

				result = (T) getCachedObject(cacheKey);

				if (result == null) {
					result = queryExecutor.queryForObject(statementTemplate, parameter, rowMapper);
					cacheManager.getCache(defaultCacheName).put(new Element(cacheKey, result));
				}

				addCacheKey(CommonVariableHolder.getCacheKey(), cacheKey);

			} else {
				result = queryExecutor.queryForObject(statementTemplate, parameter, rowMapper);
			}

			return result;

		} finally {
			decorator.afterExecution(result);
		}
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	@Deprecated
	public Object queryForObject(Object statementTemplate, Object parameter) {
		decorator.beforeExecution(statementTemplate, parameter);

		Object result = null;

		try {
			if(StringUtils.hasText(CommonVariableHolder.getCacheKey()))	{
				Object cacheKey = generateKey(CommonVariableHolder.getCacheKey(), statementTemplate, parameter);

				result = getCachedObject(cacheKey);

				if (result == null) {
					result = queryExecutor.queryForObject(statementTemplate, parameter);
					cacheManager.getCache(defaultCacheName).put(new Element(cacheKey, result));
				}

				addCacheKey(CommonVariableHolder.getCacheKey(), cacheKey);

			} else {
				result = queryExecutor.queryForObject(statementTemplate, parameter);
			}

			return result;

		} finally {
			decorator.afterExecution(result);
		}
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	public Map<String, Object> queryForMap(Object statementTemplate, Object parameter) {
		return queryForMap(statementTemplate, parameter, usePropertyName);
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @param usePropertyName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public Map<String, Object> queryForMap(Object statementTemplate, Object parameter, boolean usePropertyName) {
		decorator.beforeExecution(statementTemplate, parameter, usePropertyName);

		Map<String, Object> result = null;

		try {
			if(StringUtils.hasText(CommonVariableHolder.getCacheKey()))	{
				Object cacheKey = generateKey(CommonVariableHolder.getCacheKey(), statementTemplate, parameter, usePropertyName);

				result = (Map<String, Object>) getCachedObject(cacheKey);

				if (result == null) {
					result = queryExecutor.queryForMap(statementTemplate, parameter, usePropertyName);
					cacheManager.getCache(defaultCacheName).put(new Element(cacheKey, result));
				}

				addCacheKey(CommonVariableHolder.getCacheKey(), cacheKey);

			} else {
				result = queryExecutor.queryForMap(statementTemplate, parameter, usePropertyName);
			}

			return result;
		} finally {
			decorator.afterExecution(result);
		}
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	public Integer queryForInt(Object statementTemplate, Object parameter){
		decorator.beforeExecution();

		try {
			return queryExecutor.queryForInt(statementTemplate, parameter);
		} finally {
			decorator.afterExecution();
		}
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	public Long queryForLong(Object statementTemplate, Object parameter){
		decorator.beforeExecution();

		try {
			return queryExecutor.queryForLong(statementTemplate, parameter);
		} finally {
			decorator.afterExecution();
		}
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	public Integer update(Object statementTemplate, Object parameter) {
		decorator.beforeExecution();

		try {
			if(StringUtils.hasText(CommonVariableHolder.getCacheKey()))	{
				removeCacheKey(CommonVariableHolder.getCacheKey());
			}

			return queryExecutor.update(statementTemplate, parameter);
		} finally {
			decorator.afterExecution();
		}
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameters
	 * @return
	 */
	public int[] batchUpdate(Object statementTemplate, Object[] parameters){
		decorator.beforeExecution();

		try {
			return queryExecutor.batchUpdate(statementTemplate, parameters);
		} finally {
			decorator.afterExecution();
		}
	}

	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param action
	 * @return
	 * @throws DataAccessException
	 */
	@Deprecated
	public <T> T execute(Object statementTemplate, Object parameter, PreparedStatementCallback<T> action) throws DataAccessException {
		decorator.beforeExecution();

		try {
			return queryExecutor.execute(statementTemplate, parameter, action);
		} finally {
			decorator.afterExecution();
		}
	}


	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param clazz
	 * @return
	 */
	@Deprecated
	public <T> T executeProcedure(Object statementTemplate,Object parameter, Class<T> clazz) {
		decorator.beforeExecution();

		try {
			return queryExecutor.executeProcedure(null, null,statementTemplate.toString(), parameter, clazz);
		} finally {
			decorator.afterExecution();
		}
	}


	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param clazz
	 * @return
	 */
	public <T> T executeProcedure(String schema, String packageName, String procedureName,Object parameter, Class<T> clazz) {
		decorator.beforeExecution();

		try {
			return queryExecutor.executeProcedure(schema, packageName,procedureName, parameter, clazz);
		} finally {
			decorator.afterExecution();
		}
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	@Deprecated
	public Map<String, Object> executeProcedure(Object statementTemplate, Object parameter) {
		decorator.beforeExecution();

		try {
			return queryExecutor.executeProcedure(null, null,statementTemplate.toString(), parameter);
		} finally {
			decorator.afterExecution();
		}
	}


/**
 *
 * @param schema
 * @param packageName
 * @param procedureName
 * @param parameter
 * @return
 */
	public Map<String, Object> executeProcedure(String schema, String packageName, String procedureName, Object parameter) {
		decorator.beforeExecution();

		try {
			return queryExecutor.executeProcedure(schema, packageName,procedureName, parameter);
		} finally {
			decorator.afterExecution();
		}
	}


	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param clazz
	 * @return
	 */
	@Deprecated
	public <T> T executeFunction(Object statementTemplate,  Object parameter, Class<T> clazz) {
		decorator.beforeExecution();

		try {
			return queryExecutor.executeFunction(null, null,statementTemplate.toString(), parameter, clazz);
		} finally {
			decorator.afterExecution();
		}
	}


/**
 *
 * @param schema
 * @param packageName
 * @param functionName
 * @param parameter
 * @param clazz
 * @return
 */
	public <T> T executeFunction(String schema, String packageName, String functionName,  Object parameter, Class<T> clazz) {
		decorator.beforeExecution();

		try {
			return queryExecutor.executeFunction(schema, packageName,functionName, parameter, clazz);
		} finally {
			decorator.afterExecution();
		}
	}



    /**
     *
     * @param schema
     * @param packageName
     * @param functionName
     * @param parameter
     * @return
     */
	public Map<String, Object> executeFunction(String schema, String packageName, String functionName, Object parameter) {
		decorator.beforeExecution();

		try {
			return queryExecutor.executeFunction(schema, packageName, functionName, parameter);
		} finally {
			decorator.afterExecution();
		}
	}

/**
 *
 * @param statementTemplate
 * @param parameter
 * @return
 */
     @Deprecated
	public Map<String, Object> executeFunction(Object statementTemplate, Object parameter) {
		decorator.beforeExecution();

		try {
			return queryExecutor.executeFunction(null, null, statementTemplate.toString(), parameter);
		} finally {
			decorator.afterExecution();
		}
	}


	private Object getCachedObject(Object cacheKey) {
		Object cachedObject = null;

		if(cacheManager != null){
			Element element = cacheManager.getCache(defaultCacheName).get(cacheKey);

			if (element != null) {
				if ((System.currentTimeMillis() - element.getCreationTime() > keepAliveTime) || System.currentTimeMillis() > element.getExpirationTime()) {
					cacheManager.getCache(defaultCacheName).remove(cacheKey);
				} else {
					cachedObject = element.getObjectValue();

					if(logger.isDebugEnabled()){
						logger.debug("Cache된 결과를 반환합니다. - CacheKey={}", cacheKey);
					}
				}
			}
		}

		return cachedObject;
	}

	public Object generateKey(Object... elements) {
		return cacheKeyGenerator.generateKey(elements);
	}

	private void addCacheKey(String baseKey, Object cacheKey){
		List<Object> keys = cacheKeyHolder.get(baseKey);

		if(keys == null){
			keys = new ArrayList<Object>();
			cacheKeyHolder.put(baseKey, keys);
		}

		keys.add(cacheKey);
	}

	private void removeCacheKey(String baseKey) {
		List<Object> keys = cacheKeyHolder.get(baseKey);

		for(Object key : keys){
			if(cacheManager.getCache(defaultCacheName).isKeyInCache(key))	{
				cacheManager.getCache(defaultCacheName).remove(key);
			}
		}

		cacheKeyHolder.remove(baseKey);
	}
}
