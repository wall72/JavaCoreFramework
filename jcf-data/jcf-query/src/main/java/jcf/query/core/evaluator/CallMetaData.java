package jcf.query.core.evaluator;

/**
 *
 * @author nolang
 *
 */
public class CallMetaData {

	private String schemaName;
	private String procedureName;
	private boolean isFunction;

	public CallMetaData(String schemaName, String procedureName, boolean isFunction) {
		this.schemaName = schemaName;
		this.procedureName = procedureName;
		this.isFunction = isFunction;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public boolean isFunction() {
		return isFunction;
	}

}
