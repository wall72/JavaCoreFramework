package jcf.util.properties;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author mina
 * @since 2009/11/17
 * @Description  PropertyService interface
 * @deprecated bad practice. use explicit properties on services.
 */
@SuppressWarnings("rawtypes")
public interface PropertyService {




	Log LOGGER = LogFactory.getLog(PropertyService.class);
	/**
	 * boolean 타입의 프로퍼티 값 얻기
	 *
	 * @param name
	 *             프로퍼티키
	 * @return boolean 타입의 값
	 */
	boolean getBoolean(String name);

	/**
	 * boolean 타입의 프로퍼티 값 얻기(디폴트값을 입력받음)
	 *
	 * @param name
	 *            프로퍼티키
	 * @param def
	 *            디폴트 값
	 * @return boolean 타입의 값
	 */
	boolean getBoolean(String name, boolean def);

	/**
	 * double 타입의 프로퍼티 값 얻기
	 *
	 * @param name
	 *            프로퍼티키
	 * @return double 타입의 값
	 */
	double getDouble(String name);

	/**
	 * double 타입의 프로퍼티 값 얻기
	 *
	 * @param name
	 *            프로퍼티키
	 * @param def
	 *            디폴트 값
	 * @return double 타입의 값
	 */
	double getDouble(String name, double def);

	/**
	 * float 타입의 프로퍼티 값 얻기
	 *
	 * @param name
	 *            프로퍼티키
	 * @return Float 타입의 값
	 */
	float getFloat(String name);

	/**
	 * float 타입의 프로퍼티 값 얻기
	 *
	 * @param name
	 *            프로퍼티키
	 * @param def
	 *            디폴트 값
	 * @return float 타입의 값
	 */
	float getFloat(String name, float def);

	/**
	 * int 타입의 프로퍼티 값 얻기
	 *
	 * @param name
	 *            프로퍼티키
	 * @return int 타입의 값
	 */
	int getInt(String name);

	/**
	 * int 타입의 프로퍼티 값 얻기
	 *
	 * @param name
	 *            프로퍼티키
	 * @param def
	 *            디폴트 값
	 * @return int 타입의 값
	 */
	int getInt(String name, int def);

	/**
	 * 프로퍼티 키 목록 읽기
	 *
	 * @return Key를 위한 Iterator
	 */
	Iterator getKeys();

	/**
	 * prefix를 이용한 키 목록 읽기
	 *
	 * @param prefix
	 *             prefix
	 * @return prefix에 매칭되는 키목록
	 */
	Iterator getKeys(String prefix);

	/**
	 * long 타입의 프로퍼티 값 얻기
	 *
	 * @param name
	 *            프로퍼티키
	 * @return long 타입의 값
	 */
	long getLong(String name);

	/**
	 * long 타입의 프로퍼티 값 얻기
	 *
	 * @param name
	 *            프로퍼티키
	 * @param def
	 *            디폴트 값
	 * @return long 타입의 값
	 */
	long getLong(String name, long def);

	/**
	 * String 타입의 프로퍼티 값 얻기
	 *
	 * @param name
	 *            프로퍼티키
	 * @return String 타입의 값
	 */
	String getString(String name);

	/**
	 * String 타입의 프로퍼티 값 얻기
	 *
	 * @param name
	 *            프로퍼티키
	 * @param def
	 *            디폴트 값
	 * @return String 타입의 값
	 */
	String getString(String name, String def);

	/**
	 * String[] 타입의 프로퍼티 값 얻기
	 *
	 * @param name
	 *            프로퍼티키
	 * @return String[] 타입의 값
	 */
	String[] getStringArray(String name);

	/**
	 * Vector 타입의 프로퍼티 값 얻기
	 *
	 * @param name
	 *            프로퍼티키
	 * @return Vector 타입의 값
	 */
	Vector getVector(String name);

	/**
	 * Vector 타입의 프로퍼티 값 얻기
	 *
	 * @param name
	 *            프로퍼티키
	 * @param def
	 *            디폴트 값
	 * @return Vector 타입의 값
	 */
	Vector getVector(String name, Vector def);

	/**
	 * resource 변경시 refresh
	 */
	void refreshPropertyFiles() throws Exception ;

	/**
	 * 전체 키/값 쌍 얻기
	 *
	 * @return Collection 타입의 값
	 */
	Collection getAllKeyValue();


}
