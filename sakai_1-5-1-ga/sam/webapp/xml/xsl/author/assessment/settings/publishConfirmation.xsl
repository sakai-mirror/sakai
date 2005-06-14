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
              <xsl:value-of select="stxx/form/assessmentActionForm/assessmentID"/>
            </xsl:attribute>
          </input>
          <table align="center" border="0" cellpadding="4" cellspacing="0" width="90%">
            <tr>
              <td align="left" width="100%" class="navigo_border"> Publish Assessment Confirmation </td>
              <td/>
            </tr>
            <tr>
              <td width= "100%" class="bold"> This assessment has been published with the following settings: </td>
              <td/>
            </tr>
            <tr width="100%">
              <table align="middle" width="100%">
                <tr>
                  <td width="10%" align="left"></td>
                  <td> Available date: <xsl:value-of select="/stxx/form/assessmentActionForm/confirmation_start_date"/></td>
                </tr>
              </table>
            </tr>
            <tr>
              <table align="middle" width="100%">
                <tr>
                  <td width="10%" align="left"></td>
                   <td width="90%"> Due date: <xsl:value-of select="/stxx/form/assessmentActionForm/confirmation_end_date"/></td>
                 </tr>
               </table>
            </tr>
            <tr>
              <table align="middle" width="100%">
                <tr>
                  <td width="10%" align="left"></td>
                  <td width="90%"> Retract date: <xsl:value-of select="/stxx/form/assessmentActionForm/confirmation_retract_date"/></td>
                </tr>
              </table>
            </tr>
            <tr>
              <td width="100%">&#160;&#160;
              </td>
            </tr>            
            <tr>
              <table align="middle" width="100%">
                <tr>
                  <td width="10%" align="left"></td>
                  <td width="90%"> Number of submissions allowed: <xsl:value-of select="/stxx/form/assessmentActionForm/confirmation_max_attemps"/></td>
                </tr>
              </table>
            </tr>
            <tr>
              <td width="100%">&#160;&#160;
              </td>
            </tr>            
            <tr>
              <table align="middle" width="100%">
                <tr>
                  <td width="10%" align="left"></td>
                  <td width="90%">Released to:</td>
                </tr>
              </table>
            </tr>
<!-- chen testing -->            
<!--            <xsl:apply-templates select="/stxx/form/publishedAssessmentActionForm/confirmation_released_to"/>-->
            <xsl:apply-templates select="/stxx/form/assessmentActionForm/confirmation_released_to"/>
            <tr>
              <table align="middle" width="100%">
                <tr>
                  <td width="10%" align="left"></td>
                  <td width="90%">URL:
                    <a>
                      <xsl:attribute name="href">
                        <xsl:value-of select="/stxx/request/attribute[@name='URI_PREFIX']/URI_PREFIX"/><xsl:value-of select="$base"/><xsl:value-of select="/stxx/applicationResources/key[@name='author.assessment.publishingURL']"/>?id=<xsl:value-of select="/stxx/request/attribute[@name='PUBLISHED_ID']/PUBLISHED_ID"/>
                      </xsl:attribute>
                      <xsl:value-of select="/stxx/request/attribute[@name='URI_PREFIX']/URI_PREFIX"/><xsl:value-of select="$base"/><xsl:value-of select="/stxx/applicationResources/key[@name='author.assessment.publishingURL']"/>?id=<xsl:value-of select="/stxx/request/attribute[@name='PUBLISHED_ID']/PUBLISHED_ID"/>
                    </a>
                  </td>
                </tr>
              </table>
            </tr>
            <tr>
              <td width="100%">&#160;&#160;
              </td>
            </tr>            
            <tr>
              <table align="middle" width="100%">
                <tr>
                  <td width="0%" align="left"></td>
                  <td align="center" width="100%">
                    <input name="action" type="submit" value="       OK       "/>
                  </td>
                </tr>                   
              </table>
            </tr>
          </table>
        </form>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="confirmation_released_to">
    <xsl:for-each select="*">
      <tr>
        <table align="middle" width="100%">
          <tr>
            <td width="16%" align="left"></td>
            <td width="84%">
              <xsl:value-of select="."/>
            </td>
          </tr>
        </table>
      </tr>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
