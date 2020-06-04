package jcf.sua.ux.excel.core.worksheet;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcf.sua.ux.excel.core.stylesheet.DefaultStyleSheet;
import jcf.sua.ux.excel.core.stylesheet.style.DefaultStyle;

import org.springframework.beans.PropertyAccessorFactory;

/**
 *
 * @author nolang
 *
 */
public class DefaultWorkSheet extends AbstractGridSheet {
	private static Map<String, String> primitives = null;

	private List<?> xlsDataList = null;

	private String[] title = null;
	private String[] style = null;

	public DefaultWorkSheet(List<?> xlsDataList, Class<?> clz) {
		this.xlsDataList = xlsDataList;

		setStyleSheet(new DefaultStyleSheet());
		setDefaultType();
		setDefaultElement(clz);
	}

	private synchronized void setDefaultType() {
		if (primitives == null) {
			primitives = new HashMap<String, String>();

			primitives.put(Integer.TYPE.getName(), DefaultStyle.STYLE_NUMBER);
			primitives.put(Double.TYPE.getName(), DefaultStyle.STYLE_NUMBER);
			primitives.put(Float.TYPE.getName(), DefaultStyle.STYLE_NUMBER);
			primitives.put(Boolean.TYPE.getName(), DefaultStyle.STYLE_STRING_CENTER);
			primitives.put(String.class.getName(), DefaultStyle.STYLE_STRING_CENTER);
		}
	}

	private void setDefaultElement(Class<?> clz) {
		Field[] declaredFields = clz.getDeclaredFields();

		String[] workTitle = new String[declaredFields.length];
		String[] workStyle = new String[declaredFields.length];

		int columnCount = 0;

		for (int i = 0; i < declaredFields.length; ++i) {
			Field field = declaredFields[i];

			if (Modifier.isTransient(field.getModifiers())) {
				continue;
			}

			if (field.getType().isPrimitive() || primitives.containsKey(field.getType().getName())) {
				String style = DefaultStyle.STYLE_STRING_LEFT;

				if (field.getType().isPrimitive()) {
					style = DefaultStyle.STYLE_NUMBER;

				} else {
					style = (String) primitives.get(field.getType().getName());
				}

				workTitle[columnCount] = field.getName();
				workStyle[columnCount++] = style;
			}
		}

		this.title = new String[columnCount];
		this.style = new String[columnCount];

		System.arraycopy(workTitle, 0, title, 0, columnCount);
		System.arraycopy(workStyle, 0, style, 0, columnCount);
	}

	@Override
	public Object getValueAt(int rowIndex, int colIndex) {
		if (rowIndex > getRowCount() || colIndex > getColumnCount()) {
			throw new IllegalArgumentException();
		}

		return get(title[colIndex], xlsDataList.get(rowIndex));
	}

	@Override
	public String getStyleIdAt(int rowIndex, int colIndex) {
		return style[colIndex];
	}

	@Override
	public long getRowCount() {
		return xlsDataList.size();
	}

	@Override
	public int getColumnCount() {
		return this.title.length;
	}

	@Override
	public String getTitle(int column) {
		return title[column];
	}

	private String get(String fieldName, Object obj) {
		Object result = "";

		try {
			result = PropertyAccessorFactory.forDirectFieldAccess(obj).getPropertyValue(fieldName);
		} catch (Exception e) {
		}

		return result.toString();
	}
}
