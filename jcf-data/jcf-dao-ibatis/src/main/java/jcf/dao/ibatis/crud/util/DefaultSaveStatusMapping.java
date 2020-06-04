package jcf.dao.ibatis.crud.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jcf.dao.ibatis.crud.RowStatus;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings({"unchecked", "rawtypes"})
public class DefaultSaveStatusMapping implements SaveStatusMapping{

	private static Log LOG = LogFactory.getLog(DefaultSaveStatusMapping.class);

	private String statusPropertyName =  "rowStatus";

	private String insertStatus = RowStatus.DEFAULT_ROW_STATUS_INSERT; // "INSERT"

	private String updateStatus = RowStatus.DEFAULT_ROW_STATUS_UPDATE; // "UPDATE"

	private String deleteStatus = RowStatus.DEFAULT_ROW_STATUS_DELETE; // "DELETE"

	private Map statusAndStatementIdMapping;

	public DefaultSaveStatusMapping(){}


	/**
	 *
	 *
	 * @param insertMapping
	 * @param updateMapping
	 * @param deleteMapping
	 */
	public DefaultSaveStatusMapping(String insertMapping,
								 String updateMapping,
								 String deleteMapping){
		this(RowStatus.DEFAULT_ROWSTATUS_PROPERTY_NAME, insertMapping, updateMapping, deleteMapping);
	}

	/**
	 *
	 *
	 * @param statusPropertyName
	 * @param insertMapping
	 * @param updateMapping
	 * @param deleteMapping
	 */
	public DefaultSaveStatusMapping(String statusPropertyName,
								 String insertMapping,
								 String updateMapping,
								 String deleteMapping){
		this.statusPropertyName = statusPropertyName;

		this.statusAndStatementIdMapping = new HashMap();
		setInsertMapping(insertMapping);
		setUpdateMapping(updateMapping);
		setDeleteMapping(deleteMapping);

		if( LOG.isDebugEnabled() ) LOG.debug( "SaveStatusMapping is set like following : " + getStatusInfo());
	}


	public DefaultSaveStatusMapping(String statusWildcardQueryMapping) {
		int wildcardIndex = statusWildcardQueryMapping.indexOf("*");
		if( wildcardIndex == -1){
			throw new RuntimeException("Error occurred in Constructor, due to argument has no wildcard character.");
		}

		this.statusPropertyName = RowStatus.DEFAULT_ROWSTATUS_PROPERTY_NAME;

		this.statusAndStatementIdMapping = new HashMap();
		setInsertMapping(statusWildcardQueryMapping.replaceFirst("\\*", "insert"));
		setUpdateMapping(statusWildcardQueryMapping.replaceFirst("\\*", "update"));
		setDeleteMapping(statusWildcardQueryMapping.replaceFirst("\\*", "delete"));

		if( LOG.isDebugEnabled() ) LOG.debug( "SaveStatusMapping is set like following : " + getStatusInfo());
	}


	private void setDeleteMapping(String deleteMapping) {
		if( StringUtils.isNotBlank(deleteMapping)) {
			if( deleteMapping.indexOf(":") == -1 ){
				this.statusAndStatementIdMapping.put(this.deleteStatus, deleteMapping);
			}
			else{
				this.deleteStatus = deleteMapping.substring(0, deleteMapping.indexOf(":"));
				this.statusAndStatementIdMapping.put(this.deleteStatus, deleteMapping.substring(deleteMapping.indexOf(":")+1));
			}
		}
	}

	private void setUpdateMapping(String updateMapping) {
		if( StringUtils.isNotBlank(updateMapping)) {
			if( updateMapping.indexOf(":") == -1 ){
				this.statusAndStatementIdMapping.put(this.updateStatus, updateMapping);
			}
			else{
				this.updateStatus = updateMapping.substring(0, updateMapping.indexOf(":"));
				this.statusAndStatementIdMapping.put(this.updateStatus, updateMapping.substring(updateMapping.indexOf(":")+1));
			}
		}
	}

	private void setInsertMapping(String insertMapping) {
		if( StringUtils.isNotBlank(insertMapping)) {
			if( insertMapping.indexOf(":") == -1 ){
				this.statusAndStatementIdMapping.put(this.insertStatus, insertMapping);
			}
			else{
				this.insertStatus = insertMapping.substring(0, insertMapping.indexOf(":"));
				this.statusAndStatementIdMapping.put(this.insertStatus, insertMapping.substring(insertMapping.indexOf(":")+1));
			}
		}
	}

	public String getStatusMappedSqlId(String status) {
		String mappedSqlId = (String) statusAndStatementIdMapping.get(status);
		if( StringUtils.isBlank(mappedSqlId) ) throw new RuntimeException("There is no statement id mapped to given status '" + status + "'");
		return mappedSqlId;
	}

	public String getStatusPropertyName() {
		return statusPropertyName;
	}

	public boolean isDelete(String status) {
		return deleteStatus.equalsIgnoreCase(status);
	}

	public boolean isInsert(String status) {
		return insertStatus.equalsIgnoreCase(status);
	}

	public boolean isUpdate(String status) {
		return updateStatus.equalsIgnoreCase(status);
	}

	public void setStatusPropertyName(String statusPropertyName) {
		this.statusPropertyName = statusPropertyName;
	}

	public Map getStatusAndStatementIdMapping() {
		return statusAndStatementIdMapping;
	}

	public String getStatusInfo() {
		Iterator entries = statusAndStatementIdMapping.entrySet().iterator();
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		while( entries.hasNext()){
			Entry entry = (Entry) entries.next();
			sb.append(entry.getKey()).append(":").append(entry.getValue());
			if( entries.hasNext() ) sb.append(",");
		}
		return sb.append("]").toString();
	}

}
