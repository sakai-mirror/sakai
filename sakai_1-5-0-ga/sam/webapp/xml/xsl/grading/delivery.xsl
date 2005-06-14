<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: delivery.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="assessment.xsl" />
<xsl:import href="section.xsl" />
<xsl:import href="item.xsl" />
<xsl:import href="../layout/header.xsl" />
<xsl:import href="../layout/footer.xsl" />
<xsl:import href="../layout/menu.xsl" />
<xsl:import href="defaultLayout.xsl" />
<xsl:include href="common.xsl"/>

<xsl:output method="html"/>

<!-- This template processes the root node ("/") -->
<xsl:template match="/">
 <xsl:call-template name="defaultLayout" />
</xsl:template>

<xsl:template name="head">
  	<link rel="stylesheet" type="text/css" href="{$CONTEXT_PATH}/css/navigo.css"/>
</xsl:template>

<xsl:template name="body" >
<table width="90%" align="center">
<tr>
<td>
    <b><xsl:value-of select="stxx/form/xmlDeliveryActionForm/assessmentTitle" /> - <xsl:apply-templates select="stxx/qti_result_report/result/context/name" /></b>        
</td>
<td></td>
<td align="right">
  </td>
</tr>
<tr>
  <xsl:variable name="base">
    <xsl:call-template name="baseHREF"/>
  </xsl:variable>
  <td align="right">
   <form method="post">
    <xsl:attribute name="action">
        <xsl:value-of select="concat($base,'asi/grading/gradingAction.do')"/>
    </xsl:attribute>
    <input>
     <xsl:attribute name="type">submit</xsl:attribute>
     <xsl:attribute name="name">tableOfContents</xsl:attribute>
     <xsl:attribute name="value">
         <xsl:value-of select="/stxx/applicationResources/key[@name='grading.button.tableOfContents']" />
    </xsl:attribute>
    </input>
  </form>
  </td>
  <td>
  <form method="get">
    <xsl:attribute name="action">
        <xsl:value-of select="concat($base, 'histogramScores.do')"/>
    </xsl:attribute>
    <input>
        <xsl:attribute name="type">hidden</xsl:attribute>
        <xsl:attribute name="name">id</xsl:attribute>
        <xsl:attribute name="value">
            <xsl:value-of select="/stxx/qti_result_report/result/context/generic_identifier[type_label='coreId']/identifier_string"/>
        </xsl:attribute>
    </input>
    <input>
        <xsl:attribute name="type">submit</xsl:attribute>
        <xsl:attribute name="name">statsView</xsl:attribute>
        <xsl:attribute name="value">
            <xsl:value-of select="/stxx/applicationResources/key[@name='grading.button.statsView']"/>
        </xsl:attribute>
    </input>
  </form>
  </td>
  <td>
  <form method="get">
    <xsl:attribute name="action">
        <xsl:value-of select="concat($base, 'totalScores.do')"/>
    </xsl:attribute>
    <input>
        <xsl:attribute name="type">hidden</xsl:attribute>
        <xsl:attribute name="name">id</xsl:attribute>
        <xsl:attribute name="value">
            <xsl:value-of select="/stxx/qti_result_report/result/context/generic_identifier[type_label='coreId']/identifier_string"/>
        </xsl:attribute>
    </input>
    <input>
        <xsl:attribute name="type">submit</xsl:attribute>
        <xsl:attribute name="name">action</xsl:attribute>
        <xsl:attribute name="value">
            <xsl:value-of select="/stxx/applicationResources/key[@name='grading.button.scoreView']"/>
        </xsl:attribute>
    </input>
    </form>
  </td>
  </tr>
  </table>
  <xsl:apply-templates select="stxx" />
</xsl:template>

<xsl:template match="name">
    <xsl:choose>
        <xsl:when test="contains(/stxx/qti_result_report/result/assessment_result/asi_metadata/asi_metadatafield[field_name='ASSESSMENT_RELEASED_TO']/field_value, 'Anonymous') or /stxx/qti_result_report/result/assessment_result/asi_metadata/asi_metadatafield[field_name='ANONYMOUS_GRADING']/field_value='TRUE'">
            <xsl:value-of select="/stxx/qti_result_report/result/assessment_result/@ident_ref"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="."/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template match="stxx">
  <xsl:variable name="base">
    <xsl:call-template name="baseHREF"/>
  </xsl:variable>
  <xsl:variable name="formname" select="concat($base,'asi/grading/gradingAction.do')"/>
  <form action="{$formname}" method="post"  name="ASIDeliveryForm">
  <xsl:apply-templates select="form" />
  <xsl:apply-templates select="assessment | section |item" />
  <table width="80%" align="center">
  <tr>
  <td>
  </td>
  <td align="center">
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">saveAndExit</xsl:attribute>
    <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='grading.button.saveAndExit']" /></xsl:attribute>
  </input>
  </td>
  <td align="right">
  <xsl:if test="/stxx/form/xmlDeliveryActionForm/previous ='true'">
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">previous</xsl:attribute>
    <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='grading.button.previous']" /></xsl:attribute>
  </input>
  </xsl:if>
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">saveAndContinue</xsl:attribute>
    <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='grading.button.saveAndContinue']" /></xsl:attribute>
  </input>
  </td>
  </tr>
  </table>
  </form>
</xsl:template>

<xsl:template match="form">
<xsl:apply-templates select="xmlDeliveryActionForm" />
</xsl:template>

<!-- ****** Display general information such as user and test information ****** -->
<xsl:template match="xmlDeliveryActionForm">
<xsl:apply-templates select="/stxx/form/xmlDeliveryActionForm/sectionPM/section" />

</xsl:template>


<!-- ****** Calculate time remaining based on beginTime and endTime ****** -->
<xsl:template match="beginTime">


</xsl:template>

</xsl:stylesheet>

