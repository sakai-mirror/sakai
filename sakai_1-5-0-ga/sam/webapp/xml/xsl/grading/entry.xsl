<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: entry.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
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
<table border="0" width="90%">
<tr><td valign="top">
    <h3><xsl:value-of select="/stxx/applicationResources/key[@name='delivery.beginTest']" /></h3>
    </td>
    <td align="right" valign="top">
    <a href="/Navigo/asi/select/selectAssessment.do">Test and Survey</a>
    </td>
</tr>
</table>
    <xsl:variable name="base">
    <xsl:call-template name="baseHREF"/>
  </xsl:variable>
  <xsl:variable name="formname" select="concat($base,'asi/delivery/xmlAction.do')"/>
  <form action="{$formname}" method="post">
  <xsl:apply-templates select="stxx" />
 <p align="center"> 
 <table border="0" width="400"><tr><td align="left">
<!--  <input type="submit" name="Return to Tests and Surveys" value="Return to Tests and Surveys" />-->
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">returnToTestsAndSurveys</xsl:attribute>
    <xsl:attribute name="value"><xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.returnToTestsAndSurveys']" /></xsl:attribute>
  </input>
  </td><td align="right">
<!--  <input type="submit" name="Begin Test" value="Begin Test" /> -->
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">beginTest</xsl:attribute>
    <xsl:attribute name="value"><xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.beginTest']" /></xsl:attribute>
  </input>
  </td></tr>
  </table>
</p>
  </form>

</xsl:template>

<xsl:template match="stxx">
<p align="center">
  <xsl:apply-templates select="assessment" />
</p>
</xsl:template>

<xsl:template match="assessment">

<table width="90%" height="30" class="border" border="0" cellpadding="0" cellspacing="0" bgcolor="#ccccff"><tr><td>
&#160;<b><xsl:value-of select="@title" /> <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.assessmentInformation']" /></b></td></tr></table>
	<!-- Note From Instructor:-->
	<xsl:value-of select="rubric" />

  <table border = "1" width = "400" border-style="solid">
	<tr> <td align ="left"><b><xsl:value-of select="/stxx/applicationResources/key[@name='delivery.generalInformation']" /></b></td></tr>
        <tr><td align="left" ><font color="#663366"><b>Course: </b> Test for CAS Migration</font></td></tr>
        <tr><td align="left" ><font color="#663366"><b>Instructor: </b> Intermill C</font> </td></tr>
	<tr> <td align ="left"><b><xsl:value-of select="/stxx/applicationResources/key[@name='delivery.testTitle']" />: </b><xsl:value-of select="@title" /></td> </tr>
	<tr> <td align ="left">&#160; </td>	</tr>
	<tr> <td align ="left"><B><xsl:value-of select="/stxx/applicationResources/key[@name='delivery.generalSetting']" />:</B></td>	</tr>
	<tr> <td align ="left"><b><xsl:value-of select="/stxx/applicationResources/key[@name='delivery.timeLimit']" />: </b>
			<SCRIPT LANGUAGE="JavaScript1.2">
				var duration="";
				pattern =  /^P((\d*)Y)?((\d*)M)?((\d*)W)?((\d*)D)?T?((\d*)H)?((\d*)M)?((\d*)S)?/;
				//	var m = pattern.exec("P2Y3M4W5DT1H4M10S");
				var m = pattern.exec("<xsl:value-of select="duration" />");
			    if (m != null)
				{
				if (m[2] != "")
				{
					duration = m[2]+ " Year(s) ";
				}
				if (m[4] != "")
				{
				duration = duration + m[4]+ " Month(s) ";
				}
				if (m[6] != "")
				{ if(m[6] !="0"){
					duration = duration + m[6]+ " Week(s) ";
					}
				}
				if (m[8] != "")
				{
				  if(m[8] !="0"){
					duration = duration + m[8]+ " Day(s) ";
				  }
				}
				if (m[10] != "")
				{
				  if(m[10] != "00") {
				  duration = duration + m[10]+ " Hour(s) ";
				 }
				}
				if (m[12] != "")
				{
				  if(m[12] != "00"){
				    duration = duration + m[12]+ " Minute(s) ";
				  }
				}
				if (m[14] != "")
				{
				  duration = duration + m[14]+ " Second(s) ";
				}
				}
				//alert(duration)
				if(duration !=""){
				document.write(duration);
				}
				else{
				 document.write("No Time Limited");
				}
				</SCRIPT>
        </td>	</tr>
	<!-- *****************FOR FUTURE MAKE IT MORE DYNAMIC TO TAKE ANY KEY FROM qtimetadatafield -->
        <tr> <td align ="left"><b><xsl:value-of select="/stxx/applicationResources/key[@name='delivery.numberAttempts']" />: </b>
        <xsl:call-template name="fieldlabel">
        <xsl:with-param name = "fl" select ="'MAX_ATTEMPTS'"/>
        </xsl:call-template>
        </td></tr>
        <tr> <td align ="left"><b><xsl:value-of select="/stxx/applicationResources/key[@name='delivery.autoSubmit']" />: </b>
        <xsl:call-template name="fieldlabel">
        <xsl:with-param name = "fl" select ="'AUTO_SUBMIT'"/>
        </xsl:call-template>
        </td></tr>
  </table>
  <br/>
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

