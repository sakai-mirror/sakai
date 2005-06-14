<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
* <p>Title: NavigoProject.org</p>
* <p>Description:Assessment Settings Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: assessmentIntroduction.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
  <xsl:import href="qtimetadata.xsl"/>
  <xsl:template name="assessmentIntroduction">
    <xsl:param name="tableWidth"/>
    <xsl:param name="firstColWidth"/>
    <xsl:param name="tableAlign"/>
    <!--Variables deciding whether to show a entry or not   -->
    <!-- Show Authors -->
    <xsl:variable name="showAuthors">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_AUTHORS</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show Creator -->
    <xsl:variable name="showCreator">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">SHOW_CREATOR</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show Desc -->
    <xsl:variable name="showDesc">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_DESCRIPTION</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
     <!--BASE HREF -->
     <xsl:variable name="base">
       <xsl:call-template name="baseHREF"/>
     </xsl:variable>
    <!-- Main body -->
    <table align="{$tableAlign}" border="0" width="{$tableWidth}">
      <!--******************************************************************-->
      <tr>
        <td class="form_label" width="{$firstColWidth}"> Assessment Title </td>
        <td colspan="2">
<!--          <xsl:value-of select="/stxx/questestinterop/assessment/@title"/>-->
          <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/publishedName"/>
        </td>
      </tr>
      <!--******************************************************************-->
       <xsl:if test="$showCreator='SHOW'">
      <tr>
        <td class="form_label"  width="{$firstColWidth}"> Creator </td> <td colspan="2"><xsl:value-of select="/stxx/form/publishedAssessmentActionForm/username"/>
        </td>
      </tr>
      </xsl:if>
      <!--******************************************************************-->
      <xsl:if test="$showAuthors='SHOW'">
      <tr>
        <td class="form_label" width="{$firstColWidth}"> Author(s)</td>
        <td colspan="2" >
   
              <xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='AUTHORS']/following-sibling::fieldentry"/>
        </td>
      </tr>
      </xsl:if>
      <!--******************************************************************-->
      <xsl:if test="$showDesc='SHOW'">
      <tr>
        <td class="form_label" width="{$firstColWidth}"> Description/Intro (optional)</td>
        <td >
              <xsl:value-of select="/stxx/questestinterop/assessment/presentation_material/flow_mat/material/mattext"/>
        </td>
         <td align="left" valign="top">
        </td>
     </tr>
     </xsl:if>
      <!--******************************************************************-->
    </table>
 
  </xsl:template>
</xsl:stylesheet>

