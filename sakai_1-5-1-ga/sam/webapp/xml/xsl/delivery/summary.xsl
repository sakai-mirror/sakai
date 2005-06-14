<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: summary.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
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
<table width="90%" align="center"><tr><td><b><xsl:value-of select="stxx/form/xmlDeliveryActionForm/assessmentTitle" /> - 
           <xsl:value-of select="stxx/form/xmlDeliveryActionForm/username" /></b>
           </td><td align="right">
<xsl:if test="stxx/form/xmlDeliveryActionForm/endTime != ''" >
  <xsl:variable name="base"> <xsl:call-template name="baseHREF"/> </xsl:variable>
  <script LANGUAGE="JavaScript" src="{$base}js/progressBar.js" />

  <script LANGUAGE="JavaScript">
    var begin = new Date("<xsl:value-of select="stxx/form/xmlDeliveryActionForm/beginTime"/> ");
    var end = new Date("<xsl:value-of select="stxx/form/xmlDeliveryActionForm/endTime"/> ");
    var now = new Date("<xsl:value-of select="stxx/form/xmlDeliveryActionForm/currentTime"/> ");
  </script>	

  <script LANGUAGE="JavaScript" src="{$base}js/progressBar2.js" />
  </xsl:if>
    </td></tr>
      <tr><td>&#160;</td><td align="right">
  <xsl:variable name="base">
    <xsl:call-template name="baseHREF"/>
  </xsl:variable>
  <xsl:variable name="formname" select="concat($base,'asi/delivery/xmlAction.do')"/>
  <form action="{$formname}" method="post">
<!--    <input type="submit" name="Table of Contents" value="Table of Contents" /> -->
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">tableOfContents</xsl:attribute>
    <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.tableOfContents']" /></xsl:attribute>
  </input>
    </form>
  </td></tr>
    </table>
          <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>  
  <xsl:apply-templates select="stxx/form/xmlDeliveryActionForm" />
  <form action="{$base}asi/delivery/xmlAction.do" method="post">
  <xsl:apply-templates select="stxx" />
  </form>

</xsl:template>

<xsl:template match="stxx">

<table align="center" width="90%" height="30" class="border" border="0" cellpadding="0" cellspacing="0" bgcolor="#ccccff"><tr><td>
&#160;<b>Review</b></td></tr></table>


<table border="0" width="70%" align="center">
<tr><td>
<xsl:if test="number(form/xmlDeliveryActionForm/markedForReview/value[1]) &gt; -1">
<br /> <a><xsl:attribute name="href">xmlAction.do?markedItemIndex=<xsl:value-of select="form/xmlDeliveryActionForm/markedForReview/value[1]" /></xsl:attribute>
Review Marked Questions: 
</a>&#160;&#160;
<xsl:for-each select="form/xmlDeliveryActionForm/markedForReview/value">
<a>
<xsl:variable name="displayIndex" select="." />
<xsl:attribute name="href">xmlAction.do?markedItemIndex=<xsl:value-of select="$displayIndex" /></xsl:attribute>
<xsl:value-of select="$displayIndex+1" />
</a>&#160;&#160;
</xsl:for-each>
</xsl:if>

</td></tr>

<tr><td>
<xsl:if test="number(form/xmlDeliveryActionForm/blankItems/value[1]) &gt; -1">
<br /><a><xsl:attribute name="href">xmlAction.do?blankItemIndex=<xsl:value-of select="form/xmlDeliveryActionForm/blankItems/value[1]" /></xsl:attribute>
Reivew Unanswered Questions: 
</a>&#160;&#160;
<xsl:for-each select="form/xmlDeliveryActionForm/blankItems/value">
<a>
<xsl:variable name="displayIndex" select="." />
<xsl:attribute name="href">xmlAction.do?blankItemIndex=<xsl:value-of select="$displayIndex" /></xsl:attribute>
<xsl:value-of select="$displayIndex+1" />
</a>&#160;&#160;
</xsl:for-each>
</xsl:if>
</td></tr>
<tr> <td>
<br/>
	<a href="xmlAction.do?reviewAll=Review All">
	Review All Questions
	</a>&#160;&#160;
	</td> </tr>
  </table>
  <br />
  <table align="center" width="90%" height="30" class="border" border="0" cellpadding="0" cellspacing="0" bgcolor="#ccccff"><tr><td>
&#160;<b>Submit</b></td></tr></table>
  <p align="center">
<!--  <input type="submit" name="Submit" value="Submit" /> -->
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">submit</xsl:attribute>
    <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.submit']" /></xsl:attribute>
  </input>
  </p>
</xsl:template>

<!--
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
-->
<!-- ****** Display general information such as user and test information ****** -->
<xsl:template match="xmlDeliveryActionForm">
<table align="center" width="90%" height="30">
<tr>
           <td align="right"> 
           <xsl:apply-templates select="/stxx/form/xmlDeliveryActionForm/beginTime" />

</td></tr>
<tr><td colspan="2">
<br/>
<br />
You have seen all questions for this assessment and may now choose from the following options.

</td></tr>
</table>
</xsl:template>

<!-- ****** Calculate time remaining based on beginTime and endTime ****** -->
<xsl:template match="beginTime">
</xsl:template>
</xsl:stylesheet>

