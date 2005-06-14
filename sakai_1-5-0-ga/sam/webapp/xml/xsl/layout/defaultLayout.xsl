<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: defaultLayout.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:preserve-space elements="flow" />
<xsl:output method="html"/>

<xsl:template name="defaultLayout">

<html>
  <head>
  <title>Assessment Item Delivery</title>
  </head>
  <body>
<!--  
  <TABLE border="0" width="100%" cellspacing="5">
  <TR>
  <TD colspan="2">  
  <xsl:call-template name="header" />
  </TD>
  </TR>
  <TR>
  <TD width="100" valign="top" bgcolor="ffffff">
  <xsl:call-template name="menu" />
  </TD>
  <TD valign="top" align="left" bgcolor="ffffff">
  <xsl:call-template name="body" />
  </TD>
  </TR>
  <TR>
  <TD colspan="2" bgcolor="ffffff">
  <xsl:call-template name="footer" />
  </TD>
  </TR>
  </TABLE>
-->
  <TABLE border="0" width="100%" cellpadding="0" cellspacing="0">
  <TR>
  <TD>  
  <xsl:call-template name="header" />
  </TD>
  </TR>
  <TR>
  <TD valign="top" align="left" bgcolor="ffffff">
  <xsl:call-template name="body" />
  </TD>
  </TR>
  <TR>
  <TD bgcolor="ffffff">
  <xsl:call-template name="footer" />
  </TD>
  </TR>
  </TABLE>  
  </body>
</html>
</xsl:template>
</xsl:stylesheet>