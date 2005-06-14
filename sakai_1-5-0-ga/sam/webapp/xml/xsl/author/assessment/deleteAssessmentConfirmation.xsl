<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:import href="../../layout/header.xsl"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>Remove Assessment Confirmation</title>
      </head>
      <body>
        <xsl:call-template name="header">
          <xsl:with-param name="displayText" select="' Delete Assessment '"/>
        </xsl:call-template>
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
        <xsl:variable name="formname" select="concat($base,'asi/author/assessment/deleteAssessmentAction.do')"/>
        <form action="{$formname}" method="post">
          <input name="assessmentID" type="hidden">
            <xsl:attribute name="value">
              <xsl:value-of select="stxx/form/assessmentActionForm/assessmentID"/>
            </xsl:attribute>
          </input>
          <table align="center" border="0" cellpadding="4" cellspacing="0" width="90%">
            <tr>
              <td align="left" class="navigo_border"> Remove Assessment Confirmation </td>
            </tr>
            <!--************** ************************** -->
            <tr>
              <td class="bold"> &#160;&#160;&#160;&#160;&#160;&#160; Are you certain you want to delete this assessment and all questions contained in it? </td>
            </tr>
            <tr>
              <td align="center">
                <br/>
                <input name="action" type="submit" value="Remove"/> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; <input name="action" type="submit" value="Cancel"/>
              </td>
            </tr>
          </table>
        </form>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
