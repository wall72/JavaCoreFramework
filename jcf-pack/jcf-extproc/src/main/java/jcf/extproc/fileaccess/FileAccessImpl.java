package jcf.extproc.fileaccess;

import java.io.File;
import java.io.FileFilter;

import jcf.extproc.config.ExtProcConstant;
import jcf.extproc.execution.ExternalProcessRepository;
import jcf.extproc.execution.FileBasedExternalProcessRepository;
import jcf.extproc.process.JobInstanceInfo;

public class FileAccessImpl implements FileAccess {

	ExternalProcessRepository repository;

	public FileAccessImpl(File baseDirectory) {
		repository = new FileBasedExternalProcessRepository(baseDirectory);
	}

	public File get(JobInstanceInfo instanceInfo, String fileName) {
		checkIfSecureFileName(fileName);
		return new File(getJobSpecificWorkDirectory(instanceInfo), fileName);
	}

	private void checkIfSecureFileName(String fileName) {
		if (fileName == null) {
			throw new NullPointerException("fileName is null");
		}
		if (fileName.contains("..")) {
			throw new IllegalArgumentException("invalid fileName : " + fileName);
		}
	}

	/**
	 * Job에서 별도로 workDirectory를 지정하여 실행한 경우 JobInstance 이력에 기록되어 이를 사용하고,
	 * 지정되어 실행되지 않은 JobInstance의 경우에는 repository 기본 workDirectory가 이 JobInstance의 workDirectory이다.
	 *  
	 * @param instanceInfo
	 * @return
	 */
	private File getJobSpecificWorkDirectory(JobInstanceInfo instanceInfo) {
		String workDirectory = instanceInfo.getWorkDirectory();
		
		if (workDirectory == null || workDirectory.length() == 0) {
			return getDefaultWorkDirectory(instanceInfo);
		}
			
		return new File(workDirectory);
	}

	private File getDefaultWorkDirectory(JobInstanceInfo instanceInfo) {
		return repository.getInstanceDirectory(instanceInfo);
	}
	
	public Iterable<FileInfo> list(JobInstanceInfo instanceInfo) {
		File pathname = getJobSpecificWorkDirectory(instanceInfo);
		
		FileInfoAggregator fileInfoManager = new FileInfoAggregator(pathname);
		lsResursively(fileInfoManager , pathname);
		
		return fileInfoManager.getFileList();
	}

	private void lsResursively(final FileInfoAggregator fileInfoManager, File pathname) {
		pathname.listFiles(new FileFilter() {
			
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					lsResursively(fileInfoManager, pathname);
					
				} else {
					fileInfoManager.put(pathname);
				}
				
				return false;
			}
		});
	}

	public File getLogFile(JobInstanceInfo instanceInfo) {
		return new File(getDefaultWorkDirectory(instanceInfo), ExtProcConstant.LOGFILE_NAME);
	}
	
}
