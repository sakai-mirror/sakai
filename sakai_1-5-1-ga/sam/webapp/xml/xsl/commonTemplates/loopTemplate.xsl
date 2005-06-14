<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template name="loop">
        <xsl:param name="counter"/>
        <xsl:param name="increment"/>
        <xsl:param name="end"/>
        <xsl:if test="$counter &lt;= $end">
            <xsl:value-of select="$counter"/> .<br/>
            <xsl:call-template name="loop">
                <xsl:with-param name="counter" select="$counter + $increment"/>
                <xsl:with-param name="increment" select="$increment"/>
                <xsl:with-param name="end" select="$end"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <xsl:template name="createOptionsinLoop">
        <xsl:param name="counter"/>
        <xsl:param name="increment"/>
        <xsl:param name="end"/>
         <xsl:param name="selected"/>  
        <xsl:if test="$counter &lt;= $end">
            <option value="{$counter}">
              <xsl:if test="$counter=$selected">
                   <xsl:attribute name="selected">true</xsl:attribute>
               </xsl:if>  
                <xsl:value-of select="$counter"/>
            </option>
            <xsl:call-template name="createOptionsinLoop">
                <xsl:with-param name="counter" select="$counter + $increment"/>
                <xsl:with-param name="increment" select="$increment"/>
                <xsl:with-param name="end" select="$end"/>
                  <xsl:with-param name="selected" select="$selected"/>  
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
