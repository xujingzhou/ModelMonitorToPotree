/**   
* @Title: SingleInstanceChecker
* @Package common 
* @Description: 
* @author 徐景周(Johnny Xu,xujingzhou2016@gmail.com)  
* @date 2017/5/6 
* @version V1.0   
*/
package com.scientree.common;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import org.apache.log4j.Logger;

public class SingleInstanceChecker 
{
	private static Logger errorLogger = Logger.getLogger("errorfile");
		
	public static boolean lockInstance(final String lockFile) 
	{
	    try {
	        final File file = new File(lockFile);
	        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
	        final FileLock fileLock = randomAccessFile.getChannel().tryLock();
	        if (fileLock != null) 
	        {
	            Runtime.getRuntime().addShutdownHook(new Thread() 
	            {
	                public void run() 
	                {
	                    try 
	                    {
	                        fileLock.release();
	                        randomAccessFile.close();
	                        file.delete();
	                    } 
	                    catch (Exception e) 
	                    {
	                    	errorLogger.error("Unable to remove lock file: " + lockFile, e);
	                    }
	                }
	            });
	            
	            return true;
	        }
	    } 
	    catch (Exception e) 
	    {
	    	errorLogger.error("Unable to create and/or lock file: " + lockFile, e);
	    }
	    
	    return false;
	}
    
}
