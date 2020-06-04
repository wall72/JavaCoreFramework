package jcf.dao.ibatis.typehandler;

import java.util.List;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class TypeHandlerDao extends SqlMapClientDaoSupport {

	public int insertMember(Type type) {
		return getSqlMapClientTemplate().update("createMember", type);
	}

	@SuppressWarnings("rawtypes")
	public List findMembers() {
		return getSqlMapClientTemplate().queryForList("findMembers", null);
	}

}
