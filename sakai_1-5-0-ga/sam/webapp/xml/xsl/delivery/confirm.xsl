<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: confirm.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
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

<!-- This template processes the root node ("/") -->
<xsl:template name="body">
<xsl:if test="stxx/form/xmlDeliveryActionForm/endTime != ''" >
<script LANGUAGE="JavaScript" src="../../js/progressBar.js">
</script>
<script LANGUAGE="JavaScript">

      var begin = new Date("<xsl:value-of select="stxx/form/xmlDeliveryActionForm/beginTime"/> ");
      var end = new Date("<xsl:value-of select="stxx/form/xmlDeliveryActionForm/endTime"/> ");
      var now = new Date("<xsl:value-of select="stxx/form/xmlDeliveryActionForm/currentTime"/> ");

</script>
<script LANGUAGE="JavaScript" src="../../../js/progressBar2.js">
  </script>
  
  </xsl:if>
  <xsl:apply-templates select="stxx/form/xmlDeliveryActionForm" />
    <xsl:variable name="base">
    <xsl:call-template name="baseHREF"/>
  </xsl:variable>
  <xsl:variable name="formname" select="concat($base,'asi/delivery/xmlAction.do')"/>
  <form action="{$formname}" method="post">
  <xsl:apply-templates select="stxx" />
  </form>

</xsl:template>

<xsl:template match="stxx">

<table align="center" width="90%" height="30" class="border" border="0" cellpadding="0" cellspacing="0" bgcolor="#ccccff"><tr><td>
<b><xsl:value-of select="/stxx/applicationResources/key[@name='delivery.submit']" /></b></td></tr></table>


<table border="0" width="80%" align="center">
<tr><td>
<br /> 
<b><xsl:value-of select="/stxx/applicationResources/key[@name='delivery.reviewMarked']" />: &#160;&#160;
<xsl:if test="number(form/xmlDeliveryActionForm/markedForReview/value[1]) &gt; -1">
<xsl:value-of select="/stxx/applicationResources/key[@name='delivery.none']" />
</xsl:if>
<xsl:for-each select="form/xmlDeliveryActionForm/markedForReview/value">

<xsl:variable name="displayIndex" select="." />
<xsl:value-of select="$displayIndex+1" />
&#160;&#160;
</xsl:for-each>
</b>
</td></tr>

<tr><td>
<br /><b>
<xsl:value-of select="/stxx/applicationResources/key[@name='delivery.reviewUnanswered']" />: 
&#160;&#160;
<xsl:if test="number(form/xmlDeliveryActionForm/blankItems/value[1]) &lt; 0">
<xsl:value-of select="/stxx/applicationResources/key[@name='delivery.none']" />
</xsl:if>
<xsl:for-each select="form/xmlDeliveryActionForm/blankItems/value">
<xsl:variable name="displayIndex" select="." />
<xsl:value-of select="$displayIndex+1" />
&#160;&#160;
</xsl:for-each>
</b>
</td></tr>
<tr><td>
  <br />
  Continuing will submit your answers and editing will not be possible. Do you want to submit
without further review of the questions?
</td></tr>
<tr><td align="right">
<br />
<!--
  <input type="submit" name="Review Questions" value="Review Questions" />&#160;&#160;
  <input type="submit" name="Submit" value="Submit" />
  -->
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">reviewQuestions</xsl:attribute>
    <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.reviewQuestions']" /></xsl:attribute>
  </input>
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">Submit</xsl:attribute>
    <xsl:attribute name="value"><xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.submit']" /></xsl:attribute>
  </input>
  
</td></tr>
  </table>
  <br />
</xsl:template>


<xsl:template match="markedForReview">
<xsl:apply-templates select="value" />
</xsl:template>

<xsl:template match="value">
<a>
<xsl:variable name="displayIndex" select="." />
<xsl:attribute name="href">xmlAction.do?markedItemIndex=<xsl:value-of select="$displayIndex" /></xsl:attribute>
<xsl:value-of select="$displayIndex+1" />
</a>&#160;
</xsl:template>

<!-- ****** Display general information such as user and test information ****** -->
<xsl:template match="xmlDeliveryActionForm">
<table align="center" width="90%" height="30">
<tr><td><b><xsl:value-of select="assessmentTitle" /> - 
           <xsl:value-of select="username" /></b>
           </td>
           <td align="right"> 
           <xsl:apply-templates select="/stxx/form/xmlDeliveryActionForm/beginTime" />

</td></tr>
<tr><td colspan="2">
<br/>

</td></tr>
</table>
</xsl:template>

<!-- ****** Calculate time remaining based on beginTime and endTime ****** -->
<xsl:template match="beginTime">
</xsl:template>
</xsl:stylesheet>

