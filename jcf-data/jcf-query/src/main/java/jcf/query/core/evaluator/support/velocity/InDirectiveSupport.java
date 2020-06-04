package jcf.query.core.evaluator.support.velocity;

import java.util.List;

import jcf.query.core.evaluator.support.MacroSupport;
import jcf.query.util.QueryUtils;

/**
 *
 * @author nolang
 *
 */
public class InDirectiveSupport implements MacroSupport {

	public String execute(Object... args) {
		return String.format(" %s IN (%s) ", getColumnName(args), getOperand(args));
	}

	@SuppressWarnings("rawtypes")
	private String getOperand(Object[] args) {
		StringBuilder builder = new StringBuilder();

		if(args != null && args.length == 2){
			Object operand = args[1];

			if(List.class.isAssignableFrom(operand.getClass()))	{
				for (int i = 0; i < ((List) operand).size(); ++i) {
					if (i > 0) {
						builder.append(" ,");
					}

					boolean isStringValue = String.class.isAssignableFrom(((List) operand).get(i).getClass());

					if(isStringValue)	{
						builder.append("'");
					}

					builder.append(((List) operand).get(i));

					if(isStringValue)	{
						builder.append("'");
					}
				}
			} else if(QueryUtils.isPrimitiveType(operand.getClass()))	{
				if(operand.getClass().getName().startsWith("[L")  || String.class.isAssignableFrom(operand.getClass()))	{
					boolean isStringValue = String[].class.isAssignableFrom(operand.getClass());

					if(isStringValue){
						for (int i = 0; i < ((Object[]) operand).length; ++i) {
							if (i > 0) {
								builder.append(" ,");
							}

							builder.append("'");
							builder.append(((Object[]) operand)[i]);
							builder.append("'");
						}
					}else{
						builder.append("'");
						builder.append((Object) operand);
						builder.append("'");
					}
				} else {
					builder.append(operand);
				}
			}
		}

		return builder.toString();
	}

	private String getColumnName(Object[] args) {
		String columnName = "";

		if(args != null && args.length > 0){
			columnName = (String) args[0];
		}

		return columnName;
	}

}
