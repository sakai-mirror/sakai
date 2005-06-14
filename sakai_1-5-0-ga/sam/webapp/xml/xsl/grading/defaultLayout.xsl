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
<xsl:variable name="base" select="/stxx/request/attribute/CONTEXT_PATH"/>
<html>
  <head>
  	<xsl:call-template name="head"/>
    <link type="text/css" rel="stylesheet" href="{$base}/htmlarea/htmlarea.css"/>
    <script type="text/javascript" src="{$base}/htmlarea/htmlarea.js"/>
    <script type="text/javascript" src="{$base}/htmlarea/lang/en.js"/>
    <script type="text/javascript" src="{$base}/htmlarea/dialog.js"/>
    <script type="text/javascript" src="{$base}/htmlarea/popupwin.js"/>
    <script type="text/javascript" src="{$base}/htmlarea/navigo_js/navigo_editor.js"/>
<script type="text/javascript">
HTMLArea.loadPlugin("SpellChecker", "<xsl:value-of select="$base"/>/htmlarea/");
var ta_editor =  [];
var hidden = [];
var textAreas = document.getElementsByTagName("textarea");
  
function initHtmlArea()
{
    for (var i = 0; i &lt; textAreas.length; i++)
    {
        var textArea = textAreas.item(i);
        ta_editor[i] = initEditorById(textArea.id, "<xsl:value-of select="$base"/>/htmlarea/", "two", true);
        
    }
    document.forms["ASIDeliveryForm"].onsubmit = function()
    {
        for (var i = 0; i &lt; ta_editor.length; i++)
        {
            var editor = ta_editor[i];
            editor._textArea.value = editor.getHTML();
        }
    };
}

function toggleToolbar(textAreaId)
{
    var textArea = document.getElementById(textAreaId);
    for (var i = 0; i &lt; textAreas.length; i++)
    {
        if (textArea == textAreas.item(i))
        {
          var editor = ta_editor[i];
          toggle_display_toolbar(textArea, editor, "two");
        }
    }
}
</script>
  </head>
  <body onload="initHtmlArea();">
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
  <table border="0" width="100%" cellpadding="0" cellspacing="0">
  <tr>
      <td><xsl:call-template name="header"/></td>
  </tr>
  <tr>
      <td valign="top" align="left" bgcolor="ffffff"><xsl:call-template name="body"/></td>
  </tr>
  <tr>
      <td bgcolor="ffffff"><xsl:call-template name="footer"/></td>
  </tr>
  </table>  
  </body>
</html>
</xsl:template>
</xsl:stylesheet>