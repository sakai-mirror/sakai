<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description:Assessment Settings Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: submissions.xsl,v 1.2 2004/08/03 18:23:58 lydial.stanford.edu Exp $
-->
<xsl:import href="qtimetadata.xsl"/>
<xsl:template name="submissions">
    <xsl:param name="tableWidth"/>
    <xsl:param name="firstColWidth"/>
    <xsl:param name="tableAlign"/>
    <!--Variables deciding whether to show a entry or not   -->
    <!-- Show Due Date -->
    <xsl:variable name="showMaxAttempts">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_MAX_ATTEMPTS</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show Retract Date -->
    <xsl:variable name="showLateHandling">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_LATE_HANDLING</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show Auto Save -->
    <xsl:variable name="showAutoSave">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_AUTO_SAVE</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Main body -->
    <table align="{$tableAlign}" border="0" width="{$tableWidth}">
      <!--******************************************************************-->
      <xsl:if test="$showMaxAttempts='SHOW'">
      <tr>
       <td  valign="top" width="{$firstColWidth}"><span class="form_label"> Number of Submissions Allowed</span> <BR/>
        
          <xsl:variable name="maxAttempts" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='MAX_ATTEMPTS']/following-sibling::fieldentry"/>
         <input name="unlimitedAttempts" type="radio" value="'Yes'">
            <xsl:if test="$maxAttempts =0">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> Unlimited <br/>
          <input name="unlimitedAttempts" type="radio" value="'No'">
            <xsl:if test="$maxAttempts !=0">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> Only  
            <input name="maxAttemps" type="text" size="2" >
            <xsl:attribute name="value"><xsl:value-of select="$maxAttempts"/></xsl:attribute> 
            </input> submissions allowed  

           <br/><br/>
         </td>
      </tr></xsl:if>
      <!--******************************************************************-->
       <xsl:if test="$showLateHandling='SHOW'">
      <tr>
        <td  valign="top" width="{$firstColWidth}"> <span class="form_label">Late Handling</span> <br/>
          <xsl:variable name="lateHandling" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='LATE_HANDLING']/following-sibling::fieldentry"/>
          <xsl:variable name="entryXpath">
            <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
              <xsl:with-param name="keyName" select="'LATE_HANDLING'"/>
              <xsl:with-param name="return_xpath" select="'xpath'"/>
            </xsl:apply-templates>
          </xsl:variable>
          <input name="{$entryXpath}" type="radio" value="False">
            <xsl:if test="$lateHandling ='False'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> Late Submissions (After Due Date) will NOT be accepted<br/>
         
          <input name="{$entryXpath}" type="radio" value="True">
            <xsl:if test="$lateHandling ='True'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> Late Submissions will be accepted and will be tagged late during grading.
           <br/><br/>
         </td>
     </tr>
     </xsl:if>
      <!--******************************************************************-->
       <xsl:if test="$showAutoSave='SHOW'">
      <tr>
        <td  valign="top" width="{$firstColWidth}"> <span class="form_label">Auto Save</span> <br/>
          <xsl:variable name="autoSave" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()= 'AUTO_SAVE']/following-sibling::fieldentry"/>
          <xsl:variable name="entryXpath">
            <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
              <xsl:with-param name="keyName" select="'AUTO_SAVE'"/>
              <xsl:with-param name="return_xpath" select="'xpath'"/>
            </xsl:apply-templates>
          </xsl:variable>
          <input name="{$entryXpath}" type="radio" value="False">
            <xsl:if test="$autoSave='False'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> User must click "Save" button to save input.<br/>

          <input name="{$entryXpath}" type="radio" value="True">
            <xsl:if test="$autoSave='True'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
          </input> All user input saved automatically.<br/>
         </td>
     </tr>
     </xsl:if>

      <!--******************************************************************-->
    </table>  
</xsl:template>
</xsl:stylesheet>
