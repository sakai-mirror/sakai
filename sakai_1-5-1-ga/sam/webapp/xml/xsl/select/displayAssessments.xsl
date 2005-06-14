<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: displayAssessments.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="../layout/header.xsl" />
<xsl:import href="../layout/footer.xsl" />
<xsl:import href="../layout/menu.xsl" />
<xsl:import href="../layout/defaultLayout.xsl" />

<xsl:output method="html"/>
<!-- This template processes the root node ("/") -->
<xsl:template match="/">
<xsl:call-template name="defaultLayout" />
</xsl:template>

<xsl:template name="body">
<xsl:variable name="base"> <xsl:call-template name="baseHREF"/> </xsl:variable>
<script src="{$base}js/tigra_tables.js" type="text/javascript"/>
  <h3>
    &#160;&#160;Assessments
  </h3>
  <xsl:apply-templates select="stxx" />
</xsl:template>

<xsl:template match="stxx">
 <p align="center" >
 
  <table width="90%" class="border" bgcolor="ccccff"><tr><td><b class="navigo_border_text_font">Take an assessment</b></td></tr></table>
  <table id="test_survey_table" width="80%"><tr><td>
 The assessments listed below are currently available for you to take.  To begin, click on the assessment title.</td></tr>
  <tr><td><xsl:apply-templates select="authorizedAssessments/activeAssessments" /></td></tr>
  </table>
  <br />
  <!-- New takeable assessment Table, will replace the section above -->
  <table id="select_table" border="0" width="70%">
  <xsl:apply-templates select="form/selectAssessmentForm/takeableAssessments" />
  <xsl:apply-templates select="form/selectAssessmentForm/lateHandlingAssessments" />
			<script language="JavaScript">
				tigra_tables('select_table', 0, 0, '#ffffff', '#ffffcc', '#ffcc66', '#cccccc');
			</script>
  </table>
  
  <table width="90%" class="border" bgcolor="ccccff">
  <tr><td><b class="navigo_border_text_font">Review an assessment</b>
  </td></tr></table>
  <!-- Assessments for Review -->
  <table width="90%">
  <tr><td>You have completed the assessments listed below and they are currently available for you to review.  Click on the assessment title to review your responses (and instructor feedback if  available).</td></tr></table>
  <table id="review_table" width="80%" border="1"><tr><th align="center">
  Assessment Title</th><th align="center"> 
  Statistics</th><th align="center">
 <!-- Grade  </th><th align="center"> 
  Raw Score    </th><th align="center"> 
  -->
  Time </th><th align="center">  
  Submitted</th></tr>
  <xsl:apply-templates select="form/selectAssessmentForm/reviewAssessments" />
  <script language="JavaScript">tigra_tables('review_table', 1, 0, '#ffffff', '#ccccff', '#ffccff', '#cc99ff');</script>
  </table>
</p>
<br />
<br />
</xsl:template>

  <!-- =================================== -->
  <!-- child element: takeableAssessments    -->
  <!-- =================================== -->
<xsl:template match="takeableAssessments">
  <xsl:for-each select="value">
  <tr><td>
  <b>
  <xsl:variable name="base"> <xsl:call-template name="baseHREF"/> </xsl:variable>
  <xsl:variable name="assessmentId"><xsl:value-of select="ident" /></xsl:variable>
  <xsl:variable name="link1" select="concat($base,'asi/delivery/xmlSelectAction.do?assessmentId=')"/>
  <xsl:variable name="link" select="concat($link1,$assessmentId)"/>
  <a href="{$link}"><xsl:value-of select="title" /></a>
  </b>
  <br />
  &#160; &#160; &#160; &#160; Due: <xsl:value-of select="dueDate" />
  </td></tr>
  </xsl:for-each>
</xsl:template>

  <!-- ======================================== -->
  <!-- child element: lateHandlingAssessments   -->
  <!-- ======================================== -->
<xsl:template match="lateHandlingAssessments">

  <xsl:for-each select="value">
  <tr><td>
  <b>
  <xsl:variable name="base"> <xsl:call-template name="baseHREF"/> </xsl:variable>
  <xsl:variable name="assessmentId"><xsl:value-of select="ident" /></xsl:variable>
  <xsl:variable name="link1" select="concat($base,'asi/delivery/xmlSelectAction.do?assessmentId=')"/>
  <xsl:variable name="link" select="concat($link1,$assessmentId)"/>
  <a href="{$link}"><xsl:value-of select="title" /></a>
  </b>
  <br />
  &#160; &#160; &#160; &#160; <font color="red">Due: <xsl:value-of select="dueDate" /></font>
  </td></tr>
  </xsl:for-each>
</xsl:template>

  <!-- =================================== -->
  <!-- child element: reviewAssessments    -->
  <!-- =================================== -->
<xsl:template match="reviewAssessments">

  <xsl:for-each select="value">
  <tr><td>
   <b>
  <xsl:variable name="base"> <xsl:call-template name="baseHREF"/> </xsl:variable>
  <xsl:variable name="assessmentId"><xsl:value-of select="ident" /></xsl:variable>
  <xsl:variable name="link1" select="concat($base,'asi/review/xmlSelectAction.do?assessmentId=')"/>
  <xsl:variable name="link" select="concat($link1,$assessmentId)"/>
  <a href="{$link}"><xsl:value-of select="title" /></a>
  </b> 
  </td><td> 
  n/a</td><td>
  <!--<xsl:value-of select="grade" /></td><td> 
  <xsl:value-of select="rawScore" /></td><td>  -->
  <xsl:value-of select="time" /></td><td>  
  <xsl:value-of select="submittedTime" /></td></tr>
  </xsl:for-each>
</xsl:template>

<xsl:template match="activeAssessments">
<table id="select_table" border="0" width="70%">
<xsl:for-each select="assessment">
<tr>
<td>
<b>
<xsl:variable name="base"> <xsl:call-template name="baseHREF"/> </xsl:variable>
<xsl:variable name="assessmentId"><xsl:value-of select="@ident" /></xsl:variable>
<xsl:variable name="link1" select="concat($base,'asi/delivery/xmlSelectAction.do?assessmentId=')"/>
<xsl:variable name="link" select="concat($link1,$assessmentId)"/>
<a href="{$link}"><xsl:value-of select="@title" /></a>
</b>

<br />
&#160; &#160; &#160; &#160; Due: <xsl:value-of select="@expirationDate" /></td>
</tr>
</xsl:for-each>
</table>
			<script language="JavaScript">
				tigra_tables('select_table', 0, 0, '#ffffff', '#ffffcc', '#ffcc66', '#cccccc');
			</script>
</xsl:template>

<!-- *****************qtimetadatafield *****************-->
<xsl:template  name="fieldlabel">
        <xsl:param name="fl"/>
        <xsl:for-each select="qtimetadata/qtimetadatafield/fieldlabel">
                <xsl:variable name="fieldlabel" select= "."/>
                <xsl:if test="$fieldlabel= $fl">
                        <xsl:variable name="fieldentry" select= "../fieldentry"/>
                        <xsl:value-of select="$fieldentry" />
                </xsl:if>
        </xsl:for-each>
</xsl:template>
</xsl:stylesheet>

