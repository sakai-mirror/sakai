<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template name="requireRationale">
    <tr>
      <td class="form_label">
        <table border="0" width="100%">
          <tr>
            <td width="23">
              <xsl:call-template name="questionCircle"/>
            </td>
            <td align="left">
              <span class="bold"> Require rationale: </span>
              <xsl:variable name="require_rationale">
                <xsl:apply-templates select="stxx/item/presentation/flow/response_str" mode="rationale"/>
              </xsl:variable>
            <xsl:variable name="rationalePresent">
              <xsl:choose>
                <xsl:when test="$require_rationale='SHOW'">Yes</xsl:when>
                <xsl:otherwise>No</xsl:otherwise>
              </xsl:choose>
              </xsl:variable>
              <input type="hidden" name= "rationalePresent" value="{$rationalePresent}" />
              <input name="requiredRationale" type="radio" value="Yes">
                <xsl:if test="$require_rationale='SHOW'">
                  <xsl:attribute name="checked">true</xsl:attribute>
                </xsl:if>
              </input>Yes &#160; 
              <input name="requiredRationale" type="radio" value="No">
                <xsl:if test="$require_rationale!='SHOW'">
                  <xsl:attribute name="checked">true</xsl:attribute>
                </xsl:if>
              </input>No &#160; </td>
          </tr>
        </table>
      </td>
    </tr>
  </xsl:template>
  <xsl:template match="stxx/item/presentation/flow/response_str" mode="rationale">SHOW</xsl:template>
  <!--****************************************************************************** -->
</xsl:stylesheet>
