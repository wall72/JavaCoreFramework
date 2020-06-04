package jcf.sua.dataset;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jcf.data.RowStatus;
import jcf.sua.dataset.converter.StringToBigDecimalConverter;
import jcf.sua.dataset.converter.StringToDateConverter;
import jcf.sua.dataset.converter.StringToIntConverter;
import jcf.sua.exception.MciException;
import jcf.sua.validation.ViolationChecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.util.StringUtils;

/**
*
* {@link DataSet} 의 구현체
*
* @author nolang
*
*/
@SuppressWarnings("unchecked")
public class DataSetImpl implements DataSet {

	private static final Logger logger = LoggerFactory.getLogger(DataSetImpl.class);

	protected String id;
	protected List<DataSetColumn> columns;
	protected List<DataSetRow> rows;
	private GenericConversionService conversionService = ConversionServiceFactory.createDefaultConversionService();

	public void setColumns(List<DataSetColumn> columns) {
		this.columns = columns;
	}

	public DataSetImpl() {
	}

	public DataSetImpl(String id, List<DataSetColumn> cols, List<DataSetRow> rows) {
		this.id = id;
		this.columns = cols;
		this.rows = rows;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	public final int getColumnCount() {
		return columns == null ? 0 : columns.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public final int getRowCount() {
		return rows == null ? 0 : rows.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public final RowStatus getRowStatus(int row) {
		if (row >= getRowCount()) {
			throw new ArrayIndexOutOfBoundsException(row);
		}

		String rowStatus = rows.get(row).getRowStatus();

		if (rowStatus == null) {
			return null;
		}

		return RowStatus.valueOf(rowStatus.toUpperCase());
	}

	/**
	 * {@inheritDoc}
	 */
	public final DataSetRow getDataSetRow(int row) {
		if (row >= getRowCount()) {
			throw new ArrayIndexOutOfBoundsException(row);
		}

		return rows.get(row);
	}

	/**
	 * {@inheritDoc}
	 */
	public final DataSetColumn getDataSetColumn(int col) {
		if (col >= getColumnCount()) {
			throw new ArrayIndexOutOfBoundsException(col);
		}

		return columns.get(col);
	}

	/**
	 * {@inheritDoc}
	 */
	public final <E> E getOrgDataBean(Class<E> clazz, int row) {
		DataSetRow dataSetRow = null;

		if (getRowStatus(row) == RowStatus.UPDATE) {
			dataSetRow = getDataSetRow(row).getOrgDataSetRow();
		}

		if (dataSetRow == null) {
			return null;
		}

		return populateBeanInstance(clazz, dataSetRow, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public final <E> E getBean(Class<E> clazz, int row) {
		return getBean(clazz, row, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public final <E> E getBean(Class<E> clazz, int row, String filter) {
		return populateBeanInstance(clazz, getDataSetRow(row), filter);
	}

	protected <E> E populateBeanInstance(Class<E> clazz, DataSetRow dataSetRow, String filter) {
		final E bean = newInstance(clazz);

		int columnCount = getColumnCount();

		if (Map.class.isAssignableFrom(clazz)) {
			for (int col = 0; col < columnCount; col++) {
				String columnName = getDataColunmName(col);

				if(StringUtils.hasText(filter))	{
					logger.debug("[JCF-SUA] 사용자 요청에 의한 유효성 체크. Expression={}", filter);

					if(!isSelectedProperty(filter, columnName))	{
						continue;
					}

					if(isRequiredProperty(filter, columnName))	{
						Object value = dataSetRow.get(columnName);

						if(value == null || (value instanceof String && !StringUtils.hasText((String) value)))	{
							throw new MciException("Column[" + columnName + "] 는 요청["+filter+"]에 의해 필수값으로 설정되었습니다.");
						}
					}
				}

				((Map<String, Object>) bean).put(columnName, dataSetRow.get(columnName));
			}
		} else {
			ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(bean);
			conversionService.addConverter(new StringToDateConverter());
			conversionService.addConverter(new StringToIntConverter());
			conversionService.addConverter(new StringToBigDecimalConverter());
			propertyAccessor.setConversionService(conversionService);

			for (int col = 0; col < columnCount; col++) {
				String columnName = getDataColunmName(col);

				if(StringUtils.hasText(filter))	{
					logger.debug("[JCF-SUA] 사용자 요청에 의한 유효성 체크. Expression={}", filter);

					if(!isSelectedProperty(filter, columnName))	{
						continue;
					}

					if(isRequiredProperty(filter, columnName))	{
						Object value = dataSetRow.get(columnName);

						if(value == null || (value instanceof String && !StringUtils.hasText((String) value)))	{
							throw new MciException("Column[" + columnName + "] 는 요청["+filter+"]에 의해 필수값으로 설정되었습니다.");
						}
					}
				}

				try {
					propertyAccessor.setPropertyValue(getDataColunmName(col), dataSetRow.get(getDataColunmName(col)));
				} catch (Exception e) {
					logger.debug("[JCF-SUA] DataSetImpl - getBean() : Class={" + clazz.getName()+ "}, field:" + getDataColunmName(col) + " 가 존재하지 않습니다." );
				}
			}

//			checkBeanValidation(bean);
		}

		return bean;
	}

	protected <E> void checkBeanValidation(final E bean) {
		try {
			ViolationChecker<E>	violationChecker = (ViolationChecker<E>) Class.forName("jcf.sua.support.validation.ModelPropertyViolationChecker").newInstance();
			violationChecker.checkViolations(bean);
		} catch (InstantiationException e) {
			logger.info("jcf.sua.support.validation.ModelPropertyViolationChecker is not instantiated");
		} catch (IllegalAccessException e) {
			logger.info("jcf.sua.support.validation.ModelPropertyViolationChecker is Illegal");
		} catch (ClassNotFoundException e) {
			logger.info("jcf.sua.support.validation.ModelPropertyViolationChecker is not existed");
		}
	}

	protected final <E> E newInstance(Class<E> clazz) {
		E bean;

		try {
			bean = clazz.newInstance();
		} catch (InstantiationException e) {
			throw new MciException("InstantiationException", e);
		} catch (IllegalAccessException e) {
			throw new MciException("IllegalAccessException", e);
		}

		return bean;
	}

	protected final String getDataColunmName(int index) {
		return columns.get(index).getColumnName();
	}

	protected final Object getDataSetValue(int rowIndex, int colIndex) {
		return rows.get(rowIndex).get(getDataColunmName(colIndex));
	}

	protected void setDataColumn(Object object, Class<?> dataType) {
		if (columns != null && columns.size() > 0) {
			return;
		}

		columns = new ArrayList<DataSetColumn>();

		if (dataType != null && !Map.class.isAssignableFrom(dataType)) {
			Field[] fields = dataType.getDeclaredFields();

			for (Field field : fields) {
				columns.add(new DataSetColumn(field.getName(), field.getType()));
			}
		} else {
			if (object != null) {
				if (Map.class.isAssignableFrom(object.getClass())) {
					Iterator<String> it = ((Map<String, ?>) object).keySet().iterator();

					while (it.hasNext()) {
						String columnName = it.next();
						Class<?> columnType = null;

						if (((Map<String, ?>) object).get(columnName) == null) {
							columnType = String.class;
						} else {
							columnType = ((Map<String, ?>) object).get(columnName).getClass();
						}

						columns.add(new DataSetColumn(columnName, columnType));
					}
				} else {
					Field[] fields = object.getClass().getDeclaredFields();

					for (Field field : fields) {
						columns.add(new DataSetColumn(field.getName(), field.getType()));
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addRowBean(Object bean, Class<?> clazz) {
		setDataColumn(bean, clazz);

		if (rows == null) {
			rows = new ArrayList<DataSetRow>();
		}

		if (bean != null) {
			rows.add(newDataSetRowFromBean(bean));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addRowMap(Map<String, ?> map, Class<?> clazz) {
		setDataColumn(map, clazz);

		if (rows == null) {
			rows = new ArrayList<DataSetRow>();
		}

		if (map != null) {
			rows.add(newDataSetRowFromMap(map));
		}
	}

	protected DataSetRow newDataSetRowFromBean(Object bean) {
		DataSetRow row = new DataSetRowImpl();

		if (bean != null) {
			ConfigurablePropertyAccessor propertyAccessor = PropertyAccessorFactory.forDirectFieldAccess(bean);
			propertyAccessor.setConversionService(conversionService);
			conversionService.addConverter(new StringToDateConverter());
			conversionService.addConverter(new StringToIntConverter());
			conversionService.addConverter(new StringToBigDecimalConverter());

			for (int i = 0; i < this.getColumnCount(); i++) {
				row.add(getDataColunmName(i),
						propertyAccessor.getPropertyValue(getDataColunmName(i)));
			}
		}

		return row;
	}

	protected DataSetRow newDataSetRowFromMap(Map<String, ?> map) {
		DataSetRow row = new DataSetRowImpl();

		if(map != null){
			for (int i = 0; i < this.getColumnCount(); i++) {
				row.add(getDataColunmName(i), map.get(getDataColunmName(i)));
			}
		}

		return row;
	}

	protected boolean isSelectedProperty(String expr, String property)	{
		boolean result = true;

		if(expr != null)	{
			result = expr.contains(property);
		}

		return result;
	}

	protected boolean isRequiredProperty(String expr, String property)	{
		return expr.contains(String.format("%s+", property));
	}
}