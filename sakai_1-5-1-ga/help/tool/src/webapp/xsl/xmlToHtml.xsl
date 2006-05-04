<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html"/>

    <xsl:template match="document">
    <html>
    <head>
    </head>
       <xsl:apply-templates select="kbml/body" />
    </html>
    </xsl:template>
    
    <xsl:template match="body">
      <body>
        <xsl:apply-templates/>
      </body>
    </xsl:template>
    
    <xsl:template match="p">
    <p> 
      <xsl:apply-templates/>
      </p>
    </xsl:template>
    
    <xsl:template match="domain" />
    <xsl:template match="visibility" />

    <xsl:template match="ul">
    <ul>
    <xsl:apply-templates/>
    </ul>
    </xsl:template>
    
    <xsl:template match="ol">
    <ol>
    <xsl:apply-templates/>
    </ol>
    </xsl:template>
    
    <xsl:template match="li">
    <li>
       <xsl:apply-templates/>
    </li>
    </xsl:template>
    
    <xsl:template match="mi">
    <strong>
    <xsl:apply-templates/>
    </strong>
    </xsl:template>
    
   <xsl:template match="h3">
    <h3>
    <xsl:apply-templates/>
    </h3>
    </xsl:template>
    
    <xsl:template match="h4">
    <h4>
    <xsl:apply-templates/>
    </h4>
    </xsl:template>
    
    <xsl:template match="strong">
    <strong>
    <xsl:apply-templates/>
    </strong>
    </xsl:template>
    
    <xsl:template match="br">
    <xsl:copy-of select="."/>
    </xsl:template>
    
   
    <xsl:template match="kbh">
      <xsl:apply-templates />
    </xsl:template>
</xsl:stylesheet>