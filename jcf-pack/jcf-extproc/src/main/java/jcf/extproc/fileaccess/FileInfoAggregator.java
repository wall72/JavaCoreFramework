package jcf.extproc.fileaccess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileInfoAggregator {

	private int index;
	private List<FileInfo> list = new ArrayList<FileInfo>();
	
	public FileInfoAggregator(File pathname) {
		index = pathname.getAbsolutePath().length() + 1;
	}

	public void put(File pathname) {
		String relativePathName = pathname.getAbsolutePath().substring(index);
		list.add(new FileInfo(relativePathName, pathname.length()));
	}

	public Iterable<FileInfo> getFileList() {
		return list;
	}

}
