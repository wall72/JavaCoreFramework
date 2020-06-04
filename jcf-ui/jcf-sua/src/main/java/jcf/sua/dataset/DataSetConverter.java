package jcf.sua.dataset;

/**
 *
 * 특정 타입의 객체가 별도의 메세지 표준을 가지는 경우 확장하여 구현한다.
 *
 * @author nolang
 *
 */
public interface DataSetConverter {

	/**
	 *
	 * 입력된 객체가 데이터셋 컨버터에서 처리되어야 하는 타입인지 결정한다.
	 *
	 * @param object
	 * @return
	 */
	boolean support(Object object);

	/**
	 *
	 * 입력 객체를 데이터셋으로 변환한다.
	 *
	 * @param dataSet
	 * @param object
	 */
	void convert(DataSet dataSet, Object object);

}
