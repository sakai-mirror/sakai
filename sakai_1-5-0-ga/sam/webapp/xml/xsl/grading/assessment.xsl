<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: assessment.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
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
  <xsl:apply-templates select="assessment" />
</xsl:template>

<xsl:template match="assessment" >
  <xsl:apply-templates select="section" />
</xsl:template>

</xsl:stylesheet>