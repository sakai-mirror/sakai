<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: itemSubmitButtons.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="itemAuthorQtimetadata.xsl"/>
  <xsl:import href="itemImages.xsl"/>
  <!-- This template processes the root node ("/") -->
  <xsl:template name="submitButtons">
    <xsl:variable name="showMetadata"><xsl:value-of select="stxx/form/itemActionForm/showMetadata"/></xsl:variable>
    <xsl:if test="$showMetadata='True'">
    <tr>
      <td width="30%">
        <table border="0" width="100%">
          <tr>
            <td width="23">
              <xsl:call-template name="questionCircle"/>
            </td>
            <td align="left" class="bold"> Metadata: </td>
            <td>
              </td>
          </tr>
          <!--****************************************************************************** -->
          <tr>
            <td width="23"/>
            <td align="left" class="bold"> Objective: </td>
            <td>
              <xsl:variable name="obj">
                <xsl:value-of select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='ITEM_OBJECTIVE']/following-sibling::fieldentry"/>
              </xsl:variable>
              <xsl:variable name="objXpath">
                <xsl:apply-templates select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield">
                  <xsl:with-param name="keyName" select="'ITEM_OBJECTIVE'"/>
                  <xsl:with-param name="return" select="'xpath'"/>
                </xsl:apply-templates>
              </xsl:variable>
              <input name="{$objXpath}" size="80" value="{$obj}"/>
            </td>
          </tr>
          <!--****************************************************************************** -->
          <tr>
            <td width="23"/>
            <td align="left" class="bold"> Keyword: </td>
            <td>
              <xsl:variable name="key">
                <xsl:value-of select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='ITEM_KEYWORD']/following-sibling::fieldentry"/>
              </xsl:variable>
              <xsl:variable name="keyXpath">
                <xsl:apply-templates select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield">
                  <xsl:with-param name="keyName" select="'ITEM_KEYWORD'"/>
                  <xsl:with-param name="return" select="'xpath'"/>
                </xsl:apply-templates>
              </xsl:variable>
              <input name="{$keyXpath}" size="80" value="{$key}"/>
            </td>
          </tr>
          <!--****************************************************************************** -->
            <tr>
            <td width="23"/>
            <td align="left" class="bold"> Rubric: </td>
            <td>
              <xsl:variable name="rubric">
                <xsl:value-of select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='ITEM_RUBRIC']/following-sibling::fieldentry"/>
              </xsl:variable>
              <xsl:variable name="rubricXpath">
                <xsl:apply-templates select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield">
                  <xsl:with-param name="keyName" select="'ITEM_RUBRIC'"/>
                  <xsl:with-param name="return" select="'xpath'"/>
                </xsl:apply-templates>
              </xsl:variable>
              <input name="{$rubricXpath}" size="80" value="{$rubric}"/>
            </td>
          </tr>
          <!--****************************************************************************** -->
        </table>
      </td>
    </tr>
    </xsl:if>
    <!--****************************************************************************** -->
<!--    <tr>
      <td width="30%">
        <table border="0" width="100%">
          <tr>
            <td width="23">
              <xsl:call-template name="questionCircle"/>
            </td>
            <td align="left" class="bold"> Save or cancel changes: </td>
          </tr>
        </table>
      </td>
    </tr>
-->
<tr>
</tr>
<tr>
</tr>
    <!--****************************************************************************** -->
    <tr>
      <td align="center" width="100%">
        <xsl:variable name="questionType">
          <xsl:value-of select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='qmd_itemtype']/following-sibling::fieldentry"/>
        </xsl:variable>
<!--         <xsl:if test="$questionType='Matching'">
          <input name="action" type="submit" value="Back to Terms"/> &#160; &#160; </xsl:if> -->
        <input name="action" onClick="javascript:onSubmitFn();" type="submit" value="Save"/> &#160; &#160; <input name="action" type="submit" value="Cancel"/>
      </td>
    </tr>
  </xsl:template>
  <!--****************************************************************************** -->
</xsl:stylesheet>
