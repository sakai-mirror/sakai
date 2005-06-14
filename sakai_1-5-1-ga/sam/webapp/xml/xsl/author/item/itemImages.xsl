<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: itemImages.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
  <xsl:import href="../../layout/header.xsl"/>
  <xsl:template name="questionCircle">
    <xsl:variable name="base">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>
    <xsl:variable name="path" select="concat($base,'images/circle.gif')"/>
    <table background="{$path}" border="0" height="23" width="100%">
      <tr>
        <td align="center" class="navigo_border_text_font" valign="top">
          <script language="javascript"> item += 1; document.write(item); </script>
        </td>
      </tr>
    </table>
  </xsl:template>
</xsl:stylesheet>
