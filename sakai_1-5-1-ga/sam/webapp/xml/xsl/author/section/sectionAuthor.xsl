<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: sectionAuthor.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="../../layout/header.xsl"/>
  <xsl:import href="sectionImages.xsl"/>
  <xsl:import href="sectionQtiMetadata.xsl"/>
  <xsl:import href="sectionMetadata.xsl"/>
  <xsl:import href="sectionPool.xsl"/>
  <xsl:import href="../item/itemRecordingData.xsl"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>Section Edit<xsl:value-of select="/stxx/applicationResources/key[@name='author.common.section']"/> Display - Author mode</title>
        <script language="javascript"> var section=0; </script>
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
        <link href="{$base}htmlarea/htmlarea.css" rel="STYLESHEET" type="TEXT/CSS"/>
        <!-- load the main HTMLArea files  -->
        <script src="{$base}htmlarea/htmlarea.js" type="text/javascript"/>
        <script src="{$base}htmlarea/lang/en.js" type="text/javascript"/>
        <script src="{$base}htmlarea/dialog.js" type="text/javascript"/>
        <!-- <script type="text/javascript" src="popupdiv.js"></script> -->
        <script src="{$base}htmlarea/popupwin.js" type="text/javascript"/>
        <script src="{$base}htmlarea/navigo_js/navigo_editor.js" type="text/javascript"/>
        <!-- load the plugins -->
        <xsl:variable name="root" select="concat($base,'htmlarea/')"/>
        <script type="text/javascript">
        //HTMLArea.loadPlugin("TableOperations","<xsl:value-of select="$root"/>" );
         HTMLArea.loadPlugin("SpellChecker", "<xsl:value-of select="$root"/>");
         var ta_editor = [];
         var hidden = [];
         var textAreaNames = [];
         var runFocus=true;
         function startup()
         {
           loadEditor("<xsl:value-of select="$root"/>",1,1,"two");
         };
          <!-- HTMLAREA AUDIO INFO -->
          <xsl:call-template name="js_recording_variables"/>
        </script>
        <!-- END HTML AREA -->
      </head>
      <body onload="startup();">
        <xsl:call-template name="header">
          <xsl:with-param name="displayText" select="' Section Settings '"/>
        </xsl:call-template>
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
        <xsl:variable name="formname" select="concat($base,'asi/author/section/sectionAction.do')"/>
        <xsl:variable name="formname1" select="concat($base,'asi/author/assessment/assessmentAction.do')"/>
        <form action="{$formname1}" method="post" name="author">
          <table align="center" border="0" cellpadding="4" cellspacing="0" width="750">
            <tr>
              <td align="left" class="Title"> Create / Modify <xsl:value-of select="/stxx/applicationResources/key[@name='author.common.section']"/> - <xsl:value-of select="stxx/form/sectionActionForm/assessTitle"/>
              </td>
              <td align="right">
               <!--  <input name="action" type="submit" value="Test Questions"/> -->
              </td>
            </tr>
          </table>
          <input name="assessmentID" type="hidden">
            <xsl:attribute name="value">
              <xsl:value-of select="stxx/form/sectionActionForm/assessmentID"/>
            </xsl:attribute>
          </input>
        </form>
        <form action="{$formname}" method="post">
          <input name="assessmentID" type="hidden">
            <xsl:attribute name="value">
              <xsl:value-of select="stxx/form/sectionActionForm/assessmentID"/>
            </xsl:attribute>
          </input>
          <table align="center" border="0" cellpadding="4" cellspacing="0" width="750">
            <xsl:variable name="ident" select="stxx/section/@ident"/>
            <tr>
              <td align="left" class="navigo_border" colspan="3">
                <table border="0" width="100%">
                  <tr>
                    <td align="left" class="navigo_border_text_font">
                      <xsl:variable name="secTitle">
                        <xsl:value-of select="stxx/section/@title"/>
                      </xsl:variable>
                      <xsl:if test="$secTitle =''">New <xsl:value-of select="/stxx/applicationResources/key[@name='author.common.section']"/>
                      </xsl:if>
                      <xsl:value-of select="stxx/section/@title"/>
                    </td>
                    <td align="right">
                      <input name="removeConfirm" type="submit" value="Remove"/>
                      <input name="SectionIdent" type="hidden" value="{$ident}"/> &#160; </td>
                  </tr>
                </table>
              </td>
            </tr>
            <!--**************Title************************** -->
            <tr>
              <td colspan="3">
                <br/>
              </td>
            </tr>
            <tr>
              <td width="23">
                <xsl:call-template name="questionCircle"/>
              </td>
              <td align="left" class="form_label" width="18%">
                <xsl:value-of select="/stxx/applicationResources/key[@name='author.common.section']"/> Title:</td>
              <td>
                   <xsl:variable name="secTitle">
                  <xsl:value-of select="/stxx/section/@title"/>
                </xsl:variable>
                  <input maxlength="250" name="stxx/section/@title" size="50" type="text" value="{$secTitle}"/> <br/>
<!--                    NOTE:  Parts with the title “Default’ will not be visible to assessment takers.-->
                    NOTE: Part headers with the title "Default" will not be visible to assessment takers.
              </td>
            </tr>
            <!--**************Section Information************************** -->
            <tr>
              <td width="23">
                <xsl:call-template name="questionCircle"/>
              </td>
              <td align="left" class="form_label" colspan="2">
                <xsl:value-of select="/stxx/applicationResources/key[@name='author.common.section']"/> Information:</td>
            </tr>
            <xsl:variable name="sectionInfo">
              <xsl:value-of select="/stxx/section/presentation_material/flow_mat/material/mattext"/>
            </xsl:variable>
            <xsl:variable name="sectionInfoXpath">stxx/section/presentation_material/flow_mat/material/mattext</xsl:variable>
            <tr>
              <td colspan="3">
                <table>
                  <tr>
                    <td width="23"/>
                    <td align="left" class="form_label">
                      <textarea cols="80" id="{$sectionInfoXpath}" name="{$sectionInfoXpath}" rows="10">
                        <xsl:value-of select="$sectionInfo"/>
                      </textarea>
                    </td>
                    <td valign="top">
                      <xsl:variable name="base">
                        <xsl:call-template name="baseHREF"/>
                      </xsl:variable>
            <!--      <img alt="Toggle Toolbar" src="{$base}htmlarea/images/htmlarea_editor.gif">
                        <xsl:attribute name="onClick">javascript:hideUnhide("<xsl:value-of select="$sectionInfoXpath"/>","two");</xsl:attribute>
                      </img> -->
                      <a>
                        <xsl:attribute name="href">javascript:hideUnhide("<xsl:value-of select="$sectionInfoXpath"/>","two");</xsl:attribute>
                        Show/Hide <br />Editor
                      </a>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <!--**************metadata************************** -->
            <xsl:variable name="showMetadata">
              <xsl:value-of select="/stxx/form/sectionActionForm/showMetadata"/>
            </xsl:variable>
            <xsl:if test="$showMetadata='True'">
              <xsl:call-template name="sectionMetadata"/>
            </xsl:if>
            <!--**************Pool************************** -->
            <xsl:call-template name="sectionPool"/>
            <!--**************Order************************** -->
            <tr>
              <td width="23">
                <xsl:call-template name="questionCircle"/>
              </td>
              <td align="left" class="bold" colspan="2">Question ordering (not available for random draw):</td>
            </tr>
            <tr>
              <td/>
              <td align="left" class="bold" colspan="2">
                <xsl:variable name="orderType" select="/stxx/section/selection_ordering/order/@order_type"/>
                <input name="order_type" type="radio">
                  <xsl:if test="$orderType='Random'">
                    <xsl:attribute name="checked">true</xsl:attribute>
                  </xsl:if>
                </input> As listed on Assessment Questions page </td>
            </tr>
            <tr>
              <td/>
              <td class="bold" colspan="2">
                <xsl:variable name="orderType" select="/stxx/section/selection_ordering/order/@order_type"/>
                <input name="order_type" type="radio">
                  <xsl:if test="$orderType='Sequential'">
                    <xsl:attribute name="checked">true</xsl:attribute>
                  </xsl:if>
                </input> Random within <xsl:value-of select="/stxx/applicationResources/key[@name='author.common.section']"/>
              </td>
            </tr>
            <!--**************save and cancel************************** -->
            <tr>
              <td align="center" colspan="3">
                <input name="action" type="submit" value="Save"/> &#160;&#160;&#160;&#160; <input name="action" type="submit" value="Cancel"/>
              </td>
            </tr>
          </table>
        </form>
      </body>
    </html>
  </xsl:template>
  <!--****************************************************************************** -->
</xsl:stylesheet>
