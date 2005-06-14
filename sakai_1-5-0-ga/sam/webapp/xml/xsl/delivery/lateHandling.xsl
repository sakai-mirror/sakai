<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: lateHandling.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
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
<table border="0" width="90%">
<tr><td valign="top">
    </td>
    <td align="right" valign="top">
    <a href="/Navigo/asi/select/selectAssessment.do">Test and Survey</a>
    </td>
</tr>
</table>

  <form action="/Navigo/asi/delivery/xmlAction.do" method="post">
  <xsl:apply-templates select="stxx" />
  </form>
</xsl:template>

<xsl:template match="stxx">
Due time has passed, you can not continue to take this test any more. 
<table align="center" width="90%" height="30" class="border" border="0" cellpadding="0" cellspacing="0" bgcolor="#ccccff"><tr><td>
&#160;<b><xsl:value-of select="form/xmlDeliveryActionForm/assessmentTitle"/> Submission Information</b></td></tr>
<tr>
<td>
</td>
</tr>
</table>

  <table border="1" width="50%" align="center">
   	<tr> <td ><b>General Test Information</b></td></tr>
<tr><td ><font color="#663366">Course:  Navigo UI Design</font>
</td></tr><tr><td><font color="#663366">Instructor:  Intermill C</font>
<tr><td><b>Test Title: </b><xsl:value-of select="form/xmlDeliveryActionForm/assessmentTitle"/></td></tr>
</td></tr>
<tr><td><b>&#160;</b></td></tr>
<tr><td><b>Submission Information</b></td></tr>
	<tr> <td ><b>Time Submit: </b><xsl:apply-templates select="form/xmlDeliveryActionForm/submissionTime" /> </td> </tr>
	<tr> <td><b>Confirmation Number: </b><xsl:value-of select="form/xmlDeliveryActionForm/assessmentId" /></td> </tr>
  </table>
  <table align="center"><tr><td>
  <b><a href="/Navigo/asi/select/selectAssessment.do" >Return to Tests and Surveys</a></b>
  </td></tr></table>
<br />
</xsl:template>
</xsl:stylesheet>