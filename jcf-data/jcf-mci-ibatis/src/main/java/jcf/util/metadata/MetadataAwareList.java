package jcf.util.metadata;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSetMetaData;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.SystemUtils;

public class MetadataAwareList implements MetadataAware, List {

	ResultSetMetaData resultsetMetadata;
	List list;
	
	public MetadataAwareList(List list) {
		this.list = list;
	}
	
	public ResultSetMetaData getMetadata() {
		return resultsetMetadata;
	}
	
	public void setMetadata(ResultSetMetaData resultsetMetadata) {
		this.resultsetMetadata = resultsetMetadata;
	}

	////////////////////////////////////
	// simple deligation codes below.
	////////////////////////////////////
	
	public boolean add(Object o) {

		return list.add(o);
	}

	public void add(int index, Object o) {
		list.add(index, o);
		
	}

	public boolean addAll(Collection c) {

		return list.addAll(c);
	}

	public boolean addAll(int index, Collection c) {

		return list.addAll(index, c);
	}

	public void clear() {
		
		list.clear();
	}

	public boolean contains(Object o) {

		return list.contains(o);
	}

	public boolean containsAll(Collection c) {

		return list.containsAll(c);
	}

	public Object get(int index) {

		return list.get(index);
	}

	public int indexOf(Object o) {

		return list.indexOf(o);
	}

	public boolean isEmpty() {

		return list.isEmpty();
	}

	public Iterator iterator() {

		return list.iterator();
	}

	public int lastIndexOf(Object o) {

		return list.lastIndexOf(o);
	}

	public ListIterator listIterator() {

		return list.listIterator();
	}

	public ListIterator listIterator(int index) {

		return list.listIterator(index);
	}

	public boolean remove(Object o) {

		return list.remove(o);
	}

	public Object remove(int index) {

		return list.remove(index);
	}

	public boolean removeAll(Collection c) {

		return list.removeAll(c);
	}

	public boolean retainAll(Collection c) {

		return list.retainAll(c);
	}

	public Object set(int index, Object o) {

		return list.set(index, o);
	}

	public int size() {

		return list.size();
	}

	public List subList(int fromIndex, int toIndex) {

		return list.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {

		return list.toArray();
	}

	public Object[] toArray(Object[] a) {

		return list.toArray(a);
	}
	
	public String toString(){
		if( list == null ) return "null";
		StringBuffer sb = new StringBuffer();
		String lineSaperator = (SystemUtils.LINE_SEPARATOR != null) ? SystemUtils.LINE_SEPARATOR : "\n";
		
		try {
			sb.append(lineSaperator);
			sb.append("MetadataAwareList's message.");
			
			Map map = null;
			for (int i = 0; i < list.size(); i++) {
				sb.append(lineSaperator);
				if( list.get(i) instanceof Map){
					map = (Map) list.get(i);
				}else{
					map = PropertyUtils.describe(list.get(i));
					map.remove("class");					
				}
				sb.append(map.toString());
			}
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}

}
