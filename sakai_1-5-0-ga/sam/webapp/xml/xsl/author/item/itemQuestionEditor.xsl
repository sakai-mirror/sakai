<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: itemQuestionEditor.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="itemAuthorQtimetadata.xsl"/>
  <xsl:import href="itemImages.xsl"/>
  <xsl:import href="../../layout/header.xsl"/>
  <!-- This template processes the root node ("/") -->
  <xsl:template name="questionEditor">
    <xsl:variable name="points">
      <xsl:value-of select="format-number(stxx/item/resprocessing/outcomes/decvar/@minvalue, '0.00')"/>
    </xsl:variable>
    <xsl:variable name="question" select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='qmd_itemtype']/following-sibling::fieldentry"/>
    <tr>
      <td>
        <table border="0" width="100%">
          <tr>
            <td width="23">
              <xsl:call-template name="questionCircle"/>
            </td>
            <td>
              <span class="bold"> Answer Point Value: <input STYLE="text-align:right" id="stxx/item/resprocessing/outcomes/decvar/@minvalue" name="stxx/item/resprocessing/outcomes/decvar/@minvalue" size="2" type="text" value="{$points}">
                  <xsl:if test="$question = 'Multiple Choice Survey'">
                    <xsl:attribute name="disabled">true</xsl:attribute>
                  </xsl:if>
                </input>
              </span> &#160;&#160;(0 = survey or ungraded question) </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table border="0" width="100%">
          <tr>
            <td valign="top" width="23">
              <xsl:call-template name="questionCircle"/>
            </td>
            <td colspan="2">
              <span class="bold"> Question Text:</span>
              <xsl:if test="$question = 'Fill In the Blank'">&#160;NOTE: Place curly braces ({}) around words requiring a blank.
              </xsl:if>
              <xsl:if test="$question = 'Fill In the Blank'">
                <table>
                  <tr>
                    <td>
                      &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;For example:  Roses are {red} and violets are {blue}.
                    </td>
                  </tr>
                </table>
              </xsl:if>
            </td>
          </tr>
          <tr>
            <td valign="top" width="23"/>
            <td width="58">
              <TEXTAREA>
                <xsl:if test="$question != 'Fill In the Blank'">
                  <xsl:attribute name="name">stxx/item/presentation/flow/material/mattext</xsl:attribute>
                  <xsl:attribute name="id">stxx/item/presentation/flow/material/mattext</xsl:attribute>
                </xsl:if>
                <xsl:if test="$question = 'Fill In the Blank'">
                  <xsl:attribute name="name">stxx/form/itemActionForm/fibAnswer</xsl:attribute>
                  <xsl:attribute name="id">stxx/form/itemActionForm/fibAnswer</xsl:attribute>
                </xsl:if>
<!--                <xsl:attribute name="cols">54</xsl:attribute>
                <xsl:attribute name="rows">15</xsl:attribute>-->
                <xsl:attribute name="cols">60</xsl:attribute>
                <xsl:attribute name="rows">8</xsl:attribute>
                <xsl:if test="$question != 'Fill In the Blank'">
                  <xsl:value-of select="stxx/item/presentation/flow/material/mattext"/>
                </xsl:if>
                <xsl:if test="$question = 'Fill In the Blank'">
                  <xsl:value-of select="/stxx/form/itemActionForm/fibAnswer"/>
                </xsl:if>
              </TEXTAREA>
            </td>
            <td valign="top">
              <xsl:variable name="base">
                <xsl:call-template name="baseHREF"/>
              </xsl:variable>
<!--
              <img alt="Toggle Toolbar"
onClick="javascript:toggle_display_toolbar(ta_editor[1],ta_editor[1],'two')"
src="{$base}htmlarea/images/htmlarea_editor.gif"/>
-->
              <a>
                <xsl:attribute name="href">javascript:toggle_display_toolbar(ta_editor[1],ta_editor[1],'two')</xsl:attribute>
                Show/Hide <br />Editor
              </a>
<!-- fix to prevent TEXTAREA from stealing focus: ref Bug 744
In IE we set this to the first data entry, In Mozilla the focus is on the top link
-->
<script language="javascript">
  elemToFocus = document.getElementById("stxx/item/resprocessing/outcomes/decvar/@minvalue");
  document.focus();
  if (elemToFocus != null){
    elemToFocus.focus();
  }
</script>

            </td>
          </tr>
        <!--   <xsl:if test="$question = 'Fill In the Blank'">
            <tr>
              <td valign="top" width="23">
                <xsl:call-template name="questionCircle"/>
              </td>
              <td class="bold" colspan="2"> Mutually Exclusive : <xsl:variable name="key">
                  <xsl:value-of select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='MUTUALLY_EXCLUSIVE']/following-sibling::fieldentry"/>
                </xsl:variable>
                <xsl:variable name="keyXpath">
                  <xsl:apply-templates select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield">
                    <xsl:with-param name="keyName" select="'MUTUALLY_EXCLUSIVE'"/>
                    <xsl:with-param name="return" select="'xpath'"/>
                  </xsl:apply-templates>
                </xsl:variable>
                <input name="{$keyXpath}" type="radio" value="True">
                  <xsl:if test="$key='True'">
                    <xsl:attribute name="checked">True</xsl:attribute>
                  </xsl:if>
                </input> Yes <input name="{$keyXpath}" type="radio" value="False">
                  <xsl:if test="$key='False'">
                    <xsl:attribute name="checked">True</xsl:attribute>
                  </xsl:if>
                </input> No </td>
            </tr>
          </xsl:if> -->
        </table>
      </td>
    </tr>
  </xsl:template>
  <!--****************************************************************************** -->
</xsl:stylesheet>
