package com.springpad2evernote.convert;


import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;
import java.util.zip.*;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vostrikovva
 */
public class Converter {

private static String INDEX_NAME = "index.html";    
    
public static String filename;    
public static ZipFile zip;
    
public static void main(String[] args) throws FileNotFoundException, IOException, TransformerConfigurationException, TransformerException, ParserConfigurationException, SAXException {
    if(args.length<1) {
        System.out.println("Need file name to convert");
        return;
    }
    
    filename = args[0];
    
    zip = new ZipFile(new File(filename));
    
    ZipEntry idx = zip.getEntry(INDEX_NAME);
            
    if(idx==null){
        System.out.println("Zip archive must have "+INDEX_NAME+" inside");
        return;
    }
        
    InputStream is = zip.getInputStream(idx);

    OutputStream os = new FileOutputStream(filename+".enex");

    String str = new java.util.Scanner(is).useDelimiter("\\A").next();
    str = str.replaceAll("[\\x00-\\x09\\x0B-\\x0D\\x0E-\\x1F]", "").replaceAll("&nbsp;","&#160;").replaceAll("&(?![\\w#]+;)","&amp;");

    is = new ByteArrayInputStream(str.getBytes());
    
    StreamSource ssStyle = new StreamSource(StreamSource.class.getResourceAsStream("/convert.xsl"));
    StreamSource ssSrc = new StreamSource(is);
    Transformer transformer = (TransformerFactory.newInstance()).newTransformer(ssStyle);    
    
    DateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    Calendar cal = Calendar.getInstance();
    transformer.setParameter("currDate", df.format(cal.getTime()));
    transformer.transform(ssSrc, new StreamResult(os));
    
}
}