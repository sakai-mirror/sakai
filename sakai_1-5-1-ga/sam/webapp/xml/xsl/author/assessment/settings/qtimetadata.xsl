<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
* <p>Title: NavigoProject.org</p>
* <p>Description:Assessment Settings Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: qtimetadata.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
  <xsl:template match="qtimetadatafield" mode="display">
    <xsl:param name="req_fieldlabel"/>
    <xsl:for-each select="fieldlabel">
      <xsl:variable name="label" select="."/>
      <xsl:if test="$label=$req_fieldlabel">
       <xsl:variable name="fieldentry" select="../fieldentry"/>
       <xsl:if test="$fieldentry='True'">SHOW</xsl:if>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  <!--**********************************************************************************-->
  <xsl:template match="qtimetadatafield">
    <xsl:param name="keyName"/>
    <xsl:param name="keyCompare"/>
    <xsl:param name="display"/>
    <xsl:param name="return_xpath"/>
    <xsl:param name="fieldlabel_value"/>
     <xsl:apply-templates select="fieldlabel">
      <xsl:with-param name="keyName" select="$keyName"/>
      <xsl:with-param name="keyCompare" select="$keyCompare"/>
      <xsl:with-param name="display" select="$display"/>
      <xsl:with-param name="pos" select="position()"/>
      <xsl:with-param name="return_xpath" select="$return_xpath"/>
      <xsl:with-param name="fieldlabel_value" select="$fieldlabel_value"/>
    </xsl:apply-templates>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="fieldlabel">
    <xsl:param name="keyName"/>
    <xsl:param name="keyCompare"/>
    <xsl:param name="display"/>
    <xsl:param name="pos"/>
    <xsl:param name="return_xpath"/>
    <!-- optional // returns xpath-->
    <xsl:param name="fieldlabel_value"/>
    <!-- optional // returns value of fieldlabel-->
    <xsl:variable name="fieldlabel" select="."/>
    <xsl:if test="$fieldlabel=$keyName">
      <xsl:variable name="xpath" select="concat('stxx/questestinterop/assessment/qtimetadata/qtimetadatafield[', $pos,']/fieldentry')"/>
      <xsl:variable name="xpathFieldlabel" select="concat('stxx/questestinterop/assessment/qtimetadata/qtimetadatafield[', $pos,']/fieldlabel')"/>
      <xsl:variable name="fieldentry" select="../fieldentry"/>
      <xsl:variable name="xpathNEW" select="concat('questestinterop/assessment/qtimetadata/qtimetadatafield[', $pos,']/fieldentry')"/>
     <xsl:choose>
       <!--returns the xpath of fieldentry -->  
        <xsl:when test="$return_xpath='xpath'">
          <xsl:value-of select="$xpath"/>
        </xsl:when>
        <!--returns the xpath of fieldlabel -->
        <xsl:when test="$return_xpath='xpathFieldlabel'">
          <xsl:value-of select="$xpathFieldlabel"/>
        </xsl:when>
       <!--returns the position of fieldlabel -->
        <xsl:when test="$return_xpath='position'">
          <xsl:value-of select="$pos"/>
        </xsl:when>
        <xsl:otherwise>
         <xsl:choose>
            <xsl:when test="$fieldentry=$keyCompare">
              <input checked="true"  id="{$xpathNEW}" name="{$xpathNEW}" type="{$display}"/>
              <input name="{$keyName}"   type="hidden" value="{$pos}"/>
            </xsl:when>
            <xsl:otherwise>
              <input name="{$xpathNEW}" id="{$xpathNEW}" type="{$display}"/>
              <input name="{$keyName}" type="hidden" value="{$pos}"/>
            </xsl:otherwise>
          </xsl:choose>  
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
