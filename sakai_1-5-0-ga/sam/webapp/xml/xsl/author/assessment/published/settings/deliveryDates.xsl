<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
* <p>Title: NavigoProject.org</p>
* <p>Description:Assessment Settings Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: deliveryDates.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
--><xsl:import href="qtimetadata.xsl"/>
  <xsl:template name="deliveryDates">
    <xsl:param name="tableWidth"/>
    <xsl:param name="firstColWidth"/>
    <xsl:param name="tableAlign"/>
    <!--Variables deciding whether to show a entry or not   -->
    <!-- Show Due Date -->
    <xsl:variable name="showDueDate">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_END_DATE</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Show Retract Date -->
    <xsl:variable name="showRetractDate">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_RETRACT_DATE</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <!-- Base HREF-->
    <xsl:variable name="base">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>
    <!-- Main body -->
    <table align="{$tableAlign}" border="0" width="{$tableWidth}">
      <!--******************************************************************-->
     <tr>
        <td class="form_label" width="{$firstColWidth}">
<!--          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'CONSIDER_START_DATE'"/>--><!--CONSIDER_START_DATE -->
<!--            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
             <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates>--> Available Date </td>
        <td>
          <input id="publish_start_day" name="publish_start_day" type="text">
            <xsl:attribute name="value">
              <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/publish_start_day"/>
            </xsl:attribute>
          </input>&#160; <a>
            <xsl:attribute name="href">javascript:cal1.popup("","<xsl:value-of select="concat($base,'html/')"/>");</xsl:attribute>
            <img alt="Click Here to Pick up the date" border="0" height="16" src="{$base}images/calendar/cal.gif" width="16"/>
          </a>
          <xsl:variable name="stHr" select="/stxx/form/publishedAssessmentActionForm/publish_start_hours"/> &#160;&#160; <select name="start_hours">
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
          </select> &#160;&#160;: <select name="start_minutes">
            <xsl:call-template name="minuteDropdown">
              <xsl:with-param name="mSelected">
                <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/publish_start_minutes"/>
              </xsl:with-param>
            </xsl:call-template>
          </select>
        </td>
      </tr>
      <!--******************************************************************-->
      <xsl:if test="$showDueDate='SHOW'">
      <tr>
        <td class="form_label" width="{$firstColWidth}"> 
<!--          <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'CONSIDER_END_DATE'"/>--><!--CONSIDER_END_DATE -->
<!--            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
             <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates>--> Due Date</td>
        <td>
          <input id="publish_end_day" name="publish_end_day" type="text">
            <xsl:attribute name="value">
              <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/publish_end_day"/>
            </xsl:attribute>
          </input>&#160; <a>
            <xsl:attribute name="href">javascript:cal2.popup("","<xsl:value-of select="concat($base,'html/')"/>");</xsl:attribute>
            <img alt="Click Here to Pick up the date" border="0" height="16" src="{$base}images/calendar/cal.gif" width="16"/>
          </a>
          <xsl:variable name="endHr" select="/stxx/form/publishedAssessmentActionForm/publish_end_hours"/> &#160;&#160; <select name="end_hours">
            <xsl:call-template name="hourDropdown">
              <xsl:with-param name="hSelected">
                <xsl:choose>
                  <xsl:when test="$endHr='0'">0</xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="$endHr"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:call-template>
          </select> &#160;&#160;: <select name="end_minutes">
            <xsl:call-template name="minuteDropdown">
              <xsl:with-param name="mSelected">
                <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/publish_end_minutes"/>
              </xsl:with-param>
            </xsl:call-template>
          </select>
        </td>
      </tr></xsl:if>
      <!--******************************************************************-->
      <xsl:if test="$showRetractDate='SHOW'">
      <tr>
        <td class="form_label" width="{$firstColWidth}"> 
<!--         <xsl:apply-templates select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
            <xsl:with-param name="keyName" select="'CONSIDER_RETRACT_DATE'"/>--><!-- -->
<!--            <xsl:with-param name="keyCompare" select="'True'"/>
            <xsl:with-param name="display" select="'checkbox'"/>
             <xsl:with-param name="disabled">true</xsl:with-param>
          </xsl:apply-templates>--> Retract Date</td>
        <td>
          <input id="publish_retract_day" name="publish_retract_day" type="text">
            <xsl:attribute name="value">
              <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/publish_retract_day"/>
            </xsl:attribute>
          </input>&#160; <a>
            <xsl:attribute name="href">javascript:cal3.popup("","<xsl:value-of select="concat($base,'html/')"/>");</xsl:attribute>
            <img alt="Click Here to Pick up the date" border="0" height="16" src="{$base}images/calendar/cal.gif" width="16"/>
          </a>
          <xsl:variable name="retractHr" select="/stxx/form/publishedAssessmentActionForm/publish_retract_hours"/> &#160;&#160; 
          <select name="retract_hours">
            <xsl:call-template name="hourDropdown">
              <xsl:with-param name="hSelected">
                <xsl:choose>
                  <xsl:when test="$retractHr='0'">0</xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="$retractHr"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:call-template>
          </select> &#160;&#160;: <select name="retract_minutes">
            <xsl:call-template name="minuteDropdown">
              <xsl:with-param name="mSelected">
                <xsl:value-of select="/stxx/form/publishedAssessmentActionForm/publish_retract_minutes"/>
              </xsl:with-param>
            </xsl:call-template>
          </select>
        </td>
      </tr>
      <xsl:variable name="isActive" select="/stxx/form/publishedAssessmentActionForm/isActive"/>
      <xsl:if test="$isActive='true'">
      <tr>
        <td class="form_label" width="{$firstColWidth}"/>
        <td align = "center"><br/>
          <input type = "submit" name= "action" value="Revoke Published Assessment"/>
        </td>
       </tr>  
       </xsl:if>
        </xsl:if>
      <!--******************************************************************-->
    </table>
  </xsl:template>
</xsl:stylesheet>
