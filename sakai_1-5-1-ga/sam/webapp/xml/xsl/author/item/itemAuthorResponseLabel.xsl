<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: itemAuthorResponseLabel.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="../../layout/header.xsl"/>
  <!-- This template processes the root node ("/") -->
  <!--****************************************************************************** -->
  <xsl:template match="material">
    <xsl:param name="path"/>
    <xsl:param name="response_label_position"/>
    <xsl:variable name="myparent" select="$path"/>
    <xsl:apply-templates select="mattext">
      <xsl:with-param name="path">
        <xsl:value-of select="concat($myparent,'/',name(),'[',position(),']')"/>
      </xsl:with-param>
      <xsl:with-param name="response_label_position" select="$response_label_position"/>
    </xsl:apply-templates>
    <!-- <xsl:apply-templates select="matimage">
            <xsl:with-param name="path">
                <xsl:value-of select="concat($myparent,'/',name(),'[',position(),']')"/>
            </xsl:with-param>
        </xsl:apply-templates> -->
  </xsl:template>
  <!--***************************************************************************** -->
  <xsl:template match="matimage">
    <xsl:param name="path"/>
    <xsl:value-of select="$path"/>
    <xsl:variable name="myparent" select="$path"/>
    <xsl:variable name="matImg" select="concat($myparent,'/',name(),'[',position(),']/@uri')"/>
    <xsl:variable name="matURI" select="@uri"/> Image:<br/>
    <input name="{$matImg}" size="38" type="text" value="{$matURI}"/>
    <xsl:if test="position()='2'"/>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="mattext">
    <xsl:param name="path"/>
    <xsl:param name="response_label_position"/>
    <xsl:variable name="myparent" select="$path"/>
    <xsl:variable name="matName" select="concat($myparent,'/',name(),'[',position(),']')"/>
    <xsl:variable name="mattext">
      <xsl:value-of select="self::*"/>
    </xsl:variable>
    <xsl:variable name="base">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="contains($myparent,'render_choice')">
        <xsl:variable name="cardinality" select="/stxx/item/presentation/flow/response_lid/@rcardinality"/>
        <xsl:variable name="questionType" select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='qmd_itemtype']/following-sibling::fieldentry"/>
        <xsl:variable name="respcondition" select="concat('item/resprocessing/respcondition[',$response_label_position,']')"/>
        <xsl:variable name="setvar" select="concat($respcondition,'/setvar')"/>
        <xsl:variable name="linkref" select="concat($respcondition,'/displayfeedback/@linkrefid')"/>
        <xsl:variable name="linkval">
          <xsl:apply-templates mode="multiple_choice" select="/stxx/item/resprocessing/respcondition">
            <xsl:with-param name="linkrefid" select="'true'"/>
            <xsl:with-param name="position">
              <xsl:value-of select="$response_label_position"/>
            </xsl:with-param>
          </xsl:apply-templates>
        </xsl:variable>
        <tr>
          <td valign="top">
             <xsl:choose>
               <xsl:when test="$cardinality='Single'">
                <input name="correctChoice" type="radio" value="{$setvar}">
                  <xsl:if test="$linkval='Correct'">
                    <xsl:attribute name="checked">True</xsl:attribute>
                  </xsl:if>
                </input>
                <xsl:if test="$questionType != 'True False'">
                  <xsl:number count="response_label" format="A" level="single"/>. </xsl:if>
                <input name="{$setvar}" type="hidden" value="{$linkref}"/>
              </xsl:when>
              <xsl:when test="$cardinality='Multiple'">
                <input name="correctChoice" type="checkbox" value="{$setvar}">
                  <xsl:if test="$linkval='Correct'">
                    <xsl:attribute name="checked">True</xsl:attribute>
                  </xsl:if>
                </input>
                <xsl:number count="response_label" format="A" level="single"/>. <input name="{$setvar}" type="hidden" value="{$linkref}"/>
              </xsl:when>
              <xsl:otherwise>
                <input name="correctChoice" type="radio" value="{$setvar}">
                  <xsl:if test="$linkval='Correct'">
                    <xsl:attribute name="checked">True</xsl:attribute>
                  </xsl:if>
                </input>
                <xsl:number count="response_label" format="A" level="single"/>. <input name="{$setvar}" type="hidden" value="{$linkref}"/>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="$questionType = 'True False'">
              <xsl:value-of select="$mattext"/>
            </xsl:if>
          </td>
          <td valign="top" width="45">
            <!--replaced with test area on 12/ 12 -->
            <xsl:if test="$questionType != 'True False'">
              <textarea cols="37" id="{$matName}" name="{$matName}" rows="10">
                <xsl:value-of select="$mattext"/>
              </textarea>
            </xsl:if>
          </td>
          <td valign="top" width="2">
            <xsl:if test="$questionType != 'True False'">
              <xsl:variable name="base">
                <xsl:call-template name="baseHREF"/>
              </xsl:variable>
<!--
              <img alt="Toggle Toolbar" src="{$base}htmlarea/images/htmlarea_editor.gif">
                <xsl:attribute name="onClick">javascript:hideUnhide("<xsl:value-of select="$matName"/>");</xsl:attribute>
              </img>
-->
          <a>
            <xsl:attribute name="href">javascript:hideUnhide("<xsl:value-of select="$matName"/>");</xsl:attribute>
            Show/Hide<br />Editor
          </a>
            </xsl:if>
          </td>
          <td valign="top">
              <xsl:variable name="showOverAllFeedback" select="/stxx/form/itemActionForm/showOverallFeedback"/>
              <xsl:variable name="showSelectionLevelFeedback" select="/stxx/form/itemActionForm/showSelectionLevelFeedback"/>
              <xsl:if test="$questionType != 'True False'">
              <xsl:if test="$showOverAllFeedback !='NONE'">
              <xsl:if test="$showSelectionLevelFeedback !='False'">
              <xsl:apply-templates mode="multiple_choice" select="/stxx/item/itemfeedback">
                <xsl:with-param name="position" select="$response_label_position"/>
              </xsl:apply-templates>
            </xsl:if></xsl:if></xsl:if>
          </td>
          <td/>
        </tr>
      </xsl:when>
      <xsl:otherwise/>
    </xsl:choose>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="response_label">
    <xsl:param name="path"/>
    <xsl:variable name="myparent" select="$path"/>
    <xsl:variable name="responseLabelName" select="concat($myparent,'/',name(),'[',position(),']')"/>
    <xsl:variable name="questionType" select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='qmd_itemtype']/following-sibling::fieldentry"/>
    <xsl:if test="$questionType != 'True False'">
     <tr>
<!--        <td class="bold" width="10%">-->
        <td width="10%">
          <xsl:if test="position()=1">Correct answer</xsl:if>
        </td>
        <td class="bold" width="45%">
<!--          <xsl:number count="response_label" format="A" level="single"/>. &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160; &#160;&#160;&#160; <xsl:variable name="actionname" select="concat('Remove', position())"/>-->
          &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160; &#160;&#160;&#160; <xsl:variable name="actionname" select="concat('Remove', position())"/>
          <input name="{$actionname}" onClick="javascript:onSubmitFn();" type="submit" value="Remove"/>
        </td>
        <td width="2"/>
        <td class="bold" width="45%">
        <xsl:variable name="showOverAllFeedback" select="/stxx/form/itemActionForm/showOverallFeedback"/>
        <xsl:variable name="showSelectionLevelFeedback" select="/stxx/form/itemActionForm/showSelectionLevelFeedback"/>
          <xsl:if test="$showOverAllFeedback !='NONE'">
          <xsl:if test="$showSelectionLevelFeedback !='False'">
            Feedback(optional)</xsl:if></xsl:if>
        </td>
        <td/>
      </tr>
    </xsl:if>
    <xsl:apply-templates select="material">
      <xsl:with-param name="path">
        <xsl:value-of select="concat($myparent,'/',name(),'[',position(),']')"/>
      </xsl:with-param>
      <xsl:with-param name="response_label_position" select="position()"/>
    </xsl:apply-templates>
  </xsl:template>

  <!--****************************************************************************** -->
  <xsl:template mode="multiple_choice" match="respcondition">
    <xsl:param name="linkrefid"/>
    <xsl:param name="position"/>
    <xsl:if test="$linkrefid='true'">
     <xsl:if test="$position=position()">
        <xsl:apply-templates select="displayfeedback">
          <xsl:with-param name="linkrefid" select="'true'"/>
        </xsl:apply-templates>
      </xsl:if>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="displayfeedback">
    <xsl:param name="linkrefid"/>
    <xsl:variable name="val" select="@linkrefid"/>
    <xsl:if test="$linkrefid='true'">
        <xsl:if test="$val='Correct'">
          <xsl:value-of select="@linkrefid"/>
        </xsl:if>
        <xsl:if test="$val='InCorrect'">
          <xsl:value-of select="@linkrefid"/>
        </xsl:if>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template mode="multiple_choice" match="itemfeedback">
    <xsl:param name="position"/>
    <xsl:if test="$position = position()">
      <xsl:variable name="name" select="concat('stxx/item/itemfeedback[',position(),']/flow_mat[1]/material[1]/mattext[1]')"/>
      <xsl:variable name="uri" select="concat('stxx/item/itemfeedback[',position(),']/flow_mat[1]/material[2]/matimage[1]/@uri')"/>
      <xsl:variable name="mattext" select="flow_mat[1]/material[1]/mattext[1]"/>
      <xsl:variable name="matimage" select="flow_mat[1]/material[2]/matimage[1]/@uri"/>
      <table border="0" width="100%">
        <tr>
          <td valign="top" width="45">
            <textarea cols="37" id="{$name}" name="{$name}" rows="10">
              <xsl:value-of select="$mattext"/>
            </textarea>
          </td>
          <td valign="top">
            <xsl:variable name="base">
              <xsl:call-template name="baseHREF"/>
            </xsl:variable>
<!--
            <img alt="Toggle Toolbar" src="{$base}htmlarea/images/htmlarea_editor.gif">
              <xsl:attribute name="onClick">javascript:hideUnhide("<xsl:value-of select="$name"/>");</xsl:attribute>
            </img>
-->
          <a>
            <xsl:attribute name="href">javascript:hideUnhide("<xsl:value-of select="$name"/>");</xsl:attribute>
            Show/Hide <br />Editor
          </a>

          </td>
        </tr>
      </table>
      <!--Image:<br/><input name="{$uri}" size = "38"  type="text" value="{$matimage}"/> -->
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
