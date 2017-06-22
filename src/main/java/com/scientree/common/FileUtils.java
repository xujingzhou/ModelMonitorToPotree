/**   
* @Title: FileUtils
* @Package common 
* @Description: File utils 
* @author 徐景周(Johnny Xu,xujingzhou2016@gmail.com)  
* @date 2017/5/6 
* @version V1.0   
*/
package com.scientree.common; 

import java.io.BufferedInputStream; 
import java.io.BufferedOutputStream; 
import java.io.BufferedReader; 
import java.io.ByteArrayOutputStream; 
import java.io.File; 
import java.io.FileFilter; 
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.io.FileReader; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.io.InputStream; 
import java.io.PrintWriter; 
import java.io.UnsupportedEncodingException;
import java.math.BigInteger; 
import java.net.URLDecoder;
import java.nio.ByteBuffer; 
import java.nio.MappedByteBuffer; 
import java.nio.channels.FileChannel; 
import java.security.MessageDigest; 
import java.text.DecimalFormat; 
import java.util.Arrays; 
import java.util.ArrayList;
import java.util.HashSet; 
import java.util.List; 
import java.util.Set; 
import java.util.zip.ZipEntry; 
import java.util.zip.ZipInputStream; 
import java.util.zip.ZipOutputStream; 


@SuppressWarnings("resource") 
public class FileUtils 
{ 

	public static boolean isWindowsOS()
    {
    	boolean isWindowsOS = false;
    	String osName = System.getProperty("os.name");
    	if(osName.toLowerCase().indexOf("windows") > -1)
    	{
    	 	isWindowsOS = true;
    	}
    	
    	return isWindowsOS;
    }
    
    public static String getIndependentOSPath(String path)
    {
    	path = path.trim();
    	if(isWindowsOS())
    	{
    		path.replaceAll("/", File.separator);
    	}
    	else
    	{
    		path.replaceAll("\\\\", File.separator);
    	}
    	
    	return path;
    }
    
    /** 
    * Valid file format
    *  
    * @param file 
    * @return 
    */    
    public static final String validFileSuffix = ".ply|.xyz";  
    public static boolean  isValidFileSuffix(String suffix)
	{  
        boolean flg = false;  
        String [] s = validFileSuffix.split("\\|");  
        for(String str:s)
	    {  
            if(suffix.equalsIgnoreCase(str))
	        {  
                flg = true;  
                break;  
            }  
        }  

        return flg;  
    }  

    /** 
    * get file suffix according to the filename 
    *  
    * @param fileName 
    * @return 
    * @throws IOException 
    */  
    public static String getFileSuffix(String fileName)  
            throws IOException 
	{  
        int sufIndex = fileName.lastIndexOf(".");  
        if (sufIndex < 0)  
        	return null;   
        String suffix = fileName.substring(sufIndex);  
  
        return suffix.trim();  
    }  
  
	/** 
    * get file name according to the file path 
    *  
    * @param fileName 
    * @return 
    * @throws IOException 
    */  
    public static String getFileName(String fileName) throws IOException 
	{  
        int start = fileName.lastIndexOf(File.separatorChar);  
        int sufIndex = fileName.lastIndexOf(".");  
        String suffix = null;
        if (sufIndex < 0)  
        {
        	suffix = fileName.substring(start + 1);  
        }
        else
        {
        	suffix = fileName.substring(start + 1, sufIndex);
        }
  
        return suffix;  
    }  

    public static String getRealPath() 
	{
        String realPath = FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath(); //FileMonitor.class.getClassLoader().getResource("").getFile();
        File file = new File(realPath);
        realPath = file.getAbsolutePath();
        
        int sufIndex = realPath.lastIndexOf(".");  
        if (sufIndex > 0)  
        {
        	realPath = realPath.substring(0, realPath.lastIndexOf(File.separatorChar));  
        }
        
        try 
        {
			realPath = URLDecoder.decode(realPath, "utf-8");
		} 
        catch (UnsupportedEncodingException e) 
        {
			e.printStackTrace();
		}
        
        return realPath;
    }
    
    public static String getRealClassPath() 
   	{
		String realPath = FileUtils.class.getClassLoader().getResource("")
				.getFile();
		File file = new File(realPath);
		realPath = file.getAbsolutePath();
		
		int sufIndex = realPath.lastIndexOf(".");  
        if (sufIndex > 0)  
        {
        	realPath = realPath.substring(0, realPath.lastIndexOf(File.separatorChar));  
        }
        
		try 
		{
			realPath = URLDecoder.decode(realPath, "utf-8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}

		return realPath;
    }

    public static String getTomcatProjectPath(String projectName)
	{
        String nowpath;             // D:\java\software\apache-tomcat-6.0.14\bin
        String tempdir;
        nowpath = System.getProperty("user.dir");
        tempdir = nowpath.replace("bin", "webapps");   
        tempdir += File.separatorChar + projectName;  // D:\java\software\apache-tomcat-6.0.14\webapps\projectName 
        return tempdir;  
    } 

 
    public static List<String> find(String dir,String suffix)
	{   
        List<String> list = new ArrayList<String>();   
        try 
	    {   
            File file = new File(dir);   
            if(file.exists() && file.isDirectory())
	        {   
                find(file, suffix, list);   
            }
            else
			{   
                throw new IllegalArgumentException("param \"dir\" must be an existing directory .dir = " + dir);   
            }   
        } 
	    catch (Exception e) 
	    {   
	    	e.printStackTrace();   
        }   

        return list;   
    }
   
 
    private static void find(File dirFile, String suffix, List<String> list)
	{   
        if(dirFile.exists() && dirFile.isDirectory())
	    {   
            File[] subFiles = dirFile.listFiles();   
            for(File subFile : subFiles) 
	        {   
                if(subFile.isDirectory())
	            {   
                    find(subFile, suffix, list);   
                }
                else
				{   
                    String path = subFile.getAbsolutePath();   
                    if(path.endsWith(suffix))
					{   
                        list.add(path);   
                    }   
                }   
            }   
        }
        else
		{   
            throw new IllegalArgumentException("param \"dir\" must be an existing directory .dir = "+dirFile.getAbsolutePath());   
        }   
    }   

 
    public static String getMd5ByFile(File file) 
	{  
        String value = null;  
        FileInputStream in = null;  

        try {  
            in = new FileInputStream(file);  
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0,  
                    file.length());  
            MessageDigest md5 = MessageDigest.getInstance("MD5");  
            md5.update(byteBuffer);  
            BigInteger bi = new BigInteger(1, md5.digest());  
            value = bi.toString(16);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (null != in) {  
                try {  
                    in.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  

        return value;  
    }  
       
 
    public static long getFileLength(File file)  
            throws IOException 
	{  
        FileInputStream fis = null;  
        fis = new FileInputStream(file);  
        return fis.available();  
    }  
       

    public static String getFileSize(File file)  
            throws IOException 
	{  
        long size = getFileLength(file);  
        DecimalFormat df = new DecimalFormat("###.##");  
        float f;  
        if (size < 1024 * 1024) {  
            f = (float) ((float) size / (float) 1024);  
            return (df.format(new Float(f).doubleValue()) + " KB");  
        } else {  
            f = (float) ((float) size / (float) (1024 * 1024));  
            return (df.format(new Float(f).doubleValue()) + " MB");  
        }  
           
    }  


    public static byte[] getBytesFromFile(File file)  
            throws IOException 
	{  
        InputStream is = new FileInputStream(file);  
           
        long length = file.length();  
           
        if (length > Integer.MAX_VALUE) {  
            // File is too large  
        }  
           
        byte[] bytes = new byte[(int) length];  
           
        // Read in the bytes  
        int offset = 0;  
        int numRead = 0;  
        while (offset < bytes.length  
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {  
            offset += numRead;  
        }  
           
        // Ensure all the bytes have been read in  
        if (offset < bytes.length) {  
            throw new IOException("getBytesFromFile: " + file.getName());  
        }  
           
        is.close();  
        return bytes;  
    }  
       
 
    public static void copyFile(File f1, File f2)  
            throws Exception 
	{  
        int length = 2097152;  
        FileInputStream in = new FileInputStream(f1);  
        FileOutputStream out = new FileOutputStream(f2);  
        FileChannel inC = in.getChannel();  
        FileChannel outC = out.getChannel();  
        ByteBuffer b = null;  
        while (true) {  
            if (inC.position() == inC.size()) {  
                inC.close();  
                outC.close();  
            }  
            if ((inC.size() - inC.position()) < length) {  
                length = (int) (inC.size() - inC.position());  
            } else 
                length = 2097152;  
            b = ByteBuffer.allocateDirect(length);  
            inC.read(b);  
            b.flip();  
            outC.write(b);  
            outC.force(false);  
        }  
    }  
       

    public static boolean existFile(String fileName)  
            throws IOException 
	{  
        File file = new File(fileName);   
        return file.exists();  
    }  
       

    public static void deleteFile(String fileName)  
            throws IOException 
	{  
        File file = new File(fileName);   
        file.delete();  
    }  
       
 
    public static String readFile(String fileName)  
            throws IOException 
	{  
        File file = new File(fileName);  
        if (!file.exists()) {  
            throw new IOException("readFile " + fileName);  
        }  
           
        BufferedReader in = new BufferedReader(new FileReader(file));  
        StringBuffer sb = new StringBuffer();  
        String str = "";  
        while ((str = in.readLine()) != null) {  
            sb.append(str);  
        }  
        in.close();  

        return sb.toString();  
    }  
       

    public static List<File> listFiles(String fileName)  
            throws IOException 
	{  
        File file = new File(fileName);  
        if (!file.exists()) {  
            throw new IOException("listFiles:" + fileName);  
        }  
        return Arrays.asList(file.listFiles());  
    }  
       

    public static void mkdir(String dir) 
	{  
        String dirTemp = dir;  
        File dirPath = new File(dirTemp);  
        if (!dirPath.exists()) {  
            dirPath.mkdir();  
        }  
    }  
       
 
    public static void createNewFile(String fileName, String content)  
            throws IOException 
	{  
    	String fileNameTemp = fileName;  
        File filePath = new File(fileNameTemp);  
        if (filePath.exists()) 
        {  
        	deleteFile(fileNameTemp);
        }  
        
        filePath.createNewFile();  
        
        FileWriter fw = new FileWriter(filePath);  
        PrintWriter pw = new PrintWriter(fw);  
        String strContent = content;  
        pw.println(strContent);  
        pw.flush();  
        pw.close();  
        fw.close();   
    }  
       

    public static void delFolder(String folderPath) 
	{  
        delAllFile(folderPath);  
        String filePath = folderPath;  
        java.io.File myFilePath = new java.io.File(filePath);  
         
        myFilePath.delete();  
    }  
       

    public static void delAllFile(String path) 
	{  
        File file = new File(path);  
        if (!file.exists()) {  
            return;  
        } 
	
        if (!file.isDirectory()) {  
            return;  
        } 
	
        String[] childFiles = file.list();  
        File temp = null;  
        for (int i = 0; i < childFiles.length; i++) {  
           
            if (path.endsWith(File.separator)) {  
                temp = new File(path + childFiles[i]);  
            } else {  
                temp = new File(path + File.separator + childFiles[i]);  
            }  

            if (temp.isFile()) {  
                temp.delete();  
            }  

            if (temp.isDirectory()) {  
                delAllFile(path + File.separatorChar + childFiles[i]);
                delFolder(path + File.separatorChar + childFiles[i]);
            }  
        }  
    }  
       
 
    public static void copyFile(String srcFile, String dirDest)  
            throws IOException 
	{  
        FileInputStream in = new FileInputStream(srcFile);  
        mkdir(dirDest);  
        FileOutputStream out = new FileOutputStream(dirDest + "/" + new File(srcFile).getName());  
        int len;  
        byte buffer[] = new byte[1024];  
        while ((len = in.read(buffer)) != -1) {  
            out.write(buffer, 0, len);  
        }  

        out.flush();  
        out.close();  
        in.close();  
    }  
       

    public static void copyFolder(String oldPath, String newPath)  
            throws IOException 
	{   
        mkdir(newPath);  
        File file = new File(oldPath);  
        String[] files = file.list();  
        File temp = null;  
        for (int i = 0; i < files.length; i++) {  
            if (oldPath.endsWith(File.separator)) {  
                temp = new File(oldPath + files[i]);  
            } else {  
                temp = new File(oldPath + File.separator + files[i]);  
            }  
               
            if (temp.isFile()) {  
                FileInputStream input = new FileInputStream(temp);  
                FileOutputStream output = new FileOutputStream(newPath + "/" 
                        + (temp.getName()).toString());  
                byte[] buffer = new byte[1024 * 2];  
                int len;  
                while ((len = input.read(buffer)) != -1) {  
                    output.write(buffer, 0, len);  
                }  
                output.flush();  
                output.close();  
                input.close();  
            }  
            
            if (temp.isDirectory()) {
                copyFolder(oldPath + "/" + files[i], newPath + "/" + files[i]);  
            }  
        }  
    }  
       

    public static void moveFile(String oldPath, String newPath)  
            throws IOException 
	{  
        copyFile(oldPath, newPath);  
        deleteFile(oldPath);  
    }  
       
 
    public static void moveFiles(String oldPath, String newPath)  
            throws IOException 
	{  
        copyFolder(oldPath, newPath);  
        delAllFile(oldPath);  
    }  
       
 
    public static void moveFolder(String oldPath, String newPath)  
            throws IOException 
	{  
        copyFolder(oldPath, newPath);  
        delFolder(oldPath);  
    }  
       
 
    public static void unZip(String srcDir, String destDir)  
            throws IOException 
	{  
        int leng = 0;  
        byte[] b = new byte[1024 * 2];  

        File[] zipFiles = new ExtensionFileFilter("zip").getFiles(srcDir);  
        if (zipFiles != null && !"".equals(zipFiles)) {  
            for (int i = 0; i < zipFiles.length; i++) {  
                File file = zipFiles[i];  
                
                ZipInputStream zis = new ZipInputStream(new FileInputStream(file));  
                ZipEntry entry = null;  
                while ((entry = zis.getNextEntry()) != null) {  
                    File destFile = null;  
                    if (destDir.endsWith(File.separator)) {  
                        destFile = new File(destDir + entry.getName());  
                    } else {  
                        destFile = new File(destDir + File.separator + entry.getName());  
                    }  
                    
                    FileOutputStream fos = new FileOutputStream(destFile);  
                    while ((leng = zis.read(b)) != -1) {  
                        fos.write(b, 0, leng);  
                    }  
                    fos.close();  
                }  
                zis.close();  
            }  
        }  
    }  
       

    public static void zip(String srcDir, String destDir)  
            throws IOException 
	{  
        String tempFileName = null;  
        byte[] buf = new byte[1024 * 2];  
        int len;  
 
        File[] files = new File(srcDir).listFiles();  
        if (files != null) {  
            for (File file : files) {  
                if (file.isFile()) {  
                    FileInputStream fis = new FileInputStream(file);  
                    BufferedInputStream bis = new BufferedInputStream(fis);  
                    if (destDir.endsWith(File.separator)) {  
                        tempFileName = destDir + file.getName() + ".zip";  
                    } else {  
                        tempFileName = destDir + File.separator + file.getName() + ".zip";  
                    }  
                    FileOutputStream fos = new FileOutputStream(tempFileName);  
                    BufferedOutputStream bos = new BufferedOutputStream(fos);  
                    ZipOutputStream zos = new ZipOutputStream(bos);
                       
                    ZipEntry ze = new ZipEntry(file.getName()); 
                    zos.putNextEntry(ze);  
                       
                    while ((len = bis.read(buf)) != -1) {  
                        zos.write(buf, 0, len);  
                        zos.flush();  
                    }  
                    bis.close();  
                    zos.close();  
                       
                }  
            }  
        }  
    }  
       
    /**  
     *  
     * @param inSream 
     * @param charsetName 
     * @return 
     * @throws Exception 
     */ 
    public static String readData(InputStream inSream, String charsetName)  
            throws IOException 
	{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = -1;  
        while ((len = inSream.read(buffer)) != -1) {  
            outStream.write(buffer, 0, len);  
        }  
        byte[] data = outStream.toByteArray();  
        outStream.close();  
        inSream.close();  
        return new String(data, charsetName);  
    }  
       
    /** 
     *  
     * @param path 
     * @return 
     * @throws Exception 
     */ 
    public static Set<String> readFileLine(String path)  
            throws IOException 
	{  
        Set<String> datas = new HashSet<String>();  
        FileReader fr = new FileReader(path);  
        BufferedReader br = new BufferedReader(fr);  
        String line = null;  
        while ((line = br.readLine()) != null) {  
            datas.add(line);  
        }  
        br.close();  
        fr.close();  
        return datas;  
    }  
       
	// Test
    public static void main(String[] args) 
	{  
        try {  
            unZip("c:/test", "c:/test");  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }  
       
}  
   
   
class ExtensionFileFilter  
        implements FileFilter 
{  
       
    private String extension;  
       
    public ExtensionFileFilter(String extension) 
	{  
        this.extension = extension;  
    }  
       
    public File[] getFiles(String srcDir) throws IOException 
	{  
        return (File[]) FileUtils.listFiles(srcDir).toArray();  
    }  
       
    public boolean accept(File file) 
	{  
        if (file.isDirectory()) {  
            return false;  
        }  
           
        String name = file.getName();  
        // find the last  
        int idx = name.lastIndexOf(".");  
        if (idx == -1) {  
            return false;  
        } else if (idx == name.length() - 1) {  
            return false;  
        } else {  
            return this.extension.equals(name.substring(idx + 1));  
        }  
    }  
       
}  
