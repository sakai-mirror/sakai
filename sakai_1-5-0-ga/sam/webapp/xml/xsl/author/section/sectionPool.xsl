<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template name="sectionPool">
    <tr>
      <td width="23">
        <xsl:call-template name="questionCircle"/>
      </td>
      <td align="left" class="bold" colspan="2"> Random draw from question pool: </td>
    </tr>
    <!--****************************************************************************** -->
    <tr>
      <td width="23"/>
      <td align="left" class="bold"> Number of questions: </td>
      <xsl:variable name="selNo">
        <xsl:value-of select="/stxx/section/selection_ordering/selection/selection_number"/>
      </xsl:variable>
      <td>
        <input name="stxx/section/selection_ordering/selection/selection_number" type="text" value="{$selNo}"/>
      </td>
    </tr>
    <!--****************************************************************************** -->
    <tr>
      <td width="23"/>
      <td align="left" class="bold"> Pool name: </td>
      <td>
        <select name="stxx/section/selection_ordering/selection/sourcebank_ref">
          <option value="">SELECT</option>
          <xsl:apply-templates select="/stxx/form/sectionActionForm/poolsAvailable">
            <xsl:with-param name="selectedPool">
              <xsl:value-of select="/stxx/section/selection_ordering/selection/sourcebank_ref"/>
            </xsl:with-param>
          </xsl:apply-templates>
        </select>
      </td>
    </tr>
    </xsl:template>
      <!--****************************************************************************** -->
    
  <xsl:template match="poolsAvailable">
    <xsl:param name="selectedPool"/>
    <xsl:for-each select="*">
      <xsl:variable name="currentID" select="substring-before(.,'+')"/>
      <option>
        <xsl:attribute name="value">
          <xsl:value-of select="substring-before(.,'+')"/>
        </xsl:attribute>
        <xsl:if test="$selectedPool = $currentID">
          <xsl:attribute name="selected">true</xsl:attribute>
        </xsl:if>
        <xsl:value-of select="substring-after(.,'+')"/>
      </option>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
