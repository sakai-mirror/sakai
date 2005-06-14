<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template name="showPools">
    <table border="0" width="100%">
      <tr>
        <td valign="top" width="23">
          <xsl:call-template name="questionCircle"/>
        </td>
        <td class="bold" width ="27%" align = "left" valign="top" >Assign to question pool:</td>
        <td valign="top">
<!--         <select name="poolIdents"  multiple = "true">-->
         <select name="poolIdents">
            <option value="">Select a pool name</option>
            <xsl:apply-templates select="/stxx/form/itemActionForm/poolsAvailable"/>
          </select>
        </td>
      </tr>
    </table>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="poolsAvailable">
    <xsl:for-each select="*">
      <xsl:variable name="currentID">
        <xsl:apply-templates select="/stxx/form/itemActionForm/poolsSelected">
          <xsl:with-param name="selected">
            <xsl:value-of select="substring-before(.,'+')"/>
          </xsl:with-param>
        </xsl:apply-templates>
      </xsl:variable>
      <option>
        <xsl:attribute name="value">
          <xsl:value-of select="substring-before(.,'+')"/>
        </xsl:attribute>
        <xsl:if test="$currentID ='Selected'">
          <xsl:attribute name="selected">true</xsl:attribute>
        </xsl:if>
        <xsl:value-of select="substring-after(.,'+')"/>
      </option>
    </xsl:for-each>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template  match="poolsSelected">
    <xsl:param name="selected"/>
    <xsl:for-each select="*">
      <xsl:variable name="this" select="."/>
      <xsl:if test="$selected=$this">Selected</xsl:if>
    </xsl:for-each>
  </xsl:template>
   <!--****************************************************************************** -->
 
</xsl:stylesheet>
