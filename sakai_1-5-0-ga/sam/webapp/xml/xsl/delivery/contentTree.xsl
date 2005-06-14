<?xml version="1.0"?>
<!--
 * <p>Title: NavigoProject.org</p>
 * <p>Description: ASI Delivery XML Style Sheet </p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: </p>
 * @author <a href="mailto:casong@indiana.edu">Pamela Song</a>
 * @version $Id: contentTree.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet 
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
      version="1.0">

<xsl:import href="../layout/header.xsl" />
<xsl:import href="../layout/footer.xsl" />
<xsl:import href="../layout/menu.xsl" />
<xsl:import href="../layout/defaultLayout.xsl" />
      
   <xsl:output method="html"></xsl:output>
   
      <xsl:template match="/">
          <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
      
      <html>
         <head>
         <title>Assessment Table of Contents</title>
         <link rel="stylesheet" type="text/css" 
            href="{$base}css/xmlTree.css"/>
         <script type="text/javascript" 
            src="{$base}js/xmlTree.js"></script>
         </head>
<xsl:call-template name="defaultLayout" />
      </html>
   </xsl:template>

<xsl:template name="body">
  <table width="90%" align="center"><tr>
  <td><b><xsl:value-of select="stxx/form/xmlDeliveryActionForm/assessmentTitle" /> - 
           <xsl:value-of select="stxx/form/xmlDeliveryActionForm/username" /></b>
  </td>
  <td align="right">
  <xsl:if test="stxx/form/xmlDeliveryActionForm/endTime != ''" >
  <xsl:variable name="base"> <xsl:call-template name="baseHREF"/> </xsl:variable>
  <script LANGUAGE="JavaScript" src="{$base}js/progressBar.js" />

  <script LANGUAGE="JavaScript">
    var begin = new Date("<xsl:value-of select="stxx/form/xmlDeliveryActionForm/beginTime"/> ");
    var end = new Date("<xsl:value-of select="stxx/form/xmlDeliveryActionForm/endTime"/> ");
    var now = new Date("<xsl:value-of select="stxx/form/xmlDeliveryActionForm/currentTime"/> ");
  </script>	

  <script LANGUAGE="JavaScript" src="{$base}js/progressBar2.js" />
  </xsl:if>

  </td></tr></table>
  <p align="center">
  <table width="90%" class="border" bgcolor="ccccff">
  <tr><td>
  <b class="navigo_border_text_font">
  Table of Contents
  </b>
  </td></tr>
  </table>
  <table width="80%" >
  <tr><td >
    <img src="../../images/tree/blank.gif" alt="{/stxx/applicationResources/key[@name='delivery.image.unanswered']}"/> denotes unanswered question
    <br />
    <img src="../../images/tree/marked.gif" alt="{/stxx/applicationResources/key[@name='delivery.image.marked']}"/> denotes marked for review question
    <br />
    <br />
  </td>
  </tr>
  <!-- begin of review element for assessment level  -->
  <tr>
      <td>
          <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showStudentScore = 'True' and /stxx/form/xmlDeliveryActionForm/feedback != 'NONE'">
          <b>Total Score: <xsl:apply-templates select="/stxx/qti_result_report/result/assessment_result/outcomes/score"/></b>
          </xsl:if>
      </td>
  </tr>
  <tr><td>
  <xsl:value-of select="//assessment_result/extension_assessment_result/comment()" />
  </td></tr>
  <!-- end of review element for assessment level -->
  <tr>
  <td>
  <xsl:apply-templates />
  </td>
  </tr>
  <tr>
  <td align="right">
        <xsl:variable name="base">
          <xsl:call-template name="baseHREF"/>
        </xsl:variable>
  <form action="{$base}asi/delivery/xmlAction.do" method="post">
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">submit</xsl:attribute>
    <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.submit']" /></xsl:attribute>
  </input>
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">saveAndExit</xsl:attribute>
    <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='delivery.button.saveAndExit']" /></xsl:attribute>
  </input>
  </form>
  </td>
  </tr>
  </table>
  </p>
  <br />
  <br />
</xsl:template>

   
   <xsl:template match="assessment">
      <body>

   <xsl:apply-templates select="presentation_material/flow_mat/material/mattext" />
   <br/>
   <br/>
      <xsl:apply-templates select="section" />
   </body>
   </xsl:template>
   
   <xsl:template match="section">
     <xsl:choose>
      <xsl:when test="@title = 'DEFAULT'"> 
        <xsl:apply-templates select="section | item" /> 
      </xsl:when>           
      <xsl:otherwise>
      <a class="trigger">
      <xsl:attribute name="href">javascript:showBranch('<xsl:value-of select="@ident"/>');</xsl:attribute>
      <img src="../../images/tree/open.gif" alt="{/stxx/applicationResources/key[@name='delivery.image.twisty']}" border="0">
         <xsl:attribute name="id">I<xsl:value-of select="@ident"/></xsl:attribute>
      </img>
      <xsl:variable name="sectionIdent" select="@ident" />
      <xsl:value-of select="@title"/> 
      <xsl:apply-templates select="/stxx/qti_result_report/result//section_result[@ident_ref=$sectionIdent]/outcomes/score"/>
      (<xsl:value-of select="count(//section_result[@ident_ref=$sectionIdent]/item_result[response/response_value])"/>/<xsl:value-of select="count(item)"/> questions answered)<br/>
      <xsl:value-of select="//section_result[@ident_ref=$sectionIdent]/extension_section_result/comment()" />
      </a>
   
      <div class="branch">
          <xsl:attribute name="id"><xsl:value-of select="@ident"/></xsl:attribute>
          <xsl:apply-templates/>
      </div>
      </xsl:otherwise>
     </xsl:choose>
   </xsl:template>
   
   <xsl:template match="item">
     <xsl:variable name="valueIdent" select="@ident" />
     <div>
<!-- Get blank items based on blankItemIdents. 
    <xsl:for-each select="/stxx/form/xmlDeliveryActionForm/blankItemIdents/value">
    <xsl:variable name="blankItemIdent" select="." />
      <xsl:if test="$blankItemIdent = $valueIdent" >
        <img src="../../images/tree/blank.gif"/>
       </xsl:if>
    </xsl:for-each>
       -->
       <!-- Get blank items based on empty itemResult in qti_result_report-->
    <xsl:choose>
      <xsl:when test="//item_result[@ident_ref=$valueIdent]/response/response_value !='' ">
          <img src="../../images/delivery/spacer.gif" width="15" height="16"/>
      </xsl:when>           
      <xsl:otherwise>
       <img src="../../images/tree/blank.gif" alt="{/stxx/applicationResources/key[@name='delivery.image.unanswered']}"/>
      </xsl:otherwise>
    </xsl:choose>
    
    <xsl:for-each select="/stxx/form/xmlDeliveryActionForm/markedForReviewIdents/value">
    <xsl:variable name="markedItemIdent" select="." />
       <xsl:if test="$markedItemIdent = $valueIdent" >
        <img src="../../images/tree/marked.gif" alt="{/stxx/applicationResources/key[@name='delivery.image.marked']}"/>
       </xsl:if>
    </xsl:for-each>
    
      <b><xsl:value-of select="@displayIndex" />. </b>
    <xsl:variable name="itemIdent" select="@ident" />  
    <xsl:apply-templates select="/stxx/qti_result_report/result//item_result[@ident_ref=$itemIdent]/outcomes/score"></xsl:apply-templates>    
     
      <a><xsl:attribute name="href">xmlAction.do?itemIndex=<xsl:number level="any" count="item" format="1" /></xsl:attribute>
       <xsl:apply-templates select="presentation/material/mattext | presentation/flow/material/mattext " />
      </a>
      </div>
   </xsl:template>
<!--
   <xsl:template match="mattext" >
       <xsl:variable name="questionText1" select="self::*" />
       <xsl:value-of select="substring($questionText1, 1, 50)" /> 
       <xsl:variable name="questionText2" select="descendant::comment()"/>
      <xsl:value-of select="substring($questionText2, 1, 50)" /> 
   </xsl:template>
-->  
    <xsl:template match="mattext">
        <xsl:value-of select="self::*"/>
        <xsl:value-of select="descendant::comment()" disable-output-escaping="yes"/>
    </xsl:template>
     
   <xsl:template match="presentation_material">
     <xsl:value-of select="flow_mat/material/mattext" />
     <xsl:value-of select="flow_mat/material/mattext/comment()" />
   </xsl:template>
   
   <xsl:template match="score">
       <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showStudentScore = 'True' and /stxx/form/xmlDeliveryActionForm/feedback != 'NONE'">
       (<xsl:value-of select="score_value"/>/<xsl:value-of select="format-number(score_max, '0.00')"/> points)
       </xsl:if>
   </xsl:template>
   
   <!-- avoid output of text node 
        with default template -->
   <xsl:template match="RecordingData" />
   <xsl:template match="duration"/>
   <xsl:template match="qticomment"/>
   <xsl:template match="qtimetadata"/>
   <xsl:template match="objectives"/>
   <xsl:template match="assessmentcontrol"/>
   <xsl:template match="rubric"/>
   <xsl:template match="presentation_material"/>
   <xsl:template match="outcomes_processing"/>
   <xsl:template match="assessfeedback"/>
   <xsl:template match="selection_ordering"/>
   <xsl:template match="reference"/>
   <xsl:template match="sectionref"/>
   <xsl:template match="form"/>
   <xsl:template match="applicationResources"/>
   <xsl:template match="request"/>
   <xsl:template match="qti_result_report" />
   
   </xsl:stylesheet>

