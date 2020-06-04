package jcf.query.core.evaluator;

/**
 *
 * @author nolang
 *
 */
public class SubQuery {

	private QueryMetaData metaData;

	private String keyProperty;

	private boolean runAfterSQL;

	public SubQuery(QueryMetaData metaData, String keyProperty, boolean runAfterSQL) {
		this.metaData = metaData;
		this.keyProperty = keyProperty;
		this.runAfterSQL = runAfterSQL;
	}

	public QueryMetaData getMetaData() {
		return metaData;
	}

	public String getKeyProperty() {
		return keyProperty;
	}

	public boolean isRunAfterSQL() {
		return runAfterSQL;
	}
}
