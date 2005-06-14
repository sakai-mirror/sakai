<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: section.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:preserve-space elements="flow" />
<xsl:output method="html"/>

<xsl:template match="/">
<html>
  <head>
    <title>Navigo Assessment Item Display</title>
  </head>
  <body>
  <xsl:apply-templates select="stxx" />
  </body>
</html>
</xsl:template>

<xsl:template match="stxx">
  <xsl:apply-templates select="section" />
</xsl:template>

<xsl:template match="section" >
<xsl:choose>
<xsl:when test="@title !='DEFAULT'" >
<table align="center" width="95%" height="30" class="border" border="0" bgcolor="#ccccff">
<tr><td>
<b class="navigo_border_text_font">Section: <xsl:value-of select="@title" /></b>
</td></tr></table>
<table align="center" width="90%" border="0">
<tr><td>
<xsl:apply-templates select="presentation_material" />
</td></tr><tr><td>
  <xsl:apply-templates select="section | item"></xsl:apply-templates>
  </td></tr></table>
  </xsl:when>
    <xsl:otherwise>
<!--  <table align="center" width="90%" border="0">
  <tr><td>
  -->
  <xsl:apply-templates select="section | item"></xsl:apply-templates>
<!--  </td></tr></table> -->
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
</xsl:stylesheet>