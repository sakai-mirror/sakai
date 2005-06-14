<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
* <p>Title: NavigoProject.org</p>
* <p>Description:Assessment Settings Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: highSecurity.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
--><xsl:import href="qtimetadata.xsl"/>
  <xsl:template name="highSecurity">
    <xsl:param name="tableWidth"/>
    <xsl:param name="firstColWidth"/>
    <xsl:param name="tableAlign"/>
    <!--Variables deciding whether to show a entry or not   -->
    <!-- Show IP -->
    <xsl:variable name="showIP">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_ALLOW_IP</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show UserID-->
    <xsl:variable name="showUsername">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_USERID</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Main body -->
    <table align="{$tableAlign}" border="0" width="{$tableWidth}">
      <!--******************************************************************-->
      <xsl:if test="$showIP='SHOW'">
      <tr>
        <td class="form_label" width="{$firstColWidth}">
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'CONSIDER_ALLOW_IP'"/> <!--CONSIDER_ALLOW_IP -->
            <!--CONSIDER_ALLOW_IP -->
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
            <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates> Allow only specified IP  <br/> &#160;&#160;&#160;&#160; Addresses: <br/> &#160;&#160;&#160;&#160;(one per line) </td>
        <td>
          <xsl:variable name="ipXpath">
            <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
              <xsl:with-param name="keyName" select="'ALLOW_IP'"/>
              <xsl:with-param name="return_xpath" select="'xpath'"/>
            </xsl:apply-templates>
          </xsl:variable>
<!--           <textarea cols="60" name="{$ipXpath}" rows="4"> -->
            <xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='ALLOW_IP']/following-sibling::fieldentry"/>
         <!--  </textarea> -->
        </td>
      </tr></xsl:if>
       <!--******************************************************************-->
       <tr><td></td><td><br/></td></tr>
        <!--******************************************************************-->
      <xsl:if test="$showUsername='SHOW'">
       <tr>
        <td valign="top" class="form_label" width="{$firstColWidth}">
           <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'CONSIDER_USERID'"/> <!--CONSIDER_USERID -->
            <!--CONSIDER_ALLOW_IP -->
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
            <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates> Secondary ID and Password:
          </td>
        <td>Username:
				<xsl:variable name="userVal" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='USERID']/following-sibling::fieldentry"/>
				  <xsl:variable name="UserXpath">
				 <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
					<xsl:with-param name="keyName" select="'USERID'" />
		 			<xsl:with-param name="return_xpath" select="'xpath'" />
 				</xsl:apply-templates>
				 </xsl:variable>
				 <!-- <input type="text" name="{$UserXpath}" value="{$userVal}"/> -->
				 <xsl:value-of select="$userVal"/>
				<br/><br/>Password: 
				<xsl:variable name="PassVal" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='PASSWORD']/following-sibling::fieldentry"/>
				<xsl:variable name="PassXpath">
				<xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
					<xsl:with-param name="keyName" select="'PASSWORD'" />
					<xsl:with-param name="return_xpath" select="'xpath'" />
 				</xsl:apply-templates>
 				 </xsl:variable>
 				<!-- <input type="text" name="{$PassXpath}" value="{$PassVal}"/> -->
 				<xsl:value-of select="$PassVal"/>
        </td>
       </tr>   </xsl:if>
    </table>
  </xsl:template>
</xsl:stylesheet>
