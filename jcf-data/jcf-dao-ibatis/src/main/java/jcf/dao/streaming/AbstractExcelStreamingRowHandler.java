package jcf.dao.streaming;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import jcf.dao.DataStreamingException;
import jcf.dao.StreamingRowHandler;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractExcelStreamingRowHandler implements StreamingRowHandler {

	private static final String ENCODING = "UTF-8";

	private OutputStream outputStream;
	private int maxRowsPerSheet;
	private PrintWriter writer;
	private VelocityEngine velocityEngine;

	private String user;
	private SimpleDateFormat woorkBookDateFormat;
	private SimpleDateFormat dateFormat;
	
	/**
	 * 워크북 내의 워크시트에서의 행.
	 */
	private long rowNumber;
	private int worksheetNumber;

	private List<Map<String,Object>> columns;
	
	public AbstractExcelStreamingRowHandler(OutputStream outputStream, int maxRowsPerSheet, VelocityEngine velocityEngine, String user) {
		this.outputStream = outputStream;
		this.maxRowsPerSheet = maxRowsPerSheet;
		this.writer = new PrintWriter(outputStream);
		this.velocityEngine = velocityEngine;
		this.user = user;
		
		/*
		 * GMT
		 */
		woorkBookDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		woorkBookDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
		
		/*
		 * LOCAL (SYSTEM) TIME ZONE
		 */
		//1899-12-31T12:00:00.000
		//2009-08-20T00:00:00.000
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	}
	
	public AbstractExcelStreamingRowHandler(OutputStream outputStream, int maxRowsPerSheet, VelocityEngine velocityEngine, String user, int thresold) {
		this(outputStream, maxRowsPerSheet, velocityEngine, user);
		this.thresold = thresold;
	}

	public final void open() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("author", user);
		map.put("lastAuthor", user);
		map.put("created", woorkBookDateFormat.format(new Date()));
		VelocityEngineUtils.mergeTemplate(velocityEngine, "wookbookHeader.vm", ENCODING, map, writer);
		
		openWorkSheet();
	}
	
	public final void close() {
		closeWorkSheet();
		writer.println("</Workbook>");
		
//		try {
//			writer.close();
//			outputStream.close();
//			
//		} catch (IOException e) {
//			throw new DataStreamingException("error streaming contents", e);
//		}
	}

//	private List columnTypes = null;

	private long thresold = 100;
	
	@SuppressWarnings("unchecked")
	public final void handleRow(Object valueObject) {
		if (rowNumber + 2 > maxRowsPerSheet) {
			closeWorkSheet();
			openWorkSheet();
		}
		
		// java.util.Date 처리 - dateFormat
		
		
		Map<String, Object> row = (Map<String, Object>)valueObject;
		
		List<Map<String,Object>> columnList = new ArrayList<Map<String,Object>>();
		
		for (Object value : row.values()) {
			Map<String, Object> column = new HashMap<String,Object>();
			
			if (value == null) {
				column.put("type", "String");
				column.put("value", "");
				
			} else if (value instanceof Date) {
				Date date = (Date)value;
				
				column.put("type", "DateTime");
				column.put("value", dateFormat.format(date));
				
			} else if (value instanceof Number) {
				column.put("type", "Number");
				column.put("value", value);
				
			} else {
				column.put("type", "String");
				column.put("value", value);
			}
			
			columnList.add(column);
		}
		
		HashMap<String, Object> model = new HashMap<String, Object>();
		
		model.put("COLUMNS", columnList);
		
		VelocityEngineUtils.mergeTemplate(velocityEngine, "row.vm", model, writer);
		
//		writer.println("handling Row..." + rowNumber);
		
		rowNumber++;
		
		if (rowNumber % thresold  == 0) {
			try {
				writer.flush();
				outputStream.flush();
				
			} catch (IOException e) {
				throw new DataStreamingException("error streaming contents", e);
			}
		}	
	}
	
	private void openWorkSheet() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("sheetName", "sheet" + worksheetNumber);
		model.put("COLUMNS", getHeaderColumns());

		model.put("columnCount", columns.size());
		/*
		 * XXX:Fix me!
		 */
		model.put("rowCount", maxRowsPerSheet);
		
		
		
		VelocityEngineUtils.mergeTemplate(velocityEngine, "worksheetHeader.vm", ENCODING, model, writer);

		worksheetNumber++;
		rowNumber = 0;
	}
	
	private List<Map<String,Object>> getHeaderColumns() {
		if (columns == null) {
			columns = new ArrayList<Map<String, Object>>();

			String[] columnNames = buildColumnInfo(getCommaSeparatedColumnNames());
//			String[] columnTypes = buildColumnInfo(getCommaSeparatedTypeNames());
			
			
			for (int i = 0; i < columnNames.length; i++) {
				Map<String, Object> map = new HashMap<String,Object>();
				
				map.put("value", columnNames[i]);
				
				columns.add(map);
			}
		}
		return columns;
	}
	
	private String[] buildColumnInfo(String commaSeparatedColumnNames) {
		return StringUtils.tokenizeToStringArray(commaSeparatedColumnNames, ", ", true , true);
	}
	
	protected abstract String getCommaSeparatedColumnNames();
	
//	protected abstract String getCommaSeparatedTypeNames();

	private void closeWorkSheet() {
		Map<String, String> model = null; //new HashMap();
		VelocityEngineUtils.mergeTemplate(velocityEngine, "worksheetFooter.vm", ENCODING, model, writer);
	}

}
