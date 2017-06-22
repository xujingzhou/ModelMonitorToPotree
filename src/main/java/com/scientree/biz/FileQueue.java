/**   
* @Title: FileQueue
* @Package biz 
* @Description:  
* @author 徐景周(Johnny Xu,xujingzhou2016@gmail.com)  
* @date 2017/5/6 
* @version V1.0   
*/
package com.scientree.biz;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.scientree.common.ConfigUtils;
import com.scientree.common.FileUtils;

public class FileQueue 
{
    private static int FILE_QUEUE_SIZE = 10; 
    private static BlockingQueue<File> queue = new LinkedBlockingQueue<File>(
            FILE_QUEUE_SIZE);
    
    public static final void doIt(File file)
	{ 
    	// Producer
        new Thread(new FileEnumerationTask(queue, file)).start();
        
        // Consumer
        new Thread(new SearchTask(queue)).start();
	}
}

class FileEnumerationTask implements Runnable 
{
	private static Logger infoLogger = Logger.getLogger("infofile");
    private BlockingQueue<File> queue;
    private File startingDirectory;
 
    public FileEnumerationTask(BlockingQueue<File> queue, File startingDirectory) 
    {
        this.queue = queue;
        this.startingDirectory = startingDirectory;
    }
 
    public void run() 
    {
        try 
        {
            enumerate(startingDirectory);
        } 
        catch (InterruptedException e) 
        {
        }
    }
 
    public void enumerate(File directory) throws InterruptedException 
    {
    	if (directory.isDirectory())
        {
    		File[] files = directory.listFiles();
            for (File file : files) 
            {
            	 enumerate(file);
            }
        }
    	else
    	{
    		queue.put(directory);
            infoLogger.info("Producer a file: " + directory.getAbsolutePath());
    	}
        
    }
}
 
class SearchTask implements Runnable 
{
	private static Logger infoLogger = Logger.getLogger("infofile");
    private static Logger errorLogger = Logger.getLogger("errorfile");  
    private BlockingQueue<File> queue;
 
    public SearchTask(BlockingQueue<File> queue) 
    {
        this.queue = queue;
    }
 
    public void run() 
    {
        try 
        {
            boolean done = false;
            while (!done)
            { 
                if (queue.isEmpty()) 
                {
//                    done = true;
                } 
                else
                {
                	File file = queue.take();
                	infoLogger.info("Consumer a file: " + file.getAbsolutePath());
                	generateWeb(file.getAbsolutePath());
                }
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        catch (InterruptedException e) 
        {
        }
    }
    
    private void generateWeb(String modelPath) throws IOException 
    {  
    	try 
    	{
			if (GeneratePotreeWeb.doIt(modelPath))
			{
				infoLogger.info("Generateing for webfile: " + getWebFile(modelPath) + System.getProperty("line.separator"));
			}
		} 
    	catch (Exception e) 
    	{
			e.printStackTrace();
			errorLogger.error(e.getLocalizedMessage());
		}
    }
    
	public String getWebFile(String modelPath) throws IOException 
	{
		String filename = FileUtils.getFileName(modelPath);
		String projectPath = ConfigUtils.getString("tomcatWebApps").trim()
				+ File.separatorChar
				+ ConfigUtils.getString("projectName").trim();
		return projectPath + File.separatorChar + filename + ".html";
	}
}