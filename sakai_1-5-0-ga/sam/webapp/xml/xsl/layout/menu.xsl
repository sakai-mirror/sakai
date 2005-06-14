<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: menu.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="header.xsl"/>
<xsl:preserve-space elements="flow" />

<xsl:output method="html"/>

<xsl:template name="menu">
    <xsl:variable name="base">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>
<div class="logintable">
  <table border="0" cellpadding="0" cellspacing="10">
    <tr valign="top">
      <td>
      <a href="{$base}Login.do">Navigation Page</a>
      </td>
    </tr>
  </table>
</div>

</xsl:template>
</xsl:stylesheet>