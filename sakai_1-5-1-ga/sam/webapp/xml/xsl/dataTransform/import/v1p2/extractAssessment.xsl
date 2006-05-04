<?xml version="1.0" encoding="UTF-8" ?>
<!--
 * <p>Copyright: Copyright (c) 2005 Sakai</p>
 * <p>Description: QTI Persistence XML to XML Transform for Import</p>
 * @author <a href="mailto:esmiley@stanford.edu">Ed Smiley</a>
 * @version $Id: extractAssessment.xsl,v 1.3.2.1 2005/03/10 18:47:53 esmiley.stanford.edu Exp $
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" doctype-public="-//W3C//DTD HTML 4.01//EN"
 doctype-system="http://www.w3.org/TR/html4/strict.dtd"/>

<xsl:template match="/">
<assessmentData>
  <ident><xsl:value-of select="//assessment/@ident" /></ident>
  <title><xsl:value-of select="//assessment/@title" /></title>
  <displayName><xsl:value-of select="//assessment/@title" /></displayName>
  <description>
     <xsl:value-of select="//assessment//presentation_material/flow_mat/material/mattext"/>
  </description>
  <duration><xsl:value-of select="//assessment//duration" /></duration>
  <comments>
  Imported assessment(<xsl:value-of select="//assessment/@ident" />):
  '<xsl:value-of select="//assessment/@title" />'. (QTI 1.2)
  Using Sakai Assessment Manager
  Sakai Project
  Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
  Licensed under the Educational Community License Version 1.0
  http://cvs.sakaiproject.org/licenses/license_1_0.html
  </comments>
  <xsl:for-each select="//assessment/qtimetadata/qtimetadatafield">
    <xsl:variable name="metadata">meta</xsl:variable>
    <xsl:element name="metadata">
     <xsl:attribute name="type">list</xsl:attribute>
     <xsl:value-of select="fieldlabel"/>|<xsl:value-of select="fieldentry"/>
    </xsl:element>
  </xsl:for-each>
</assessmentData>
</xsl:template>

</xsl:stylesheet>