/**   
* @Title: FileMonitor
* @Package entrance 
* @Description: 
* @author 徐景周(Johnny Xu,xujingzhou2016@gmail.com)  
* @date 2017/5/6 
* @version V1.0   
*/
package com.scientree.entrance;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

//import org.apache.commons.io.filefilter.FileFilterUtils;  
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.scientree.common.SingleInstanceChecker;
import com.scientree.common.ConfigUtils;
import com.scientree.common.FileUtils;
import com.scientree.biz.FileListener;

public class FileMonitor 
{
	private static Logger infoLogger = Logger.getLogger("infofile");
	private static Logger errorLogger = Logger.getLogger("errorfile");
	
	public static void main(String[] args) throws Exception 
	{
		// Log4j save path
		setLog4jPath();

		// Get configuration
		getPlatformProperties();
				
		// Only run an instance
		String lockFile = ConfigUtils.getString("SINGLE_INSTANCE_LOCKFILE").trim();
		infoLogger.info("[lockFile]: lockFile");
		if (!SingleInstanceChecker.lockInstance(lockFile))
		{
			infoLogger.info("[SingleInstanceChecker]: Other instance tried to start...");
			System.exit(0);
		}

		// Monitor directory
		String monitorDir = ConfigUtils.getString("monitorDir").trim();
		infoLogger.info("[monitorDir]:" + monitorDir);

		// Default is 10 seconds interval
		long interval = TimeUnit.SECONDS.toMillis(ConfigUtils.getLong(
				"seconds", 10));

		try 
		{
			FileAlterationObserver observer = new FileAlterationObserver(
					monitorDir, null, null);
			/*
			 * FileFilterUtils.and( FileFilterUtils.fileFileFilter(),
			 * FileFilterUtils.suffixFileFilter(".ply")), null);
			 */

			observer.addListener(new FileListener());
			FileAlterationMonitor monitor = new FileAlterationMonitor(interval,
					observer);
			monitor.start();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private static void setLog4jPath()
	{
		String prefix = FileUtils.getRealPath() + File.separatorChar;
		infoLogger.info("[logPath]: " + prefix);
        Properties props = new Properties();
        try 
        {
        	InputStream  istream = FileMonitor.class.getResourceAsStream("/log4j.properties");
            props.load(istream);
            istream.close();

            // 1.
            String logFile = prefix + props.getProperty("log4j.appender.infofile.File");
            props.setProperty("log4j.appender.infofile.File", logFile);
            infoLogger.info("[log4j.appender.infofile.File]: " + logFile);
            
            //2.
            logFile = prefix + props.getProperty("log4j.appender.errorfile.File");
            props.setProperty("log4j.appender.errorfile.File", logFile);
            infoLogger.info("[log4j.appender.errorfile.File]: " + logFile);
            
            // Update
            PropertyConfigurator.configure(props);
        } 
        catch (Exception e) 
        {
        	errorLogger.info("[setLog4jPath]: ", e);
            return;
        }
	}
	
	private static void getPlatformProperties() 
	{
		String configFile = null;
		List<String> supplierNames = new ArrayList<String>();
		if (FileUtils.isWindowsOS()) 
		{
			configFile = System.getProperty("user.dir") + File.separatorChar + "config" + File.separatorChar
					+ "potree_windows.properties";
			configFile = FileUtils.getIndependentOSPath(configFile);
			supplierNames.add(configFile);
		} 
		else 
		{
			configFile = System.getProperty("user.dir") + File.separatorChar + "config" + File.separatorChar
					+ "potree_linux.properties";
			configFile = FileUtils.getIndependentOSPath(configFile);
			supplierNames.add(configFile);
		}

		ConfigUtils.getInstance(supplierNames);
	}
}
