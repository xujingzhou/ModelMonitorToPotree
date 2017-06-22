/**   
* @Title: ConfigUtils
* @Package common 
* @Description: config utils 
* @author 徐景周(Johnny Xu,xujingzhou2016@gmail.com)  
* @date 2017/5/6 
* @version V1.0   
*/
package com.scientree.common;

import java.util.Iterator;
import java.util.List;
 
import org.apache.commons.lang.StringUtils;
import org.apache.commons.configuration.*;
import org.apache.log4j.Logger;

public class ConfigUtils 
{
	private static Logger log = Logger.getLogger("infofile");
    private static ConfigUtils ConfigUtils = new ConfigUtils();
    private static List<String> locations;
    private static CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
    private static boolean isRead = false;
    
    private ConfigUtils()
    {
    }
 
    public static ConfigUtils getInstance(List<String> names)
    {
        if (!isRead) 
        {
            reload(names);
            isRead = true;
        }
        
        return ConfigUtils;
    }
    
    public static void reload(List<String> names)
    {
        if (locations == null) 
        {
           if (names != null)
           {
        	   locations = names;
           }
        }
        
        compositeConfiguration.clear();
        Configuration conf = null;
        int index = 0;
        int count = 0;
 
        for (String location : locations) 
        {
            if (StringUtils.isNotEmpty(location)) 
            {
                try 
                {
                    log.info("[configFile]: " + location);
 
                    if (location.toLowerCase().endsWith(".xml")) 
                    {
                        conf = new XMLConfiguration(location);
                    }
                    else 
                    {
                        conf = new PropertiesConfiguration(location);
                    }
 
                    compositeConfiguration.addConfiguration(conf);
                    index = 0;
                    for (Iterator<String> it = conf.getKeys(); it.hasNext(); ) 
                    {
                        index++;
                        count++;
                        it.next();
                        
//                       String key = it.next();
//                        log.info("key = " + key + ", value = " + conf.getString(key));
                    }
                    
                    log.info("Location item count: " + index);
                }
                catch (ConfigurationException e) 
                {
                    log.error("SystemConfiguration", e);
                }
            }
        }
 
        log.info("systemConfiguration: " + count);
    }
 
    public static boolean getBoolean(String key)
    {
        return getBoolean(key, false);
    }
 
    public static boolean getBoolean(String key, boolean defaultValue)
    {
        return compositeConfiguration.getBoolean(key, defaultValue);
    }
 
    public static String getString(String key)
    {
        return getString(key, null);
    }
 
    public static String getString(String key, String defaultValue)
    {
        return compositeConfiguration.getString(key, defaultValue);
    }
 
    public static int getInt(String key)
    {
        return getInt(key, 0);
    }
 
    public static int getInt(String key, int defaultValue)
    {
        return compositeConfiguration.getInt(key, defaultValue);
    }
 
    public static double getDouble(String key)
    {
        return getDouble(key, 0d);
    }
 
    public static double getDouble(String key, double defaultValue)
    {
        return compositeConfiguration.getDouble(key, defaultValue);
    }
 
    public static byte getByte(String key)
    {
        return getByte(key, (byte) 0);
    }
 
    public static byte getByte(String key, byte defaultValue)
    {
 
        return compositeConfiguration.getByte(key, defaultValue);
    }
 
    public static float getFloat(String key)
    {
        return getFloat(key, 0f);
    }
 
    public static float getFloat(String key, float defaultValue)
    {
        return compositeConfiguration.getFloat(key, defaultValue);
    }
 
    public static long getLong(String key)
    {
        return getLong(key, 0l);
    }
 
    public static long getLong(String key, long defaultValue)
    {
        return compositeConfiguration.getLong(key, defaultValue);
    }
 
    public static short getShort(String key)
    {
        return getShort(key, (short) 0);
    }
 
    public static short getShort(String key, short defaultValue)
    {
        return compositeConfiguration.getShort(key, defaultValue);
    }
 
    public static List<String> getLocations()
    {
        return locations;
    }
    
/*    public static void setLocations(List<String> location)
    {
        ConfigUtils.locations = location;
    }*/
}
