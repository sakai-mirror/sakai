<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="../../layout/header.xsl"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>Section - Remove <xsl:value-of select="/stxx/applicationResources/key[@name='author.common.section']"/>
        </title>
      </head>
      <body>
        <xsl:call-template name="header">
          <xsl:with-param name="displayText" select="' Delete Question '"/>
        </xsl:call-template>
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
        <xsl:variable name="formname" select="concat($base,'asi/author/section/sectionAction.do')"/>
        <form action="{$formname}" method="post">
          <input name="SectionIdent" type="hidden">
            <xsl:attribute name="value">
              <xsl:value-of select="/stxx/form/sectionActionForm/sectionIdent"/>
            </xsl:attribute>
          </input>
          <input name="assessmentID" type="hidden">
            <xsl:attribute name="value">
              <xsl:value-of select="/stxx/form/sectionActionForm/assessmentID"/>
            </xsl:attribute>
          </input>
          <table align="center" border="0" cellpadding="4" cellspacing="0" width="90%">
            <tr>
              <td align="left" class="navigo_border"> Remove <xsl:value-of select="/stxx/applicationResources/key[@name='author.common.section']"/> Confirmation </td>
            </tr>
            <!--************** ************************** -->
            <tr>
              <td class="bold"> &#160;&#160;&#160;&#160;&#160;&#160;Please choose what you would like to remove:</td>
            </tr>
            <xsl:variable name="noOfItems" select="stxx/form/sectionActionForm/noOfItems"/>
            <xsl:if test="count(//stxx/form/sectionActionForm/assessmentSectionIdents/value/section)>1 and $noOfItems !='0' ">
            <xsl:apply-templates select="/stxx/form/sectionActionForm/assessmentSectionIdents">
              <xsl:with-param name="currentSection">
                <xsl:value-of select="/stxx/form/sectionActionForm/sectionIdent"/>
              </xsl:with-param>
            </xsl:apply-templates>
            </xsl:if>
            <tr>
              <td>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Remove <xsl:value-of select="/stxx/applicationResources/key[@name='author.common.section']"/> and all questions: &#160;&#160;&#160;&#160; <input name="action" type="submit" value="Remove"/>
              </td>
            </tr>
            <tr>
              <td align="center">
                <br/> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; <input name="action" type="submit" value="Cancel"/>
              </td>
            </tr>
          </table>
        </form>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="assessmentSectionIdents">
    <xsl:param name="currentSection"/>
    <tr>
      <td> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Remove <xsl:value-of select="/stxx/applicationResources/key[@name='author.common.section']"/> only and move question(s) to: 
      <select name="SectionIdentSelected">
          <xsl:for-each select="value/section | section">
            <xsl:variable name="val">
              <xsl:value-of select="@ident"/>
            </xsl:variable>
            <xsl:variable name="title">
              <xsl:value-of select="@title"/>
            </xsl:variable>
            <xsl:if test="$val != $currentSection">
            <option value="{$val}">
              <xsl:value-of select="$title"/>
            </option>
            </xsl:if>
          </xsl:for-each>
        </select> &#160;&#160;&#160;&#160; <input name="action" type="submit" value="Move"/>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
