<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
* <p>Title: NavigoProject.org</p>
* <p>Description:Assessment Settings Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: timedAssessment.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:import href="qtimetadata.xsl"/>
  <xsl:template name="timedAssessment">
    <xsl:param name="tableWidth"/>
    <xsl:param name="firstColWidth"/>
    <xsl:param name="tableAlign"/>
    <!--Variables deciding whether to show a entry or not   -->
    <!-- Show Due Date -->
    <xsl:variable name="showDuration">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_DURATION</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show Retract Date -->
    <xsl:variable name="showAutoSubmit">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_AUTO_SUBMIT</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Main body -->
     <table align="{$tableAlign}" border="0" width="{$tableWidth}">
      <!--******************************************************************-->
      <xsl:if test="$showDuration='SHOW'">
      <tr>
        <td class="form_label" width="{$firstColWidth}">
          <!--CONSIDER_DURATION -->
          <xsl:variable name="valueDuration" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='CONSIDER_DURATION']/following-sibling::fieldentry"/>
          <xsl:variable name="temp">
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
              <xsl:with-param name="keyName" select="'CONSIDER_DURATION'"/>
              <xsl:with-param name="return_xpath" select="'xpath'"/>
              
            </xsl:apply-templates>
          </xsl:variable>
        <xsl:variable name="xpathDuration" select="substring($temp,6)"/>
         <xsl:variable name="temp1">
         <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
              <xsl:with-param name="keyName" select="'AUTO_SUBMIT'"/>
              <xsl:with-param name="return_xpath" select="'xpath'"/>
            </xsl:apply-templates></xsl:variable>           
          <xsl:variable name="xpathAutoSubmit" select="substring($temp1,6)"/>
          <input disabled="true" name="{$xpathDuration}" id="{$xpathDuration}" type="checkbox">
           <xsl:if test="$valueDuration='True'">
            <xsl:attribute name="checked">True</xsl:attribute>
              </xsl:if>
          </input> Timed Assessment with Time Limit:  &#160;: &#160;
          <xsl:variable name="durPosition">
            <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
              <xsl:with-param name="keyName" select="'CONSIDER_DURATION'"/>
              <xsl:with-param name="return_xpath" select="'position'"/>
            </xsl:apply-templates>
          </xsl:variable>
          
         <xsl:variable name="hours" select="/stxx/form/publishedAssessmentActionForm/hours"/>
         <xsl:variable name="minutes" select="/stxx/form/publishedAssessmentActionForm/minutes"/>
         <xsl:choose>
           <xsl:when test="$valueDuration='True'">
             <input name ="CONSIDER_DURATION" value = "{$durPosition}" type ="hidden" />
              <!--****************Hours***********************************-->
              <!--  <xsl:variable name="actionformHours"> -->
              <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/hours"/>
              <!--   </xsl:variable>
              <select name="hours">
                <option/>
                <xsl:call-template name="hourDropdown">
                  <xsl:with-param name="hSelected" select="$actionformHours"/>
                </xsl:call-template>
              </select>  -->hrs
              <!--****************Minutes***********************************-->
              &#160;: &#160;<!-- <xsl:variable name="actionformMin"> -->
              <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/minutes"/>
              <!-- </xsl:variable>
              <select name="minutes">
                <option/>
                <xsl:call-template name="minuteDropdown">
                  <xsl:with-param name="mSelected" select="$actionformMin"/>
                </xsl:call-template>
              </select> -->
             mins
           </xsl:when>
           <xsl:otherwise>
             <span STYLE="font-weight: normal">Unlimited</span>
           </xsl:otherwise>
         </xsl:choose>        
       </td>   
           
      </tr>
        <!--******************************************************************-->
      <xsl:if test="$showAutoSubmit='SHOW'">
      <tr>
        <td  class="form_label" width="{$firstColWidth}">
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'AUTO_SUBMIT'"/>
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
            <xsl:with-param name="disabled" select="'true'"/>
          </xsl:apply-templates> Auto-submit when time expires </td>
          </tr>
        </xsl:if>
      </xsl:if>  
    </table>
  </xsl:template>
</xsl:stylesheet>
