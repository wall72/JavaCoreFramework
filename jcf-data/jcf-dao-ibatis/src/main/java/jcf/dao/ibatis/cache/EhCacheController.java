package jcf.dao.ibatis.cache;

import java.util.Properties;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import com.ibatis.sqlmap.engine.cache.CacheController;
import com.ibatis.sqlmap.engine.cache.CacheModel;

/**
 * iBATIS 캐시 백엔드를 ehcache로 할 때 사용.
 * <p>
 * ehcache에서의 캐시 이름은 ibatis로 하여 반드시 만들어야 한다.
 * 
 * @author setq
 *
 */
public class EhCacheController implements CacheController {

	public static final String IBATIS_CACHE_NAME = "ibatis";
	private static CacheManager cacheManager;


	public void setCacheManager(CacheManager cacheManager) {
		EhCacheController.cacheManager = cacheManager;


		/*
		 * BlockingCache로 교체하려면 iBATIS의 몇 개 클래스를 교체해야함.
		 */
		//		Ehcache cache = getCache();
		//		cacheManager.replaceCacheWithDecoratedCache(cache, new BlockingCache(cache));
	}

	public void flush(CacheModel cacheModel) {
		Ehcache cache = getCache();

		String groupMarker= createGroupMarker(cacheModel);

		for (Object itnext : cache.getKeysNoDuplicateCheck()) {
			String key = (String)itnext;
			if (key.startsWith(groupMarker)) {
				cache.remove(key);
			}
		}
	}

	private String createGroupMarker(CacheModel cacheModel) {
		return cacheModel.getId() + ":";
	}

	private Ehcache getCache() {
		return EhCacheController.cacheManager.getEhcache(EhCacheController.IBATIS_CACHE_NAME);
	}

	public Object getObject(CacheModel cacheModel, Object key) {
		Element element = getCache().get(craeteCacheKey(cacheModel, key));

		return element == null? null:element.getValue();
	}

	public Object removeObject(CacheModel cacheModel, Object key) {
		Object result;

		String cacheKey = craeteCacheKey(cacheModel, key);

		Element e = getCache().get(cacheKey);
		if (e == null) {
			result = null;

		} else {
			result = e.getValue();
			getCache().remove(cacheKey);
		}

		return result;
	}

	public void putObject(CacheModel cacheModel, Object key, Object object) {
		getCache().put(
				new Element(
						craeteCacheKey(cacheModel, key),
						object,
						Boolean.FALSE,
						new Integer((int) cacheModel.getFlushIntervalSeconds()),
						null));
	}

	private String craeteCacheKey(CacheModel cacheModel, Object key) {
		return createGroupMarker(cacheModel) + key;
	}

	public void setProperties(Properties props) {
	}

}
