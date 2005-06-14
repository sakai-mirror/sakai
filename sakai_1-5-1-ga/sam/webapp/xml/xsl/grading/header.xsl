<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: header.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:preserve-space elements="flow"/>
  <xsl:output method="html"/>
  <xsl:template name="header">
    <xsl:param name="displayText"/>
    <xsl:variable name="base">
        <xsl:call-template name="baseHREF"/>
    </xsl:variable>
    <table border="0" cellpadding="0" cellspacing="1" height="103" width="100%" background="{$base}images/header_background.gif">
          <tr>
        <td height="69">
          <font color="#FFFFFF"/>
          <font color="#FFFFFF" face="Times New Roman" size="6">Sakai Assessment Manager</font>
        </td>
      </tr>
      <tr>
        <td class="bold" height="34"> &#160;&#160;<a href="/Navigo/Login.do">Back to Navigation Page </a>
        </td>
        <td height="34" width="162"/>
      </tr>
    </table>
  </xsl:template>
  <xsl:template name="baseHREF">
    <xsl:value-of select="/stxx/request/attribute/CONTEXT_PATH"/>/</xsl:template>
</xsl:stylesheet>
