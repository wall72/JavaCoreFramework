package jcf.sua.ux.extJs.dataset;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import jcf.sua.dataset.DataSetColumn;
import jcf.sua.dataset.DataSetImpl;
import jcf.sua.dataset.DataSetRow;
import jcf.sua.dataset.converter.StringToDateConverter;
import jcf.sua.exception.MciException;

/**
 *
 * {@link DataSet}
 *
 * @author nolang
 *
 */
public final class ExtJsDataSet extends DataSetImpl {

	private static final Logger logger = LoggerFactory.getLogger(ExtJsDataSet.class);

	public ExtJsDataSet(String dataSetId) {
		super(dataSetId, null, null);
	}

	public ExtJsDataSet(String dataSetId, List<DataSetColumn> cols, List<DataSetRow> rows) {
		super(dataSetId, cols, rows);
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
			GenericConversionService conversionService = ConversionServiceFactory.createDefaultConversionService();
			conversionService.addConverter(new StringToDateConverter());
			conversionService.addConverterFactory(new ConverterFactory<String, Number>() {
				public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
					return new StringToNumberWithDefult<T>(targetType);
				}
				final class StringToNumberWithDefult<T extends Number> implements Converter<String, T> {

					private final Class<T> targetType;

					public StringToNumberWithDefult(Class<T> targetType) {
						this.targetType = targetType;
					}
					public T convert(String source) {
						if (source.equals("")) {
							return NumberUtils.parseNumber("0", this.targetType);
						}else{
							return NumberUtils.parseNumber(source, this.targetType);
						}
					}
				}
			});

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
}
