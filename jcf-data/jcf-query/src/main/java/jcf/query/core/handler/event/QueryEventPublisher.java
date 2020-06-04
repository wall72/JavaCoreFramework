package jcf.query.core.handler.event;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

import jcf.query.util.QueryUtils;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 *
 * @author nolang
 *
 */
public class QueryEventPublisher implements ApplicationEventPublisherAware {

	private ApplicationEventPublisher applicationEventPublisher;

	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public void publishEvent(String statement, Object parameter, long startTime, long endTime, Exception exception) {
		if(applicationEventPublisher != null)	{
			System.out.println("Run event publisher..");
			applicationEventPublisher.publishEvent(new QueryEvent(getTraceId(),statement, getParameterString(parameter), startTime, endTime, exception));
		}
	}

	protected String getTraceId() {
		return UUID.randomUUID().toString();
	}

	@SuppressWarnings("unchecked")
	protected String getParameterString(Object parameter) {
		StringBuilder builder = new StringBuilder("Parameters - ");

		if(parameter != null)	{
			int index = 0;

			if (QueryUtils.isPrimitiveType(parameter.getClass())) {
				if(ClassUtils.isPrimitiveArray(parameter.getClass()) || ClassUtils.isPrimitiveWrapperArray(parameter.getClass()))	{
					Object[] array = (Object[]) parameter;

					for(int i=0;i<array.length;++i){
						if(i > 0){
							builder.append(",");
						}

						builder.append(array[i]);
					}
				} else {
					builder.append(parameter);
				}
			} else if (Map.class.isAssignableFrom(parameter.getClass())) {
				for(Map.Entry<String, Object> e : ((Map<String, Object>) parameter).entrySet())	{
					if(index ++ > 0)	{
						builder.append(",");
					}

					builder.append(e.getKey()).append(":").append(e.getValue());
				}
			} else {
				Field[] fields = parameter.getClass().getDeclaredFields();

				for (Field field : fields) {
					ReflectionUtils.makeAccessible(field);

					if(index ++ > 0)	{
						builder.append(",");
					}

					try {
						builder.append(field.getName()).append(":").append(field.get(parameter));
					} catch (Exception e) {
					}
				}
			}
		}

		return builder.toString();
	}

}
