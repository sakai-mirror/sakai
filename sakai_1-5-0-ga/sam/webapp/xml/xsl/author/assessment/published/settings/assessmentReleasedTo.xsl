<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
* <p>Title: NavigoProject.org</p>
* <p>Description:Assessment Settings Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: assessmentReleasedTo.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
  <xsl:import href="qtimetadata.xsl"/>
  <xsl:template name="assessmentReleasedTo">
    <xsl:variable name="base">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>
    <xsl:variable name="publishAnonymous">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_PUBLISH_ANONYMOUS</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <xsl:variable name="showAuthnUsers">
      <xsl:apply-templates mode="display" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield">
        <xsl:with-param name="req_fieldlabel">EDIT_AUTHENTICATED_USERS</xsl:with-param>
      </xsl:apply-templates>
    </xsl:variable>
    <table>
      <tr align="center">
        <td align="center" class="bold"> Available </td>
        <td align="center" valign="middle"/>
        <td align="center" class="bold"> Selected </td>
      </tr>
      <tr align="center">
        <td align="center">
          <select multiple="true" name="entirePubllishingList" size="10" scroll="true" style="width:400">
            <xsl:if test="$publishAnonymous='SHOW'">
              <option value="Anonymous">Anonymous</option>
            </xsl:if>
            <xsl:if test="$showAuthnUsers='SHOW'">
              <option value="Authenticated users">Authenticated users</option>
            </xsl:if>
<!--            <xsl:apply-templates select="/stxx/form/publishedAssessmentActionForm/groupList"/>-->
            <xsl:variable name="shownAnanymous">
              <xsl:if test="$publishAnonymous='SHOW'">True</xsl:if>
              <xsl:if test="not($publishAnonymous='SHOW')">False</xsl:if>
            </xsl:variable>
            <xsl:apply-templates select="/stxx/form/assessmentActionForm/groupList">
              <xsl:with-param name="shownAnanymous" select="$shownAnanymous"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="/stxx/form/publishedAssessmentActionForm/courseList"/>
          </select>
        </td>
        <td align="center" valign="middle">
          <!--    <A HREF="#" onClick="copyAllOptions(document.forms[0]['copy1'],document.forms[0]['copy2'],false); return false;">All &gt;&gt;</A> -->
          <input type="button" value="&lt;&lt;">
            <xsl:attribute name="onClick">removeSelectedOptions(this.form.selectedList); return false;</xsl:attribute>
          </input>
          <input type="button" value="&gt;&gt;">
            <xsl:attribute name="onClick">copySelectedOptions(this.form.entirePubllishingList,this.form.selectedList,false);return false;</xsl:attribute>
          </input>
        </td>
        <td align="center">
          <select id="selectedList" multiple="true" name="selectedList" size="10" style="width:400">
            <xsl:apply-templates select="/stxx/form/publishedAssessmentActionForm/assessmentReleasedTo_SelectedList"/>
          </select>
        </td>
      </tr>
    </table>
    <table align="left" width="100%">
      <tr align="left" width="100%">
        <td align="left" width="8%"/>
        <td align="left" class="bold" width="12%">Assessment URL: </td>
        <td align="left" width="80%">
          <a>
            <xsl:attribute name="href">
              <xsl:value-of select="/stxx/request/attribute[@name='URI_PREFIX']/URI_PREFIX"/><xsl:value-of select="$base"/><xsl:value-of select="/stxx/applicationResources/key[@name='author.assessment.publishingURL']"/>?id=<xsl:value-of select="/stxx/request/attribute[@name='PUBLISHED_ID']/PUBLISHED_ID"/>
            </xsl:attribute>
            <xsl:value-of select="/stxx/request/attribute[@name='URI_PREFIX']/URI_PREFIX"/><xsl:value-of select="$base"/><xsl:value-of select="/stxx/applicationResources/key[@name='author.assessment.publishingURL']"/>?id=<xsl:value-of select="/stxx/request/attribute[@name='PUBLISHED_ID']/PUBLISHED_ID"/>
          </a>
        </td>
      </tr>
    </table>
  </xsl:template>
  <xsl:template match="assessmentReleasedTo_SelectedList">
    <xsl:for-each select="*">
      <option>
        <xsl:attribute name="value">
          <xsl:value-of select="substring-before(.,'+')"/>
        </xsl:attribute>
        <xsl:value-of select="substring-after(.,'+')"/>
      </option>
    </xsl:for-each>
  </xsl:template>
 <xsl:template match="courseList">
    <xsl:for-each select="*">
      <option>
        <xsl:attribute name="value">
          <xsl:value-of select="substring-before(.,'+')"/>
        </xsl:attribute>
        <xsl:value-of select="substring-after(.,'+')"/>
      </option>
    </xsl:for-each>
  </xsl:template>
<!--  <xsl:template match="groupList">
    <xsl:for-each select="*">
      <option>
        <xsl:attribute name="value">
          <xsl:value-of select="."/>
        </xsl:attribute>
        <xsl:value-of select="."/>
      </option>
    </xsl:for-each>
  </xsl:template>
-->
  <xsl:template match="groupList">
    <xsl:param name="shownAnanymous"/>
    <xsl:for-each select="*">
      <xsl:if test="not($shownAnanymous='True')">
        <xsl:if test="not(substring-after(.,'+')='Anonymous')">
          <option>
            <xsl:attribute name="value">
              <xsl:value-of select="substring-before(.,'+')"/>
            </xsl:attribute>
            <xsl:value-of select="substring-after(.,'+')"/>
          </option>
        </xsl:if>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
