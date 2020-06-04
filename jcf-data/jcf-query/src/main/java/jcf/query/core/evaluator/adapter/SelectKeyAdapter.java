package jcf.query.core.evaluator.adapter;

import com.ibatis.sqlmap.engine.mapping.statement.SelectKeyStatement;

/**
 *
 * @author nolang
 *
 */
public class SelectKeyAdapter {

	private SelectKeyStatement statement;

	public SelectKeyAdapter(SelectKeyStatement statement) {
		this.statement = statement;
	}
}
