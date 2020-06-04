package jcf.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;



/**
 * @deprecated use apache commons-io's FileUtils.
 *
 */
public class FileUtil {
    /**
     * Logging output for this class.
     */
	private static final Logger logger = LoggerFactory
			.getLogger(FileUtil.class);
    public static final int BUFFER_SIZE = 4096;

    /**
         * Create folders on the file system representing the given path.
         * @param path The String representing the folders path
         * @return boolean <code>true</code> if folders were created successfully,
         * <code>false</code> otherwise
         */
    public static boolean makeDirectories(String path) {
        logger.debug(" Attempting to create folders for path: {}", path);

        if (StringUtils.isNotEmpty(path)) {
            File f = new File(path);

            if (!f.exists() && !f.isDirectory()) {
            	logger.debug(" Path does not exist on the file system. Creating folders...");

                return f.mkdirs();
            } else {
            	logger.debug(" Path already existing on the file system. Exiting...");
            }
        }

        return false;
    }

    /**
     * Remove the folders represented by the given path from the file system.
     * If flag is <code>true</code> folders are only removed if empty.
     * @param path The String representing the path to remove
     * @param flag The flag specifying if folders should be removed even if not
     * empty
     * @return boolean <code>true</code> if folders were removed successfully,
     * <code>false</code> otherwise
     * @todo implement flag
     */
    public static boolean removeDirectories(String path, boolean flag) {
    	logger.debug(" Attempting to remove folders for path: {}", path);

        if (StringUtils.isNotEmpty(path)) {
            File f = new File(path);

            if (f.exists() && f.isDirectory() && (f.listFiles().length < 1)) {
            	logger.debug(" Path exists on the file system and is empty. Resuming...");
                return f.delete();
            } else {
            	logger.debug(" Directory folders are not empty. Aborting remove action");
            }
        }

        return false;
    }

/**
 *
 * @param fromFile
 * @param toFile
 * @return
 * @update 2010/02/22
 */


    public static boolean moveFile(String fromFile, String realDir,  String toFile) {

    	makeDirectories(realDir);

        if (!copyFile(fromFile, toFile)) {
            return false;
        }

        if (!deleteFile(fromFile)) {
            return false;
        }

        return true;
    }

    /*---------------------------------------------------------------------------
       Copy File
    ---------------------------------------------------------------------------*/
    public static boolean copyFile(String fromFile, String toFile) {
        try {
            //retrieve the file data
            FileOutputStream fos = new FileOutputStream(toFile);
            FileInputStream fis = new FileInputStream(fromFile);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = 0;

            while ((bytesRead = fis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            //close the stream
            fis.close();
            fos.close();
        } catch (FileNotFoundException fnfe) {
            return false;
        } catch (Exception ioe) {
            return false;
        }

        return true;
    }

    /**
     * Return the File specified outPathName parameter using given byte[] parameter.
     * It don't validate the outPathName. So This method may throw the IOException.
     *
     * @param in
     * @param outPathName
     * @throws IOException
     */
	public static void copyFile(byte[] in, String outPathName) throws IOException {
		Assert.notNull(in, "No input byte array specified");
		File out = new File(outPathName);
		copyFile(in, out);
	}

	public static void copyFile(byte[] in, File out) throws IOException {
		Assert.notNull(in, "No input byte array specified");
		Assert.notNull(out, "No output File specified");
		ByteArrayInputStream inStream = new ByteArrayInputStream(in);
		OutputStream outStream = new BufferedOutputStream(new FileOutputStream(out));
		copyFile(inStream, outStream);
	}

	public static int copyFile(InputStream in, OutputStream out) throws IOException {
		Assert.notNull(in, "No InputStream specified");
		Assert.notNull(out, "No OutputStream specified");
		try {
			int byteCount = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
			}
			out.flush();
			return byteCount;
		}
		finally {
			try {
				in.close();
			}
			catch (IOException ex) {
				logger.warn("Could not close InputStream", ex);
			}
			try {
				out.close();
			}
			catch (IOException ex) {
				logger.warn("Could not close OutputStream", ex);
			}
		}
	}

	public static byte[] getFileToByteArray(String fullFilePath) {
		Assert.notNull(fullFilePath, "No input byte array specified");

		FileInputStream fis = null;
		byte[] out = null;
		try {
			fis = new FileInputStream(fullFilePath);
			out = new byte[fis.available()];

			fis.read(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return out;
	}


    /*---------------------------------------------------------------------------
       Delete File
    ---------------------------------------------------------------------------*/
    public static boolean deleteFile(String fullFilePath) {
        File file;

        try {
            file = new File(fullFilePath);
        } catch (NullPointerException e) {
            return false;
        }

        try {
            if (file.exists()) {
                file.delete();
            }
        } catch (SecurityException e) {
            logger.error("Security Exception when trying to delete file {}",
                      fullFilePath);

            return false;
        }

        return true;
    }



//    public static void main(String[] args){
//    	String file = "C:/hometax_log.txt";
//
//    	byte[] b = getFileToByteArray(file);
//
//    	System.out.println("OK");
//    }

}
