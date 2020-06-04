package jcf.util.metadata;

import java.sql.ResultSetMetaData;

public interface MetadataAware {

	ResultSetMetaData getMetadata();
	void setMetadata(ResultSetMetaData resultSetMetaData);

}
