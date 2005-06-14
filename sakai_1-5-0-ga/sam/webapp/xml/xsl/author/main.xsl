<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: main.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="../layout/header.xsl"/>
  <xsl:output method="html"/>
  <!-- This template processes the root node ("/") -->
  <xsl:template match="/">
    <html>
      <head>
        <title> Authoring Front Door</title>
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
        <script src="{$base}js/tigra_tables.js" type="text/javascript"/>
        <script src="{$base}js/navigo.js" type="text/javascript"/>
<!--        <script type="text/javascript">
          function keyHandler()
          {
            alert(window.event.keyCode);
            if (window.event.keyCode == 13)
              return;
            else
              document.all.item("tex").value = window.event.keyCode;
          }
      </script>
-->
      </head>
      <body>
        <xsl:call-template name="header"/>
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
        <xsl:variable name="formname" select="concat($base,'asi/author/assessment/assessmentAction.do')"/>
<!--        <table align="center" border="0" cellpadding="3" cellspacing="2" width="750">-->
        <table align="center" border="0" cellpadding="3" cellspacing="2" width="820">
<!--          <form action="{$formname}" method="post" name="author" onkeypress="keyHandler()">-->
            <xsl:variable name="errorMsg">
              <xsl:value-of select="/stxx/form/assessmentActionForm/errorMsg"/>
            </xsl:variable>
            <xsl:if test="$errorMsg !=''">
              <tr>
                <td align="center" class="alert">ERROR : <xsl:value-of select="/stxx/form/assessmentActionForm/errorMsg"/>
                </td>
              </tr>
            </xsl:if>
            <tr>
              <td align="left">
                <form action="{$formname}" method="post" name="author">
                <table width="100%">
                  <tr>
                    <td align="left" valign="bottom">
                      <h4>My Assessments</h4>
                    </td>
                    <td align="right" valign="top">
                      <input name="action" type="submit" value="Pool Manager"/> &#160;&#160;&#160; <xsl:variable name="templatesEnabled">
                        <xsl:value-of select="/stxx/form/assessmentActionForm/templatesEnabled"/>
                      </xsl:variable>
                      <xsl:if test="$templatesEnabled = 'true'">
                        <xsl:variable name="editTemplate">
                          <xsl:value-of select="/stxx/form/assessmentActionForm/editTemplate"/>
                        </xsl:variable>
                        <xsl:if test="$editTemplate = 'true'">
                          <input name="action" type="submit" value="Templates"/>
                        </xsl:if>
                      </xsl:if>
                    </td>
                  </tr>
                </table>
                </form>
              </td>
            </tr>
            <tr>
              <td align="left" class="navigo_border">New Assessment</td>
            </tr>
            <tr>
              <td>
<!--
                <table width="95%">
                  <tr>
                    <td align="left" class="form_label">Create a new assessment</td>
                    <td align="right" class="form_label">Import from existing assessment</td>
                  </tr>
                  <tr>
                    <td align="left"> &#160;&#160;&#160;&#160;Title : <input id="title" name="title" type="text"/> &#160;&#160;&#160;<input name="action" onClick="javascript:checkEmpty_focus('title', 'Assessment name can not be empty')" type="submit" value="Create"/>
                    </td>
                    <td align="right">
                      <input name="action" type="submit" value="Import"/>
                    </td>
                  </tr>
                  <xsl:variable name="templatesEnabled">
                    <xsl:value-of select="/stxx/form/assessmentActionForm/templatesEnabled"/>
                  </xsl:variable>
                  <xsl:if test="$templatesEnabled = 'true'">
                    <tr>
                      <td align="left" class="form_label" colspan="2">Use Existing Template</td>
                    </tr>
                    <tr> &#160;&#160;&#160;&#160; <td align="left">
                        <xsl:apply-templates select="/stxx/form/assessmentActionForm/existingTemplates"/>
                      </td>
                    </tr>
                  </xsl:if>
                </table>
-->
                <form action="{$formname}" method="post" name="author1">
                <table width="100%">
                  <xsl:variable name="templatesEnabled">
                    <xsl:value-of select="/stxx/form/assessmentActionForm/templatesEnabled"/>
                  </xsl:variable>
                  <tr>
                    <td align="left" class="form_label">Create a new assessment</td>
                    <xsl:if test="$templatesEnabled = 'true'">
                      <td align="middle">Choose Existing Template (optional)</td>
                    </xsl:if>
                    <td align="right" class="form_label">Import from existing assessment</td>
                  </tr>
                  <tr>
                    <td align="left">
                      <table width="40%">
                        <tr>
                          <td align="left" valign="top">Title: </td>
                          <td align="middle" valign="top"><input id="title" name="title" type="text"/> &#160;</td>
                          <td align="right" valign="top"><input name="action" onClick="javascript:checkEmpty_focus('title', 'Assessment name can not be empty')" type="submit" value="Create"/></td>
                        </tr>
                      </table>
                    </td>
                    <xsl:if test="$templatesEnabled = 'true'">
                      <td align="middle">
                        <xsl:apply-templates select="/stxx/form/assessmentActionForm/existingTemplates"/>
                      </td>
                    </xsl:if>
                    <td align="right">
                      <input name="action" type="submit" value="Import"/>
                    </td>
                  </tr>
                </table>
                </form>
              </td>
            </tr>
<!--          </form>-->
          <tr>
            <td align="left" class="navigo_border">Core Assessments</td>
          </tr>
          <tr>
            <td>
              <table align="right" border="0" width="770">
                <tr>
<!--                  <td width="38%">-->
                  <td width="315">
                    <b>Title</b>
                  </td>
                  <td width="91">
<!--chen update -->
<!--                    <b>Last Modified</b>-->
                  <b>             </b>
                  </td>
<!--                  <td align="center" colspan="4" width="52%">-->
                  <td align="center" colspan="4" width="364">
                    <b>View or Modify</b>
                  </td>
                </tr>
                <tr>
                  <td colspan="6">
                    <xsl:apply-templates select="/stxx/form/assessmentActionForm/assessmentElements"/>
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!--new code -->
                    <tr>
            <td align="left" class="navigo_border">Published Assessments</td>
          </tr>
          <tr>
            <td align="left">
              <b>Active</b>&#160;(testing in progress)
<!--               <table align="right" border="0" width="100%">-->
              <table align="right" border="0" width="770">
                <tr>
<!--                  <td width="20%">-->
                  <td valign = "top" align="left" width="150">
                    <table align="right" width="100%">
                      <tr>
                        <td valign="top" align="left">
                          <b>Title</b>
                        </td>
                      </tr>
                    </table>
<!--                    <b>Title</b>-->
                  </td>
<!--                  <td width="20%">-->
                  <td valign = "top" align="left" width="210">
                    <b>Release to </b>
                  </td>
<!--                  <td width="20%">-->
                  <td valign = "top" align="left" width="95">
                    <b>Release Date </b>
                  </td>
<!--                  <td width="20%">-->
                  <td valign = "top" align="left" width="95">
                    <b>Due Date</b>
                  </td>
<!--                  <td colspan="2" width="20%">-->
                  <td valign = "top" align="middle" width="220">
                    <b>View or Modify</b>
                  </td>
                </tr>
               </table>
            </td>
          </tr>
           <tr>
              <td >
                <xsl:apply-templates select="/stxx/form/assessmentActionForm/assessmentPublishedElements"/>
              </td>
            </tr>
          <!-- -->
          <tr>
            <td>
              <br/>
            </td>
          </tr>
          <tr>
            <td align="left">
              <b>Inactive</b> &#160;(no student access<!--, ungraded responses -->)
              <table align="right" width="770">
                <tr>
<!--                  <td width="20%">-->
                  <td valign = "top" align="left" width="150">
                    <table align="right" width="100%">
                      <tr>
                        <td valign="top" align="left">
                          <b>Title</b>
                        </td>
                      </tr>
                    </table>
                  </td>
<!--                  <td width="20%">-->
                  <td valign = "top" align="left" width="210">
                    <b>Release to </b>
                  </td>
<!--                  <td width="20%">-->
                  <td valign = "top" align="left" width="95">
                    <b>Release Date </b>
                  </td>
<!--                  <td width="20%">-->
                  <td valign = "top" align="left" width="95">
                    <b>Due Date</b>
                  </td>
                  <td colspan="2" width="220"/>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
              <td >
                <xsl:apply-templates select="/stxx/form/assessmentActionForm/assessmentExpiredPublishedElements"/>
              </td>
            </tr>
<!--    TAKING OFF COMPLETED
         <tr>
            <td>
              <br/>
            </td>
          </tr>
          <tr>
            <td align="left">
              <b>Completed </b>&#160;(testing complete, all responses graded) <table align="right" width="95%">
                <tr>
                  <td width="20%">
                    <b>Title</b>
                  </td>
                  <td width="20%">
                    <b>Location </b>
                  </td>
                  <td width="20%">
                    <b>Release Date </b>
                  </td>
                  <td width="20%">
                    <b>Due Date</b>
                  </td>
                  <td colspan="2" width="20%"/>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
              <td >
                <xsl:apply-templates select="/stxx/form/assessmentActionForm/assessmentCompletedPublishedElements"/>
              </td>
            </tr>
          <tr>
            <td>
              <br/>
            </td>
          </tr> -->
        </table>
      </body>
    </html>
  </xsl:template>
  <!--******************************************************************************* -->
  <xsl:template match="existingTemplates">
    <select name="selectTemplate">
      <option value="">Select</option>
      <xsl:for-each select="*">
        <option>
          <xsl:attribute name="value">
            <xsl:value-of select="substring-before(.,'+')"/>
          </xsl:attribute>
          <xsl:value-of select="substring-after(.,'+')"/>
        </option>
      </xsl:for-each>
    </select>
  </xsl:template>
  <!--******************************************************************************* -->
  <xsl:template match="assessmentElements">
    <xsl:variable name="base">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>
    <xsl:variable name="formname" select="concat($base,'asi/author/assessment/assessmentAction.do')"/>
    <table border="0" id="mainTable" name="mainTable" width="100%">
      <xsl:for-each select="value/assessment | assessment">
        <tr>
          <form action="{$formname}" method="post" name="author">
<!--            <td align="left" valign="top" width="38%">-->
            <td align="left" valign="top" width="315">
              <input name="assessmentID" type="Hidden">
                <xsl:attribute name="value">
                  <xsl:value-of select="@ident"/>
                </xsl:attribute>
              </input>
<!--              <xsl:value-of select="@title"/> &#160;&#160;&#160; &#160;&#160;&#160; &#160;&#160;&#160; &#160;&#160;&#160; </td>-->
            <xsl:variable name="assessmentID" select="@ident"/>
            <a href="{$formname}?assessmentID={$assessmentID}&amp;action=Questions"><xsl:value-of select="@title"/></a></td>
<!--            <td align="left" valign="top" width="10%">--><td align="left" valign="top" width="91"></td>
<!--            <td align="right" valign="top" width="15%">-->
            <td align="right" valign="top" width="91">
              <input name="action" type="submit" value="Questions"/>
            </td>
<!--            <td align="right" valign="top" width="13%">-->
            <td align="middle" valign="top" width="91">
              <input name="action" type="submit" value="Settings"/>
            </td>
<!--            <td align="right" valign="top" width="12%">-->
            <td align="middle" valign="top" width="91">
              <input name="action" type="submit" value="Export"/>
            </td>
<!--            <td align="right" valign="top" width="12%">-->
            <td align="middle" valign="top" width="91">
              <input name="action" type="submit" value="Remove"/>
            </td>
          </form>
        </tr>
      </xsl:for-each>
<!--      <script language="JavaScript"> tigra_tables('mainTable', 0, 0, '#ffffff', '#ffffcc', '#FFCC99', '#E0E0E0');</script>-->
      <script language="JavaScript"> tigra_tables('mainTable', 0, 0, '#ffffff', '#ffffcc', '#DDDDDD', '#E0E0E0');</script>
    </table>
  </xsl:template>

  <!--******************************************************************************* -->
    <xsl:template match="assessmentPublishedElements | assessmentCompletedPublishedElements | assessmentExpiredPublishedElements ">
    <xsl:apply-templates select="value"/>
   </xsl:template>
  <!--******************************************************************************* -->
  <xsl:template match="value">
      <xsl:variable name="base">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>
    <xsl:variable name="formname" select="concat($base,'asi/author/assessment/published/publishedAssessmentAction.do')"/>
    <xsl:variable name="formname1" select="concat($base,'totalScores.do')"/>
      <table border="0" width="820" name="publishedTable" id="publishedTable">
        <tr>
          <form action="{$formname}" method="post" name="author">
            <td valign = "top" align="left" width="170">
              <input name="publishedID" type="hidden">
                <xsl:attribute name="value">
                  <xsl:value-of select="publishedId"/>
                </xsl:attribute>
              </input>
              <input name="assessmentID" type="hidden">
                <xsl:attribute name="value">
                  <xsl:value-of select="coreAssessmentIdString"/>
                </xsl:attribute>
              </input>
              <table align="right" width="100%">
                 <tr>
                   <td valign="top" align="left">
                     <xsl:value-of select="assessmentDisplayName"/>
                   </td>
                 </tr>
               </table>
            </td>
<!--            <td valign = "top" align="left" width="34%">-->
             <td valign = "top" align="left" width="240">
             <xsl:for-each select="publishedTo/value">
             <xsl:value-of select="."/> <br/>
             </xsl:for-each>
            </td>
<!--            <td valign = "top" align="left" width="13%">-->
            <td valign = "top" align="middle" width="95">
              <xsl:value-of select="beginDate"/>
            </td>
<!--            <td  valign = "top" align="left" width="13%">-->
            <td valign = "top" align="middle" width="95">
              <xsl:value-of select="endDate"/>
            </td>
            <xsl:if test="parent::assessmentPublishedElements">
            <input type="hidden" name="isActive" value="true"/>
            </xsl:if>
            <td  valign = "top" align="middle" width="110">
              <input name="action" type="submit" value="Settings"/>
              <input name="publishedName" type="hidden">
                <xsl:attribute name="value">
                  <xsl:value-of select="assessmentDisplayName"/>
                </xsl:attribute>
              </input>
            </td>
          </form>
          <form action="{$formname1}" method="Get" name="author">
            <td valign = "top" align="middle" width="110">
              <xsl:if test="hasResponses='true'">
                <input name="action" type="submit" value="Responses"/>
              </xsl:if>
              <xsl:if test="not(hasResponses='true')">
                <input name="action" type="submit" disabled="true" value="Responses"/>
              </xsl:if>
              <!--AssessmentID  -->
              <input name="id" type="Hidden">
                <xsl:attribute name="value">
                  <xsl:value-of select="coreAssessmentIdString"/>
                </xsl:attribute>
              </input>
              <!-- Published Assessment ID, used for
               getAssetByDate(coreId, publishedId.getCreatedDate())-->
              <input name="publishedID" type="hidden">
                <xsl:attribute name="value">
                  <xsl:value-of select="publishedId"/>
                </xsl:attribute>
              </input>
            </td>
          </form>
        </tr>
<!--        <script language="JavaScript"> tigra_tables('publishedTable', 0, 0, '#ffffff', '#ffffcc', '#FFCC99', '#E0E0E0');</script>-->
        <script language="JavaScript"> tigra_tables('publishedTable', 0, 0, '#ffffff', '#ffffcc', '#DDDDDD', '#E0E0E0');</script>
      </table>
  </xsl:template>
    <!--******************************************************************************* -->

</xsl:stylesheet>
