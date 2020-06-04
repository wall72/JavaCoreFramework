package jcf.sua.ux.excel.core.stylesheet.style;


/**
 *
 * @author nolang
 *
 */
public class DefaultStyle implements Style {
	public static final String STYLE_CAPTION = "DEFAULT_STRING_CAPTION";

	public static final String STYLE_HEADER = "DEFAULT_HEADER_TITLE";

	public static final String STYLE_STRING_LEFT = "DEFAULT_STRING_LEFT";

	public static final String STYLE_STRING_CENTER = "DEFAULT_STRING_CENTER";

	public static final String STYLE_STRING_RIGHT = "DEFAULT_STRING_RIGHT";

	public static final String STYLE_NUMBER = "DEFAULT_NUMERIC";

	public static final String TYPE_STRING = "String";

	public static final String TYPE_NUMBER = "Number";

	public final String getStyle() {
		StringBuffer buf = new StringBuffer();

		buf.append("<Style ss:ID=\"Default\" ss:Name=\"Normal\">\n");
		buf.append("<Alignment ss:Vertical=\"Center\"/>\n");
		buf.append("<Borders/>\n");
		buf.append("<Font x:Family=\"Modern\" ss:Size=\"10\"/>\n");
		buf.append("<Interior/>\n");
		buf.append("<NumberFormat/>\n");
		buf.append("<Protection/>\n");
		buf.append("</Style>\n");
		buf.append("<Style ss:ID=\"s20\">\n");
		buf.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n");
		buf.append("<Borders>\n");
		buf.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("</Borders>\n");
		buf.append("<Font ss:FontName=\"Tahoma\" x:Family=\"Roman\" ss:Size=\"10\" ss:Color=\"#FF0000\"/>\n");
		buf.append("</Style>\n");
		buf.append("<Style ss:ID=\"").append(STYLE_CAPTION).append("\">\n");
		buf.append("<Alignment ss:Horizontal=\"Left\" ss:Vertical=\"Center\"/>\n");
		buf.append("<Borders/>\n");
		buf.append("<Font ss:FontName=\"Tahoma\" x:Family=\"Roman\" ss:Size=\"15\"/>\n");
		buf.append("</Style>\n");
		buf.append("<Style ss:ID=\"").append(STYLE_HEADER).append("\">\n");
		buf.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n");
		buf.append("<Borders>\n");
		buf.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("</Borders>\n");
		buf.append("<Font ss:FontName=\"Tahoma\" x:Family=\"Roman\" ss:Size=\"10\"/>\n");
		buf.append("<Interior ss:Color=\"#99CCFF\" ss:Pattern=\"Solid\"/>\n");
		buf.append("</Style>\n");
		buf.append("<Style ss:ID=\"").append(STYLE_STRING_LEFT).append("\">\n");
		buf.append("<Alignment ss:Horizontal=\"Left\" ss:Vertical=\"Center\"/>\n");
		buf.append("<Borders>\n");
		buf.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("</Borders>\n");
		buf.append("<Font ss:FontName=\"Tahoma\" x:Family=\"Roman\" ss:Size=\"10\"/>\n");
		buf.append("</Style>\n");
		buf.append("<Style ss:ID=\"").append(STYLE_STRING_CENTER).append("\">\n");
		buf.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n");
		buf.append("<Borders>\n");
		buf.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("</Borders>\n");
		buf.append("<Font ss:FontName=\"Tahoma\" x:Family=\"Roman\" ss:Size=\"10\"/>\n");
		buf.append("</Style>\n");
		buf.append("<Style ss:ID=\"").append(STYLE_STRING_RIGHT).append("\">\n");
		buf.append("<Alignment ss:Horizontal=\"Right\" ss:Vertical=\"Center\"/>\n");
		buf.append("<Borders>\n");
		buf.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("</Borders>\n");
		buf.append("<Font ss:FontName=\"Tahoma\" x:Family=\"Roman\" ss:Size=\"10\"/>\n");
		buf.append("</Style>\n");
		buf.append("<Style ss:ID=\"").append(STYLE_NUMBER).append("\">\n");
		buf.append("<Alignment ss:Horizontal=\"Right\" ss:Vertical=\"Center\"/>\n");
		buf.append("<Borders>\n");
		buf.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"1\" ss:Color=\"#000000\"/>\n");
		buf.append("</Borders>\n");
		buf.append("<NumberFormat ss:Format=\"#,##0_ \"/>\n");
		buf.append("<Font ss:FontName=\"Tahoma\" x:Family=\"Roman\" ss:Size=\"10\"/>\n");
		buf.append("</Style>\n");

		return buf.toString();
	}
}
