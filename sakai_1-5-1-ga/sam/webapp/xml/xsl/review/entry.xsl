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
<!--  <input type="submit" name="Begin Test" value="Begin Test" /> -->
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">beginTest</xsl:attribute>
    <xsl:attribute name="value"><xsl:value-of select="/stxx/applicationResources/key[@name='review.button.beginTest']" /></xsl:attribute>
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
&#160;<b><xsl:value-of select="@title" /> <xsl:value-of select="/stxx/applicationResources/key[@name='review.assessmentInformation']" /></b>
</td></tr></table>
  <xsl:variable name="questionLayout" select="//assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='QUESTION_LAYOUT']/following-sibling::fieldentry"/>
       <table>
       <tr>
          <td class="form_label" valign="top"> Question Layout </td>
        <td>
           <input name="questionLayout" type="radio" value="I">
            <xsl:if test="$questionLayout ='I'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> Each question is on a separate Web page.<br/>
         
          <input name="questionLayout" type="radio" value="S">
            <xsl:if test="$questionLayout ='S'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> Each Part is on a separate Web page. <br/> 
                   
          <input name="questionLayout" type="radio" value="A">
            <xsl:if test="$questionLayout ='A'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> The complete Assessment is displayed on one Web page.
        </td>
        </tr>
        </table>
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

