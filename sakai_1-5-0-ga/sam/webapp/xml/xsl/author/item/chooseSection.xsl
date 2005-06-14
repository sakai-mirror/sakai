<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template name="showSections">
    <table border="0" width="100%">
      <tr>
        <td valign="top" width="23">
          <xsl:call-template name="questionCircle"/>
        </td>
        <td valign="top" class="bold">Assign to <xsl:value-of select="/stxx/applicationResources/key[@name='author.common.section']"/>:&#160; 
        <select name="SectionIdent">
            <xsl:apply-templates select="/stxx/form/itemActionForm/assessmentSectionIdents"/>
          </select>
        </td>
      </tr>
    </table>
  </xsl:template>
  <xsl:template match="assessmentSectionIdents">
    <xsl:for-each select="value/section | section">
      <xsl:variable name="val">
        <xsl:value-of select="@ident"/>
      </xsl:variable>
      <xsl:variable name="title">
        <xsl:value-of select="@title"/>
      </xsl:variable>
      <xsl:variable name="secident" select="/stxx/form/itemActionForm/sectionIdent"/>
      <option value="{$val}">
        <xsl:if test="$val = $secident">
          <xsl:attribute name="selected">true</xsl:attribute>
        </xsl:if>
        <xsl:value-of select="$title"/>
      </option>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
