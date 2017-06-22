/**   
* @Title: GeneratePotreeWeb
* @Package biz 
* @Description: Call 'PotreeConverter' to create new 3d model  
* @author 徐景周(Johnny Xu,xujingzhou2016@gmail.com)  
* @date 2017/5/6 
* @version V1.0   
*/
package com.scientree.biz;
   
import java.io.BufferedReader;     
import java.io.InputStreamReader;    
import java.io.InputStream;
import java.io.File;
import java.lang.Process;
import java.util.Random;
import java.util.Scanner;
import java.lang.ProcessBuilder;

import org.apache.log4j.Logger;

import com.scientree.common.FileUtils;
import com.scientree.common.ConfigUtils;

public class GeneratePotreeWeb 
{   
	private static Logger infoLogger = Logger.getLogger("infofile");
	private static Logger errorLogger = Logger.getLogger("errorfile");
	
	private static final int getRandom(int maxsize)
	{
		Random random = new Random();
		return Math.abs(random.nextInt())%maxsize;
	}
	
    public static final boolean doIt(String modelPath) throws Exception
	{     
    	if (modelPath == null || modelPath.trim().length() <= 0)
		{
			errorLogger.error("modelPath is empty!");
		    throw new IllegalArgumentException("modelPath is empty!");   
		}		
		
    	modelPath = FileUtils.getIndependentOSPath(modelPath);
    	
    	// Delete old files
        String filename = FileUtils.getFileName(modelPath);
		String projectPath = ConfigUtils.getString("tomcatWebApps").trim() + File.separatorChar + ConfigUtils.getString("projectName").trim(); 
        String webFile = projectPath + File.separatorChar  + filename + ".html";
		if (FileUtils.existFile(webFile))
		{
            // Delete related with existed files
			webFile = FileUtils.getIndependentOSPath(webFile);
			FileUtils.deleteFile(webFile);

			String pointcloudsPath = projectPath + File.separatorChar  + "pointclouds" + File.separatorChar  + filename;
			pointcloudsPath = FileUtils.getIndependentOSPath(pointcloudsPath);
            FileUtils.delFolder(pointcloudsPath);
            
            infoLogger.info("Deleted the existing files: " + webFile);
            infoLogger.info("Deleted the existing directory: " + pointcloudsPath);
		}

		// Command line
		String[] cmd = new String[10];  
		if (FileUtils.isWindowsOS())
		{
	        cmd[0] = ConfigUtils.getString("cmd0") + " "; 
	        cmd[1] = ConfigUtils.getString("cmd1") + " "; 
	        cmd[2] = ConfigUtils.getString("cmd2") + " "; 
	        cmd[3] = FileUtils.getIndependentOSPath(ConfigUtils.getString("cmd3").trim());  
	        cmd[4] = ConfigUtils.getString("cmd4") + " "; 
		    cmd[5] = modelPath;
	        cmd[6] = " " + ConfigUtils.getString("cmd6") + " ";
			cmd[7] = projectPath; 
			cmd[8] = " " + ConfigUtils.getString("cmd8") + " ";
			cmd[9] = filename;
			
			infoLogger.info("There is on windows!");
		}
		else
		{
			cmd[0] = ConfigUtils.getString("cmd0") + " ";
	        cmd[1] = ConfigUtils.getString("cmd1") + " ";  
	        cmd[2] = ConfigUtils.getString("cmd2") + " ";   
	        cmd[3] = FileUtils.getIndependentOSPath(ConfigUtils.getString("cmd3").trim());  
	        cmd[4] = modelPath; 
		    cmd[5] = " " + ConfigUtils.getString("cmd5") + " ";
	        cmd[6] = projectPath; 
			cmd[7] = " " + ConfigUtils.getString("cmd7") + " ";
			cmd[8] = filename;
			cmd[9] = " " + ConfigUtils.getString("cmd9");
			
			infoLogger.info("There is on linux!");
		}
       
        try 
		{     
        	infoLogger.info("\'PotreeConverter\' path at: " + cmd[3]);
        	 
        	// ProcessBuilder
        	ProcessBuilder pb = null;
        	Process proc = null;
        	if (FileUtils.isWindowsOS())
    		{
            	infoLogger.info("Execute command line: " + cmd[0] + cmd[1] + cmd[2] + cmd[4] + cmd[5] + cmd[6] + cmd[7] + cmd[8] + cmd[9]);
            	pb = new ProcessBuilder(cmd[0].trim(), cmd[1].trim(), cmd[2].trim(), cmd[4].trim(), cmd[5].trim(), cmd[6].trim(), cmd[7].trim(), cmd[8].trim(), cmd[9].trim());
            	pb.redirectErrorStream(true);
            	pb.directory(new File(cmd[3].trim()));
            	proc = pb.start();
    		}
            else
            {
            	int random = getRandom(ConfigUtils.getInt("maxsize", 6));
            	String shellname = System.getProperty("user.dir") + File.separatorChar + "potree_" + String.valueOf(random) + ".sh";
            	String content = ConfigUtils.getString("cd") + " " + cmd[3] + ConfigUtils.getString("return") 
            			+ ConfigUtils.getString("permission") + " " + cmd[2] + ConfigUtils.getString("return") 
            			+ cmd[0] + cmd[1] + ConfigUtils.getString("quotation") + cmd[2]
            			+ cmd[4] + cmd[5] + cmd[6] + cmd[7] + cmd[8] + cmd[9] + ConfigUtils.getString("quotation") 
            			+ ConfigUtils.getString("return");
            	FileUtils.createNewFile(shellname, content);
            	
            	infoLogger.info("shellname: " + shellname);
            	infoLogger.info("Execute command line: " + content);
            	
            	String[] line1 = { ConfigUtils.getString("shell"), ConfigUtils.getString("shellParam"), ConfigUtils.getString("permission") + " " + shellname}; 
            	String[] line2 = { ConfigUtils.getString("shell"), ConfigUtils.getString("shellParam"), shellname}; 
            	pb = new ProcessBuilder(line1);
            	proc = pb.start();  
            	proc.waitFor();
            	pb = new ProcessBuilder(line2);
            	proc = pb.start(); 
            }
        	
        	int i = 0;
        	Scanner scanner = new Scanner(proc.getInputStream());
            while (scanner.hasNextLine()) 
            {
            	infoLogger.info(scanner.nextLine());
            	i++;
            }
            scanner.close();
            
            if (i > 0)
        	{
        		infoLogger.info(System.getProperty("line.separator")); 
        	}	
                         
            if (proc.waitFor() != 0) 
			{     
                if (proc.exitValue() != 0)
				{
                	errorLogger.error("Failed to execute command!");
					throw new Exception();
				}
            }   
			
			return true;
        }
		catch (Exception e) 
		{     
            e.printStackTrace();   
            errorLogger.error("Exception:" + e.getCause().getClass()+ "," + e.getCause().getMessage() + System.getProperty("line.separator"));
            
			return false;
        }     
    }     
}   

final class StreamGobbler extends Thread 
{  
	private static Logger infoLogger = Logger.getLogger("infofile");
	private static Logger errorLogger = Logger.getLogger("errorfile");
	private InputStream is;  
	private String type;  
  
    public StreamGobbler(InputStream is, String type) 
    {  
        this.is = is;  
        this.type = type;  
    }  
  
    public void run() 
    {  
        try 
        {  
            InputStreamReader isr = new InputStreamReader(is);  
            BufferedReader br = new BufferedReader(isr);  
            String line = null;  int i = 0;
            while ((line = br.readLine()) != null) 
            {  
                if (type.equals("Error")) 
                {  
                	errorLogger.error(line);
                } 
                else 
                {  
                	infoLogger.info(line); 
                }  
                
                i++;
            }  
            
            if (type.equals("Error")) 
            {  
            	if (i > 0)
            	{
            		errorLogger.error(System.getProperty("line.separator"));
            	}
            } 
            else 
            {  
            	if (i > 0)
            	{
            		infoLogger.info(System.getProperty("line.separator")); 
            	}	
            }  
        } 
        catch (Exception e) 
        {  
        	e.printStackTrace();  
        }  
        finally
        {
        }
    }  
}  