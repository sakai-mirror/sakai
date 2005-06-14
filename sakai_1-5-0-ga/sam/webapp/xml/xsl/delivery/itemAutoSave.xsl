<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: itemAutoSave.xsl,v 1.1 2004/08/03 18:24:37 lydial.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../layout/header.xsl" />
    <xsl:import href="../commonTemplates/itemRecordingData.xsl"/>
    <xsl:preserve-space elements="flow"/>

    <xsl:output method="html"/>

    <!-- this xslt has been shared by preview and delivery, I am using this variable to
      check which one I am in. If questionOrder > 0, I am in "preview". - daisyf -->
    <xsl:variable name="questionOrder">
      <xsl:value-of select="/stxx/request/param[@name='questionOrder']/value"/>
    </xsl:variable>
    <xsl:variable name="displayOrder">
      <xsl:value-of select="/stxx/item/@displayIndex"/>
    </xsl:variable>
    <xsl:variable name="totalItem">
      <xsl:value-of select="/stxx/request/attribute[@name='totalItem']/totalItem"/>
    </xsl:variable>

    <!-- This template processes the root node ("/") -->
    <xsl:template match="/">
        <html>
            <head>
                <title>Navigo Assessment Item Display</title>
            </head>
            <body>
                <xsl:apply-templates select="stxx"/>
                <div align="center">
                  <input type="button" value="  OK  " onClick="javascript:self.close();" />
                </div>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="stxx">
        <xsl:apply-templates select="item"/>
    </xsl:template>

  <!-- ============================= -->
  <!-- child element: item     -->
  <!-- ============================= -->
    <xsl:template match="item">
        <xsl:variable name="itemIdentRef" select="@ident"/>
        <table align="center" width="90%" height="30" class="border" border="0" bgcolor="#666699"> 
             
            <tr>
                <td align="left" class="navigo_border_text_font">
                    <b> 
                        <!-- For Preview @continueNumber is missing in the xml doc, so I am parsing it
			from the request -->
                        <!-- For Review -->
                        <xsl:if test="$questionOrder">
                          <xsl:if test="number($questionOrder) &lt; 0">
                            <xsl:value-of
                              select="/stxx/applicationResources/key[@name='delivery.question']"/>:<xsl:value-of
                              select="@continueNumber"/>/<xsl:value-of select="/stxx/form/xmlDeliveryActionForm/size"/>
                          </xsl:if>
                          <!-- For Preview -->
                          <xsl:if test="not(number($questionOrder) &lt; 0)">
                            Question <xsl:value-of select="$questionOrder"/>
                          </xsl:if>
                        </xsl:if>
                        <xsl:if test="not($questionOrder)">
                          <xsl:if test="number($displayOrder) &lt; 0">
                            <xsl:value-of
                              select="/stxx/applicationResources/key[@name='delivery.question']"/>:<xsl:value-of
                              select="@continueNumber"/>/<xsl:value-of select="/stxx/form/xmlDeliveryActionForm/size"/>
                          </xsl:if>
                          <xsl:if test="not(number($displayOrder) &lt; 0)">
                            Question &#160;&#160;<xsl:value-of select="$displayOrder"/>/<xsl:value-of select="$totalItem"/>
                            <input name="totalItem" type="hidden" value="{$totalItem}"/>
                          </xsl:if>
                        </xsl:if>
                    </b>
                </td>
                <td align="center">
                    <b/>
                </td>
                <td align="right" class="navigo_border_text_font">
                    <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showStudentScore = 'True'">
                        <b>
                            <xsl:value-of
                            select="//item_result[@ident_ref=$itemIdentRef]/outcomes/score/score_value"/>
                            / </b>
                    </xsl:if>
                    <xsl:if test="resprocessing/outcomes/decvar/@maxvalue != ''">
                        <b>
                            <xsl:value-of
                            select="format-number(resprocessing/outcomes/decvar/@maxvalue, '0.00')"/>&#160;
                                <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.points']"/>
                        </b>
                    </xsl:if>
                </td>
            </tr>
        </table>
        <br/>
        <table width="80%" align="center">
            <tr>
                <td valign="top" width="10" rowspan="2">
                    <b>
                        <xsl:variable name="layout" select="/stxx/questestinterop/asessment/qtimetadata/qtimetadatafield[fieldlabel='QUESTION_LAYOUT']/fieldentry"/>
                        <xsl:variable name="numbering" select="/stxx/questestinterop/assessment/qtimetadata/qtimetadatafield[fieldlabel='QUESTION_NUMBERING']/fieldentry"/>
                        <xsl:choose>
                        <xsl:when test="$layout='S' and $numbering='RESTART'">
                            <xsl:value-of select="position()"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="@displayIndex"/>.
                        </xsl:otherwise>
                        </xsl:choose>
                    </b>
                </td>
                <td valign="top" align="left">
                    <xsl:apply-templates select="rubric"/>
                    <xsl:variable name="mctype">
                        <xsl:apply-templates select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield">
                            <xsl:with-param name="keyName" select="'qmd_itemtype'"/>
                        </xsl:apply-templates>
                    </xsl:variable>
                    <xsl:if test="$mctype!='Audio Recording' and $mctype!='File Upload'">
                        <xsl:apply-templates select="presentation/flow | presentation"/>
                    </xsl:if>
                    <xsl:if test="$mctype='Audio Recording'">
                        <script>
                            <xsl:call-template name="js_recording_variables"/>
                        </script>
                        <xsl:apply-templates select="presentation/flow/material" mode="audio"/>
                    </xsl:if>
                    <xsl:if test="$mctype='File Upload'">
                        <script>
                            <xsl:call-template name="js_recording_variables"/>
                        </script>
                        <xsl:apply-templates select="presentation/flow/material" mode="upload"/>
                    </xsl:if>
                    <p/>
                    <xsl:if test="/stxx/form/xmlDeliveryActionForm/navigation != 'LINEAR'" >
                    <input>
                        <xsl:attribute name="type">checkbox</xsl:attribute>
                        <xsl:attribute name="name">item_ident_ref=<xsl:value-of select="@ident"/>&#38;markForReview=1</xsl:attribute>
                        <xsl:attribute name="value">1</xsl:attribute>
			<xsl:attribute name="onBlur">
                       		autosave()
                     	</xsl:attribute>
                    </input>
                    <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.markForReview']"/>                
                    </xsl:if>
                    <p/>
                    <xsl:variable name="mctype2">
                        <xsl:apply-templates select="itemmetadata/qtimetadata/qtimetadatafield">
                            <xsl:with-param name="keyName" select="'qmd_itemtype'"/>
                        </xsl:apply-templates>
                    </xsl:variable>
                    <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showCorrectResponse = 'True'">
                        <!-- Put Answer key here -->
                        <xsl:if test="$mctype2!='Essay'"> Answer Key:
                                <xsl:apply-templates select="//item_result[@ident_ref=$itemIdentRef]/response/response_form/correct_response"/>
                        </xsl:if>
                        <xsl:if test="$mctype2 ='Essay'"> Model Short Answer:
                                <xsl:apply-templates select="itemfeedback[@ident='Correct']/flow_mat/material/mattext "/>
                        </xsl:if>
                    </xsl:if>
                    <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showItemLevel = 'True'">
                        <!--  Put Question level feedback here -->
                        <p/>
                        <xsl:if test="$mctype2!='Essay'">
                            <xsl:variable name="feedbackIdentRef" select="//item_result [@ident_ref=$itemIdentRef]/feedback_displayed/@ident_ref"/>
                            <xsl:apply-templates select="itemfeedback[@ident=$feedbackIdentRef]/flow_mat/material"/>
                        </xsl:if>
                        <xsl:if test="$mctype2='Essay'">
                            <xsl:apply-templates select="itemfeedback[@ident='InCorrect']/flow_mat/material/mattext "/>
                        </xsl:if>
                    </xsl:if>
                    <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showGraderComment = 'True'">
                        <!-- Put Grader's comment here -->
                        <p/>
                        <xsl:value-of select="//item_result [@ident_ref=$itemIdentRef]/extenstion_item_result"/>
                    </xsl:if>
                </td>
            </tr>
        </table>
    </xsl:template>

  <!-- ============================= -->
  <!-- child element: presentation   -->
  <!-- ============================= -->
    <xsl:template match="presentation">
        <xsl:apply-templates select="response_str |response_lid | response_grp | material"/>
    </xsl:template>

  <!-- ============================= -->
  <!-- child element: rubric     -->
  <!-- ============================= -->
    <xsl:template match="rubric">
        <b>
            <xsl:value-of select="flow_mat/material"/>
        </b>
    </xsl:template>

  <!-- ============================= -->
  <!-- child element: flow_mat     -->
  <!-- ============================= -->
    <xsl:template match="flow_mat">
        <xsl:apply-templates select="flow_mat | material"/>
    </xsl:template>
  <!-- ============================= -->
  <!-- child element: flow     -->
  <!-- ============================= -->
    <xsl:template match="flow">
        <xsl:apply-templates select="response_str |response_lid | response_grp | material | flow"/>
    </xsl:template>
    <!--***** File Upload - daisyf *********************************************************** -->
    <xsl:template match="material" mode="upload">
        <xsl:apply-templates select="mattext"/>
        <xsl:apply-templates select="matimage"/>
        <xsl:apply-templates select="mat_extension" mode="upload"/>
    </xsl:template>
    <xsl:template match="mat_extension" mode="upload">
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>

        <xsl:variable name="itemIdentRef" select="/stxx/item/@ident"/>
        <xsl:variable name="responseIdentRef" select="/stxx/item/presentation/flow/response_str/@ident"/>
        <p/>
        <input>
            <xsl:attribute name="type">hidden</xsl:attribute>
            <xsl:attribute name="name">item_ident_ref</xsl:attribute>
            <xsl:attribute name="value">
                <xsl:value-of select="$itemIdentRef"/>
            </xsl:attribute>
        </input>
        <input>
            <xsl:attribute name="type">hidden</xsl:attribute>
            <xsl:attribute name="id">
                <xsl:value-of select="$itemIdentRef"/>
            </xsl:attribute>
            <xsl:attribute name="name">item_result_ident_ref=<xsl:value-of
                    select="$itemIdentRef"/>&#38;response_ident_ref=<xsl:value-of select="$responseIdentRef"/>
            </xsl:attribute>
            <xsl:attribute name="value">
                <xsl:value-of select="//item_result[@ident_ref=$itemIdentRef]/response[@ident_ref=$responseIdentRef]/response_value"/>
            </xsl:attribute>
        </input>
        <!-- on clicking this link, the fileupload.jsp page -->
        <a href="#"
            onClick="javascript: window.open('{$base}/{.}'          +'?filename='+audioFileName+'_{$itemIdentRef}'          +'&amp;dir='+audioDir          +'&amp;item_ident_ref='+'{$itemIdentRef}',          '__ha_dialog',          'toolbar=no,menubar=no,personalbar=no,width=430,height=330,scrollbars=no,resizable=no')">Upload
            a new file</a>
        <p/>
        <xsl:if test="//item_result[@ident_ref=$itemIdentRef]/response[@ident_ref=$responseIdentRef]/response_value!=''">
            <p/>
            <a
                href="{//item_result[@ident_ref=$itemIdentRef]/response[@ident_ref=$responseIdentRef]/response_value}">Your
                Uploaded File</a>
        </xsl:if>
    </xsl:template>
    <!--****************************************************************************************** -->
    <!--***** Audio Recording - daisyf *********************************************************** -->
    <xsl:template match="material" mode="audio">
        <xsl:apply-templates select="mattext"/>
        <xsl:apply-templates select="matimage"/>
        <xsl:apply-templates select="mat_extension" mode="audio"/>
    </xsl:template>
    <xsl:template match="mat_extension" mode="audio">
        <xsl:variable name="timeallowed">
            <xsl:apply-templates select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield">
                <xsl:with-param name="keyName" select="'TIMEALLOWED'"/>
            </xsl:apply-templates>
        </xsl:variable>
        <xsl:variable name="num_of_attempts">
            <xsl:apply-templates select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield">
                <xsl:with-param name="keyName" select="'NUM_OF_ATTEMPTS'"/>
            </xsl:apply-templates>
        </xsl:variable>
        <xsl:variable name="itemIdentRef" select="/stxx/item/@ident"/>
        <xsl:variable name="responseIdentRef" select="/stxx/item/presentation/flow/response_str/@ident"/>
        <xsl:variable name="attempts_taken" select="//item_result[@ident_ref=$itemIdentRef]/response[@ident_ref=$responseIdentRef]/num_attempts"/>
        <script> audioSeconds=<xsl:value-of select="$timeallowed"/>;
                audioLimit=<xsl:value-of select="$num_of_attempts"/>; </script>
        <p/>
        <!-- need the follwoing two fields to decide if num_attempt should be incremented. see ItemResultWriter.updateItemResult() -->
        <input type="hidden" name="questiontype" value="audio"/>
        <input type="hidden" name="isnew{$itemIdentRef}"
            id="isnew{$itemIdentRef}" value="false"/>
        <input>
            <xsl:attribute name="type">hidden</xsl:attribute>
            <xsl:attribute name="name">item_ident_ref</xsl:attribute>
            <xsl:attribute name="value">
                <xsl:value-of select="$itemIdentRef"/>
            </xsl:attribute>
        </input>
        <input>
            <xsl:attribute name="type">hidden</xsl:attribute>
            <xsl:attribute name="id">
                <xsl:value-of select="$itemIdentRef"/>
            </xsl:attribute>
            <xsl:attribute name="name">item_result_ident_ref=<xsl:value-of
                    select="$itemIdentRef"/>&#38;response_ident_ref=<xsl:value-of select="$responseIdentRef"/>
            </xsl:attribute>
            <xsl:attribute name="value">
                <xsl:value-of select="//item_result[@ident_ref=$itemIdentRef]/response[@ident_ref=$responseIdentRef]/response_value"/>
            </xsl:attribute>
            <xsl:attribute
                    name="onChange">javascript:document.ASIDeliveryForm.isnew<xsl:value-of select="$itemIdentRef"/>.value=true;</xsl:attribute>
        </input>
        <!-- on clicking this link, the soundRecording_captured.jsp page which contains the audio applet
         will be evoked. Values parsed by this link to the jsp page are parameters to the audio applet -->
 <xsl:variable name="base">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>


        <xsl:if test="($attempts_taken='') or ($num_of_attempts &gt; $attempts_taken) or (number($questionOrder) &gt;0)">
        <a href="#" onClick="javascript: window.open('{$base}/{.}'
         +'?filename='+audioFileName+'_{$itemIdentRef}.au'
         +'&amp;seconds='+audioSeconds
         +'&amp;limit='+audioLimit
         +'&amp;app='+audioAppName
         +'&amp;dir='+audioDir
         +'&amp;imageUrl='+audioImageURL
         +'&amp;item_ident_ref='+'{$itemIdentRef}',
         '__ha_dialog',
         'toolbar=no,menubar=no,personalbar=no,width=430,height=330,scrollbars=no,resizable=no');"><img border="0"><xsl:attribute name="src"><xsl:value-of select="$base"/>htmlarea/images/recordresponse.gif</xsl:attribute></img></a>
        </xsl:if>

        <p/>
<!--
    ident:<xsl:value-of select="$itemIdentRef" /> <br/>
    response:<xsl:value-of select="$responseIdentRef" /> <br/>
    taken:<xsl:value-of select="$attempts_taken" /> <br/>
-->
        <!--xsl:if test="$attempts_taken !='' and $attempts_taken !='0'"-->
        <xsl:if test="//item_result[@ident_ref=$itemIdentRef]/response[@ident_ref=$responseIdentRef]/response_value!=''">
            <p/>
            <a
                href="{//item_result[@ident_ref=$itemIdentRef]/response[@ident_ref=$responseIdentRef]/response_value}">Your
                recorded answer</a>(no. of tries taken: <xsl:value-of
            select="$attempts_taken"/>) </xsl:if>
        <p/> Time Allowed (seconds): <xsl:value-of select="$timeallowed"/>
        <br/> Number of Tries Allowed: 
          <xsl:if test="number($num_of_attempts) &gt; 10">Unlimited</xsl:if>
          <xsl:if test="number($num_of_attempts) &lt;= 10">
            <xsl:value-of select="$num_of_attempts"/>
          </xsl:if>
        <br/>
    </xsl:template>
    <!--*************************** end of audio recording display **************************************** -->

  <!-- ============================= -->
  <!-- child element: material       -->
  <!-- ============================= -->
    <xsl:template match="material">
        <xsl:apply-templates select="mattext | matimage"/>
    </xsl:template>
  <!-- ============================= -->
  <!-- child element: mattext       -->
  <!-- ============================= -->
    <xsl:template match="mattext">
        <xsl:value-of select="self::*" disable-output-escaping="yes"/>
        <xsl:value-of select="descendant::comment()" disable-output-escaping="yes"/>
    </xsl:template>
  <!-- ============================= -->
  <!-- child element: matimage       -->
  <!-- ============================= -->
    <xsl:template match="matimage">
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
    </xsl:template>
  <!-- ============================= -->
  <!-- child element: response_lid       -->
  <!-- ============================= -->
    <xsl:template match="response_lid">
        <input>
            <xsl:attribute name="type">HIDDEN</xsl:attribute>
            <xsl:attribute name="name">response_type</xsl:attribute>
            <xsl:attribute name="value">lid</xsl:attribute>
        </input>
        <xsl:variable name="cardinalityType" select="@rcardinality"/>
        <table>
        <xsl:apply-templates select="render_choice/response_label"/>
        </table>
        <p/>
    </xsl:template>
  <!-- =============================    -->
  <!-- child element: response_label    -->
  <!-- =============================    -->
    <xsl:template match="response_label">
        <!-- Display green check mark-->
        <xsl:variable name="rspIdent" select="../../@ident"/>
        <xsl:variable name="itemIdent" select="ancestor::item/@ident"/>
        <xsl:variable name="valueIdent" select="@ident"/>
        <tr><td width="20">
        <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showCorrectResponse = 'True'">
        <xsl:for-each select="//item_result[@ident_ref=$itemIdent]/response[@ident_ref=$rspIdent]/response_value">
            <xsl:variable name="responseValue" select="."/>
            <xsl:if test="$responseValue = $valueIdent">
                <xsl:for-each select="//item_result[@ident_ref=$itemIdent]/response/response_form/correct_response">
                    <xsl:variable name="correctValue" select="."/>
                    <xsl:if test="$responseValue = $correctValue">
                        <img src="../../images/delivery/green.gif"/>
                    </xsl:if>
                    <xsl:if test="$responseValue != $correctValue">
                        <img src="../../images/delivery/red.gif"/>
                    </xsl:if>
                </xsl:for-each>
            </xsl:if>
        </xsl:for-each>
        </xsl:if>
		</td><td>
        <input>
            <xsl:choose>
                <xsl:when test="../../@rcardinality = 'Multiple'">
                    <xsl:attribute
                            name="name">item_result_ident_ref=<xsl:value-of
                            select="ancestor::item/@ident"/>&#38;response_ident_ref=<xsl:value-of
                            select="../../@ident"/>&#38;response_value=<xsl:value-of select="@ident"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">CHECKBOX</xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@ident"/>
                    </xsl:attribute>
		    <xsl:attribute name="onBlur">
                   	autosave()
                    </xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute
                            name="name">item_result_ident_ref=<xsl:value-of
                            select="ancestor::item/@ident"/>&#38;response_ident_ref=<xsl:value-of select="../../@ident"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">RADIO</xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@ident"/>
                    </xsl:attribute>
                        <xsl:attribute name="onBlur">
                                autosave()
                        </xsl:attribute>

                </xsl:otherwise>
            </xsl:choose>
            <!--
    <xsl:variable name="rspIdent" select="../../@ident" />
    <xsl:variable name="itemIdent" select="ancestor::item/@ident" />
    <xsl:variable name="valueIdent" select="@ident" />
    -->
            <xsl:for-each select="//item_result[@ident_ref=$itemIdent]/response[@ident_ref=$rspIdent]/response_value">
                <xsl:variable name="responseValue" select="."/>
                <xsl:if test="$responseValue = $valueIdent">
                    <xsl:attribute name="checked"/>
                </xsl:if>
            </xsl:for-each>
        </input>
        <xsl:if test="ancestor::item/itemmetadata/qtimetadata/qtimetadatafield[fieldlabel='qmd_itemtype']/fieldentry='Multiple Choice'"><xsl:number value="position()" format="A. "/></xsl:if>
        <xsl:apply-templates select="descendant::material"/>
        <!-- Put selection level feedback here
    <xsl:for-each select="//item_result[@ident_ref=$itemIdent2]/response[@ident_ref=$rspIdent]/response_value">
    <xsl:variable name="responseValue" select="." />
       <xsl:if test="$responseValue = $valueIdent" >
       <font  color="blue" >
     <xsl:apply-templates select="//item[@ident=$itemIdent2]/itemfeedback[@ident=$valueIdent]/flow_mat/material "/>
       </font>
    </xsl:if>
    </xsl:for-each>
 -->

    <!-- ============================= -->
  <!-- selection based feedback      -->
  </td>
  <td>
  <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showSelectionLevel = 'True'">
   <xsl:for-each select="//item_result[@ident_ref=$itemIdent]/response[@ident_ref=$rspIdent]/response_value">
    <xsl:variable name="responseValue" select="." />
    <xsl:if test="$responseValue = $valueIdent" >
   <xsl:for-each select="//item[@ident=$itemIdent]/resprocessing/respcondition">
   <xsl:variable name="resprocessValue" select="conditionvar/varequal"/>
   <xsl:if test="$resprocessValue = $responseValue">
      <xsl:for-each select="displayfeedback">
         <xsl:if test="@linkrefid != 'InCorrect'">
                  <xsl:if test="@linkrefid != 'Correct'">
                    <xsl:variable name="feedbackId" select="@linkrefid"/>
                     <font  color="blue" >
                     <xsl:apply-templates select="//item[@ident=$itemIdent]/itemfeedback[@ident=$feedbackId]/flow_mat/material"/>
                     </font>
                  </xsl:if>
         </xsl:if>
      </xsl:for-each>
   </xsl:if>
   </xsl:for-each>
   </xsl:if>
   </xsl:for-each>
   </xsl:if>
   </td>
   </tr>
    </xsl:template>

  <!-- ============================= -->
  <!-- child element: itemfeedback      -->
  <!-- ============================= -->
    <xsl:template match="itemfeedback">
        <xsl:apply-templates select="/flow_mat/material |material"/>
    </xsl:template>
    <xsl:template match="response_value">
        <xsl:value-of select="."/>
    </xsl:template>
    <xsl:template match="response_str">
        <input>
            <xsl:attribute name="type">HIDDEN</xsl:attribute>
            <xsl:attribute name="name">response_type</xsl:attribute>
            <xsl:attribute name="value">fib</xsl:attribute>
        </input>
        <xsl:apply-templates select="render_fib"/>
    </xsl:template>
  <!-- ============================= -->
  <!-- child element: render_fib       -->
  <!-- ============================= -->
    <xsl:template match="render_fib">
        <xsl:choose>
            <xsl:when test="@rows &gt; 1">
                <br/>
                <textarea WRAP="virtual">
                    <xsl:attribute name="cols">
                        <xsl:value-of select="@columns"/>
                    </xsl:attribute>
                    <xsl:attribute name="rows">
                        <xsl:value-of select="@rows"/>
                    </xsl:attribute>
                    <xsl:attribute
                            name="name">item_result_ident_ref=<xsl:value-of
                            select="ancestor::item/@ident"/>&#38;response_ident_ref=<xsl:value-of select="../@ident"/>
                    </xsl:attribute>
                        <xsl:attribute name="onBlur">
                                autosave()
                        </xsl:attribute>

                    <xsl:variable name="rspIdent" select="../@ident"/>
                    <xsl:variable name="itemIdent" select="ancestor::item/@ident"/>
                    <xsl:value-of select="//item_result[@ident_ref=$itemIdent]/response[@ident_ref=$rspIdent]/response_value"/>
                </textarea>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="ident" select="ancestor::item/@ident" />
                <xsl:apply-templates select="//item_result[@ident_ref=$ident]" >
                  <xsl:with-param name="response_ident" select="ancestor::response_str/@ident"/>
                </xsl:apply-templates>
                <input type="text">
                    <xsl:attribute name="size">
                        <xsl:value-of select="@columns"/>
                    </xsl:attribute>
                    <xsl:attribute
                            name="name">item_result_ident_ref=<xsl:value-of
                            select="ancestor::item/@ident"/>&#38;response_ident_ref=<xsl:value-of select="../@ident"/>
                    </xsl:attribute>
                        <xsl:attribute name="onBlur">
                                autosave()
                        </xsl:attribute>

                    <xsl:attribute name="value">
                        <xsl:variable name="rspIdent" select="../@ident"/>
                        <xsl:variable name="itemIdent" select="ancestor::item/@ident"/>
                        <xsl:value-of select="//item_result[@ident_ref=$itemIdent]/response[@ident_ref=$rspIdent]/response_value"/>
                    </xsl:attribute>
                </input>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- *****************where response Type is Group.***************** -->
    <!--	<xsl:template match="response_grp">
		<xsl:apply-templates mode = "target_label" select="render_choice/response_label"/>
	</xsl:template>
-->
    <!-- *****************Seperating Targets from Sources***************** -->
    <xsl:template mode="source_label" match="response_label">
        <xsl:param name="target_x"/>
        <xsl:param name="position"/>
        <xsl:if test="contains(@match_group,$target_x)">
            <option>
                <xsl:attribute name="value">
                    <xsl:value-of select="@ident"/>
                </xsl:attribute>
                <xsl:variable name="ident" select="@ident"/>
                <xsl:variable name="itemIdent" select="ancestor::item/@ident"/>
                <xsl:if test="//item_result[@ident_ref=$itemIdent]/response/response_value[$position] = $ident">
                    <xsl:attribute name="selected"/>
                </xsl:if>
                <xsl:number level="single" count="response_label" format="A"/>
            </option>
        </xsl:if>
    </xsl:template>
    <xsl:template mode="target_label" match="response_label">
        <xsl:if test="not(@match_group)">
            <table>
                <tr>
                    <td>
                        <xsl:attribute name="value">
                            <xsl:value-of select="@ident"/>
                        </xsl:attribute>
                        <xsl:value-of select="material"/>
                    </td>
                    <td>
                        <input>
                            <xsl:attribute name="type">HIDDEN</xsl:attribute>
                            <xsl:attribute name="name">response_type</xsl:attribute>
                            <xsl:attribute name="value">grp</xsl:attribute>
                        </input>
                        <select>
                            <xsl:attribute
                                    name="name">item_result_ident_ref=<xsl:value-of
                                    select="ancestor::item/@ident"/>&#38;response_ident_ref=<xsl:value-of
                                    select="../../@ident"/>&#38;response_value=<xsl:value-of select="@ident"/>
                            </xsl:attribute>
                            <option> [Select]</option>
                            <xsl:variable name="target" select="@ident"/>
                            <xsl:apply-templates mode="source_label" select="../response_label">
                                <xsl:with-param name="target_x" select="$target"/>
                            </xsl:apply-templates>
                        </select>
                    </td>
                </tr>
            </table>
        </xsl:if>
    </xsl:template>
    <!-- *********This template can handle the response group matching case result display.
			   The condition is the response_label elements has to be grouped together
			   and presented in "Source" first "Target" second order. **********-->
    <xsl:template match="response_grp">
        <br/>
        <!-- Display all sources first -->
        <xsl:for-each select="render_choice/response_label">
            <xsl:if test="(@match_group)">
                <xsl:number level="single" count="response_label" format="A"/>.
                    <xsl:value-of select="material"/>
                <br/>
            </xsl:if>
        </xsl:for-each>
        <br/>
        <xsl:variable name="base" select="count(render_choice/response_label[@match_group])"/>
        <xsl:for-each select="render_choice/response_label">
            <xsl:if test="not(@match_group)">
                <table>
                    <tr>
                        <td>
                            <input>
                                <xsl:attribute name="type">HIDDEN</xsl:attribute>
                                <xsl:attribute name="name">response_type</xsl:attribute>
                                <xsl:attribute name="value">grp</xsl:attribute>
                            </input>
                            <select>
                                <xsl:attribute
                                        name="name">item_result_ident_ref=<xsl:value-of
                                        select="ancestor::item/@ident"/>&#38;response_ident_ref=<xsl:value-of
                                        select="../../@ident"/>&#38;response_value=<xsl:value-of select="position()-$base"/>
                                </xsl:attribute>
                                <option value="null"> [Select]</option>
                                <xsl:variable name="target" select="@ident"/>
                                <xsl:apply-templates mode="source_label" select="../response_label">
                                    <xsl:with-param name="target_x" select="$target"/>
                                    <xsl:with-param name="position" select="position()-$base"/>
                                </xsl:apply-templates>
                            </select>
                        </td>
                        <td>
                            <xsl:attribute name="value">
                                <xsl:value-of select="@ident"/>
                            </xsl:attribute>
                            <xsl:value-of select="material"/>
                        </td>
                    </tr>
                </table>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    <!-- *****************qtimetadatafield *****************-->
    <!--
<xsl:template  name="fieldlabel">
        <xsl:param name="fl"/>
        <xsl:for-each select="itemmetadata/qtimetadata/qtimetadatafield/fieldlabel">
                <xsl:variable name="fieldlabel" select= "."/>
                <xsl:if test="$fieldlabel= $fl">
                        <xsl:variable name="fieldentry" select= "../fieldentry"/>
                        <xsl:value-of select="$fieldentry" />
                </xsl:if>
        </xsl:for-each>
</xsl:template>
-->
  <!-- ================================ -->
  <!-- child element: item_result       -->
  <!-- ================================ -->
    <xsl:template match="item_result">
      <xsl:param name="response_ident"/>
            <xsl:variable name="unique_answers" select="response/response_form/correct_response[not(.=following::correct_response)]"/>
        <xsl:for-each select="response[@ident_ref=$response_ident]/response_value">
            <xsl:choose>
                <!--If the response is in the answer set and was not previously given -->
                <xsl:when test="(.=$unique_answers) and not(.=preceding::response_value)">
                        <img src="../../images/delivery/green.gif"/>
                </xsl:when>
                <xsl:otherwise>
                        <img src="../../images/delivery/red.gif"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
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
    <xsl:template match="correct_response">
        <xsl:value-of select="."/>
        <xsl:if test="following-sibling::correct_response or parent::response_form/parent::response/following-sibling::response[1]/response_form/correct_response">, </xsl:if>
    </xsl:template>
</xsl:stylesheet>
