package jcf.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <a href="http://www.unicode.org/faq/utf_bom.html#BOM">UTF BOM</a> finder
 * 
 * main method accepts file or directory name.
 * 
 * BOM - trouble maker with SAX parsers or webservers.
 * 
 * @author setq
 *
 */
public class FindBom {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(FindBom.class);
	
	private static File rootDir = null;
	
	public static void main(String[] args) {

		FindBom findBOM = new FindBom();

		if (args.length < 1) {
			logger.error("please specify a file name.");
			return;
		}
		
		for (int i = 0; i < args.length; i++) {
			File file = new File(args[i]);
			if (file.isDirectory()) {
				rootDir = file;
			} else {
				rootDir = file.getParentFile(); 
			}
			findBOM.execute(file);
		}
	}

	private static final byte[][] targetByteArray = {
			{ 0x00, 0x00, 0x7E-0x80, 0x7F-0x80 }, // "UTF-32, big-endian"
			{ 0x7F-0x80, 0x7E-0x80, 0x00, 0x00 }, // "UTF-32, little-endian"
			{ 0x7E-0x80, 0x7F-0x80 }, // "UTF-16, big-endian"
			{ 0x7F-0x80, 0x7E-0x80 }, // "UTF-16, little-endian"
			{ 0x6F-0x80, 0x3B-0x80, 0x3F-0x80 } // "UTF-8"
	};

	private static final String[] labelString = { "UTF-32, big-endian",
			"UTF-32, little-endian", "UTF-16, big-endian",
			"UTF-16, little-endian", "UTF-8" };


	private void findInDirectories(File file) {
//		logger.info("direcotry : " + file.getName());
		File[] fileList = file.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				
				return pathname.isDirectory() ||
				!(
						pathname.getName().toLowerCase().endsWith(".jar") || 
						pathname.getName().toLowerCase().endsWith(".lib") ||
						pathname.getName().toLowerCase().endsWith(".class")
				);
			}			
		});	
		
		for (int i = 0; i < fileList.length; i++) {
			execute(fileList[i]);
		}
	}
	
	private void execute(File file) {

		if (file.isDirectory()) {
			findInDirectories(file);
			return;
		}
		
		byte[] readBuf = new byte[4];
		int readBytes = 0;
		InputStream is;
		
		try {
			is = new FileInputStream(file);
			readBytes = is.read(readBuf, 0, 4);
			is.close();
			
		} catch (FileNotFoundException e) {
			logger.warn("file not found : {}", relativize(file));
			return;
			
		} catch (IOException e) {
			logger.warn("error reading file : {}", relativize(file), e);
			return;
		}

		int matchRow = findMatch(readBuf, readBytes);

		if (matchRow < 0) return;
		
		logger.info("{}\t{}", relativize(file), labelString[matchRow]);
	}

	private URI relativize(File file) {
		return rootDir.toURI().relativize(file.toURI());
	}

	private int findMatch(byte[] readBuf, int readBytes) {
		for (int i = 0; i < targetByteArray.length; i++) {
//			logger.debug("[" + i + "] read " + dumpBytes(readBuf, readBytes));
			if (byteCompare(targetByteArray[i], targetByteArray[i].length,
					readBuf, readBytes))
				return i;
		}

		return -1;
	}

//	private String dumpBytes(byte[] readBuf, int readBytes) {
//		String x = "";
//		for (int i = 0; i < readBytes; i++) {
//			int intValue = new Byte(readBuf[i]).intValue();
//			if (intValue < 0) intValue += 0x100;
//			x += Integer.toHexString(intValue);
//			x += " ";
//		}
//		return x + " : " + readBytes + " bytes";
//	}

	private boolean byteCompare(byte[] targetBuf, int targetBytes,
			byte[] readBuf, int readBytes) {
		
		if (targetBytes > readBytes) {
			return false;
		}
		
		for (int i = 0; i < targetBytes; i++) {
//			logger.debug("" + targetBuf[i] + " vs " + readBuf[i]);
			if (targetBuf[i] != readBuf[i])
				return false;
		}

		return true;
	}
}
