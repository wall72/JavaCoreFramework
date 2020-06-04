package jcf.query.core.evaluator.adapter;

import com.ibatis.sqlmap.engine.mapping.result.ResultMapping;
import com.ibatis.sqlmap.engine.type.TypeHandler;

/**
 *
 * @author nolang
 *
 */
public class ResultMappingAdapter {

	private ResultMapping mapping;

	public ResultMappingAdapter(ResultMapping mapping) {
		this.mapping = mapping;
	}

	/**
	 *
	 * @return
	 */
	public String getColumnName() {
		return mapping.getColumnName();
	}

	/**
	 *
	 * @return
	 */
	public String getPropertyName() {
		return mapping.getPropertyName();
	}

	/**
	 *
	 * @return
	 */
	public TypeHandler getTypeHandler() {
		return mapping.getTypeHandler();
	}
}
