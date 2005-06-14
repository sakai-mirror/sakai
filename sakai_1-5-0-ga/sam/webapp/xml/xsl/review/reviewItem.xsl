<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: reviewItem.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../commonTemplates/itemRecordingData.xsl"/>
  <xsl:import href="../layout/header.xsl" />
<xsl:preserve-space elements="flow" />
<xsl:output method="html"/>
<!-- This template processes the root node ("/") -->
<xsl:template match="/">
<html>
  <head>
    <title>Navigo Assessment Item Display</title>
  </head>
  <body>
    <xsl:apply-templates select="stxx" />
  </body>
</html>
</xsl:template>

<xsl:template match="stxx">
  <xsl:apply-templates select="item" />
</xsl:template>

<xsl:template match="item">
    <xsl:variable name="itemIdentRef" select="@ident" />
<table align="center" width="90%" height="30" class="border" border="0" bgcolor="#ccccff"><tr><td>
<b><xsl:value-of select="/stxx/applicationResources/key[@name='review.question']" />: 

<xsl:value-of select="@continueNumber" />/<xsl:value-of select="/stxx/form/xmlDeliveryActionForm/size" />
</b></td><td align="center"><b>

</b>
</td>
<td align="right">

<xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showStudentScore = 'True'">
<b><xsl:value-of select="format-number(//item_result[@ident_ref=$itemIdentRef]/outcomes/score/score_value, '0.00')" /> / </b>
</xsl:if>

<xsl:if test="resprocessing/outcomes/decvar/@maxvalue != ''">
<b> <xsl:value-of select="format-number(resprocessing/outcomes/decvar/@maxvalue), '0.00')"/>&#160; <xsl:value-of select="/stxx/applicationResources/key[@name='review.points']" /></b>
</xsl:if>

</td>
</tr></table>
  <br/>
  
  <input>
    <xsl:attribute name="type">HIDDEN</xsl:attribute>
    <xsl:attribute name="name">item_ident</xsl:attribute>
    <xsl:attribute name="value"><xsl:value-of select="/stxx/item/@ident" /></xsl:attribute>
  </input>
  <table width="80%" align="center"><tr><td valign="top" width="10" rowspan="2">

  <b><xsl:value-of select="@displayIndex" /></b>
  </td>
  <td valign="top" align="left" >
  <xsl:apply-templates select="rubric" />

  <xsl:variable name="mctype">
    <xsl:apply-templates select="/stxx/item/itemmetadata/qtimetadata/qtimetadatafield">
      <xsl:with-param name="keyName" select="'qmd_itemtype'"/>
    </xsl:apply-templates>
  </xsl:variable>

  <xsl:if test="$mctype!='Audio Recording'">
    <xsl:apply-templates select="presentation/flow | presentation" />
  </xsl:if>
  <xsl:if test="$mctype='Audio Recording'">
    <script>
     <xsl:call-template name="js_recording_variables"/>
    </script>
    <xsl:apply-templates select="presentation/flow/material" mode="audio"/>
  </xsl:if>
      <p />
      
      <!--
    <input>
    <xsl:attribute name="type">checkbox</xsl:attribute>
    <xsl:attribute name="name">item_ident_ref=<xsl:value-of select="@ident" />&#38;markForReview=1</xsl:attribute>
    <xsl:attribute name="value">1</xsl:attribute>
  </input>
  <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.markForReview']" />
  -->
  <p />
  
    <xsl:variable name="mctype2">
    <xsl:apply-templates select="itemmetadata/qtimetadata/qtimetadatafield">
      <xsl:with-param name="keyName" select="'qmd_itemtype'"/>
    </xsl:apply-templates>
  </xsl:variable>
  <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showCorrectResponse = 'True'">
  <!-- Put Answer key here -->
	 <xsl:if test="$mctype2!='Essay'">
	    Answer Key: 
	    <xsl:apply-templates select="//item_result[@ident_ref=$itemIdentRef]/response/response_form/correct_response " />
	  </xsl:if>

	  <xsl:if test="$mctype2 ='Essay'">
	     Model Short Answer: <xsl:apply-templates select="itemfeedback[@ident='Correct']/flow_mat/material/mattext " />
	  </xsl:if> 
  </xsl:if>
  
  <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showItemLevel = 'True'">
  <!--  Put Question level feedback here -->
  <p/>

	  <xsl:if test="$mctype!='Essay'">
		  <xsl:variable name="feedbackIdentRef" select="//item_result [@ident_ref=$itemIdentRef]/feedback_displayed/@ident_ref" />
		  <xsl:apply-templates select="itemfeedback[@ident=$feedbackIdentRef]/flow_mat/material" />
      </xsl:if>
 
      <xsl:if test="$mctype='Essay'">
        <xsl:apply-templates select="itemfeedback[@ident='InCorrect']/flow_mat/material/mattext " />
      </xsl:if>

  </xsl:if>
  
  <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showGraderComment = 'True'">
  <!-- Put Grader's comment here -->
  <p/>
  <xsl:value-of select="//item_result [@ident_ref=$itemIdentRef]/extenstion_item_result" />
  </xsl:if>
  </td></tr>
    </table>
</xsl:template>

<xsl:template match="presentation">
  <xsl:apply-templates select="response_str |response_lid | response_grp | material" />

</xsl:template>

<xsl:template match="rubric">
  <b>
    <xsl:value-of select="flow_mat/material" />
  </b>

</xsl:template>

<xsl:template match="flow_mat">
  <xsl:apply-templates select="flow_mat | material" />
</xsl:template>

<xsl:template match="flow">
  <xsl:apply-templates select="response_str |response_lid | response_grp | material | flow" />

</xsl:template>


  <!--***** Audio Recording - daisyf *********************************************************** -->
  <xsl:template match="material" mode="audio">
    <xsl:apply-templates select="mattext" />
    <xsl:apply-templates select="matimage" />
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
    
    <script>
      audioSeconds=<xsl:value-of select="$timeallowed" />;
      audioLimit=<xsl:value-of select="$num_of_attempts" />;
    </script>
    <xsl:variable name="base">
      <xsl:call-template name="baseHREF"/>
    </xsl:variable>
    <p></p>
        <a href="#" onClick="javscript:window.open('{.}'
         +'?filename='+audioFileName+'_{/stxx/item/@ident}'
         +'&amp;seconds='+audioSeconds
         +'&amp;limit='+audioLimit
         +'&amp;app='+audioAppName
         +'&amp;dir='+audioDir
         +'&amp;imageUrl='+audioImageURL,
         '__ha_dialog',
         'toolbar=no,menubar=no,personalbar=no,width=430,height=330,scrollbars=no,resizable=no')"><img border="0"><xsl:attribute name="src"><xsl:value-of select="$base"/>htmlarea/images/recordresponse.gif</xsl:attribute></img></a>
    <p></p>
    <xsl:if test="/stxx/item/presentation/flow/response_str/materials_ref!=''">
    <p></p>
      <td><a href="{/stxx/item/presentation/flow/response_str/materials}">Your recorded answer</a></td>
    </xsl:if>
    <p></p>
        Time Limit: <xsl:value-of select="$timeallowed" />s<br/>
        No. of Attempts: <xsl:value-of select="$num_of_attempts" /><br/>
  </xsl:template>


  <!--****************************************************************************** -->

<xsl:template match="material">
 <xsl:apply-templates select="mattext | matimage" />
</xsl:template>

<xsl:template match="mattext">
  <xsl:value-of select="self::*" />
  <xsl:value-of select="descendant::comment()" disable-output-escaping="yes"/>
</xsl:template>

<xsl:template match="matimage">
  <xsl:if test="@imagtype = 'text/html'" >
     <xsl:if test="@uri != ''">
     <br />
     <br />
    <img>
      <xsl:attribute name="src"><xsl:value-of select="@uri" /></xsl:attribute>
    </img>
      <br />
      <br />
    </xsl:if>

  </xsl:if>
</xsl:template>

<xsl:template match="response_lid">
  <input>
    <xsl:attribute name="type">HIDDEN</xsl:attribute>
    <xsl:attribute name="name">response_type</xsl:attribute>
    <xsl:attribute name="value">lid</xsl:attribute>
  </input>
  <xsl:variable name="cardinalityType" select="@rcardinality" />
  <xsl:apply-templates select="render_choice/response_label" />
  <p />
</xsl:template>

<xsl:template match="response_label">
<br />
  <input>
  <xsl:choose>
    <xsl:when test="../../@rcardinality = 'Multiple'">
      <xsl:attribute name="name">item_result_ident_ref=<xsl:value-of select="ancestor::item/@ident" />&#38;response_ident_ref=<xsl:value-of select="../../@ident" />&#38;response_value=<xsl:value-of select="@ident" />
    </xsl:attribute>
      <xsl:attribute name="type">CHECKBOX</xsl:attribute>
      <xsl:attribute name="disabled">true</xsl:attribute>
          <xsl:attribute name="value">
         <xsl:value-of select="@ident" />
      </xsl:attribute>
    </xsl:when>
    <xsl:otherwise>
      <xsl:attribute name="name">item_result_ident_ref=<xsl:value-of select="ancestor::item/@ident" />&#38;response_ident_ref=<xsl:value-of select="../../@ident" />
    </xsl:attribute>
      <xsl:attribute name="type">RADIO</xsl:attribute>
      <xsl:attribute name="disabled">true</xsl:attribute>
      <xsl:attribute name="value">
         <xsl:value-of select="@ident" />
      </xsl:attribute>
    </xsl:otherwise>
  </xsl:choose>
  
    <xsl:variable name="rspIdent" select="../../@ident" />
    <xsl:variable name="itemIdent" select="ancestor::item/@ident" />
  <xsl:variable name="valueIdent" select="@ident" />
    <xsl:for-each select="//item_result[@ident_ref=$itemIdent]/response[@ident_ref=$rspIdent]/response_value">
    <xsl:variable name="responseValue" select="." />
       <xsl:if test="$responseValue = $valueIdent" >
         <xsl:attribute name="checked"/>
    </xsl:if>
    </xsl:for-each>
  
  </input>
   <xsl:apply-templates select="descendant::material" />
    <!-- Put selection level feedback here 
    <xsl:for-each select="/stxx/qti_result_report/result/assessment_result/item_result[@ident_ref=$itemIdent2]/response[@ident_ref=$rspIdent]/response_value">
    <xsl:variable name="responseValue" select="." />
       <xsl:if test="$responseValue = $valueIdent" >
       <font  color="blue" > 
     <xsl:apply-templates select="//item[@ident=$itemIdent2]/itemfeedback[@ident=$valueIdent]/flow_mat/material "/> 
       </font>
    </xsl:if>
    </xsl:for-each>
 -->
</xsl:template>
<xsl:template match="itemfeedback">
<xsl:apply-templates select= "/flow_mat/material |material" />
</xsl:template>

<xsl:template match="response_value" >
<xsl:value-of select="." />
</xsl:template>

<xsl:template match="response_str">

  <input>
    <xsl:attribute name="type">HIDDEN</xsl:attribute>
    <xsl:attribute name="name">response_type</xsl:attribute>
    <xsl:attribute name="value">fib</xsl:attribute>
  </input>

  <xsl:apply-templates select="render_fib" />
</xsl:template>

<xsl:template match="render_fib">
  <xsl:choose>
    <xsl:when test="@rows &gt; 1">
      <br />
      <textarea WRAP="virtual">
       <xsl:attribute name="cols"><xsl:value-of select="@columns" /></xsl:attribute>
       <xsl:attribute name="rows"><xsl:value-of select="@rows" /></xsl:attribute>
       <xsl:attribute name="name">item_result_ident_ref=<xsl:value-of select="ancestor::item/@ident" />&#38;response_ident_ref=<xsl:value-of select="../@ident" />
       </xsl:attribute> 
    <xsl:variable name="rspIdent" select="../@ident" />
    <xsl:variable name="itemIdent" select="ancestor::item/@ident" />
    <xsl:value-of select="//item_result[@ident_ref=$itemIdent]/response[@ident_ref=$rspIdent]/response_value" />
      </textarea>
    </xsl:when>
    <xsl:otherwise>
      <input type="text">
      <xsl:attribute name="size"><xsl:value-of select="@columns" /></xsl:attribute>
      <xsl:attribute name="name">item_result_ident_ref=<xsl:value-of select="ancestor::item/@ident" />&#38;response_ident_ref=<xsl:value-of select="../@ident" />
      </xsl:attribute>
    <xsl:attribute name="value">
    <xsl:variable name="rspIdent" select="../@ident" />
    <xsl:variable name="itemIdent" select="ancestor::item/@ident" />
    <xsl:value-of select="//item_result[@ident_ref=$itemIdent]/response[@ident_ref=$rspIdent]/response_value" />
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
	<xsl:template mode = "source_label" match="response_label">
		<xsl:param name = "target_x"/>
		<xsl:param name="position" />
			<xsl:if test="contains(@match_group,$target_x)">
				<option>
					<xsl:attribute name="value">
					<xsl:value-of select="@ident" />
					</xsl:attribute>
					<xsl:variable name="ident" select="@ident" />
					<xsl:variable name="itemIdent" select="ancestor::item/@ident" />
					<xsl:if test="//item_result[@ident_ref=$itemIdent]/response/response_value[$position] = $ident" >
					<xsl:attribute name="selected" />
					</xsl:if>
					<xsl:number level="single" count="response_label" format="A" />
				</option>
			</xsl:if>
	</xsl:template>

	<xsl:template mode = "target_label" match="response_label">
		<xsl:if test="not(@match_group)" >
			<table><tr>
				<td>
					<xsl:attribute name="value">
					<xsl:value-of select="@ident" />
					</xsl:attribute><xsl:value-of select="material" />
				</td>
				<td>
				
  <input>
    <xsl:attribute name="type">HIDDEN</xsl:attribute>
    <xsl:attribute name="name">response_type</xsl:attribute>
    <xsl:attribute name="value">grp</xsl:attribute>
  </input>
				<select>
				<xsl:attribute name="name">item_result_ident_ref=<xsl:value-of select="ancestor::item/@ident" />&#38;response_ident_ref=<xsl:value-of select="../../@ident" />&#38;response_value=<xsl:value-of select="@ident" />
				

				</xsl:attribute>
					<option> [Select]</option>
				<xsl:variable name="target" select="@ident"/>
						<xsl:apply-templates mode= "source_label" select="../response_label">
							<xsl:with-param name="target_x" select ="$target"/>
						</xsl:apply-templates>
					</select>
	 			</td>
			</tr></table>
		</xsl:if>
	</xsl:template>
	
<!-- *********This template can handle the response group matching case result display.
			   The condition is the response_label elements has to be grouped together
			   and presented in "Source" first "Target" second order. **********-->
<xsl:template match="response_grp">
  <br /> <!-- Display all sources first -->
  <xsl:for-each select="render_choice/response_label">
    <xsl:if test="(@match_group)" >
       <xsl:number level="single" count="response_label" format="A" />.  <xsl:value-of select="material" /><br/>
    </xsl:if>
  </xsl:for-each>
  <br />
  
  <xsl:variable name="base" select="count(render_choice/response_label[@match_group])" />
  <xsl:for-each select="render_choice/response_label">
     <xsl:if test="not(@match_group)" >
	<table><tr>
	 <td>
	 
	          <input>
    	<xsl:attribute name="type">HIDDEN</xsl:attribute>
    	<xsl:attribute name="name">response_type</xsl:attribute>
    	<xsl:attribute name="value">grp</xsl:attribute>
  	</input>
	<select>
	<xsl:attribute name="name">item_result_ident_ref=<xsl:value-of select="ancestor::item/@ident" />&#38;response_ident_ref=<xsl:value-of select="../../@ident" />&#38;response_value=<xsl:value-of select="position()-$base" />
				

	</xsl:attribute>
	<option value="null"> [Select]</option>
	<xsl:variable name="target" select="@ident"/>
      <xsl:apply-templates mode= "source_label" select="../response_label">
	<xsl:with-param name="target_x" select ="$target"/>
	<xsl:with-param name="position" select ="position()-$base"/>
	</xsl:apply-templates>
    </select>
	 </td>
	 <td>
	  <xsl:attribute name="value">
	  <xsl:value-of select="@ident" />
	  </xsl:attribute><xsl:value-of select="material" />
	</td>
	</tr></table>
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

</xsl:stylesheet>
