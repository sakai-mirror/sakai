<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description:Assessment Settings Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: feedback.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:import href="qtimetadata.xsl"/>
<xsl:template name="feedback">
    <xsl:param name="tableWidth"/>
    <xsl:param name="firstColWidth"/>
    <xsl:param name="tableAlign"/>
    <!--Variables deciding whether to show a entry or not   -->
    <!-- Show FEEDBACK_DELIVERY -->
    <xsl:variable name="showFeedbkDelivery">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_FEEDBACK_DELIVERY</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show CONSIDER_FEEDBACK_COMPONENTS -->
    <xsl:variable name="showFeedbkComponents">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_FEEDBACK_COMPONENTS</xsl:with-param>
      </xsl:apply-templates>
   </xsl:variable>
    <!-- Base HREF-->
    <xsl:variable name="base">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>   
    <!-- Main body -->
     <table align="{$tableAlign}" border="0" width="{$tableWidth}">
      <!--******************************************************************-->
      <xsl:if test="$showFeedbkDelivery='SHOW'">
      <tr>
        <td colspan="2" align="left" valign="top" ><span class="form_label">Feedback delivery:</span><br/>
         <xsl:variable name="FdbkXpath">
            <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
              <xsl:with-param name="keyName" select="'FEEDBACK_DELIVERY'"/>
              <xsl:with-param name="return_xpath" select="'xpath'"/>
              <xsl:with-param name="disabled">true</xsl:with-param>
            </xsl:apply-templates>
          </xsl:variable>
          <xsl:variable name="FdbkValue" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='FEEDBACK_DELIVERY']/following-sibling::fieldentry"/>
         &#160;&#160;&#160;&#160;
         
         <!-- <xsl:value-of select="$FdbkValue"/> -->
         <input  disabled = "true" name="{$FdbkXpath}" type="radio" value="IMMEDIATE">
            <xsl:if test="$FdbkValue='IMMEDIATE'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
            </input>Immediate Feedback 
        <br/>
        &#160;&#160;&#160;&#160;
         <input disabled = "true" name="{$FdbkXpath}" type="radio" value="DATED">
            <xsl:if test="$FdbkValue='DATED'">
               <xsl:attribute name="checked">true</xsl:attribute>
             </xsl:if>
            </input> Feedback will be displayed to the student at a specified date:
        <br/>
        &#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;
        
          <input  id="feedback_delivery_day" name="feedback_delivery_day" type="text">
            <xsl:attribute name="value">
              <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/feedback_delivery_day"/>
            </xsl:attribute>
          </input>&#160;  
          <xsl:variable name="stHr" select="/stxx/form/publishedAssessmentActionForm/feedback_delivery_hours"/> &#160;&#160; 
          <select  name="feedback_delivery_hours">
            <xsl:call-template name="hourDropdown">
              <xsl:with-param name="hSelected">
                <xsl:choose>
                  <xsl:when test="$stHr='0'">0</xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="$stHr"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:call-template>
          </select> &#160;&#160;: <select  name="feedback_delivery_minutes">
            <xsl:call-template name="minuteDropdown">
              <xsl:with-param name="mSelected">
                <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/feedback_delivery_minutes"/>
              </xsl:with-param>
            </xsl:call-template>
          </select>    
        <br/>
        
        &#160;&#160;&#160;&#160;
          <input disabled = "true" name="{$FdbkXpath}" type="radio" value="NONE">
            <xsl:if test="$FdbkValue='NONE'">
              <xsl:attribute name="checked">true</xsl:attribute>
            </xsl:if>
            </input> NO Feedback will be displayed to the student.
            
        <br/>
        </td>
        </tr></xsl:if>
         <!--******************************************************************-->
         <xsl:if test="$showFeedbkComponents='SHOW'">
         <tr>
        <td colspan="2" align="left" valign="top" class="form_label"><br/> Select the Feedback Components to be displayed to the student:
 
        </td>
        </tr>
         <!--******************************************************************-->
        
         <tr>
        <td   align="left" valign="top" width="50%" >&#160;&#160;&#160;&#160;
         <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'FEEDBACK_SHOW_QUESTION'"/> 
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
            <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates>  Question Text<br/>&#160;&#160;&#160;&#160;
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'FEEDBACK_SHOW_RESPONSE'"/> 
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
            <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates>  Student Response <br/>&#160;&#160;&#160;&#160;
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'FEEDBACK_SHOW_CORRECT_RESPONSE'"/> 
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
            <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates>  Correct Response <br/>&#160;&#160;&#160;&#160;
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'FEEDBACK_SHOW_STUDENT_SCORE'"/> 
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
            <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates>  Student’s Score  <br/>
        </td>
       <td   align="left" valign="top" >
        <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'FEEDBACK_SHOW_ITEM_LEVEL'"/> 
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
            <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates> Question-level Feedback <br/>
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'FEEDBACK_SHOW_SELECTION_LEVEL'"/> 
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
            <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates> Selection-level Feedback
          <br/>
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'FEEDBACK_SHOW_GRADER_COMMENT'"/> 
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
            <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates> Grader’s Comments
          <br/>
          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'FEEDBACK_SHOW_STATS'"/> 
            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
            <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates> Statistics and Histograms<br/>
          </td>
        </tr> </xsl:if>
      </table>
</xsl:template>
</xsl:stylesheet>