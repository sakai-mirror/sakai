<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="../../../layout/header.xsl"/>
  <xsl:template match="/">
    <html>
      <head>
        <title> Assessment - Publish Assessment</title>
      </head>
      <body>
        <xsl:call-template name="header">
          <xsl:with-param name="displayText" select="'Published  Assessment '"/>
        </xsl:call-template>
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
        <xsl:variable name="formname" select="concat($base,'asi/author/assessment/assessmentSettingsAction.do')"/>
        <form action="{$formname}" method="post">
          <input name="assessmentID" type="hidden">
            <xsl:attribute name="value">
              <xsl:value-of select="stxx/form/publishedAssessmentActionForm/assessmentID"/>
            </xsl:attribute>
          </input>
          <table align="center" border="0" cellpadding="4" cellspacing="0" width="90%">
            <tr>
              <td align="left" colspan ="2" class="navigo_border"> Publish Assessment Confirmation </td>
            </tr>
            <!--*************************************** -->
            <tr>
              <td class="bold" colspan="2"> This assessment has been published with the following settings: </td>
            </tr>
            <tr>
              <td width= "15%"> Available date: </td>
              <td>
                <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/confirmation_start_date"/>
              </td>
            </tr>
            <tr>
              <td> Due date: </td>
              <td>
                <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/confirmation_end_date"/>
              </td>
            </tr>
            <tr>
              <td> Retract date: </td>
              <td>
                <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/confirmation_retract_date"/>
              </td>
            </tr>
            <tr>
              <td colspan="2">
                <br/>
              </td>
            </tr>
            <tr>
              <td> Number of submissions allowed: </td>
              <td>
                <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/confirmation_max_attemps"/>
              </td>
            </tr>
            <tr>
              <td colspan="2">
                <br/>
              </td>
            </tr>
            <tr>
              <td colspan="2">Released to: </td>
            </tr>
            <xsl:apply-templates select="/stxx/form/publishedAssessmentActionForm/confirmation_released_to"/>
            <tr>
              <td>URL: </td>
              <td>
                 <a>
                  <xsl:attribute name="href">
                    <xsl:value-of select="/stxx/request/attribute[@name='URI_PREFIX']/URI_PREFIX"/><xsl:value-of select="$base"/><xsl:value-of select="/stxx/applicationResources/key[@name='author.assessment.publishingURL']"/>?id=<xsl:value-of select="/stxx/request/attribute[@name='PUBLISHED_ID']/PUBLISHED_ID"/>
                  </xsl:attribute>
                  <xsl:value-of select="/stxx/request/attribute[@name='URI_PREFIX']/URI_PREFIX"/><xsl:value-of select="$base"/><xsl:value-of select="/stxx/applicationResources/key[@name='author.assessment.publishingURL']"/>?id=<xsl:value-of select="/stxx/request/attribute[@name='PUBLISHED_ID']/PUBLISHED_ID"/>
                </a>
              </td>
            </tr>
             <tr>
              <td colspan="2">
                <br/>
              </td>
            </tr>
            <tr>
              <td align="center" colspan="2">
                <input name="action" type="submit" value="OK"/>
              </td>
            </tr>
          </table>
        </form>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="confirmation_released_to">
    <xsl:for-each select="*">
      <tr>
        <td/>
        <td>
          <xsl:value-of select="."/>
        </td>
      </tr>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
