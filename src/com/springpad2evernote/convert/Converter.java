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


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vostrikovva
 */
public class Converter {

public static String filename;    
    
public static void main(String[] args) throws FileNotFoundException, IOException, TransformerConfigurationException, TransformerException, ParserConfigurationException, SAXException {
    if(args.length<1) {
        System.out.println("Need file name to convert");
    }
    
    filename = args[0];
    
    InputStream is = new FileInputStream(args[0]);

    OutputStream os = new FileOutputStream(args[0]+".enex");

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