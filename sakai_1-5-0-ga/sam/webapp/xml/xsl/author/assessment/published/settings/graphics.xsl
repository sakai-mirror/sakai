<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
* <p>Title: NavigoProject.org</p>
* <p>Description:Assessment Settings Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: graphics.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:import href="qtimetadata.xsl"/>
  <xsl:template name="graphics">
    <xsl:param name="tableWidth"/>
    <xsl:param name="firstColWidth"/>
    <xsl:param name="tableAlign"/>
    <!-- Main body -->
    <table align="{$tableAlign}" border="0" width="{$tableWidth}">
      <!--******************************************************************-->
      <tr>
        <td class="form_label" width="{$firstColWidth}"> BG Color </td>
        <td align="left" valign="top" >
         <xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='BGCOLOR']/following-sibling::fieldentry"/>
      </td>  
      </tr> 
      <!--******************************************************************-->
       <tr>
        <td class="form_label" width="{$firstColWidth}"> BG Image </td>
        <td>       
          <xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='BGIMG']/following-sibling::fieldentry"/>
        </td>
      </tr> 
      <!--******************************************************************-->
    </table>
  </xsl:template>
</xsl:stylesheet>
