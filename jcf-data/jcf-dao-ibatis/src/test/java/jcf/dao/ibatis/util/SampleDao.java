package jcf.dao.ibatis.util;

import java.util.List;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class SampleDao extends SqlMapClientDaoSupport {
	
	@SuppressWarnings("rawtypes")
	public List findAll() {
		return getSqlMapClientTemplate().queryForList("findAll");
	}
	
}
