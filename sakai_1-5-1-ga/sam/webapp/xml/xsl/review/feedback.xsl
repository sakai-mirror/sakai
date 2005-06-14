<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: feedback.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
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

<xsl:template name="body" >
   <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
<table border="0" width="90%">
<tr><td valign="top">
    <h3><xsl:value-of select="/stxx/applicationResources/key[@name='review.beginTest']" /></h3>
    </td>
    <td align="right" valign="top">
    <a href="{$base}asi/select/selectAssessment.do">Test and Survey</a>
    </td>
</tr>
</table>
    <xsl:variable name="base">
    <xsl:call-template name="baseHREF"/>
  </xsl:variable>
  <xsl:variable name="formname" select="concat($base,'asi/review/reviewAction.do')"/>
  <form action="{$formname}" method="post">
  <xsl:apply-templates select="stxx" />
 <p align="center"> 
 <table border="0" width="400"><tr><td align="left">
<!--  <input type="submit" name="Return to Tests and Surveys" value="Return to Tests and Surveys" />-->
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">returnToTestsAndSurveys</xsl:attribute>
    <xsl:attribute name="value"><xsl:value-of select="/stxx/applicationResources/key[@name='review.button.returnToTestsAndSurveys']" /></xsl:attribute>
  </input>
  </td><td align="right">

  </td></tr>
  </table>
</p>
  </form>

</xsl:template>

<xsl:template match="stxx">
  <table width="90%" class="border" bgcolor="ccccff">
  <tr><td><b>
  Review an Assessment</b>
  </td></tr>
  </table>
    <table width="80%" border="0">
  <tr><td>
    <xsl:value-of select="//xmlDeliveryActionForm/errorMessage" />
  </td></tr>
  </table>
</xsl:template>

</xsl:stylesheet>

