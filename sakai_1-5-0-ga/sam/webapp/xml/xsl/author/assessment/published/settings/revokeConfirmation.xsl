<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: revokeConfirmation.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 <xsl:import href="../../../../layout/header.xsl"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>Revoke Confirmation</title>
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
      </head>
      <body>
        <xsl:call-template name="header">
          <xsl:with-param name="displayText" select="' Revoke Assessment '"/>
        </xsl:call-template>
         <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
        <xsl:variable name="formname" select="concat($base,'asi/author/assessment/published/publishedAssessmentAction.do')"/>
        <form action="{$formname}" method="post" name="assessmentSettingAction">
          <input name="publishedID" type="hidden">
            <xsl:attribute name="value">
              <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/publishedID"/>
            </xsl:attribute>
          </input>
          <table align="center" border="0" cellpadding="4" cellspacing="0" width="90%">
            <tr>
              <td align="left" class="navigo_border"> Revoke Published Assessment Confirmation </td>
            </tr>
            <!--************** ************************** -->
            <tr>
              <td class="bold"> &#160;&#160;&#160;&#160;&#160;&#160; Are you certain you want to revoke this published assessment?</td>
            </tr>
            <tr>
              <td align="center">
                <br/>
                <input name="action" type="submit" value="Revoke"/> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; <input name="action" type="submit" value="Cancel"/>
              </td>
            </tr>
          </table>
        </form>
      </body>
    </html>
  </xsl:template>
  <!--****************************************************************************** -->
</xsl:stylesheet>
