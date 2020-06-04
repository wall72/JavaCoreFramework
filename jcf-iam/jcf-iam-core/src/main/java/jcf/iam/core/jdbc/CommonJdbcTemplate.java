package jcf.iam.core.jdbc;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

/**
 * <pre>
 * Data Access Object
 * <pre>
 *
 * @author nolang
 *
 */
public class CommonJdbcTemplate extends JdbcDaoSupport {

	/**
	 * <pre>
	 * 다건 조회
	 * <pre>
	 *
	 * @param <T>
	 * @param statement
	 * @param args
	 * @param mapper
	 * @return
	 */
	@Cacheable(cacheName = "iamCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
			@Property(name = "useReflection", value = "true"),
			@Property(name = "checkforCycles", value = "true"),
			@Property(name = "includeMethod", value = "false") }))
	public <T> List<T> query(String statement, Object[] args, RowMapper<T> mapper) {
		return getJdbcTemplate().query(statement, args, mapper);
	}

	/**
	 * <pre>
	 * 단건조회
	 * <pre>
	 *
	 * @param <T>
	 * @param statement
	 * @param args
	 * @param mapper
	 * @return
	 */
	@Cacheable(cacheName = "iamCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
			@Property(name = "useReflection", value = "true"),
			@Property(name = "checkforCycles", value = "true"),
			@Property(name = "includeMethod", value = "false") }))
	public <T> T queryForObject(String statement, Object[] args, RowMapper<T> mapper) {
		T result = null;

		try	{
			result = getJdbcTemplate().queryForObject(statement, args, mapper);
		} catch(EmptyResultDataAccessException e){
			// 조회된 Row가 하나도 없을때 null을 반환한다.
		}

		return result;
	}

	/**
	 * <pre>
	 * 단건추가
	 * <pre>
	 *
	 * @param statement
	 * @param args
	 * @return
	 */
	@TriggersRemove(cacheName = "iamCache", removeAll = true)
	public int insert(String statement, Object[] args)	{
		return getJdbcTemplate().update(statement, args);
	}

	/**
	 * <pre>
	 * 수정
	 * <pre>
	 *
	 * @param statement
	 * @param args
	 * @return
	 */
	@TriggersRemove(cacheName = "iamCache", removeAll = true)
	public int update(String statement, Object[] args)	{
		return getJdbcTemplate().update(statement, args);
	}

	/**
	 * <pre>
	 * 삭제
	 * <pre>
	 *
	 * @param statement
	 * @param args
	 * @return
	 */
	@TriggersRemove(cacheName = "iamCache", removeAll = true)
	public int delete(String statement, Object[] args)	{
		return getJdbcTemplate().update(statement, args);
	}
}
