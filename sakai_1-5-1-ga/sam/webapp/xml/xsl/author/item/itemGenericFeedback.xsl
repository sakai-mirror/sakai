<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: itemGenericFeedback.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!-- This template processes the Tyroot node ("/") -->
  <xsl:import href="itemImages.xsl"/>
  <xsl:template name="genericFeedback">
    <xsl:variable name="questionType" select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='qmd_itemtype']/following-sibling::fieldentry"/>
    <xsl:variable name="base">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>
    <table border="0" width="100%">
      <tr>
        <td colspan="5">
          <table border="0" width="100%">
            <tr>
              <td width="23">
                <xsl:call-template name="questionCircle"/>
              </td>
              <td class="bold">
                <xsl:choose>
                  <xsl:when test="$questionType='Multiple Choice Survey' or $questionType='Audio Recording' or $questionType='File Upload'">Feedback (optional):</xsl:when>
                  <xsl:when test="$questionType='Essay'">Answer: Provide a model answer to show students and to assist graders.</xsl:when>
                  <xsl:otherwise> Correct/Incorrect answer feedback:</xsl:otherwise>
                </xsl:choose>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <xsl:variable name="correctPos">
        <xsl:apply-templates mode="getPosition" select="stxx/item/itemfeedback">
          <xsl:with-param name="ident" select="'Correct'"/>
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:variable name="correctMattext">
        <xsl:apply-templates mode="getMattext" select="stxx/item/itemfeedback">
          <xsl:with-param name="ident" select="'Correct'"/>
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:variable name="correctMatimage">
        <xsl:apply-templates mode="getImage" select="stxx/item/itemfeedback">
          <xsl:with-param name="ident" select="'Correct'"/>
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:variable name="correctName" select="concat('stxx/item/itemfeedback[',$correctPos,']/flow_mat[1]/material[1]/mattext[1]')"/>
      <xsl:variable name="correctUri" select="concat('stxx/item/itemfeedback[',$correctPos,']/flow_mat[1]/material[2]/matimage[1]/@uri')"/>
      <tr>
        <td width="25"/>
        <td colspan="2">
          <span nowrap="nowrap">
            <xsl:choose>
              <xsl:when test="$questionType='Multiple Choice Survey' or $questionType='Audio Recording' or $questionType='File Upload'"/>
              <xsl:when test="$questionType='Essay'">Model Short Answer (optional)</xsl:when>
              <xsl:otherwise>Correct answer (optional)</xsl:otherwise>
            </xsl:choose>
          </span>
        </td>
        <td colspan="2">
          <xsl:if test="$questionType!='Multiple Choice Survey'  and $questionType!='Audio Recording' and $questionType!='File Upload'">
            <span nowrap="nowrap">
              <xsl:choose>
                <xsl:when test="$questionType='Essay'"> Feedback (optional)</xsl:when>
                <xsl:otherwise>Incorrect answer (optional)</xsl:otherwise>
              </xsl:choose>
            </span>
          </xsl:if>
        </td>
      </tr>
      <xsl:variable name="incorrectPos">
        <xsl:apply-templates mode="getPosition" select="stxx/item/itemfeedback">
          <xsl:with-param name="ident" select="'InCorrect'"/>
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:variable name="incorrectMattext">
        <xsl:apply-templates mode="getMattext" select="stxx/item/itemfeedback">
          <xsl:with-param name="ident" select="'InCorrect'"/>
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:variable name="incorrectMatimage">
        <xsl:apply-templates mode="getImage" select="stxx/item/itemfeedback">
          <xsl:with-param name="ident" select="'InCorrect'"/>
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:variable name="incorrectName" select="concat('stxx/item/itemfeedback[',$incorrectPos,']/flow_mat[1]/material[1]/mattext[1]')"/>
      <xsl:variable name="incorrectUri" select="concat('stxx/item/itemfeedback[',$incorrectPos,']/flow_mat[1]/material[2]/matimage[1]/@uri')"/>
      <xsl:if test="$questionType!='Multiple Choice Survey' and $questionType!='Audio Recording' and $questionType!='File Upload'">
        <tr>
          <td width="25"/>
          <td nowrap="nowrap" title="" width="36">
            <textarea cols="36" id="{$correctName}" name="{$correctName}" rows="10">
              <xsl:value-of select="$correctMattext"/>
            </textarea>
          </td>
          <td valign="top">
            <!--<img alt="Toggle Toolbar" src="{$base}htmlarea/images/htmlarea_editor.gif">
              <xsl:attribute name="onClick">javascript:hideUnhide("<xsl:value-of select="$correctName"/>");</xsl:attribute>
            </img>-->
            <a>
              <xsl:attribute name="href">javascript:hideUnhide("<xsl:value-of select="$correctName"/>");</xsl:attribute>
              Show/Hide <br />Editor
            </a>

          </td>
          <xsl:if test="$questionType!='Audio Recording' and $questionType!='File Upload'">
            <td nowrap="nowrap" title="" width="36"><textarea cols="36" id="{$incorrectName}" name="{$incorrectName}" rows="10">
                <xsl:value-of select="$incorrectMattext"/>
              </textarea>
            </td>
          </xsl:if>
          <td valign="top">
            <xsl:if test="$questionType!='Audio Recording' and $questionType!='File Upload'">
              <!--<img alt="Toggle Toolbar" src="{$base}htmlarea/images/htmlarea_editor.gif">
                <xsl:attribute name="onClick">javascript:hideUnhide("<xsl:value-of select="$incorrectName"/>");</xsl:attribute>
              </img>-->
              <a>
                <xsl:attribute name="href">javascript:hideUnhide("<xsl:value-of select="$incorrectName"/>");</xsl:attribute>
                Show/Hide <br />Editor
              </a>

            </xsl:if>
          </td>
        </tr>
      </xsl:if>
      <xsl:if test="$questionType='Multiple Choice Survey' or $questionType='Audio Recording' or $questionType='File Upload'">
        <tr>
          <td width="25"/>
          <td nowrap="nowrap" title="" width="36"> <textarea cols="36" id="{$incorrectName}" name="{$incorrectName}" rows="10">
              <xsl:value-of select="$incorrectMattext"/>
            </textarea>
          </td>
          <td valign="top" colspan="3">
            <!-- <img alt="Toggle Toolbar" src="{$base}htmlarea/images/htmlarea_editor.gif">
              <xsl:attribute name="onClick">javascript:hideUnhide("<xsl:value-of select="$incorrectName"/>");</xsl:attribute>
            </img> -->
            <a>
              <xsl:attribute name="href">javascript:hideUnhide("<xsl:value-of select="$incorrectName"/>");</xsl:attribute>
              Show/Hide <br />Editor
            </a>

          </td>
        </tr>
      </xsl:if>
    </table>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="itemfeedback" mode="getPosition">
    <xsl:param name="ident"/>
    <xsl:if test="$ident=@ident">
      <xsl:value-of select="position()"/>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="itemfeedback" mode="getMattext">
    <xsl:param name="ident"/>
    <xsl:if test="$ident=@ident">
      <xsl:value-of select="flow_mat/material/mattext"/>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="itemfeedback" mode="getImage">
    <xsl:param name="ident"/>
    <xsl:if test="$ident=@ident">
      <xsl:value-of select="flow_mat/material[2]/matimage[1]/@uri"/>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
</xsl:stylesheet>
