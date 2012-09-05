<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : convert.xsl
    Created on : August 30, 2012, 1:04 PM
    Author     : vostrikovva
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
                xmlns:java="java"
                xmlns:utl="com.springpad2evernote.convert.Utils">
    <xsl:output method="xml" 
                doctype-system="http://xml.evernote.com/pub/evernote-export.dtd" 
                media-type="en-export" />
    <xsl:param name="currDate"/>            
    <xsl:template match="/">
      <xsl:element name="en-export">
        <xsl:attribute name="export-date"><xsl:value-of select="$currDate"/></xsl:attribute>  
        <xsl:attribute name="application"><xsl:text>Evernote/Windows</xsl:text></xsl:attribute>  
        <xsl:attribute name="version"><xsl:text>4.x</xsl:text></xsl:attribute>  
    <!--xsl:apply-templates select="//div[contains(@class,'instance') and position() &lt; 11]"/-->
    <xsl:apply-templates select="//div[contains(@class,'instance')]"/>
      </xsl:element>  
    </xsl:template>
    <xsl:template match="div[contains(@class,'instance')]">
        <xsl:variable name="created" select="translate(string(div[@class='date' and span[@class='label']='Created On']/span[@class='content']/abbr/@title),'-+: ','')"/>
        <xsl:variable name="modified" select="translate(string(div[@class='date' and span[@class='label']='Modified On']/span[@class='content']/abbr/@title),'-+: ','')"/>
        <note>
            <title><xsl:value-of select="h2[@class='fn']"/></title>
            <xsl:element name="content">
             <xsl:text disable-output-escaping="yes">&lt;![CDATA[&lt;?xml version="1.0" encoding="UTF-8"?&gt;
             &lt;!DOCTYPE en-note SYSTEM "http://xml.evernote.com/pub/enml2.dtd"&gt;
             </xsl:text>   
             <xsl:element name="en-note">
              <xsl:for-each select="div[contains(@class,'photo')][1]">
                <xsl:element name="en-media">
                 <xsl:variable name="filename" select="./a/@file | ./img/@src"/>   
                 <xsl:attribute name="hash">
                   <xsl:value-of select="utl:hash($filename)"/>  
                 </xsl:attribute>
                 <xsl:attribute name="type">
                    <xsl:text>image/</xsl:text><xsl:value-of select="utl:getFileExt(string($filename))"/>                     
                 </xsl:attribute>
                </xsl:element>    
              </xsl:for-each>   
              <xsl:choose>
                <xsl:when test="contains(@class,'type:Recipe')">
                 <ul>   
                  <xsl:value-of select="utl:makeList(string(div[span[@actual-name='ingredientsText']]/span[contains(@class,'content')]))" disable-output-escaping="yes"/>
                 </ul>
                 <div>
                  <xsl:value-of select="utl:lineBreak(string(div[span[@actual-name='preparationText']]/span[contains(@class,'content')]))"  disable-output-escaping="yes"/>
                 </div>
                </xsl:when>  
                <xsl:when test="contains(@class,'type:GeneralList')">
                 <xsl:for-each select="ul/li/div/div">
                  <xsl:variable name="checked" select="not(boolean(contains(@class,'unfinished')))"/>
                  <div>
                   <xsl:element name="en-todo">
                    <xsl:attribute name="checked"><xsl:value-of select="$checked"/></xsl:attribute> 
                    <xsl:value-of select="."/>
                   </xsl:element>   
                  </div>
                 </xsl:for-each>   
                </xsl:when>  
                <xsl:otherwise>
                 <xsl:copy-of select="div[span[@actual-name='text']]/span[contains(@class,'content')]"/>
                </xsl:otherwise>
              </xsl:choose>   
             </xsl:element>
             <xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
            </xsl:element>
             <created><xsl:value-of select="substring($created,1,string-length($created)-4)"/><xsl:text>Z</xsl:text></created>
             <modified><xsl:value-of select="substring($modified,1,string-length($modified)-4)"/><xsl:text>Z</xsl:text></modified>
             <xsl:for-each select="div[@class='meta' and span[@class='label']='Tags']/span[@class='content']/a[@rel='tag']">
                   <tag><xsl:value-of select="normalize-space()"/></tag>  
             </xsl:for-each>
             <xsl:for-each select="div[contains(@class,'attachment') and (contains(@class,'photo') or contains(@class,'file'))]|div[@class='photo']">
                <xsl:variable name="filepath">
                 <xsl:choose>
                    <xsl:when test="contains(@class,'file')"><xsl:value-of select="./a/@href"/></xsl:when>
                    <xsl:when test="contains(@class,'photo')"><xsl:value-of select="./a/@file | ./img/@src"/></xsl:when>
                 </xsl:choose>    
                </xsl:variable> 
                <xsl:variable name="filename">
                 <xsl:choose>
                    <xsl:when test="contains(@class,'file')"><xsl:value-of select="./a"/></xsl:when>
                    <xsl:when test="contains(@class,'photo')"><xsl:value-of select="utl:getFileName(string($filepath))"/></xsl:when>
                 </xsl:choose>    
                </xsl:variable> 
                <xsl:variable name="mimetype">
                 <xsl:choose>
                    <xsl:when test="contains(@class,'file')"><xsl:text>application/</xsl:text><xsl:value-of select="utl:getFileExt(string(./a/@href))"/></xsl:when>
                    <xsl:when test="contains(@class,'photo')"><xsl:text>image/</xsl:text><xsl:value-of select="utl:getFileExt(string($filepath))"/></xsl:when>
                 </xsl:choose>    
                </xsl:variable> 
                <resource>
                <data encoding="base64">
                  <xsl:if test="$filepath = ''">
                    <xsl:message terminate="yes">
                      <xsl:value-of select="."/>  
                    </xsl:message>
                  </xsl:if>  
                <xsl:value-of select="utl:toBase64(string($filepath))" disable-output-escaping="yes"/>   
                </data>   
                <mime><xsl:value-of select="$mimetype"/></mime>
                <resource-attributes>
                 <file-name><xsl:value-of select="$filename"/></file-name>   
                </resource-attributes>
                </resource>
             </xsl:for-each>
             <note-attributes>
                 <source-url><xsl:value-of select="div[span[@actual-name='url']]/span[contains(@class,'content')]"/></source-url>
             </note-attributes>
        </note>
    </xsl:template>
</xsl:stylesheet>
