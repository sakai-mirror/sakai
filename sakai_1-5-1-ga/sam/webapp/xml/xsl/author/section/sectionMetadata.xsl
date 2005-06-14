<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template name="sectionMetadata">
    <tr>
      <td width="23">
        <xsl:call-template name="questionCircle"/>
      </td>
      <td align="left" class="bold"> Metadata: </td>
      <td/>
    </tr>
    <!--****************************************************************************** -->
    <tr>
      <td width="23"/>
      <td align="left" class="bold"> Objective: </td>
      <td>
        <xsl:variable name="obj">
          <xsl:value-of select="/stxx/section/qtimetadata/qtimetadatafield/fieldlabel[text()='SECTION_OBJECTIVE']/following-sibling::fieldentry"/>
        </xsl:variable>
        <xsl:variable name="objXpath">
          <xsl:apply-templates select="/stxx/section/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'SECTION_OBJECTIVE'"/>
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
          <xsl:value-of select="/stxx/section/qtimetadata/qtimetadatafield/fieldlabel[text()='SECTION_KEYWORD']/following-sibling::fieldentry"/>
        </xsl:variable>
        <xsl:variable name="keyXpath">
          <xsl:apply-templates select="/stxx/section/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'SECTION_KEYWORD'"/>
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
          <xsl:value-of select="/stxx/section/qtimetadata/qtimetadatafield/fieldlabel[text()='SECTION_RUBRIC']/following-sibling::fieldentry"/>
        </xsl:variable>
        <xsl:variable name="rubricXpath">
          <xsl:apply-templates select="/stxx/section/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'SECTION_RUBRIC'"/>
            <xsl:with-param name="return" select="'xpath'"/>
          </xsl:apply-templates>
        </xsl:variable>
        <input name="{$rubricXpath}" size="80" value="{$rubric}"/>
      </td>
    </tr>
    <!--****************************************************************************** -->
  </xsl:template>
</xsl:stylesheet>
