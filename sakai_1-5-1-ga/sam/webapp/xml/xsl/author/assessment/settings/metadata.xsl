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
          <xsl:variable name="keyXpath">
            <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
              <xsl:with-param name="keyName" select="'ASSESSMENT_KEYWORDS'"/>
              <xsl:with-param name="return_xpath" select="'xpath'"/>
            </xsl:apply-templates>
          </xsl:variable>
          <xsl:variable name="keyVal" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='ASSESSMENT_KEYWORDS']/following-sibling::fieldentry"/>
          <input name="{$keyXpath}" size="85" type="text" value="{$keyVal}"/>
        </td>
      </tr>
      <xsl:variable name="objXpath">
        <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
          <xsl:with-param name="keyName" select="'ASSESSMENT_OBJECTIVES'"/>
          <xsl:with-param name="return_xpath" select="'xpath'"/>
        </xsl:apply-templates>
      </xsl:variable>
      <tr>
        <td valign="top" width="{$firstColWidth}"> &#160;&#160;&#160;&#160;Objectives: </td>
        <td>
          <!-- <textarea cols="70" id="{$objXpath}" name="{$objXpath}" rows="15">
            <xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='ASSESSMENT_OBJECTIVES']/following-sibling::fieldentry"/>
          </textarea> -->
            <input name="{$objXpath}" size="85" type="text" >
              <xsl:attribute name="value"><xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='ASSESSMENT_OBJECTIVES']/following-sibling::fieldentry"/></xsl:attribute>
              </input>
        </td>
        <td align="left" valign="top">
       <!--    <img alt="Toggle Toolbar" src="{$base}htmlarea/images/htmlarea_editor.gif">
            <xsl:attribute name="onClick">javascript:hideUnhide("<xsl:value-of select="$objXpath"/>","two");</xsl:attribute>
          </img>
Note as per bug 695 if you uncomment this line you should use link:
          <a>
            <xsl:attribute name="href">javascript:hideUnhide("<xsl:value-of select="$objXpath"/>","two");</xsl:attribute>
            Show/Hide <br />Editor
          </a>
-->
        </td>
      </tr>
      <xsl:variable name="rubricXpath">
        <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
          <xsl:with-param name="keyName" select="'ASSESSMENT_RUBRICS'"/>
          <xsl:with-param name="return_xpath" select="'xpath'"/>
        </xsl:apply-templates>
      </xsl:variable>
      <tr>
        <td valign="top" width="{$firstColWidth}"> &#160;&#160;&#160;&#160;Rubrics: </td>
        <td>
       <!--    <textarea cols="70" id="{$rubricXpath}" name="{$rubricXpath}" rows="15">
            <xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='ASSESSMENT_RUBRICS']/following-sibling::fieldentry"/>
          </textarea> -->
            <input name="{$rubricXpath}" size="85" type="text" >
              <xsl:attribute name="value"><xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='ASSESSMENT_RUBRICS']/following-sibling::fieldentry"/>
              </xsl:attribute>
            </input>
          <br/>
        </td>
        <td align="left" valign="top">
        <!--   <img alt="Toggle Toolbar" src="{$base}htmlarea/images/htmlarea_editor.gif">
            <xsl:attribute name="onClick">javascript:hideUnhide("<xsl:value-of select="$rubricXpath"/>","two");</xsl:attribute>
          </img>
Note as per bug 695 if you uncomment this line you should use link:
          <a>
            <xsl:attribute name="href">javascript:hideUnhide("<xsl:value-of select="$rubricXpath"/>","two");</xsl:attribute>
            Show/Hide <br />Editor
          </a>
-->
        </td>
      </tr></xsl:if>
      <!--******************************************************************-->
      <xsl:if test="$showSectionMetadata='SHOW'">
      <tr>
        <td class="form_label" colspan="3">
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'COLLECT_SECTION_METADATA'"/>
            <!--COLLECT_SECTION_METADATA -->
            <!--CONSIDER_GRADEBOOK_OPTIONS -->
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
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
            <!--COLLECT_ITEM_METADATA -->
            <!--CONSIDER_GRADEBOOK_OPTIONS -->
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
          </xsl:apply-templates>Collect Metadata for Questions in Question Editor </td>
      </tr></xsl:if>
      <!--******************************************************************-->
    </table>
  </xsl:template>
</xsl:stylesheet>
