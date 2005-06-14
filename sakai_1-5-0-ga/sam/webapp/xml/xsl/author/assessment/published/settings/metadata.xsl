<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
* <p>Title: NavigoProject.org</p>
* <p>Description:Assessment Settings Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: metadata.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
--><xsl:import href="qtimetadata.xsl"/>
  <xsl:template name="metadata">
    <xsl:param name="tableWidth"/>
    <xsl:param name="firstColWidth"/>
    <xsl:param name="tableAlign"/>
    <!--Variables deciding whether to show a entry or not   -->
    <!-- Show EDITABLE_ASSESSMENT_METADATA-->
    <xsl:variable name="showAssessmentMetadata">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_ASSESSMENT_METADATA</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show COLLECT_SECTION_METADATA -->
    <xsl:variable name="showSectionMetadata">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_COLLECT_SECTION_METADATA</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show COLLECT_ITEM_METADATA-->
    <xsl:variable name="showItemMetadata">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_COLLECT_ITEM_METADATA</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
     <!--BASE HREF -->
     <xsl:variable name="base">
       <xsl:call-template name="baseHREF"/>
     </xsl:variable>    
    <!-- Main body -->
    <table align="{$tableAlign}" border="0" width="{$tableWidth}">
      <!--******************************************************************-->
      <xsl:if test="$showAssessmentMetadata='SHOW'">
      <tr>
        <td align="left" class="form_label" colspan="2" valign="top">Assessment Metadata </td>
      </tr>
      <tr>
        <td valign="top" width="{$firstColWidth}"> &#160;&#160;&#160;&#160;Keywords: </td>
        <td>
          <xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='ASSESSMENT_KEYWORDS']/following-sibling::fieldentry"/>
        </td>
      </tr>
      <tr>
        <td valign="top" width="{$firstColWidth}"> &#160;&#160;&#160;&#160;Objectives: </td>
        <td>
       <xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='ASSESSMENT_OBJECTIVES']/following-sibling::fieldentry"/>
        </td>
        <td align="left" valign="top">
        </td>
      </tr>
       <tr>
        <td valign="top" width="{$firstColWidth}"> &#160;&#160;&#160;&#160;Rubrics: </td>
        <td>
            <xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='ASSESSMENT_RUBRICS']/following-sibling::fieldentry"/>
          <br/>
        </td>
        <td align="left" valign="top">
        </td>
      </tr></xsl:if>
      <!--******************************************************************-->
      <xsl:if test="$showSectionMetadata='SHOW'">
      <tr>
        <td class="form_label" colspan="3">
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'COLLECT_SECTION_METADATA'"/>
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
            <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates>Collect Metadata for Sections in Question Editor<br/>
          <br/>
        </td>
      </tr></xsl:if>
      <!--******************************************************************-->
      <xsl:if test="$showItemMetadata='SHOW'">
      <tr>
        <td class="form_label" colspan="2">
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'COLLECT_ITEM_METADATA'"/>
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
            <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates>Collect Metadata for Questions in Question Editor </td>
      </tr></xsl:if>
      <!--******************************************************************-->
    </table>
  </xsl:template>
</xsl:stylesheet>
