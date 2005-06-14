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
  
     <!--BASE HREF -->
    <xsl:variable name="base">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>
    <!-- Main body -->
    <table align="{$tableAlign}" border="0" width="{$tableWidth}">
      <!--******************************************************************-->
      <xsl:variable name="showColor" >
        <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
          <xsl:with-param name="req_fieldlabel">EDIT_BGCOLOR</xsl:with-param>
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:if test="$showColor='SHOW'">
        <tr>
           <xsl:variable name="ColVal" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='BGCOLOR']/following-sibling::fieldentry"/>
          <td class="form_label" width="{$firstColWidth}"> BG Color </td>
          <td align="left" valign="top" >
            <xsl:variable name="colXpath">
              <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
                <xsl:with-param name="keyName" select="'BGCOLOR'"/>
                <xsl:with-param name="return_xpath" select="'xpath'"/>
              </xsl:apply-templates>
            </xsl:variable>
            <input id="{$colXpath}" name="{$colXpath}" size="80" type="text" value="{$ColVal}"/>
        
           <img id="colorPicker" alt="Click Here to Pick up the color" border="0" height="13" src="{$base}images/sel.gif" width="15" style="cursor:pointer;">
                   <xsl:attribute name="onClick">javascript:TCP.popup(document.forms[0].elements["<xsl:value-of select="$colXpath"/>"],"","<xsl:value-of select="concat($base,'html/')"/>")</xsl:attribute>
           </img>   </td>  
        </tr> 
      </xsl:if>
      <!--******************************************************************-->
      <xsl:variable name="showImg" >
        <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
          <xsl:with-param name="req_fieldlabel">EDIT_BGIMG</xsl:with-param>
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:if test="$showImg='SHOW'">
       <tr>
        <td class="form_label" width="{$firstColWidth}"> BG Image </td>
        <td >
         <xsl:variable name="imgXpath">
            <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
              <xsl:with-param name="keyName" select="'BGIMG'"/>
              <xsl:with-param name="return_xpath" select="'xpath'"/>
            </xsl:apply-templates>
          </xsl:variable>
       
          <xsl:variable name="imgVal" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='BGIMG']/following-sibling::fieldentry"/>
<!--          <input id="{$imgXpath}" name="{$imgXpath}" size="80" type="text" value="{$imgVal}"/>-->
          <input id="{$imgXpath}" name="{$imgXpath}" size="80" type="file" value="{$imgVal}"/>
          
          
        </td>
      </tr> 
      </xsl:if>
      <!--******************************************************************-->
    </table>
  </xsl:template>
</xsl:stylesheet>
