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
  <!--
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
  -->
  </td></tr></table>
  <p align="center">
  <table width="90%" class="border" bgcolor="ccccff">
  <tr><td>
  Table of Contents
  </td></tr>
  </table>
  <table width="80%" >
  <tr><td >
    <img src="../../images/tree/blank.gif" alt="{/stxx/applicationResources/key[@name='review.image.unaswered']}"/> denotes unanswered question
    <br />
    <br />
  </td>
  </tr>
  <!-- begin of review element for assessment level  -->
  <tr><td>
      <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showStudentScore = 'True'"> 
      <b>Total Score: <xsl:value-of select="format-number(sum(//item_result/outcomes/score/score_value), '0.00')" />/<xsl:value-of select="format-number(sum(//item/resprocessing/outcomes/decvar/@maxvalue), '0.00')" /> Points</b>
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
  <xsl:variable name="formname" select="concat($base,'asi/review/reviewAction.do')"/>
  <form action="{$formname}" method="post"  name="ASIDeliveryForm">
  
 
  <input>
    <xsl:attribute name="type">submit</xsl:attribute>
    <xsl:attribute name="name">saveAndExit</xsl:attribute>
    <xsl:attribute name="value"> <xsl:value-of select="/stxx/applicationResources/key[@name='review.button.exit']" /></xsl:attribute>
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
      <span class="trigger">
      <xsl:attribute name="onClick">
         showBranch
         ('<xsl:value-of select="@ident"/>');
      </xsl:attribute>
   
      <img src="../../images/tree/closed.gif" alt="{/stxx/applicationResources/key[@name='review.image.twisty']}">
         <xsl:attribute name="id">I<xsl:value-of select="@ident"/></xsl:attribute>
      </img>
      <xsl:variable name="sectionIdent" select="@ident" />
      <xsl:value-of select="@title"/> 
      <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showStudentScore = 'True'"> 
      (<xsl:value-of select="format-number(sum(//section_result[@ident_ref=$sectionIdent]/item_result/outcomes/score/score_value), '0.00')" />
      /<xsl:value-of select="format-number(sum(item/resprocessing/outcomes/decvar/@maxvalue), '0.00')" /> Points)
      </xsl:if>
      (<xsl:value-of select="count(//section_result[@ident_ref=$sectionIdent]/item_result)" />/<xsl:value-of select="count(item)" /> questions answered)
      <br/>

      <xsl:value-of select="//section_result[@ident_ref=$sectionIdent]/extension_section_result/comment()" />
      </span>
   
      <span class="branch">
      <xsl:attribute name="id"><xsl:value-of select="@ident"/></xsl:attribute>
      <xsl:apply-templates/>
      </span>
           </xsl:otherwise>
     </xsl:choose>
   </xsl:template>
   
   <xsl:template match="item">
     <xsl:variable name="valueIdent" select="@ident" />
     <table border="0"><tr><td width="16">
<!-- Get blank items based on blankItemIdents. 
    <xsl:for-each select="/stxx/form/xmlDeliveryActionForm/blankItemIdents/value">
    <xsl:variable name="blankItemIdent" select="." />
      <xsl:if test="$blankItemIdent = $valueIdent" >
        <img src="../../images/tree/blank.gif"/>
       </xsl:if>
    </xsl:for-each>
       -->
       <!-- Get blank items based on empty itemResult in qti_result_report-->
    <xsl:if test="//item_result[@ident_ref=$valueIdent] = null " >
      <img src="../../images/tree/blank.gif" alt="{/stxx/applicationResources/key[@name='review.image.unanswered']}"/>
    </xsl:if>

    </td>
    <td width="16">
    <xsl:for-each select="/stxx/form/xmlDeliveryActionForm/markedForReviewIdents/value">
    <xsl:variable name="markedItemIdent" select="." />
       <xsl:if test="$markedItemIdent = $valueIdent" >
        <img src="../../images/tree/marked.gif" alt="{/stxx/applicationResources/key[@name='review.image.marked']}"/>
       </xsl:if>
    </xsl:for-each>
    </td><td>
      <b><xsl:value-of select="@displayIndex" />. </b>
    <xsl:variable name="itemIdent" select="@ident" />  
    <xsl:if test="/stxx/form/xmlDeliveryActionForm/feedbackComponent/showStudentScore = 'True'"> 
        (<xsl:value-of select="//item_result[@ident_ref=$itemIdent]/outcomes/score/score_value" /> / 
        <xsl:value-of select="resprocessing/outcomes/decvar/@maxvalue" /> points)
       
     </xsl:if>
     
      <a><xsl:attribute name="href">reviewAction.do?itemIndex=<xsl:number level="any" count="item" format="1" /></xsl:attribute>
    <xsl:apply-templates select="presentation/material/mattext | presentation/flow/material/mattext " />
      </a>
      </td></tr></table>
   </xsl:template>

   <xsl:template match="mattext" >
       <xsl:variable name="questionText1" select="self::*" />
       <xsl:value-of select="substring($questionText1, 1, 50)" /> 
       <xsl:variable name="questionText2" select="descendant::comment()"/>
      <xsl:value-of select="substring($questionText2, 1, 50)" /> 
   </xsl:template>
   
   <xsl:template match="presentation_material">
     <xsl:value-of select="flow_mat/material/mattext" />
     <xsl:value-of select="flow_mat/material/mattext/comment()" />
   </xsl:template>
   
   <!-- avoid output of text node 
        with default template -->
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

