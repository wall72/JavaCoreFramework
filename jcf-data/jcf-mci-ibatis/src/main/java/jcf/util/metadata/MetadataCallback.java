package jcf.util.metadata;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class MetadataCallback implements MethodInterceptor {

	private static final String SETTTER_RESULT_SET_METADATA_HOLDER = "setMetadata";
	private static final String GETTER_RESULT_SET_METADATA_HOLDER = "getMetadata";
	
	ResultSetMetadataHolder rsmdHolder;

	public ResultSetMetadataHolder getResultSetMetadataHolder() {
		return rsmdHolder;
	}

	public void setResultSetMetadataHolder(ResultSetMetadataHolder rsmdHolder) {
		this.rsmdHolder = rsmdHolder;
	}

	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		Object retValFromSuper = null;

		if (GETTER_RESULT_SET_METADATA_HOLDER.equals(method.getName())) {
			return this.getResultSetMetadataHolder();
		} else if (SETTTER_RESULT_SET_METADATA_HOLDER.equals(method.getName())) {
			this.setResultSetMetadataHolder((ResultSetMetadataHolder) args[0]);
			return null;
		} else {
			retValFromSuper = proxy.invokeSuper(obj, args);
			return retValFromSuper;
		}

	}
}
