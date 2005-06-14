<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: itemAuthor.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--****************************************************************************** -->
  <xsl:import href="../../layout/header.xsl"/>
  <xsl:import href="itemAuthorResponseLabel.xsl"/>
  <xsl:import href="itemAuthorQtimetadata.xsl"/>
  <xsl:import href="itemQuestionEditor.xsl"/>
  <xsl:import href="itemGenericFeedback.xsl"/>
  <xsl:import href="itemSubmitButtons.xsl"/>
  <xsl:import href="lidRandomize.xsl"/>
  <xsl:import href="matchRandomize.xsl"/>
  <xsl:import href="matchItems.xsl"/>
  <xsl:import href="requireRationale.xsl"/>
  <xsl:import href="audioParameters.xsl"/>
  <xsl:import href="itemSurvey.xsl"/>
  <xsl:import href="chooseSection.xsl"/>
  <xsl:import href="choosePool.xsl"/>
  <xsl:import href="itemRecordingData.xsl"/>
  <xsl:import href="itemTypes.xsl"/>
  
  <!-- This template processes the root node ("/") -->
  <xsl:template match="/">
    <html>
      <head>
        <title> Item Display - Author mode</title>
        <script language="javascript"> var item =0; </script>
        <!-- BEGIN HTML AREA -->
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

function PopupWin(url)
{
   window.open(url,"ha_fullscreen","toolbar=no,location=no,directories=no,status=no,menubar=yes,"+"scrollbars=yes,resizable=yes,width=640,height=480");

}

function changeMultiQuestion(url)
{
  window.location = url;
}
</script>


          <script type="text/javascript"> 
               //HTMLArea.loadPlugin("TableOperations","<xsl:value-of select="$root"/>" );
                //HTMLArea.loadPlugin("SpellChecker", "<xsl:value-of select="$root"/>");
                var ta_editor =  [];
                var hidden = [];
                var textAreaNames = [];
                var runFocus=true;
                function startup()
                {  
                 <xsl:variable name="question" select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='qmd_itemtype']/following-sibling::fieldentry"/>
                 <xsl:if test="$question='Fill In the Blank'">
                    var questionXpath="stxx/form/itemActionForm/fibAnswer";
                 </xsl:if>
                 <xsl:if test="$question != 'Fill In the Blank'">
                     var questionXpath= "stxx/item/presentation/flow/material/mattext" ;
                 </xsl:if>

        	<xsl:variable name="targetpage" select="/stxx/form/itemActionForm/target"/>
          	<xsl:if test="$targetpage!='questionpool'">
                   loadEditor("<xsl:value-of select="$root"/>", 2,2,"three", questionXpath);
                 </xsl:if>
          	<xsl:if test="$targetpage ='questionpool'">
                   loadEditor("<xsl:value-of select="$root"/>", 2,1,"three", questionXpath);
                 </xsl:if>

                 };                 
                 <!-- HTMLAREA AUDIO INFO --> 
                 <xsl:call-template name="js_recording_variables"/>
           </script>
        <!-- END HTML AREA -->
      </head>
      <body onload="startup();">
        <!-- Determine where to save the item to -->
        <xsl:variable name="target" select="/stxx/form/itemActionForm/target"/>
        <xsl:variable name="question" select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='qmd_itemtype']/following-sibling::fieldentry"/>
        <xsl:choose>
          <!--TO DO put these item types in a common xsl and reuse variables -->
          <xsl:when test="$question='True False'">
            <xsl:call-template name="header">
              <xsl:with-param name="displayText" select="'True False'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="$question='Multiple Correct Answer'">
            <xsl:call-template name="header">
              <xsl:with-param name="displayText" select="'Multiple Correct Answer'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="$question='Fill In the Blank'">
            <xsl:call-template name="header">
              <xsl:with-param name="displayText" select="'Fill In the Blank'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="$question='Essay'">
            <xsl:call-template name="header">
              <xsl:with-param name="displayText" select="'Essay'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="$question='Matching'">
            <xsl:call-template name="header">
              <xsl:with-param name="displayText" select="'Matching'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="$question='File Upload'">
            <xsl:call-template name="header">
              <xsl:with-param name="displayText" select="'File Upload'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="$question='Audio Recording'">
            <xsl:call-template name="header">
              <xsl:with-param name="displayText" select="'Audio Recording'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="header">
              <xsl:with-param name="displayText" select="'Multiple Choice'"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>


        <xsl:variable name="formname">
          <xsl:if test="$target!='questionpool'">
            <xsl:value-of select="concat($base,'asi/author/item/editItemAction.do')"/>
          </xsl:if>
          <xsl:if test="$target ='questionpool'">
            <xsl:value-of select="concat($base,'asi/author/item/editItemAction.do?target=questionpool')"/>
          </xsl:if>
        </xsl:variable>


        <xsl:variable name="formname2">
          <xsl:if test="$target!='questionpool'">
            <xsl:value-of select="concat($base,'asi/author/item/itemAction.do')"/>
          </xsl:if>
          <xsl:if test="$target ='questionpool'">
            <xsl:value-of select="concat($base,'asi/author/item/itemAction.do?target=questionpool')"/>
          </xsl:if>
        </xsl:variable>

        <xsl:variable name="formname3" select="concat($base,'asi/author/assessment/assessmentAction.do')"/>
        <table align="center" border="0" width="95%">
          <!--removed width 750 -->
          <!--BEGIN not needed for question pool-->
          <xsl:if test="$target!='questionpool'"> 
            <form action="{$formname3}" method="post" name="author">
              <xsl:variable name="assessID" select="/stxx/form/itemActionForm/assessmentID"/>
              <input name="assessmentID" type="hidden" value="{$assessID}"/>
              <tr>
                <td>
                  <table border="0" width="100%">
                    <tr>
                      <td align="left" class="Title"> Modify Question - <xsl:value-of select="stxx/form/itemActionForm/assessTitle"/>
                      </td>
                      <td align="right">
                        <!--   <input name="action" type="submit" value="Test Questions"/> -->
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </form>
            <tr>
              <td>
                <br/>
              </td>
            </tr>
      </xsl:if> 
          <!--END not needed for question pool-->
          <form action="{$formname2}" method="post" name="itemAction">
            <xsl:variable name="questionType">
              <xsl:value-of select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='qmd_itemtype']/following-sibling::fieldentry"/>
            </xsl:variable>
            <xsl:variable name="ident" select="stxx/item/@ident"/>
            <input name="ItemIdent" type="hidden" value="{$ident}"/>
            <xsl:variable name="items" select="/stxx/form/itemActionForm/itemNo"/>
            <xsl:variable name="assessTitle" select="/stxx/form/itemActionForm/assessTitle"/>
            <xsl:variable name="secident" select="/stxx/form/itemActionForm/sectionIdent"/>
            <xsl:variable name="insertPosition" select="/stxx/form/itemActionForm/insertPosition"/>
            <xsl:variable name="assessID" select="/stxx/form/itemActionForm/assessmentID"/>
            <input name="itemNo" type="hidden" value="{$items}"/>
            <input name="assessTitle" type="hidden" value="{$assessTitle}"/>
            <input name="itemType" type="hidden" value="{$question}"/>
            <input name="SectionIdent" type="hidden" value="{$secident}"/>
            <input name="insertPosition" type="hidden" value="{$insertPosition}"/>
            <input name="assessmentID" type="hidden" value="{$assessID}"/>
            <!--BEGIN not needed for question pool-->
            <xsl:if test="$target!='questionpool'">
              <xsl:if test="$ident=''">
                <tr>
                  <td align="center" colspan="2">
                    <table align="center" border="0" width="80%">
                      <tr>
                        <td align="right" class="form_label" width="35%"> Change Question Type : &#160;</td>
                        <td align="left">
                          <xsl:call-template name="itemTypesAuto"/> &#160;&#160; <!--<input type="submit" value="Change"/>-->
                        </td>
                      </tr>
                      <tr>
                        <td>
                          <br/>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </xsl:if>
            </xsl:if>
            <!--END not needed for question pool-->
            <tr>
              <td>
<!--                <table class="navigo_border" width="100%">-->
                <table class="navigo_border" width="100%">
                  <tr>
                    <td align="left" class="navigo_border_text_font"> Question <xsl:value-of select="/stxx/form/itemActionForm/itemNo"/> - <xsl:call-template name="resolveItemTypes">
                        <xsl:with-param name="itemType">
                          <xsl:value-of select="$questionType"/>
                        </xsl:with-param>
                      </xsl:call-template>
                    </td>
                    <td align="right">
               <xsl:if test="$target='questionpool'">
<xsl:variable name="questionOrder" select="/stxx/form/itemActionForm/itemNo"/>
<xsl:variable name="itemId" select="/stxx/item/@ident"/>
<xsl:variable name="link0" select="concat('../item/itemAction.do?target=questionpool&amp;action=Preview&amp;questionOrder=',$questionOrder)"/>
<xsl:variable name="link1" select="concat($link0,'&amp;ItemIdent=')"/>
<xsl:variable name="link" select="concat($link1,$itemId)"/>
           <xsl:if test="$itemId!=''">
    <input name="action" type="button" onClick="javascript:PopupWin('{$link}')" value="Preview"/> &#160;&#160; 
</xsl:if>
              </xsl:if>
          	<xsl:if test="$target!='questionpool'">
<xsl:variable name="questionOrder" select="/stxx/form/itemActionForm/itemNo"/>
<xsl:variable name="itemId" select="/stxx/item/@ident"/>
<xsl:variable name="link0" select="concat('../item/itemAction.do?action=Preview&amp;questionOrder=',$questionOrder)"/>
<xsl:variable name="link1" select="concat($link0,'&amp;ItemIdent=')"/>
<xsl:variable name="link" select="concat($link1,$itemId)"/>
           <xsl:if test="$itemId!=''">
    <input name="action" type="button" onClick="javascript:PopupWin('{$link}')" value="Preview"/> &#160;&#160; 
</xsl:if>
              </xsl:if>

<input name="removeConfirm" type="submit" value="Remove"/>&#160;&#160;
<input name="myQuestions" type="submit" value="My Questions"/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </form>
        </table>
        <form action="{$formname}" method="post" name="item">
          <xsl:variable name="ident" select="/stxx/item/@ident"/>
          <xsl:variable name="items" select="/stxx/form/itemActionForm/itemNo"/>
          <xsl:variable name="assessTitle" select="/stxx/form/itemActionForm/assessTitle"/>
          <xsl:variable name="secident" select="/stxx/form/itemActionForm/sectionIdent"/>
          <xsl:variable name="currentSection" select="/stxx/form/itemActionForm/currentSection"/>
          <xsl:variable name="insertPosition" select="/stxx/form/itemActionForm/insertPosition"/>
          <xsl:variable name="assessID" select="/stxx/form/itemActionForm/assessmentID"/>

          <xsl:variable name="var1" select="concat($formname2, '?ItemIdent=')"/>
          <xsl:variable name="var2" select="concat($var1, $ident)"/>
          <xsl:variable name="var3" select="concat($var2, '&amp;assessTitle=')"/>
          <xsl:variable name="var4" select="concat($var3, $assessTitle)"/>
          <xsl:variable name="var5" select="concat($var4, '&amp;SectionIdent=')"/>
          <xsl:variable name="var6" select="concat($var5, $secident)"/>
          <xsl:variable name="var7" select="concat($var6, '&amp;insertPosition=')"/>
          <xsl:variable name="var8" select="concat($var7, $insertPosition)"/>
          <xsl:variable name="var9" select="concat($var8, '&amp;assessmentID=')"/>
          <xsl:variable name="var10" select="concat($var9, $assessID)"/>          
          <xsl:variable name="var11" select="concat($var10, '&amp;itemNo=')"/>
          <xsl:variable name="var12" select="concat($var11, $items)"/>
          <xsl:variable name="changeToSingle" select="concat($var12, '&amp;action=Multiple Choice')"/>
          <xsl:variable name="changeToMultiple" select="concat($var12, '&amp;action=Multiple Correct Answer')"/>

          <input name="ItemIdent" type="hidden" value="{$ident}"/>
          <input name="itemNo" type="hidden" value="{$items}"/>
          <input name="assessTitle" type="hidden" value="{$assessTitle}"/>
          <input name="itemType" type="hidden" value="{$question}"/>
          <input name="currentSection" type="hidden" value="{$currentSection}"/>
          <input name="insertPosition" type="hidden" value="{$insertPosition}"/>
          <input name="assessmentID" type="hidden" value="{$assessID}"/>
          <table align="center" border="0" width="700">
            <xsl:call-template name="questionEditor"/>
            <xsl:variable name="questionType" select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='qmd_itemtype']/following-sibling::fieldentry"/>
            <xsl:if test="$questionType='True False'">
              <tr>
                <td>
                  <table border="0" width="100%">
                    <tr>
                      <td width="23">
                        <xsl:call-template name="questionCircle"/>
                      </td>
                      <td class="bold"> Correct Answer: </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr>
                <td>
                  <table align="right" border="0" width="95%">
                    <xsl:apply-templates select="stxx/item/presentation/flow/response_lid/render_choice/response_label">
                      <xsl:with-param name="path">stxx/item/presentation/flow/response_lid/render_choice[1]</xsl:with-param>
                    </xsl:apply-templates>
                  </table>
                </td>
              </tr>
            </xsl:if>
            <xsl:if test="$questionType='Multiple Choice'">
              <tr>
                <td>
                  <table border="0" width="100%">
                    <tr>
                      <td width="23">
                        <xsl:call-template name="questionCircle"/>
                      </td>
                      <td class="bold"> Answers:
                        <xsl:if test="$ident=''">
                          &#160;&#160; Single correct answer
                          <input type="radio" value="Multiple Choice" onclick="javascript:changeMultiQuestion('{$changeToSingle}')">
                            <xsl:if test="$questionType='Multiple Choice'">
                              <xsl:attribute name="checked">True</xsl:attribute>
                            </xsl:if>
                          </input>
                          &#160;&#160; Multiple correct answers
                          <input type="radio" value="Multiple Correct Answer" onclick="javascript:changeMultiQuestion('{$changeToMultiple}')">
                            <xsl:if test="$questionType='Multiple Correct Answer'">
                              <xsl:attribute name="checked">True</xsl:attribute>
                            </xsl:if>
                          </input>
                        </xsl:if>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr>
                <td>
                  <table align="right" border="0" width="95%">
                    <xsl:apply-templates select="stxx/item/presentation/flow/response_lid/render_choice/response_label">
                      <xsl:with-param name="path">stxx/item/presentation/flow/response_lid/render_choice[1]</xsl:with-param>
                    </xsl:apply-templates>
                  </table>
                </td>
              </tr>
              <!--*********************************************-->
              <xsl:call-template name="randomizeAnswers"/>
              <xsl:call-template name="requireRationale"/>
              <!--*********************************************-->
            </xsl:if>
            <xsl:if test="$questionType='Multiple Correct Answer'">
              <tr>
                <td>
                  <table border="0" width="100%">
                    <tr>
                      <td valign="top" width="23">
                        <xsl:call-template name="questionCircle"/>
                      </td>
                      <td class="bold"> Answers:
                        <xsl:if test="$ident=''">
                          &#160;&#160; Single correct answer
                          <input type="radio" value="Multiple Choice" onclick="javascript:changeMultiQuestion('{$changeToSingle}')">
                            <xsl:if test="$questionType='Multiple Choice'">
                              <xsl:attribute name="checked">True</xsl:attribute>
                            </xsl:if>
                          </input>
                          &#160;&#160; Multiple correct answers
                          <input type="radio" value="Multiple Correct Answer" onclick="javascript:changeMultiQuestion('{$changeToMultiple}')">
                            <xsl:if test="$questionType='Multiple Correct Answer'">
                              <xsl:attribute name="checked">True</xsl:attribute>
                            </xsl:if>
                          </input>
                        </xsl:if>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <!--**********************************************-->
              <tr>
                <td>
                  <table align="right" border="0" width="95%">
                    <xsl:apply-templates select="stxx/item/presentation/flow/response_lid/render_choice/response_label">
                      <xsl:with-param name="path">stxx/item/presentation/flow/response_lid/render_choice[1]</xsl:with-param>
                    </xsl:apply-templates>
                  </table>
                </td>
              </tr>
              <!--***********************************************-->
              <xsl:call-template name="randomizeAnswers"/>
              <!--***********************************************-->
            </xsl:if>
            <xsl:if test="$questionType='Multiple Choice Survey'">
              <tr>
                <td>
                  <table border="0" width="100%">
                    <tr>
                      <td width="23">
                        <xsl:call-template name="questionCircle"/>
                      </td>
<!--                      <td> Answer Text: Predefined set of scales for survey reporting. </td>-->
                      <td class="bold"> Answer: </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr>
                <td>
                  <xsl:call-template name="itemSurvey"/>
                </td>
              </tr>
            </xsl:if>
            <!--***********************************************-->
            <xsl:if test="$questionType='Audio Recording'">
              <!--***********************************************-->
              <xsl:call-template name="timeallowed"/>
              <xsl:call-template name="num_of_attempts"/>
              <!--***********************************************-->
            </xsl:if>
            <!--***********************************************-->
            <xsl:if test="$questionType='Matching'">
              <xsl:variable name="secident" select="/stxx/form/itemActionForm/sectionIdent"/>
              <input name="SectionIdent" type="hidden" value="{$secident}"/><!-- Need to be removed ...was for former UI (back to items) -->
              <tr>
                <td>
                  <xsl:apply-templates select="stxx/item/presentation/flow/response_grp"/>
                </td>
              </tr>
              <!--***********************************************-->
              <xsl:call-template name="matchRandomize"/>
              <!--***********************************************-->
            </xsl:if>
          <!--    <xsl:if test="$questionType!='Matching'">  -->
              <!--***********************************************-->
              <!--BEGIN not needed for question pool-->
               <xsl:if test="$target!='questionpool'">
                <tr>
                  <td>
                    <xsl:call-template name="showSections"/>
                  </td>
                </tr>
                <!--***********************************************-->
                <tr>
                  <td>
                    <xsl:call-template name="showPools"/>
                  </td>
                </tr>
               </xsl:if>  
              <!--END not needed for question pool-->
              <!--***********************************************-->
              <xsl:variable name="showOverallFeedbk">
                <xsl:value-of select="stxx/form/itemActionForm/showOverallFeedback"/>
              </xsl:variable>
              <xsl:variable name="showItemFeedbk">
                <xsl:value-of select="stxx/form/itemActionForm/showQuestionLevelFeedback"/>
              </xsl:variable>
              
              <xsl:if test="$showOverallFeedbk!='NONE'">
                <xsl:if test="$showItemFeedbk='True'">
                  <tr>
                    <td>
                      <xsl:call-template name="genericFeedback"/>
                    </td>
                  </tr>
                </xsl:if>
              </xsl:if>
              
              <!--**********************Metadata and Submit buttons*************************-->
              <tr>
                <td>
                  <xsl:call-template name="submitButtons"/>
                </td>
              </tr>
              <!--***********************************************-->
<!--            </xsl:if>   -->
          </table>
        </form>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="render_choice">
    <xsl:value-of select="count(child::response_label)+1"/>
  </xsl:template>
</xsl:stylesheet>
