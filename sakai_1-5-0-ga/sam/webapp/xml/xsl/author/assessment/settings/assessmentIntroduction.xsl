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
    <xsl:variable name="showTemplate">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">DISPLAY_TEMPLATE</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>  
     <!--BASE HREF -->
     <xsl:variable name="base">
       <xsl:call-template name="baseHREF"/>
     </xsl:variable>
    <!-- Main body -->
    <xsl:if test="$showTemplate='SHOW'">
    <table align="{$tableAlign}" border="0" width="{$tableWidth}">
      <!--******************************************************************-->
      <tr>
        <td class="form_label" width="{$firstColWidth}"> Assessment Title </td>
        <td colspan="2">
          <input maxlength="300" name="stxx/questestinterop/assessment/@title" size="80" type="text">
            <xsl:attribute name="value">
              <xsl:value-of select="stxx/questestinterop/assessment/@title"/>
            </xsl:attribute>
          </input>
        </td>
      </tr>
      <!--******************************************************************-->
       <xsl:if test="$showCreator='SHOW'">
      <tr>
        <td class="form_label" width="{$firstColWidth}"> Creator </td>
        <td colspan="2">
          <xsl:value-of select="/stxx/form/assessmentActionForm/username"/>
        </td>
      </tr>
      </xsl:if>
      <!--******************************************************************-->
      <xsl:if test="$showAuthors='SHOW'">
      <xsl:variable name="authorXpath">
        <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
          <xsl:with-param name="keyName">AUTHORS</xsl:with-param>
          <xsl:with-param name="return_xpath">xpath</xsl:with-param>
        </xsl:apply-templates>
      </xsl:variable>
      <tr>
        <td class="form_label" width="{$firstColWidth}"> Author(s)</td>
        <td colspan="2" >

           <input maxlength="300" name="{$authorXpath}" size="80" type="text">
            <xsl:attribute name="value">
              <xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='AUTHORS']/following-sibling::fieldentry"/>
            </xsl:attribute>
          </input>
        </td>
      </tr>
      </xsl:if>
      <!--******************************************************************-->
      <xsl:if test="$showDesc='SHOW'">
        <xsl:variable name="descXpath">stxx/questestinterop/assessment/presentation_material/flow_mat/material/mattext</xsl:variable>
       <tr>
	     <td class="form_label" width="{$firstColWidth}"> Description/Intro (optional)</td>
  	     <td align="left">
	       <textarea cols="70" name="{$descXpath}" id= "{$descXpath}" rows="15">
	  <!--chen update -->
	  <!--              <xsl:value-of select="/stxx/questestinterop/assessment/presentation_material/flow_mat/material/mattext"/>-->
	         <xsl:value-of select="/stxx/questestinterop/assessment/presentation_material/flow_mat/material/mattext/comment()"/>
	       </textarea>
  	     </td>
	     <td align="left" valign="top">
	  <!--
	             <img alt="Toggle Toolbar" src="{$base}htmlarea/images/htmlarea_editor.gif">
	              <xsl:attribute name="onClick">javascript:hideUnhide("<xsl:value-of select="$descXpath"/>","two");</xsl:attribute>
	            </img>
	  -->
	       <a>
  	         <xsl:attribute name="href">javascript:hideUnhide("<xsl:value-of select="$descXpath"/>","two");</xsl:attribute>
	         Show/Hide <br />Editor
	       </a>
	     </td>
       </tr>     
     </xsl:if>
      <!--******************************************************************-->
    </table>
    </xsl:if>
    <xsl:if test="not($showTemplate='SHOW')">
      <input name="stxx/questestinterop/assessment/@title" type="hidden">
        <xsl:attribute name="value">
          <xsl:value-of select="/stxx/questestinterop/assessment/@title"/>
        </xsl:attribute>
      </input>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>

