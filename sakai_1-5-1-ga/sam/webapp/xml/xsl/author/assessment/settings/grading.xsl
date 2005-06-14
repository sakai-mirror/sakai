<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
* <p>Title: NavigoProject.org</p>
* <p>Description:Assessment Settings Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: grading.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:import href="qtimetadata.xsl"/>
  <xsl:template name="grading">
    <xsl:param name="tableWidth"/>
    <xsl:param name="firstColWidth"/>
    <xsl:param name="tableAlign"/>
    <!--Variables deciding whether to show a entry or not   -->
    <!-- Show Annonymous Grading-->
    <xsl:variable name="showAnonymous">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_ANONYMOUS_GRADING</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show Gradebook Options-->
    <xsl:variable name="showGradebookOptions">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_GRADEBOOK_OPTIONS</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show Grade Score -->
    <xsl:variable name="showGradebookScore">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_GRADE_SCORE</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Main body -->
    <table align="{$tableAlign}" border="0" width="{$tableWidth}">
      <!--******************************************************************-->
      <xsl:if test="$showAnonymous='SHOW'">
      <tr>
        <td align="left" valign="top">
          <span class="form_label">Grading Options </span>
          <br/>
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'ANONYMOUS_GRADING'"/>
            <!--CONSIDER_GRADEBOOK_OPTIONS -->
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
          </xsl:apply-templates>Anonymous Grading Only <br/>
          <br/>
        </td>
      </tr>
      </xsl:if>
      <!--******************************************************************-->
      <xsl:if test="$showGradebookOptions='SHOW'">
      <tr>
        <td>
          <span class="form_label">Gradebook Options </span>
          <br/> Gradebook: &#160;&#160; &#160;&#160; <select>
            <option>none</option>
          </select>
          <br/> Assessment: &#160;&#160;&#160; <select>
            <option>none</option>
          </select>
          <br/>
          <br/>
        </td>
      </tr>
      </xsl:if>
      <!--******************************************************************-->
      <xsl:if test="$showGradebookScore='SHOW'">
      <tr>
        <td>
          <xsl:variable name="GSXpath">
            <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
              <xsl:with-param name="keyName" select="'GRADE_SCORE'"/>
              <xsl:with-param name="return_xpath" select="'xpath'"/>
            </xsl:apply-templates>
          </xsl:variable>
          <xsl:variable name="GSValue" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='GRADE_SCORE']/following-sibling::fieldentry"/>
          <span class="form_label"> If Multiple Submissions per User: </span>
          <br/>
          <input name="{$GSXpath}" type="radio" value="HIGHEST">
            <xsl:if test="$GSValue='HIGHEST'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          &#160;&#160;&#160;&#160;</input> Record the highest score <br/>
          <input name="{$GSXpath}" type="radio" value="AVERAGE">
            <xsl:if test="$GSValue='AVERAGE'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
         &#160;&#160;&#160;&#160;</input> Record the average score </td>
      </tr>
     </xsl:if>
        <!--******************************************************************-->
    </table>
  </xsl:template>
</xsl:stylesheet>
