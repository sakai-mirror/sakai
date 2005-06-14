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
 <xsl:variable name="sectionIdentRef" select="@ident"/>
<xsl:choose>
<xsl:when test="@title !='DEFAULT'" >
<table align="center" width="95%" height="30" class="border" border="1" bgcolor="#cccccc" cellspacing="0">
<tr>
<td><b>Section: <xsl:value-of select="@title" /></b></td>
<td align="right"><b><xsl:value-of select="format-number(/stxx/qti_result_report/result/assessment_result/section_result[@ident_ref=$sectionIdentRef]/outcomes/score/score_value, '0.00')"/>/<xsl:value-of select="format-number(/stxx/qti_result_report/result/assessment_result/section_result[@ident_ref=$sectionIdentRef]/outcomes/score/score_max, '0.00')"/>&#160;<xsl:value-of select="/stxx/applicationResources/key[@name='grading.points']"/></b></td>
</tr>
</table>
<table align="center" width="90%" border="0">
    <tr>
        <td colspan="2"><xsl:apply-templates select="presentation_material/flow_mat" /></td>
    </tr>
    <tr>
        <td colspan="2"><b><xsl:value-of select="/stxx/applicationResources/key[@name='grading.section.comments']"/></b></td>
    </tr>
    <tr>
        <td><xsl:element name="input">
            <xsl:attribute name="type">hidden</xsl:attribute>
            <xsl:attribute name="name">sectionIds</xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="@ident"/></xsl:attribute>
        </xsl:element>
        <xsl:element name="textarea">
            <xsl:attribute name="name"><xsl:value-of select="$sectionIdentRef"/>.comments</xsl:attribute>
            <xsl:attribute name="id"><xsl:value-of select="$sectionIdentRef"/>.comments</xsl:attribute>
            <xsl:attribute name="rows">10</xsl:attribute>
            <xsl:attribute name="cols">80</xsl:attribute>
            <xsl:value-of select="/stxx/qti_result_report/result//section_result[@ident_ref=$sectionIdentRef]/extension_section_result/comment()"/>
        </xsl:element></td>
        <td align="left" valign="top">
            <a>
            <xsl:attribute name="href">javascript:toggleToolbar('<xsl:value-of select="$sectionIdentRef"/>.comments')</xsl:attribute>
              Show/Hide <br />Editor
            </a>
        </td>
     </tr>
     <tr>
         <td colspan="2"><xsl:apply-templates select="section | item"></xsl:apply-templates></td>
     </tr>
</table>
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