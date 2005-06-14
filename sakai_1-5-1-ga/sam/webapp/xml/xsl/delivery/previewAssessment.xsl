<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: previewAssessment.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="assessment.xsl" />
<xsl:import href="section.xsl" />
<xsl:import href="item.xsl" />
<xsl:import href="../layout/header.xsl" />
<xsl:import href="../layout/footer.xsl" />
<xsl:import href="../layout/menu.xsl" />
<xsl:import href="../layout/defaultLayout.xsl" />

<xsl:output method="html"/>

<!-- This template processes the root node ("/") -->
<xsl:template match="/">
 <xsl:call-template name="defaultLayout" />
</xsl:template>

<xsl:template name="body" >
<table width="90%" align="center">
<tr><td>
    <b><xsl:value-of select="stxx/form/xmlDeliveryActionForm/assessmentTitle" /> - 
           <xsl:value-of select="stxx/form/xmlDeliveryActionForm/username" />
    </b>        
  </td>
<td align="right">
  <xsl:if test="stxx/form/xmlDeliveryActionForm/endTime != ''" >
  <div id="progressBar" >
  <xsl:variable name="base"> <xsl:call-template name="baseHREF"/> </xsl:variable>
  <script LANGUAGE="JavaScript" src="{$base}js/progressBar.js" />
  <script LANGUAGE="JavaScript">
    var begin = new Date("<xsl:value-of select="stxx/form/xmlDeliveryActionForm/beginTime"/> ");
    var end = new Date("<xsl:value-of select="stxx/form/xmlDeliveryActionForm/endTime"/> ");
    var now = new Date("<xsl:value-of select="stxx/form/xmlDeliveryActionForm/currentTime"/> ");
  </script>	
  <script LANGUAGE="JavaScript" src="{$base}js/progressBar2.js" />
  </div>
  <INPUT ID="progress_bar_btn" TYPE="button" VALUE="Show Progress Bar" onclick="handleClick()" />
  </xsl:if>
  </td>
</tr>
<tr><td>&#160;</td><td align="right">
  <xsl:if test="stxx/form/xmlDeliveryActionForm/navigation != 'LINEAR'" >
  <xsl:variable name="base">
    <xsl:call-template name="baseHREF"/>
  </xsl:variable>
  <xsl:variable name="formname" select="concat($base,'asi/delivery/xmlAction.do')"/>
   <form action="{$formname}" method="post">
    <input>
     <xsl:attribute name="type">submit</xsl:attribute>
     <xsl:attribute name="name">tableOfContents</xsl:attribute>
     <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.tableOfContents']" /></xsl:attribute>
     <xsl:attribute name="disabled">disabled</xsl:attribute>
    </input>
    </form>
  </xsl:if>
  </td></tr>
  </table>
  <xsl:apply-templates select="stxx" />
</xsl:template>

<xsl:template match="stxx">
  <xsl:apply-templates select="form" />
  <xsl:variable name="base">
    <xsl:call-template name="baseHREF"/>
  </xsl:variable>
  <xsl:variable name="formname" select="concat($base,'asi/delivery/xmlAction.do')"/>
  <form action="{$formname}" method="post"  name="ASIDeliveryForm">
  <xsl:apply-templates select="assessment | section |item" />
  <table width="80%" align="center">
    <tr><td>
    <xsl:choose>
      <xsl:when test="/stxx/form/xmlDeliveryActionForm/reviewMarked = 'true'">
        <input>
          <xsl:attribute name="type">submit</xsl:attribute>
          <xsl:attribute name="name">cancelReview</xsl:attribute>
          <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.cancelReview']" /></xsl:attribute>
       <xsl:attribute name="disabled">disabled</xsl:attribute>
        </input>
      </xsl:when>
      <xsl:when test="/stxx/form/xmlDeliveryActionForm/reviewAll = 'true'">
        <input>
          <xsl:attribute name="type">submit</xsl:attribute>
          <xsl:attribute name="name">cancelReview</xsl:attribute>
          <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.cancelReview']" /></xsl:attribute>
          <xsl:attribute name="disabled">disabled</xsl:attribute>
        </input>
      </xsl:when>
      <xsl:when test="/stxx/form/xmlDeliveryActionForm/reviewBlank = 'true'">
        <input>
          <xsl:attribute name="type">submit</xsl:attribute>
          <xsl:attribute name="name">cancelReview</xsl:attribute>
          <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.cancelReview']" /></xsl:attribute>
          <xsl:attribute name="disabled">disabled</xsl:attribute>
        </input>
      </xsl:when> 
      <xsl:when test="/stxx/form/xmlDeliveryActionForm/navigation != 'LINEAR'">

       <xsl:if test="/stxx/form/xmlDeliveryActionForm/previous ='true'">
       <input>
         <xsl:attribute name="type">submit</xsl:attribute>
         <xsl:attribute name="name">previous</xsl:attribute>
         <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.previous']" /></xsl:attribute>
         <xsl:attribute name="disabled">disabled</xsl:attribute>
       </input>
       </xsl:if>
      </xsl:when>
    <xsl:otherwise>
    </xsl:otherwise>
  </xsl:choose>
  </td>
  <td align="center">
<!--    <input type="submit" name="Save_and_Exit" value="Save and Exit" /> -->
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">saveAndExit</xsl:attribute>
    <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.saveAndExit']" /></xsl:attribute>
    <xsl:attribute name="disabled">disabled</xsl:attribute>
  </input>
  </td>
  
 <td align="center">
  <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedback = 'IMMEDIATE'" > 
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">showFeedback</xsl:attribute>
    <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.showFeedback']" /></xsl:attribute>
    <xsl:attribute name="disabled">disabled</xsl:attribute>
  </input>
  </xsl:if>
 </td>
  <td align="right">
<!--  <input type="submit" name="Save_and_Continue" value="Save and Continue" /> -->
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">saveAndContinue</xsl:attribute>
    <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.saveAndContinue']" /></xsl:attribute>
    <xsl:attribute name="disabled">disabled</xsl:attribute>
  </input>
 
    </td></tr>
    </table>
  </form>
</xsl:template>

<xsl:template match="form">
<xsl:apply-templates select="xmlDeliveryActionForm" />
</xsl:template>

<!-- ****** Display general information such as user and test information ****** -->
<xsl:template match="xmlDeliveryActionForm">
<xsl:variable name="questionLayout" select="questionLayout" />
<xsl:if test="$questionLayout = 'I'">
<xsl:apply-templates select="/stxx/form/xmlDeliveryActionForm/sectionPM/section" />
</xsl:if>
</xsl:template>


<!-- ****** Calculate time remaining based on beginTime and endTime ****** -->
<xsl:template match="beginTime">


</xsl:template>

</xsl:stylesheet>

