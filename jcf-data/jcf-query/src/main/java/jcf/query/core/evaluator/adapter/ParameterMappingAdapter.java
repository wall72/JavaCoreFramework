package jcf.query.core.evaluator.adapter;

import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMapping;

/**
 *
 * @author nolang
 *
 */
public class ParameterMappingAdapter {

	public static final String MODE_IN = "IN";
	public static final String MODE_OUT = "OUT";
	public static final String MODE_INOUT = "INOUT";

	private ParameterMapping mapping;

	public ParameterMappingAdapter(ParameterMapping mapping) {
		this.mapping = mapping;
	}

	/**
	 *
	 * @return
	 */
	public String getMode() {
		return mapping.getMode();
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
	public int getJdbcType() {
		return mapping.getJdbcType();
	}
}
