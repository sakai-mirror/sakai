<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="../../layout/header.xsl"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>Sakai Assessment - Remove Question</title>
      </head>
      <body>
        <xsl:variable name="target" select="/stxx/form/itemActionForm/target"/>

        <xsl:call-template name="header">
          <xsl:with-param name="displayText" select="' Delete Question '"/>
        </xsl:call-template>
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>

        <xsl:variable name="formname">
          <xsl:if test="$target!='questionpool'">
            <xsl:value-of select="concat($base,'asi/author/item/itemAction.do')"/>
          </xsl:if>
          <xsl:if test="$target ='questionpool'">
            <xsl:value-of select="concat($base,'asi/author/item/itemAction.do?target=questionpool')"/>
          </xsl:if>
        </xsl:variable>


        <form action="{$formname}" method="post">
          <input name="ItemIdent" type="hidden">
            <xsl:attribute name="value">
              <xsl:value-of select="/stxx/form/itemActionForm/itemIdent"/>
            </xsl:attribute>
          </input>
          <input name="itemType" type="hidden">
            <xsl:attribute name="value">
              <xsl:value-of select="/stxx/form/itemActionForm/itemType"/>
            </xsl:attribute>
          </input>
          <input name="SectionIdent" type="hidden">
            <xsl:attribute name="value">
              <xsl:value-of select="/stxx/form/itemActionForm/sectionIdent"/>
            </xsl:attribute>
          </input>
         <input name="assessmentID" type="hidden">
            <xsl:attribute name="value">
              <xsl:value-of select="/stxx/form/itemActionForm/assessmentID"/>
            </xsl:attribute>
          </input>
          
         <table align="center" border="0" cellpadding="4" cellspacing="0" width="90%">
            <tr>
              <td align="left" class="navigo_border"> Remove Question Confirmation </td>
            </tr>
            <!--************** ************************** -->
            <tr>
              <td class="bold"> &#160;&#160;&#160;&#160;&#160;&#160;Are you sure you would like to remove this question? </td>
            </tr>
            <tr>
              <td align="center">
                <br/>
                <input name="action" type="submit" value="Remove"/> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; 
                <input name="removeCancel" type="submit" value="Cancel"/>
              </td>
            </tr>
          </table>
        </form>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
