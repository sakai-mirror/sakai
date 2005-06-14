<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: matchContinued.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!--****************************************************************************** -->
    <xsl:import href="../../layout/header.xsl"/>
    <xsl:import href="itemGenericFeedback.xsl"/>
    <xsl:import href="itemSubmitButtons.xsl"/>
   <xsl:import href="chooseSection.xsl"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>Item Display - Author mode</title>
                <script language="javascript"> var item =6; </script>
                <xsl:variable name="base"> <xsl:call-template name="baseHREF"/> </xsl:variable>
                <link href="{$base}htmlarea/htmlarea.css" rel="STYLESHEET" type="TEXT/CSS" />
		         <!-- load the main HTMLArea files  -->
                <script src="{$base}htmlarea/htmlarea.js" type="text/javascript"/>
                <script src="{$base}htmlarea/lang/en.js" type="text/javascript"/>
                <script src="{$base}htmlarea/dialog.js" type="text/javascript"/>
                <!-- <script type="text/javascript" src="popupdiv.js"></script> -->
                <script src="{$base}htmlarea/popupwin.js" type="text/javascript"/>
                <script src="{$base}js/wysiwygEditor.js" type="text/javascript"/>
                <!-- load the plugins -->
                <xsl:variable name="root" select="concat($base,'htmlarea/')"/>
                <script type="text/javascript">
                    //HTMLArea.loadPlugin("TableOperations","<xsl:value-of select="$root"/>" );
                    HTMLArea.loadPlugin("SpellChecker", "<xsl:value-of select="$root"/>");
                var ta_editor =  [];
                var hidden = [];
                var textAreaNames = [];

               function startup()
                {
                   loadEditor("<xsl:value-of select="$root"/>", 1,2);
                }
                 </script>
            </head>
            <body onload="javascript:startup();">
                <xsl:variable name="question" select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='qmd_itemtype']/following-sibling::fieldentry"/>
                <xsl:if test="$question='Matching'">
                    <xsl:call-template name="header">
                        <xsl:with-param name="displayText" select="'Matching Continued...'"/>
                    </xsl:call-template>
                </xsl:if>
                <xsl:variable name="base">
                    <xsl:call-template name="baseHREF"/>
                </xsl:variable>
                <xsl:variable name="formname" select="concat($base,'asi/author/item/editItemAction.do')"/>
                <xsl:variable name="formname2" select="concat($base,'asi/author/item/itemAction.do')"/>
                <xsl:variable name="formname3" select="concat($base,'asi/author/assessment/assessmentAction.do')"/>
                <table align="center" border="0" width="750">
                    <form action="{$formname3}" method="post" name="author">
                       <xsl:variable name="secident" select="/stxx/form/itemActionForm/sectionIdent"/>
                       <input name="SectionIdent" type="hidden" value="{$secident}"/>
                       <xsl:variable name="assessID" select="/stxx/form/itemActionForm/assessmentID"/>
                       <input name="assessmentID" type="hidden" value="{$assessID}"/>
                        <tr>
                            <td>
                                <table border="0" width="100%">
                                    <tr>
                                        <td align="left" class="Title"/>
                                        <td align="right">
                                        <input name="action" type="submit" value="Test Questions"/>
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
                    <form action="{$formname2}" method="post" name="authorItem">
                        <tr>
                            <xsl:variable name="questionType">
                                <xsl:value-of select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='qmd_itemtype']/following-sibling::fieldentry"/>
                            </xsl:variable>
                            <xsl:variable name="ident" select="stxx/item/@ident"/>
                            <input name="ItemIdent" type="hidden" value="{$ident}"/>
                                                   <xsl:variable name="assessID" select="/stxx/form/itemActionForm/assessmentID"/>
                       <input name="assessmentID" type="hidden" value="{$assessID}"/>
                            <td class="border">
                                <table width="100%">
                                    <tr>
                                        <td align="left">
                                        <b> Matching Question - Continued</b>
                                        </td>
                                        <td align="right">
                                        <input name="action" type="submit" value="Remove"/>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </form>
                </table>
                <form action="{$formname}" method="post" name="item">
                    <xsl:variable name="ident" select="stxx/item/@ident"/>
                    <xsl:variable name="items" select="/stxx/form/itemActionForm/itemNo"/>
                    <xsl:variable name="assessTitle" select="/stxx/form/itemActionForm/assessTitle"/>
                    <xsl:variable name="currentSection" select="/stxx/form/itemActionForm/currentSection"/>
                    <xsl:variable name="questionType" select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield/fieldlabel[text()='qmd_itemtype']/following-sibling::fieldentry"/>
                    <xsl:variable name="insertPosition" select="/stxx/form/itemActionForm/insertPosition"/>
                     <xsl:variable name="assessID" select="/stxx/form/itemActionForm/assessmentID"/>
                    <input name="ItemIdent" type="hidden" value="{$ident}"/>
                    <input name="itemNo" type="hidden" value="{$items}"/>
                    <input name="assessTitle" type="hidden" value="{$assessTitle}"/>
                    <input name="itemType" type="hidden" value="{$question}"/>
                    <input name="currentSection" type="hidden" value="{$currentSection}"/>
                    <input name="insertPosition" type="hidden" value="{$insertPosition}"/>
                    <input name="assessmentID" type="hidden" value="{$assessID}"/>

                    <table align="center" border="0" width="700">
                         <tr>
                            <td>
                                <table border="0" width="100%">
                                    <tr>
                                        <td width="23">
                                        <xsl:call-template name="questionCircle"/>
                                        </td>
                                        <td> Answers: Select answer below. </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td class="form_label">
                                <table align="right" border="0" width="95%">
                                    <tr>
                                        <td width="2%">&#160;</td>
                                        <td align="left" class="form_label"> Items:</td>
                                        <xsl:for-each select="/stxx/item/presentation/flow/response_grp/render_choice/response_label">
                                        <xsl:if test="(@match_group)">
                                        <tr>
                                        <td width="2%">&#160;</td>
                                        <td>&#160; <xsl:number
                                        count="response_label"
                                        format="A"
                                        level="single"/>.
                                        &#160; <xsl:value-of select="material/mattext"/>
                                        </td>
                                        </tr>
                                        </xsl:if>
                                        </xsl:for-each>
                                        <tr>
                                        <td>
                                        <br/>
                                        </td>
                                        <td/>
                                        </tr>
                                        <xsl:apply-templates mode="notMatch" select="/stxx/item/presentation/flow/response_grp/render_choice/response_label[not(@match_group)]"/>

                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <xsl:call-template name="genericFeedback"/>
                            </td>
                        </tr>
                         <tr><td><xsl:call-template name="showSections"/></td></tr>
                        <tr>
                            <td>
                                <xsl:call-template name="submitButtons"/>
                            </td>
                        </tr>
                    </table>
                </form>
            </body>
        </html>
    </xsl:template>
     <xsl:template match="response_label" mode="notMatch">
        <tr>
            <td width="2%">
                <xsl:number format="1" level="single" value="position()"/>.</td>
            <td>
                <span class="form_label">Match</span> &#160;&#160;&#160;&#160;&#160;  <xsl:value-of select="material/mattext"/>
            </td>
        </tr>
         <tr> <td>  <br/> </td>  <td/> </tr>
        <tr>
            <td/>
            <td>
                <span class="form_label">Item </span>&#160; &#160;&#160;&#160; &#160;
                <xsl:variable name="pos"> <xsl:number format="1" level="single" value="position()"/></xsl:variable>
                <xsl:variable
                    name="resp" select="concat('stxx/item/resprocessing/respcondition[',$pos,']/conditionvar/varequal')"/>
                <xsl:variable name="selectedChoice">
                <xsl:apply-templates select="/stxx/item/resprocessing/respcondition" >
                    <xsl:with-param name="pos"   select="$pos"></xsl:with-param>
                    </xsl:apply-templates>
                </xsl:variable>

                <select name="{$resp}">
                    <xsl:for-each select="/stxx/item/presentation/flow/response_grp/render_choice/response_label">
                        <xsl:if test="(@match_group)">
                            <xsl:variable name="respIdent" select="@ident"/>
                            <option value="{$respIdent}">
                                <xsl:if test="$respIdent=$selectedChoice">
                                <xsl:attribute name="selected">true</xsl:attribute>
                                </xsl:if>
                                <xsl:number format="A" level="single" value="position()"/>
                            </option>
                        </xsl:if>
                    </xsl:for-each>
                </select>
            </td>
        </tr>

      <xsl:apply-templates select="/stxx/item/itemfeedback">
            <xsl:with-param name="position" select="position()"/>
        </xsl:apply-templates>
        <tr> <td>  <br/> </td>  <td/> </tr>
    </xsl:template>
    <!--******************************************************************************** -->
    <xsl:template match="respcondition">
      <xsl:param name="pos"/>
      <xsl:if test="$pos=position()">
      <xsl:value-of select="conditionvar/varequal"/>
      </xsl:if>
    </xsl:template>
   <!--******************************************************************************** -->
   <xsl:template match="itemfeedback">
        <xsl:param name="position"/>
        <xsl:if test="$position = position()">
            <xsl:variable name="name" select="concat('stxx/item/itemfeedback[',position(),']/flow_mat[1]/material[1]/mattext[1]')"/>
            <xsl:variable name="uri" select="concat('stxx/item/itemfeedback[',position(),']/flow_mat[1]/material[2]/matimage[1]/@uri')"/>
            <xsl:variable name="mattext" select="flow_mat[1]/material[1]/mattext[1]"/>
            <xsl:variable name="matimage" select="flow_mat[1]/material[2]/matimage[1]/@uri"/>
            <tr> <td></td><td>
            <table border="0" width="100%">
           <tr><td width= "6%"><span class="form_label"> Feedback </span> (optional)</td><td width ="45">
            <textarea cols="45" name="{$name}" id="{$name}" rows="10">
                <xsl:value-of select="$mattext"/>
            </textarea>
             </td>
               <td valign="top"><xsl:variable name="base">
                    <xsl:call-template name="baseHREF"/>
                </xsl:variable>
            <!--  <img alt="Toggle Toolbar" src="{$base}htmlarea/images/htmlarea_editor.gif">
                    <xsl:attribute
                        name="onClick">javascript:hideUnhide("<xsl:value-of select="$name"/>");</xsl:attribute>
                </img>-->
                <a>
                  <xsl:attribute
                    name="href">javascript:hideUnhide("<xsl:value-of
                    select="$name"/>");</xsl:attribute>
                  Show/Hide <br />Editor
                </a>

             </td>

             </tr>
            <!-- <tr><td width= "6%"> Image: </td><td>  <input name="{$uri}" size = "38"  type="text" value="{$matimage}"/>
            </td></tr>--></table>
         </td></tr>
               </xsl:if>
    </xsl:template>
</xsl:stylesheet>
