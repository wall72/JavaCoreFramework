package jcf.sua.dataset;

import java.util.ArrayList;
import java.util.List;

import jcf.data.GridData;
import jcf.data.RowStatus;
import jcf.data.RowStatusCallback;
import jcf.sua.exception.BeanConstraintViolationException;
import jcf.sua.mvc.validation.MciRequestValidator;

/**
*
* {@link GridData} 의 구현체
*
* @author nolang
*
*/
public class GridDataImpl<E> implements GridData<E> {

	private List<E> list;
	private List<E> orgList;
	private List<RowStatus> rowStatusList;

	/**
	 * Method description : Constructor
	 */
	public GridDataImpl(DataSet dataSet, Class<E> clazz, MciRequestValidator requestValidator, String filter) {
		this.list = new ArrayList<E>();
		this.orgList = new ArrayList<E>();
		this.rowStatusList = new ArrayList<RowStatus>();

		if (dataSet != null) {
			for (int i = 0; i < dataSet.getRowCount(); i++) {
				E bean = dataSet.getBean(clazz, i, filter);

				if(requestValidator != null)	{
					try {
						requestValidator.checkValidation(bean);
					} catch (BeanConstraintViolationException e) {
						throw new BeanConstraintViolationException("[JCF-SUA] RowNumber={" + i + "} " + e.getMessage(), e.getConstraintViolations());
					}
				}

				this.list.add(bean);
				this.orgList.add(dataSet.getOrgDataBean(clazz, i));

				RowStatus rowStatus = dataSet.getRowStatus(i);

				if (rowStatus == null) {
//					if (rowStatus == null) {
//						throw new RowStatusException("DataSet={" + dataSet.getId() + "}의 rowStatus가 누락 되었습니다.");
//					}
					rowStatus = RowStatus.NORMAL;
				}

				this.rowStatusList.add(dataSet.getRowStatus(i));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public E get(int rowIndex) {
		return list.get(rowIndex);
	}


	/**
	 * {@inheritDoc}
	 */
	public List<E> getList() {
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	public RowStatus getStatusOf(int rowIndex) {
		return rowStatusList.get(rowIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	public int size()	{
		return list == null ? 0 : list.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public void forEachRow(RowStatusCallback<E> callback) {
		for (int i = 0; i < list.size(); ++i) {
			switch(getStatusOf(i))	{
				case INSERT :
					callback.insert(get(i), i);
					break;
				case DELETE :
					callback.delete(get(i), i);
					break;
				case UPDATE :
					callback.update(get(i), orgList.get(i), i);
					break;
				default :
					callback.normal(get(i), i);
			}
		}
	}
}
