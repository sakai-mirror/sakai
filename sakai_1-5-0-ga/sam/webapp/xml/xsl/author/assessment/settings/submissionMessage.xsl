<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
* <p>Title: NavigoProject.org</p>
* <p>Description:Assessment Settings Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: submissionMessage.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
--><xsl:import href="qtimetadata.xsl"/>
  <xsl:template name="submissionMessage">
    <xsl:param name="tableWidth"/>
    <xsl:param name="firstColWidth"/>
    <xsl:param name="tableAlign"/>
    <!--Variables deciding whether to show a entry or not   -->
    <!-- Show CONSIDER_ASSESSFEEDBACK -->
    <xsl:variable name="showConsiderAssessFeedback">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_ASSESSFEEDBACK</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show FINISH_URL -->
    <xsl:variable name="showFinishURL">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_FINISH_URL</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!--BASE HREF -->
     <xsl:variable name="base">
       <xsl:call-template name="baseHREF"/>
     </xsl:variable>
    <!-- Main body -->
    <table align="{$tableAlign}" border="0" width="{$tableWidth}">
      <!--******************************************************************-->
       <xsl:if test="$showConsiderAssessFeedback='SHOW'">
      <tr>
        <td class="form_label" width="{$firstColWidth}" valign="top"> Submission Message </td>
        <td width="60"  align="left"  >
<!--chen update-->
<!--          <xsl:variable name="feedbk" select="/stxx/questestinterop/assessment/assessfeedback/flow_mat/material/mattext"/>-->
          <xsl:variable name="feedbk" select="/stxx/questestinterop/assessment/assessfeedback/flow_mat/material/mattext/comment()"/>
          <textarea cols="60" id="stxx/questestinterop/assessment/assessfeedback/flow_mat/material/mattext" name="stxx/questestinterop/assessment/assessfeedback/flow_mat/material/mattext"  rows="15">
            <xsl:value-of select="$feedbk"/>
          </textarea>
        </td>
        <td align="left" valign="top">
<!--
          <img alt="Toggle Toolbar" src="{$base}htmlarea/images/htmlarea_editor.gif">
            <xsl:attribute name="onClick">javascript:hideUnhide("stxx/questestinterop/assessment/assessfeedback/flow_mat/material/mattext","two");</xsl:attribute>
          </img>
-->
          <a>
            <xsl:attribute name="href">javascript:hideUnhide("stxx/questestinterop/assessment/assessfeedback/flow_mat/material/mattext","two");</xsl:attribute>
            Show/Hide <br />Editor
          </a>

        </td>
      </tr>
      </xsl:if>
      <!--******************************************************************-->
<!-- ON CAITLIN's REQUEST
    <xsl:if test="$showFinishURL='SHOW'">
      <xsl:variable name="authorXpath">
        <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
          <xsl:with-param name="keyName">FINISH_URL</xsl:with-param>
          <xsl:with-param name="return_xpath">xpath</xsl:with-param>
        </xsl:apply-templates>
      </xsl:variable>
      <tr>
        <td class="form_label" width="{$firstColWidth}"> Final Page URL </td>
        <td colspan="2">
          <input maxlength="300" name="{$authorXpath}" size="80" type="text">
            <xsl:attribute name="value">
              <xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='FINISH_URL']/following-sibling::fieldentry"/>
            </xsl:attribute>
          </input>
        </td>
      </tr>
      </xsl:if> -->
      <!--******************************************************************-->
    </table>
  </xsl:template>
</xsl:stylesheet>
