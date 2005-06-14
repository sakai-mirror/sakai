<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
* <p>Title: NavigoProject.org</p>
* <p>Description:Assessment Settings Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: assessmentOrganization.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
  <xsl:import href="qtimetadata.xsl"/>
  <xsl:template name="assessmentOrganization">
    <xsl:param name="tableWidth"/>
    <xsl:param name="firstColWidth"/>
    <xsl:param name="tableAlign"/>
    <!--Variables deciding whether to show a entry or not   -->
    <!-- Show Navigation -->
    <xsl:variable name="showNavigation">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_NAVIGATION</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show Question Layout -->
    <xsl:variable name="showQuestionLayout">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_QUESTION_LAYOUT</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show QUESTION_NUMBERING -->
    <xsl:variable name="showQuestionNumbering">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_QUESTION_NUMBERING</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>    
    <!-- Main body -->
    <xsl:variable name="questionLayout" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='QUESTION_LAYOUT']/following-sibling::fieldentry"/>
      <xsl:variable name="entryXpath">
        <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
          <xsl:with-param name="keyName" select="'QUESTION_LAYOUT'"/>
          <xsl:with-param name="return_xpath" select="'xpath'"/>
        </xsl:apply-templates>
      </xsl:variable>
     <xsl:variable name="navigation" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='NAVIGATION']/following-sibling::fieldentry"/>
      <xsl:variable name="navXpath">
        <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
          <xsl:with-param name="keyName" select="'NAVIGATION'"/>
          <xsl:with-param name="return_xpath" select="'xpath'"/>
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:variable name="questionNumbering" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='QUESTION_NUMBERING']/following-sibling::fieldentry"/>
      <xsl:variable name="num_entryXpath">
        <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
          <xsl:with-param name="keyName" select="'QUESTION_NUMBERING'"/>
          <xsl:with-param name="return_xpath" select="'xpath'"/>
        </xsl:apply-templates>
      </xsl:variable>
      <!--*********************************************************************** -->
      <table align="{$tableAlign}" border="0" width="{$tableWidth}">
     <!--******************************************************************-->
       <xsl:if test="$showQuestionLayout='SHOW'">
      <tr>
        <td class="form_label" valign="top" width="{$firstColWidth}"> Question Layout </td>
        <td>
           <input name="{$entryXpath}" type="radio" value="I">
            <xsl:attribute name="onClick">
            javascript:enableDisableAssessmentOrganization("<xsl:value-of select="$navXpath"/>","<xsl:value-of select="$num_entryXpath"/>",false);
            </xsl:attribute>
            <xsl:if test="$questionLayout ='I'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> Each question is on a separate Web page.<br/>
         
          <input name="{$entryXpath}" type="radio" value="S">
            <xsl:attribute name="onClick">
            javascript:enableDisableAssessmentOrganization("<xsl:value-of select="$navXpath"/>","<xsl:value-of select="$num_entryXpath"/>",false);
            </xsl:attribute>
            <xsl:if test="$questionLayout ='S'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> Each Part is on a separate Web page. <br/> 
         
          <input name="{$entryXpath}" type="radio" value="A">
            <xsl:attribute name="onClick">
            javascript:enableDisableAssessmentOrganization("<xsl:value-of select="$navXpath"/>","<xsl:value-of select="$num_entryXpath"/>",true);
            </xsl:attribute>
            <xsl:if test="$questionLayout ='A'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> The complete Assessment is displayed on one Web page.
        </td>
     </tr>
     </xsl:if>
      <!--******************************************************************-->
      <xsl:if test="$showNavigation='SHOW'">
      <tr>
        <td class="form_label" valign="top" width="{$firstColWidth}">Navigation </td>
        <td>
           <input name="{$navXpath}" id="{$navXpath}" type="radio" value="RANDOM">
            <xsl:if test="$navigation ='RANDOM'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> Random access to questions from a Table of Contents. <br/> &#160; &#160; &#160; There are NEXT and PREV buttons on each page for navigation. <br/>
          <input name="{$navXpath}" id="{$navXpath}" type="radio" value="LINEAR">
            <xsl:if test="$navigation ='LINEAR'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> Linear Access to questions with NO return to previous pages. <br/> &#160; &#160; &#160; There are only NEXT buttons only to go forward. <br/> &#160; &#160; &#160; There is NO Table of Contents page. <br/>
         </td>
      </tr>
      </xsl:if>
      
      <!--******************************************************************-->
       <xsl:if test="$showQuestionNumbering='SHOW'">
      <tr>
        <td class="form_label" valign="top" width="{$firstColWidth}"> Numbering </td>
        <td>
          <input name="{$num_entryXpath}" id="{$num_entryXpath}1" type="radio" value="CONTINUOUS">
            <xsl:if test="$questionNumbering ='CONTINUOUS'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> Continuous numbering between part.<br/>
         
          <input name="{$num_entryXpath}" id="{$num_entryXpath}1" type="radio" value="RESTART">
            <xsl:if test="$questionNumbering='RESTART'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> Restart numbering between part. </td>
     </tr>
     </xsl:if>
      <!--******************************************************************-->
    </table>
  </xsl:template>
</xsl:stylesheet>
