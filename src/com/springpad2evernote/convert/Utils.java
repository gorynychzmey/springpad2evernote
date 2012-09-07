/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springpad2evernote.convert;
import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Encoder;

/**
 *
 * @author vostrikovva
 */
public class Utils {
    private static final int BUFSIZE = 65536;
    
    private static final HashMap filecache = new HashMap();
    
    private static byte [] readFile(String filename) throws MalformedURLException, IOException {
      if(filecache.containsKey(filename)){
       return (byte[])filecache.get(filename);   
      }  
      InputStream is;
      try {   
      if(filename.matches("https?://.*"))
       is = (new URL(filename)).openConnection().getInputStream();
      else {
       ZipEntry entry = Converter.zip.getEntry(filename);   
       if(entry==null)
        return null;   
       is = Converter.zip.getInputStream(entry);
      } 
      byte[] buf = new byte[BUFSIZE];
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int bytesRead;
      while((bytesRead=is.read(buf))>-1) 
       baos.write(buf,0,bytesRead);   
      filecache.put(filename, baos.toByteArray());
      return baos.toByteArray();
      } catch (FileNotFoundException e) {
        System.err.println("Error: "+e.getStackTrace());
        throw(e);
      }
    }
    
    public static String toBase64(String filename) throws FileNotFoundException, IOException{
      byte [] bytes = readFile(filename);
      BASE64Encoder codec = new BASE64Encoder();
      return codec.encode(bytes);
    }
    
    public static String getFileName(String filename){
	return filename.substring(filename.lastIndexOf(File.separatorChar) + 1);
}

    public static String getFileExt(String filename){
       String name =  getFileName(filename);
       return name.substring(name.lastIndexOf('.')+1); 
    }
    
    public static String lineBreak(String text){
     return StringUtils.join(text.split("\n"), "<br/>");
    }

    public static String makeList(String text){
     String[] lines = text.split("\n");
     StringBuilder buf = new StringBuilder();
     for(int i = 0; i < lines.length; i++)
      buf.append("<li>"+lines[i]+"</li>");   
     return buf.toString();
    }
    
    public static String hash(String filename) throws NoSuchAlgorithmException, MalformedURLException, IOException {
        byte [] data = readFile(filename);
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(data);
        BigInteger digestValue = new BigInteger(1, digest);
        return String.format("%032x", digestValue);
    } 
}
