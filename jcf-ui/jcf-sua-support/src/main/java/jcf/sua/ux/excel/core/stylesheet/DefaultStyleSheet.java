package jcf.sua.ux.excel.core.stylesheet;

import jcf.sua.ux.excel.core.stylesheet.style.DefaultStyle;
import jcf.sua.ux.excel.core.stylesheet.style.Style;

/**
 *
 * @author nolang
 *
 */
public class DefaultStyleSheet extends AbstractStyleSheet {
	public String getStyleIdAt(int rowIndex, int colIndex) {
		return DefaultStyle.STYLE_STRING_LEFT;
	}

	public String getTypeAt(int rowIndex, int colIndex) {
		return DefaultStyle.TYPE_STRING;
	}

	@Override
	public String getNamedCellAt(int rowIndex, int colIndex) {
		return "";
	}

	public Style[] getCustomStyles() {
		return new Style[] { new DefaultStyle() };
	}
}
