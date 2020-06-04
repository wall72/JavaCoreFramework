package jcf.query.core.decorator.aspect;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jcf.query.web.CommonVariableHolder;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import com.googlecode.ehcache.annotations.key.HashCodeCacheKeyGenerator;

/**
 *
 * @author nolang
 *
 */
@Aspect
public class CacheAspect implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(CacheAspect.class);

	@Autowired
	private CacheManager cacheManager;

	private String defaultCacheName = "testCache";

	private CacheKeyGenerator<?> cacheKeyGenerator = new HashCodeCacheKeyGenerator();

	private long keepAliveTime = 30000;

	private Map<String, List<Object>> cacheKeyHolder = new LinkedHashMap<String, List<Object>>();

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

	@Around("execution(public * jcf.query.core.QueryExecutor.*(..))")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		Object object = null;

		if(StringUtils.hasText(CommonVariableHolder.getCacheKey()))	{
			String methodName = pjp.getSignature().getName();

			if(methodName.startsWith("query"))	{
				Object cacheKey = generateKey(pjp.getArgs());

				object = getCachedObject(cacheKey);

				if (object == null) {
					object = pjp.proceed();

					cacheManager.getCache(defaultCacheName).put(new Element(cacheKey, object));
				}

				addCacheKey(CommonVariableHolder.getCacheKey(), cacheKey);
			} else {
				object = pjp.proceed();
				removeCacheKey(CommonVariableHolder.getCacheKey());
			}
		} else {
			object = pjp.proceed();
		}

		return object;
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
			cacheManager.getCache(defaultCacheName).remove(key);
		}
	}

	public void afterPropertiesSet() throws Exception {
		((HashCodeCacheKeyGenerator) cacheKeyGenerator).setUseReflection(true);
	}
}
