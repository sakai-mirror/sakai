<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>must
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: showCompleteAssessment.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="../../layout/header.xsl"/>
  <xsl:import href="../../commonTemplates/loopTemplate.xsl"/>
  <xsl:import href="../../commonTemplates/itemRecordingData.xsl"/>
  <xsl:import href="../item/itemTypes.xsl"/>
  <xsl:import href="../../delivery/delivery.xsl"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>Sakai Assessment - Preview Questions</title>
      </head>
      <body>
        <!-- for audio questions: constructing parameters required to save an audio recording -->
        <script>
          <xsl:call-template name="js_recording_variables"/>
        </script>
        <xsl:call-template name="header">
          <xsl:with-param name="displayText" select="/stxx/questestinterop/assessment/@title"/>
        </xsl:call-template>
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
        <script type="text/javascript"> 
            function PopupWin(url)
            {
               window.open(url,"ha_fullscreen","toolbar=no,location=no,directories=no,status=no,menubar=yes,"+"scrollbars=yes,resizable=yes,width=640,height=480");
            }
              
        </script>
        <xsl:variable name="formname" select="concat($base,'asi/author/assessment/assessmentAction.do')"/>
        <xsl:variable name="formname1" select="concat($base,'asi/author/item/itemAction.do')"/>
        <table align="center" border="0" cellpadding="3" width="750">
<!--
          <form action="{$formname}" method="post" name="author">
            <tr>
              <td align="left" class="Title" width="30%">
                <br/>Assessment Questions</td>
              <td align="right">
                <input name="action" type="submit" value="My Assessments"/>
              </td>
            </tr>
            <tr>
              <td colspan="2">
                <br/>
              </td>
            </tr>
            <tr>
              <td cellpadding="13" class="navigo_border" colspan="2">
                <table width="100%">
                  <tr>
                    <td align="left" class="navigo_border_text_font">
                      <b>
                        <xsl:value-of select="/stxx/questestinterop/assessment/@title"/>
                        <xsl:variable name="ident" select="/stxx/questestinterop/assessment/@ident"/>
                        <input name="assessmentID" type="hidden" value="{$ident}"/>
                      </b>
                    </td>
                    <td align="right" class="navigo_border_text_font">
                      <input name="action" type="submit" value="Settings"/> &#160; <b>
                        <xsl:variable name="assessmentId" select="/stxx/questestinterop/assessment/@ident"/>
                        <xsl:variable name="link1" select="concat('../../delivery/xmlAction.do?previewAssessment&amp;','assessmentId=')"/>
                        <xsl:variable name="link" select="concat($link1,$assessmentId)"/>
<input name="action" type="button" onClick="javascript:PopupWin('{$link}')" value="Preview Assessment" />
                        
                      </b>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </form>
-->          
          <form action="{$formname}" method="post" name="author">
            <tr>
              <td><br/></td>
            </tr>
<!--
            <tr>
              <td cellpadding="13" class="navigo_border" colspan="2">
                <table width="100%">
                  <tr>
                    <td align="left" class="navigo_border_text_font">
                      <b>
                        <xsl:value-of select="/stxx/questestinterop/assessment/@title"/>
                        <xsl:variable name="ident" select="/stxx/questestinterop/assessment/@ident"/>
                        <input name="assessmentID" type="hidden" value="{$ident}"/>
                      </b>
                    </td>
                    <td align="left" class="navigo_border_text_font"> ( <xsl:value-of select="format-number(sum(//item/resprocessing/outcomes/decvar/@minvalue), '0.00')"/> total points)</td>
                    <td align="right" class="navigo_border_text_font">
                      <input name="action" type="submit" value="Settings"/> &#160;
                      <input name="action" type="submit" value="My Assessments"/> &#160;
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
-->

            <tr>
              <td cellpadding="13" class="navigo_border" colspan="2">
                <table width="100%">
                  <tr>
                    <td align="left" class="navigo_border_text_font">
                      <b>
                        <xsl:value-of select="/stxx/questestinterop/assessment/@title"/>
                        <xsl:variable name="ident" select="/stxx/questestinterop/assessment/@ident"/>
                        <input name="assessmentID" type="hidden" value="{$ident}"/>
                      </b>
                    </td>
                    <td align="right" class="navigo_border_text_font">
                      <input name="previousPage" type="hidden" value="showCompleteAssessment"/>
                      <input name="action" type="submit" value="Settings"/> &#160;
                      <input name="action" type="submit" value="My Assessments"/>
                    </td>
                  </tr>
                  <tr>
                    <td align="left" class="navigo_border_text_font"> ( <xsl:value-of select="format-number(sum(//item/resprocessing/outcomes/decvar/@maxvalue), '0.00')"/> total points)</td>
                    <td align="right" class="navigo_border_text_font">
                      <b>
                        <xsl:variable name="assessmentId" select="/stxx/questestinterop/assessment/@ident"/>
                        <xsl:variable name="link1" select="concat('../../delivery/xmlAction.do?previewAssessment&amp;','assessmentId=')"/>
                        <xsl:variable name="link" select="concat($link1,$assessmentId)"/>
                      </b>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </form>

          <!--******************************Insert Question********************************************-->
          <form action="{$formname}" method="post" name="author">
            <tr>
              <td align="center" colspan="2">
                <table align="center" border="0" width="80%">
                  <tr>
                    <td colspan="3" class="bold">Existing Questions (<xsl:value-of select="count(//item)"/> total) </td>
                  </tr>
                  <tr>
                  </tr>
                  <tr>
                    <td width="60"/>
                    <td align="left" class="form_label" valign="top" width="30%"> Insert <xsl:value-of select="/stxx/applicationResources/key[@name='author.common.section']"/>: &#160;<br/>
                    </td>
                    <td align="left">
                      <input name="action" type="submit" value="Add"/> &#160;&#160;&#160;&#160;&#160;&#160;<br/>
                      <xsl:variable name="assessTitle" select="/stxx/questestinterop/assessment/@title"/>
                      <input name="assessTitle" type="hidden" value="{$assessTitle}"/>
                      <xsl:variable name="ident" select="/stxx/questestinterop/assessment/@ident"/>
                      <input name="assessmentID" type="hidden" value="{$ident}"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </form>

          <form action="{$formname1}" method="post" name="author">
            <tr>
              <td align="center" colspan="2">
                <table align="center" border="0" width="80%">
                    <xsl:variable name="assessTitle" select="/stxx/questestinterop/assessment/@title"/>
                    <input name="assessTitle" type="hidden" value="{$assessTitle}"/>
                    <xsl:variable name="ident" select="/stxx/questestinterop/assessment/@ident"/>
                    <input name="assessmentID" type="hidden" value="{$ident}"/>
                  <tr>
                    <td width="60"/>
                    <td align="left" class="form_label" width="30%"> Insert Question: &#160;</td>
                    <!--if there is no section then do not allow to insert a question -->
                    <xsl:if test="count(//section)&gt; 0">
                      <td align="left">
<!--                        <xsl:call-template name="itemTypes"/> &#160;&#160; <input type="submit" value="Add"/>-->
<xsl:call-template name="itemTypesAuto">
  <xsl:with-param name="formName">'author'</xsl:with-param>
</xsl:call-template>
                      </td>
                    </xsl:if>
                    <xsl:if test="count(//section)= 0">
                      <td align="left" class="alert"> Please add <xsl:value-of select="/stxx/applicationResources/key[@name='author.common.section']"/> before adding a question. </td>
                    </xsl:if>
                  </tr>
                </table>
              </td>
            </tr>
          </form>
          <!--************************************************************** -->
          <xsl:apply-templates select="stxx/questestinterop/assessment"/>
        </table>
      </body>
    </html>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="assessment">
    <xsl:apply-templates select="section">
      <xsl:with-param name="totalItems" select="count(//item)"/>
      <xsl:with-param name="totalSections" select="count(//section)"/>
    </xsl:apply-templates>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="section">
    <xsl:param name="totalItems"/>
    <xsl:param name="totalSections"/>
    <xsl:variable name="title" select="@title"/>
    <xsl:variable name="currentSecitonID" select="@ident"/>
    <xsl:variable name="base1">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>
    <xsl:variable name="formname2" select="concat($base1,'asi/author/section/sectionAction.do')"/>
    <form action="{$formname2}" method="post">
      <tr>
        <td cellpadding="13" class="section_border" colspan="2">
          <table border="0" width="100%">
            <tr>
              <td align="left" class="navigo_border_text_font">
                <xsl:value-of select="/stxx/applicationResources/key[@name='author.common.section']"/> - <xsl:value-of select="@title"/> &#160; &#160;
                <!--Add Section reordering capability -->
                <select name="reorder" onchange="javascript:form.submit()">
                  <xsl:call-template name="createOptionsinLoop">
                    <xsl:with-param name="counter" select="number(1)"/>
                    <xsl:with-param name="increment" select="number(1)"/>
                    <xsl:with-param name="end">
                      <xsl:value-of select="$totalSections"/>
                    </xsl:with-param>
                    <xsl:with-param name="selected">
                      <xsl:value-of select="count((preceding::section)) + 1"/>
                    </xsl:with-param>
                  </xsl:call-template>
                </select>
                <!--End section reordering -->
              </td>
              <td align="right">
                <input name="removeConfirm" type="submit" value="Remove"/>
                <input name="SectionIdent" type="hidden" value="{@ident}"/> &#160; <input name="action" type="submit" value="Modify"/>
                <xsl:variable name="assessTitle" select="/stxx/questestinterop/assessment/@title"/>
                <input name="assessTitle" type="hidden" value="{$assessTitle}"/>
                <xsl:variable name="ident" select="/stxx/questestinterop/assessment/@ident"/>
                <input name="assessmentID" type="hidden" value="{$ident}"/>
                <input name="totalSections" type="hidden">
                  <xsl:attribute name="value">
                    <xsl:value-of select="$totalSections"/>
                  </xsl:attribute>
                </input>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </form>
    <tr>
      <td align="right"/>
      <td align="left">
        <xsl:apply-templates select="presentation_material/material/matimage"/>
      </td>
    </tr>
    <xsl:apply-templates select="item">
      <xsl:with-param name="totalItems" select="$totalItems"/>
      <xsl:with-param name="itemsInCurrentSection">
        <xsl:value-of select="count(item)"/>
      </xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="section">
      <xsl:with-param name="totalItems" select="$totalItems"/>
      <xsl:with-param name="itemsInCurrentSection"/>
    </xsl:apply-templates>
    <xsl:variable name="totalSectionItems" select="count(item)"/>
    <xsl:if test="$totalSectionItems=0">
      <xsl:variable name="formname3" select="concat($base1,'asi/author/item/itemAction.do')"/>
      <form action="{$formname3}" method="post">
        <tr>
          <td align="center" colspan="2">
            <table align="center" border="0" width="80%">
              <xsl:variable name="assessTitle" select="/stxx/questestinterop/assessment/@title"/>
              <input name="assessTitle" type="hidden" value="{$assessTitle}"/>
              <xsl:variable name="ident" select="/stxx/questestinterop/assessment/@ident"/>
              <input name="assessmentID" type="hidden" value="{$ident}"/>
              <input name="currentSecitonID" type="hidden" value="{$currentSecitonID}"/>
              <tr>
                <td width="60"/>
                <td align="left" class="form_label" width="25%"> Insert Question: &#160;</td>
                <td align="left">
                  <xsl:call-template name="itemTypesAuto">
                    <xsl:with-param name="formName">'author'</xsl:with-param>
                  </xsl:call-template>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </form>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="item">
    <xsl:param name="totalItems"/>
    <xsl:param name="itemsInCurrentSection"/>
    <xsl:variable name="base1">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>
    <xsl:variable name="formname2" select="concat($base1,'asi/author/item/itemAction.do')"/>
    <form action="{$formname2}" method="post" name="itemAction">
      <xsl:variable name="secident" select="../@ident"/>
      <input name="SectionIdent" type="hidden" value="{$secident}"/>
      <xsl:variable name="mctype">
        <xsl:apply-templates select="itemmetadata/qtimetadata/qtimetadatafield">
          <xsl:with-param name="keyName" select="'qmd_itemtype'"/>
        </xsl:apply-templates>
      </xsl:variable>
      <tr>
        <td cellpadding="13" colspan="2">
          <table align="right" border="0" class="navigo_border" width="95%">
            <tr>
              <td align="left" class="navigo_border_text_font">
                <b> Question <select name="reorder" onchange="javascript:form.submit()">
                    <xsl:call-template name="createOptionsinLoop">
                      <xsl:with-param name="counter" select="number(1)"/>
                      <xsl:with-param name="increment" select="number(1)"/>
                      <xsl:with-param name="end">
                        <xsl:value-of select="$totalItems"/>
                      </xsl:with-param>
                      <xsl:with-param name="selected">
                        <xsl:value-of select="count((preceding::item)) + 1"/>
                      </xsl:with-param>
                    </xsl:call-template>
                  </select> &#160; &#160; <xsl:call-template name="resolveItemTypes">
                    <xsl:with-param name="itemType">
                      <xsl:value-of select="$mctype"/>
                    </xsl:with-param>
                  </xsl:call-template> - <xsl:value-of select="format-number(resprocessing/outcomes/decvar/@maxvalue, '0.00')"/> Points &#160; </b>
              </td>
              <td align="right" cellpadding="13">
                <input name="removeConfirm" type="submit" value="Remove"/>
                <input name="ItemIdent" type="hidden" value="{@ident}"/> &#160; <input name="action" type="submit" value="Modify"/>
                <xsl:variable name="itemType" select="itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='qmd_itemtype']/following-sibling::fieldentry"/>
                <input name="itemType" type="hidden" value="{$itemType}"/>
                <xsl:variable name="items" select="count((preceding::item)) + 1"/>
                <input name="itemNo" type="hidden" value="{$items}"/>
                <xsl:variable name="assessTitle" select="/stxx/questestinterop/assessment/@title"/>
                <input name="assessTitle" type="hidden" value="{$assessTitle}"/>
                <input name="totalItems" type="hidden" value="{$totalItems}"/>
                <xsl:variable name="ident" select="/stxx/questestinterop/assessment/@ident"/>
                <input name="assessmentID" type="hidden" value="{$ident}"/>
                <input name="itemsInCurrentSection" type="hidden" value="{itemsInCurrentSection}"/>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <xsl:if test="$mctype!='Audio Recording' and $mctype!='File Upload' and $mctype!='Fill In the Blank'">
        <xsl:apply-templates select="presentation/flow">
          <xsl:with-param name="fibOrNot">false</xsl:with-param>
        </xsl:apply-templates>
      </xsl:if>
      <xsl:if test="$mctype='Fill In the Blank'">
        <xsl:apply-templates select="presentation/flow">
          <xsl:with-param name="fibOrNot">true</xsl:with-param>
        </xsl:apply-templates>
      </xsl:if>
      <xsl:if test="$mctype='File Upload'">
        <xsl:apply-templates select="presentation/flow/material" mode="upload"/>
      </xsl:if>
      <xsl:if test="$mctype='Audio Recording'">
        <xsl:apply-templates select="presentation/flow/material" mode="audio"/>
      </xsl:if>
      <tr>
        <td colspan="2">
          <br/>
        </td>
      </tr>
      <!--********************Show/ Hide Feedback************************************************************************** -->
      <!--TO DO PASS THIS FROM ASSSESSMENT AS IT IS CALLED N TIMES with EACH ITEM  -->
      <xsl:variable name="showOverall">
        <xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='FEEDBACK_DELIVERY']/following-sibling::fieldentry"/>
      </xsl:variable>
      <xsl:variable name="showQuestionLevel">
        <xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='FEEDBACK_SHOW_ITEM_LEVEL']/following-sibling::fieldentry"/>
      </xsl:variable>
      <xsl:variable name="showSelectionLevel">
        <xsl:value-of select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield/fieldlabel[text()='FEEDBACK_SHOW_SELECTION_LEVEL']/following-sibling::fieldentry"/>
      </xsl:variable>
     <!--Find out if the selection level feedbacks are not null  -->
      <xsl:variable name="ShowSelectionLevelFeedback"><xsl:apply-templates mode="isEmpty" select="itemfeedback"><xsl:with-param name="level">SelectionLevel</xsl:with-param></xsl:apply-templates></xsl:variable> 
       <!--************ -->
      <xsl:if test="$ShowSelectionLevelFeedback != '' and  $showOverall !='NONE' and $showSelectionLevel ='True'">
          <tr>
<!-- chen change-->
            <td/>
<!--chen change-->                    
<!--            <td align="left">&#160;&#160; <xsl:if test="number(count(itemfeedback)) &gt; 2"> Item Level Feedback </xsl:if> -->                            
            <td align="left">
              <table align="left" width="100%">
                <tr>
                  <td align="left" width="4%"/>
                  <td align="left" width="96%">
                    &#160;&#160; <xsl:if test="number(count(itemfeedback)) &gt; 2">Answer level feedback:</xsl:if>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <xsl:apply-templates mode="itemLevelFeedback" select="itemfeedback"/>
        </xsl:if>
      <!--Find out if the question level feedbacks are not null  -->
      <xsl:variable name="ShowQuestionLevelFeedback"><xsl:apply-templates mode="isEmpty" select="itemfeedback"><xsl:with-param name="level">QuestionLevel</xsl:with-param></xsl:apply-templates></xsl:variable> 
       <!--************ -->
      <xsl:if test="$ShowQuestionLevelFeedback != '' and $showOverall !='NONE' and $showQuestionLevel ='True'">
          <tr>
            <td/>
<!--chen change    
            <td align="left">&#160;&#160; <xsl:if test="$mctype!='Essay'"> Question Level Feedback </xsl:if>
            </td>
-->
            <td align="left">
              <table align="left" width="100%">
                <tr>
                  <td width="4%" align="left"/>
                  <td width="96%" align="left">&#160;&#160; 
                    <xsl:if test="$mctype!='Essay'"> Question Level Feedback: 
                    </xsl:if>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <xsl:if test="$mctype!='Multiple Choice Survey' and $mctype!='Audio Recording' and $mctype!='File Upload'">
          <tr>
            <td/>
<!--chen change-->
<!--            <td align="left">&#160;&#160;&#160;&#160;&#160; <xsl:if test="$mctype!='Essay'"> Correct Feedback </xsl:if>
              <xsl:if test="$mctype='Essay'"> Model Short Answer</xsl:if> : <xsl:apply-templates select="itemfeedback[@ident='Correct']/flow_mat/material/mattext ">
                <xsl:with-param name="showNo">No</xsl:with-param>
              </xsl:apply-templates>
            </td>
-->
            <td align="left">
              <table align="left" width="100%">
                <tr>
                  <td width="15%" align="left"/>
                  <td width="85%" align="left">&#160;&#160;&#160;&#160;&#160; 
                    <xsl:if test="$mctype!='Essay'"> Correct</xsl:if> 
                    <xsl:if test="$mctype='Essay'"> Model Short Answer</xsl:if>:
                    <xsl:apply-templates select="itemfeedback[@ident='Correct']/flow_mat/material/mattext ">
                      <xsl:with-param name="showNo">No</xsl:with-param>
                    </xsl:apply-templates>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          </xsl:if>
          <tr>
            <td/>
<!--chen change-->
<!--            <td align="left">&#160;&#160;&#160;&#160;&#160; <xsl:if test="$mctype!='Essay' and $mctype!='Multiple Choice Survey' and $mctype!='Audio Recording' and $mctype!='File Upload'">Incorrect </xsl:if> Feedback :
              <xsl:apply-templates select="itemfeedback[@ident='InCorrect']/flow_mat/material/mattext "> 
                <xsl:with-param name="showNo">No</xsl:with-param>
              </xsl:apply-templates>
-->
            <td align="left">
              <table align="left" width="100%">
                <tr>
                  <td width="15%" align="left"/>
                  <td width="85%" align="left">&#160;&#160;&#160;&#160;&#160; 
                    <xsl:if test="$mctype!='Essay' and $mctype!='Multiple Choice Survey' and $mctype!='Audio Recording' and $mctype!='File Upload'">Incorrect: 
                    </xsl:if>
                    <xsl:apply-templates select="itemfeedback[@ident='InCorrect']/flow_mat/material/mattext ">
                      <xsl:with-param name="showNo">No</xsl:with-param>
                    </xsl:apply-templates>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
      </xsl:if>
      <tr>
        <tr>
          <td colspan="2">
            <br/>
          </td>
        </tr>
        <td align="center" colspan="2">
          <input name="action" type="submit" value="Insert New Question"/>
          <input name="insertPosition" type="hidden" value="{position()}"/>
          <br/>
        </td>
      </tr>
    </form>
  </xsl:template>
  <!--******************To show item level feedback only******************************************* -->
  <xsl:template mode="itemLevelFeedback" match="itemfeedback">
    <xsl:if test="@ident !='Correct' and @ident !='InCorrect'">
        <tr>
          <td/>
<!-- chen change-->
<!--          <td align="left">&#160;&#160;&#160;&#160;&#160; <xsl:number count="itemfeedback" format="A" level="single"/>.&#160; <xsl:apply-templates select="flow_mat/material/mattext">
              <xsl:with-param name="showNo">No</xsl:with-param>
            </xsl:apply-templates>
-->
          <td align="left">
            <table align="left" width="100%">
              <tr>
                <td align="left" width="15%"/>
                <td align="left" width="85%">&#160;&#160;&#160;&#160;&#160; 
                  <xsl:number count="itemfeedback" format="A" level="single"/>.&#160;
                  <xsl:apply-templates select="flow_mat/material/mattext">
                    <xsl:with-param name="showNo">No</xsl:with-param>
                  </xsl:apply-templates>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <br/>
          </td>
        </tr>
      </xsl:if>
   </xsl:template>
  <!--************************************************************ -->
  <xsl:template mode="isEmpty" match="itemfeedback">
    <xsl:param name="level"/>
    <xsl:if test="$level='QuestionLevel'">
      <xsl:if test="@ident ='Correct' or @ident ='InCorrect'">
          <xsl:apply-templates mode="isEmpty" select="flow_mat/material/mattext"/>
      </xsl:if>       
    </xsl:if>     
    <xsl:if test="$level='SelectionLevel' and @ident !='Correct' and @ident !='InCorrect'">
      <xsl:apply-templates mode="isEmpty" select="flow_mat/material/mattext"/>
    </xsl:if>     
  </xsl:template>
   <!--****************************************************************************** -->
   <xsl:template mode="isEmpty" match="mattext">
    <xsl:variable name="cur"> 
     <xsl:value-of select="self::*"/><xsl:value-of disable-output-escaping="yes" select="descendant::comment()"/>
    </xsl:variable> 
      <xsl:if test="$cur!=''">False</xsl:if>
   </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="resprocessing">
    <xsl:value-of select="sum(respcondition/setvar)"/>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="presentation">
    <xsl:apply-templates select="response_str |response_lid | response_grp | material"/>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="flow">
    <xsl:param name="fibOrNot"/>
    <xsl:if test="not($fibOrNot='true')">
      <xsl:apply-templates select="response_str |response_lid | response_grp | material"/>
    </xsl:if>
    <xsl:if test="$fibOrNot='true'">
      <xsl:apply-templates mode="fibOnly" select="flow"/>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="flow" mode="fibOnly">
<!-- chen change
    <tr>
      <td>
        <br/>
      </td>
      <td/>
    </tr>
-->
    <tr>
      <td/>
      <td>
        <table align="left" width="100%">
          <tr>
            <td align="left" valign="top" width="3%">
              <xsl:value-of select="count((preceding::item)) +1"/>. &#160; <xsl:value-of select="self::*"/>
            </td>
            <td align="left" valign="top" width="97%">
              <xsl:apply-templates select="response_str | material ">
                <xsl:with-param name="showNo">No</xsl:with-param>
              </xsl:apply-templates>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </xsl:template>
  <!--***** File Upload ******************************************************************* -->
  <xsl:template match="material" mode="upload">
    <xsl:param name="showNo"/>
    <xsl:apply-templates select="mattext">
      <xsl:with-param name="showNo" select="$showNo"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="matimage"/>
    <xsl:apply-templates select="mat_extension" mode="upload"/>
  </xsl:template>
  <xsl:template match="mat_extension" mode="upload">
    <xsl:variable name="base">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>
    <tr>
      <td/>
<!-- chen change-->            
      <td>
        <table align="left" width="100%">
          <tr>
            <td align="left" width="3%"/>
            <td align="left" width="97%">
              <a href="#" onClick="javscript:window.open('{$base}/{.}'          +'?filename='+audioFileName          +'&amp;seconds='+audioSeconds          +'&amp;limit='+audioLimit          +'&amp;app='+audioAppName          +'&amp;dir='+audioDir          +'&amp;imageUrl='+audioImageURL,          '__ha_dialog',          'toolbar=no,menubar=no,personalbar=no,width=430,height=330,scrollbars=no,resizable=no')">Upload
              a file</a>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </xsl:template>
  <!--***** Audio ******************************************************************* -->
  <xsl:template match="material" mode="audio">
    <xsl:param name="showNo"/>
    <xsl:apply-templates select="mattext">
      <xsl:with-param name="showNo" select="$showNo"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="matimage"/>
    <xsl:apply-templates select="mat_extension" mode="audio"/>
  </xsl:template>
  <xsl:template match="mat_extension" mode="audio">
       <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
    <tr>
      <td/>
      <td>
        <xsl:variable name="timeallowed">
          <xsl:value-of select="../../../../itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='TIMEALLOWED']/following-sibling::fieldentry"/>
        </xsl:variable>
        <xsl:variable name="num_of_attempts">
          <xsl:value-of select="../../../../itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='NUM_OF_ATTEMPTS']/following-sibling::fieldentry"/>
        </xsl:variable>
        <script> audioSeconds=<xsl:value-of select="$timeallowed"/>; audioLimit=<xsl:value-of select="$num_of_attempts"/>; </script>
        <a href="#" onClick="javscript:window.open('{$base}/{.}'          +'?filename='+audioFileName          +'&amp;seconds='+audioSeconds          +'&amp;limit='+audioLimit          +'&amp;app='+audioAppName          +'&amp;dir='+audioDir          +'&amp;imageUrl='+audioImageURL,          '__ha_dialog',          'toolbar=no,menubar=no,personalbar=no,width=430,height=330,scrollbars=no,resizable=no')">
          <img border="0"><xsl:attribute name="src"><xsl:value-of select="$base"/>/htmlarea/images/recordresponse.gif</xsl:attribute> </img>
        </a>
        <p/> Time Allowed (seconds): <xsl:value-of select="$timeallowed"/>
        <br/> Number of Tries Allowed: 
          <xsl:if test="number($num_of_attempts) &gt; 10">Unlimited</xsl:if>
          <xsl:if test="number($num_of_attempts) &lt;= 10">
            <xsl:value-of select="$num_of_attempts"/>
          </xsl:if>
        <br/>
      </td>
    </tr>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="material">
    <xsl:param name="showNo"/>
    <xsl:apply-templates select="mattext">
      <xsl:with-param name="showNo" select="$showNo"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="matimage"/>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="mattext">
    <xsl:param name="showNo"/>
    <xsl:if test="$showNo = 'No'">
      <xsl:value-of select="self::*"/>
      <xsl:value-of disable-output-escaping="yes" select="descendant::comment()"/>
    </xsl:if>
    <xsl:if test="$showNo != 'No'">
      <tr>
<!--chen change -->            
        <td align="right" width="10%"/>
        <td align="left" width="90%">
          <table align="left" width="100%">
            <tr>
              <td align="left" valign="top" width="3%">
                <xsl:value-of select="count((preceding::item)) +1"/>. &#160; <xsl:value-of select="self::*"/>
              </td>
              <td align="left" valign="top" width="97%">
                <xsl:value-of disable-output-escaping="yes" select="descendant::comment()"/>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="matimage">
    <xsl:if test="@imagtype = 'text/html'">
      <tr>
<!--chen change -->
        <td align="right" width="10%"/>
        <td align="left" width="90%">
          <xsl:variable name="uri">
            <xsl:value-of select="@uri"/>
          </xsl:variable>
          <!--  <xsl:value-of select="$uri"/> -->
          <xsl:if test="@imagtype = 'text/html'">
            <xsl:if test="@uri != ''">
              <br/>
              <br/>
              <img>
                <xsl:attribute name="src">
                  <xsl:value-of select="@uri"/>
                </xsl:attribute>
              </img>
              <br/>
              <br/>
            </xsl:if>
          </xsl:if>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="response_lid">
    <xsl:variable name="cardinalityType" select="@rcardinality"/>
    <xsl:apply-templates select="render_choice/response_label"/>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="response_label">
    <tr>
      <td align="right"/>
      <td align="left">
<!-- chen change -->
        <table align="left" width="100%">
          <tr>
            <td align="left" width="5%"/>
            <td align="left" width="95%">
              <xsl:variable name="linkval">
                <xsl:apply-templates select="../../../../../resprocessing/respcondition">
                  <xsl:with-param name="linkrefid" select="'true'"/>
                  <xsl:with-param name="position">
                    <xsl:value-of select="position()"/>
                  </xsl:with-param>
                </xsl:apply-templates>
              </xsl:variable>
              <input>
                <xsl:attribute name="disabled">true</xsl:attribute>
                  <xsl:choose>
                    <xsl:when test="../../@rcardinality = 'Single'">
                      <xsl:attribute name="type">RADIO</xsl:attribute>
                      <xsl:if test="$linkval='Correct'">
                        <xsl:attribute name="checked">True</xsl:attribute>
                      </xsl:if>
                    </xsl:when>
                    <xsl:when test="../../@rcardinality = 'Multiple'">
                      <xsl:attribute name="type">CHECKBOX</xsl:attribute>
                      <xsl:if test="$linkval='Correct'">
                        <xsl:attribute name="checked">True</xsl:attribute>
                      </xsl:if>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:attribute name="type">RADIO</xsl:attribute>
                      <xsl:if test="$linkval='Correct'">
                        <xsl:attribute name="checked">True</xsl:attribute>
                      </xsl:if>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:attribute name="name">
                    <xsl:value-of select="../../@ident"/>
                  </xsl:attribute>
                  <xsl:attribute name="value">
                    <xsl:value-of select="@ident"/>
                  </xsl:attribute>
              </input> &#160; <xsl:variable name="title" select="../../../../../itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='qmd_itemtype']/following-sibling::fieldentry"/>
              <xsl:if test="$title != 'True False'">
                <xsl:if test="$title != 'Multiple Choice Survey' and $title!='Audio Recording' and $title!='File Upload'">
                  <xsl:number count="response_label" format="A" level="single"/>. </xsl:if>
              </xsl:if>
              <xsl:apply-templates select="flow_mat/material/mattext | material/mattext">
                <xsl:with-param name="showNo">No</xsl:with-param>
              </xsl:apply-templates>
<!-- chen change-->                
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td align="right"/>
      <td align="left">
        <xsl:apply-templates select="material/matimage">
          <xsl:with-param name="showNo" select="'No'"/>
        </xsl:apply-templates>
      </td>
    </tr>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="flow_mat">
    <xsl:apply-templates select="material"/>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="qtimetadatafield">
    <xsl:param name="keyName"/>
    <xsl:apply-templates select="fieldlabel">
      <xsl:with-param name="keyName" select="$keyName"/>
    </xsl:apply-templates>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="fieldlabel">
    <xsl:param name="keyName"/>
    <xsl:variable name="fieldlabel" select="."/>
    <xsl:if test="$fieldlabel=$keyName">
      <xsl:variable name="fieldentry" select="../fieldentry"/>
      <xsl:value-of select="$fieldentry"/>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="response_value">
    <tr>
      <td align="left" colspan="2">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="response_str">
    <xsl:param name="showNo"/>
    <xsl:if test="@ident != 'MCRationale'">
      <xsl:apply-templates select="render_fib">
        <xsl:with-param name="showNo" select="$showNo"/>
        <xsl:with-param name="position" select="position()"/>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="render_fib">
    <xsl:param name="showNo"/>
    <xsl:param name="position"/>
    <xsl:if test="$showNo!=''"> &#160; <input disabled="disabled" type="text">
        <xsl:attribute name="size">
          <xsl:value-of select="@cols"/>
        </xsl:attribute>
        <xsl:attribute name="value">
          <xsl:apply-templates mode="fib" select="../../../flow/response_str">
            <xsl:with-param name="ident" select="../@ident"/>
          </xsl:apply-templates>
        </xsl:attribute>
      </input>
    </xsl:if>
    <xsl:if test="$showNo=''">
      <tr>
        <td align="right" colspan="2">
          <br/>
          <textarea WRAP="virtual" disabled="true">
            <xsl:attribute name="cols">
              <xsl:value-of select="@columns"/>
            </xsl:attribute>
            <xsl:attribute name="rows">
              <xsl:value-of select="@rows"/>
            </xsl:attribute>
            <xsl:value-of select="."/>
          </textarea>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
   <xsl:template match="response_str" mode="fib">
    <xsl:param name="ident"/>
    <xsl:if test="$ident=@ident">
      <xsl:apply-templates mode="fib" select="../../../../resprocessing/respcondition">
        <xsl:with-param name="ident" select="$ident"/>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>  
  <!--****************************************************************************** -->
  <xsl:template match="respcondition" mode="fib">
    <xsl:param name="ident"/>
    <xsl:variable name="addOn"><xsl:if test="position()!= 1">, </xsl:if></xsl:variable>
      <xsl:apply-templates mode="fib" select="conditionvar/or">
        <xsl:with-param name="ident" select="$ident"/>
        <xsl:with-param name="addOn" select="$addOn"/>
      </xsl:apply-templates>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="or" mode="fib">
    <xsl:param name="ident"/>
    <xsl:param name="addOn"/>
      <xsl:apply-templates mode="fib" select="varequal">
         <xsl:with-param name="ident" select="$ident"/>
         <xsl:with-param name="addOn" select="$addOn"/>     
      </xsl:apply-templates>
  </xsl:template>
 
 <!--****************************************************************************** -->
  <xsl:template match="varequal" mode="fib">
    <xsl:param name="ident"/>
    <xsl:param name="addOn"/>
         <xsl:if test="@respident=$ident">
            <xsl:value-of select="$addOn"/><xsl:value-of select="."/>
         </xsl:if>  
  </xsl:template>
 
<!-- <xsl:template match="or" mode="getVarequal">
    <xsl:param name="ident"/>
    <xsl:apply-templates mode="getVarequal" select="varequal">
      <xsl:with-param name="ident" select="$ident"/>
    </xsl:apply-templates>
  </xsl:template> -->
  <!--****************************************************************************** -->
<!--   <xsl:template match="and" mode="getVarequal">
    <xsl:param name="ident"/>
    <xsl:param name="lastAnd"/>
    <xsl:variable name="islastAnd">
      <xsl:if test="position()=$lastAnd">True</xsl:if>
    </xsl:variable>
    <xsl:apply-templates mode="getVarequal" select="varequal">
      <xsl:with-param name="ident" select="$ident"/>
      <xsl:with-param name="case" select="'MEx'"/>
      <xsl:with-param name="lastAnd" select="$islastAnd"/>
    </xsl:apply-templates>
  </xsl:template> -->
  <!--****************************************************************************** -->
<!--   <xsl:template match="varequal" mode="getVarequal">
    <xsl:param name="ident"/>
    <xsl:param name="case"/>
    <xsl:param name="lastAnd"/>
    <xsl:if test="@respident=$ident">
      <xsl:if test="$case !='MEx'">
        <xsl:value-of select="."/>
        <xsl:variable name="isLastVarequal" select="count(following-sibling::*)"/>
        <xsl:if test="$isLastVarequal !=0">,</xsl:if>
      </xsl:if>
      <xsl:if test="$case ='MEx'">
        <xsl:variable name="isCorrect" select="count(following-sibling::or)"/>
        <xsl:if test="$isCorrect=0">
          <xsl:value-of select="."/>
          <xsl:if test="$lastAnd !='True'">,</xsl:if>
        </xsl:if>
      </xsl:if>
    </xsl:if>
  </xsl:template> -->
  <!--****************************************************************************** -->
  <xsl:template match="response_label" mode="source_label">
    <xsl:param name="target_x"/>
    <xsl:param name="position"/>
    <xsl:if test="contains(@match_group,$target_x)">
      <option>
        <xsl:attribute name="value">
          <xsl:value-of select="@ident"/>
        </xsl:attribute>
        <xsl:variable name="ident" select="@ident"/>
        <xsl:if test="/stxx/item/item_result/response/response_value[$position] = $ident">
          <xsl:attribute name="selected"/>
        </xsl:if>
        <xsl:number count="response_label" format="A" level="single"/>. &#160; <xsl:apply-templates select="material/mattext">
          <xsl:with-param name="showNo">No</xsl:with-param>
        </xsl:apply-templates>
      </option>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="response_label" mode="target_label">
    <xsl:if test="not(@match_group)">
      <tr>
        <td align="right"/>
        <td align="left">
          <xsl:attribute name="value">
            <xsl:value-of select="@ident"/>
          </xsl:attribute>
          <xsl:value-of select="material"/> &#160; <select>
            <option> [Select]</option>
            <xsl:variable name="target" select="@ident"/>
            <xsl:apply-templates mode="source_label" select="../response_label">
              <xsl:with-param name="target_x" select="$target"/>
            </xsl:apply-templates>
          </select>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="response_label" mode="showTarget">
    <tr>
      <td align="right"/>
      <td align="left">
        <xsl:number format="A" level="single" value="position()"/>. <xsl:apply-templates select="material/mattext">
          <xsl:with-param name="showNo">No</xsl:with-param>
        </xsl:apply-templates>
      </td>
    </tr>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="response_label" mode="select">
    <tr>
      <td align="right"/>
      <td align="left"> &#160; <select disabled="true">
          <option value="null">
            <xsl:number format="A" level="single" value="position()"/>
          </option>
        </select> &#160;&#160; <xsl:apply-templates mode="getSource" select="../../../../../resprocessing/respcondition">
          <xsl:with-param name="pos" select="position()"/>
        </xsl:apply-templates>
      </td>
    </tr>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="respcondition" mode="getSource">
    <xsl:param name="pos"/>
    <xsl:if test="$pos=position()">
      <xsl:apply-templates mode="getSource" select="../../presentation/flow/response_grp/render_choice/response_label">
        <xsl:with-param name="ident">
          <xsl:value-of select="conditionvar/varequal"/>
        </xsl:with-param>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="response_label" mode="getSource">
    <xsl:param name="ident"/>
    <xsl:variable name="thisID" select="@ident"/>
    <xsl:if test="$ident=$thisID">
      <xsl:apply-templates select="material/mattext">
        <xsl:with-param name="showNo">No</xsl:with-param>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="response_grp">
    <xsl:variable name="base" select="count(render_choice/response_label[@match_group])"/>
    <xsl:apply-templates mode="showTarget" select="render_choice/response_label[not(@match_group)]"/>
    <tr>
      <td align="right"/>
      <td align="left">
        <br/>
      </td>
    </tr>
    <xsl:apply-templates mode="select" select="render_choice/response_label[not(@match_group)]"/>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="respcondition">
    <xsl:param name="linkrefid"/>
    <xsl:param name="position"/>
    <xsl:if test="$linkrefid='true' and $position=position()">
        <xsl:apply-templates select="displayfeedback">
          <xsl:with-param name="linkrefid" select="'true'"/>
        </xsl:apply-templates>
    </xsl:if>
  </xsl:template>
  <!--****************************************************************************** -->
  <xsl:template match="displayfeedback">
    <xsl:param name="linkrefid"/>
    <xsl:variable name="val" select="@linkrefid"/>
    <xsl:if test="$linkrefid='true'">
        <xsl:if test="$val='Correct' or  $val='InCorrect'"  >
          <xsl:value-of select="@linkrefid"/>
        </xsl:if>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
