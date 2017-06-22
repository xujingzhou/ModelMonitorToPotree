/**   
* @Title: FileListener
* @Package biz 
* @Description:  
* @author 徐景周(Johnny Xu,xujingzhou2016@gmail.com)  
* @date 2017/5/6 
* @version V1.0   
*/
package com.scientree.biz;

import java.io.File;  
import java.io.IOException;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;  
import org.apache.commons.io.monitor.FileAlterationObserver;  
import org.apache.log4j.Logger;  
 

import com.scientree.common.FileUtils;

public class FileListener extends FileAlterationListenerAdaptor 
{  
	private static Logger infoLogger = Logger.getLogger("infofile"); 
    
    @Override  
    public void onStart(FileAlterationObserver observer) 
    {  
        super.onStart(observer);  
        infoLogger.debug("[onStart]"); 
    }  
  
    @Override  
    public void onStop(FileAlterationObserver observer) 
    {  
        super.onStop(observer);  
        infoLogger.debug("[onStop]"); 
    } 
      
    @Override  
    public void onFileCreate(File file) 
    {  
    	super.onFileCreate(file);
    	infoLogger.info("[FileCreate]:" + file.getAbsolutePath());
    	
    	try 
    	{
    		// Validate files 
			if (FileUtils.isValidFileSuffix(FileUtils.getFileSuffix(file.getAbsolutePath())))
			{
				// Execute from message queue
		    	FileQueue.doIt(file);
			}
		} 
    	catch (IOException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }  
   
    @Override  
    public void onFileChange(File file) 
    {  
    	super.onFileChange(file);
    	infoLogger.info("[FileChange]:" + file.getAbsolutePath());  
    }  
   
    @Override  
    public void onFileDelete(File file) 
    {  
    	super.onFileDelete(file);
    	infoLogger.info("[FileDelete]:" + file.getAbsolutePath());  
    }  
    
    @Override  
    public void onDirectoryCreate(File directory) 
    {  
    	super.onDirectoryCreate(directory);
    	infoLogger.info("[DirectoryCreate]:" + directory.getAbsolutePath());  
    }  
    
    @Override  
    public void onDirectoryChange(File directory) 
    {  
    	super.onDirectoryChange(directory);
    	infoLogger.info("[DirectoryChange]:" + directory.getAbsolutePath());  
    }  
   
    @Override  
    public void onDirectoryDelete(File directory) 
    {  
    	super.onDirectoryDelete(directory);
    	infoLogger.info("[DirectoryDelete]:" + directory.getAbsolutePath());  
    }  
}
