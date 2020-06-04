package jcf.extproc.fileaccess;

import java.io.Serializable;


public class FileInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	String name;
	long length;
	
	public FileInfo(String name, long length) {
		this.name = name;
		this.length = length;
	}
	public String getName() {
		return name;
	}
	public long getLength() {
		return length;
	}

}
