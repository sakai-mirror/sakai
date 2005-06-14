<?xml version="1.0" encoding="UTF-8" ?>
<!--
 * <p>Copyright: Copyright (c) 2005 Sakai</p>
 * <p>Description: QTI Persistence XML to XML Transform for Import</p>
 * @author <a href="mailto:esmiley@stanford.edu">Ed Smiley</a>
 * @version $Id: extractItem.xsl,v 1.10 2005/02/09 22:58:05 esmiley.stanford.edu Exp $
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" doctype-public="-//W3C//DTD HTML 4.01//EN"
 doctype-system="http://www.w3.org/TR/html4/strict.dtd"/>

<xsl:template match="/">

<itemData>
  <ident><xsl:value-of select="//item/@ident" /></ident>
  <title><xsl:value-of select="//item/@title" /></title>
  <duration><xsl:value-of select="//item/duration" /></duration>
  <instruction></instruction>
  <createdBy></createdBy>
  <createdDate></createdDate>
  <lastModifiedBy></lastModifiedBy>
  <lastModifiedDate></lastModifiedDate>
  <score>
    <xsl:value-of select="//resprocessing/outcomes/decvar/@maxvalue"/>
  </score>
  <hint></hint>
  <hasRationale></hasRationale>
  <status></status>
  <itemText type="list">
   <xsl:value-of select="//presentation//material/mattext" />
  </itemText>
  <itemAnswerCorrectLabel><xsl:value-of select="//respcondition/conditionvar" /></itemAnswerCorrectLabel>

  <!-- answers -->
  <xsl:for-each select="//presentation//response_lid/render_choice/response_label/material/mattext" >
  <xsl:choose>
    <xsl:when test="./*">
      <itemAnswer type="list"><xsl:copy-of select="./*"/></itemAnswer>
    </xsl:when>
    <xsl:when test="string-length(.)">
     <itemAnswer type="list"><xsl:value-of select="."/></itemAnswer>
    </xsl:when>
  </xsl:choose>
  </xsl:for-each>
  <!-- feedback -->
  <generalItemFeedback><!-- placeholder, for now --></generalItemFeedback>
  <xsl:for-each select="//itemfeedback">
    <xsl:choose>
       <xsl:when test="@ident = 'InCorrect'">
        <xsl:for-each select="flow_mat/material/mattext">
        <xsl:choose>
        <xsl:when test="./*">
          <incorrectItemFeedback><xsl:copy-of select="./*"/></incorrectItemFeedback>
        </xsl:when>
        <xsl:when test="string-length(.)">
         <incorrectItemFeedback><xsl:value-of select="."/></incorrectItemFeedback>
        </xsl:when>
        </xsl:choose>
        </xsl:for-each>
     </xsl:when>
     <xsl:when test="@ident = 'Correct'">
        <xsl:for-each select="flow_mat/material/mattext">
        <xsl:choose>
        <xsl:when test="./*">
          <correctItemFeedback><xsl:copy-of select="./*"/></correctItemFeedback>
        </xsl:when>
        <xsl:when test="string-length(.)">
         <correctItemFeedback><xsl:value-of select="."/></correctItemFeedback>
        </xsl:when>
        </xsl:choose>
        </xsl:for-each>
     </xsl:when>
     <xsl:otherwise>
      <xsl:for-each select="flow_mat/material/mattext">
      <xsl:choose>
        <xsl:when test="./*">
          <itemFeedback type="list"><xsl:copy-of select="./*"/></itemFeedback>
        </xsl:when>
        <xsl:when test="string-length(.)">
         <itemFeedback type="list"><xsl:value-of select="."/></itemFeedback>
        </xsl:when>
      </xsl:choose>
      </xsl:for-each>
     </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>

  <!-- media id for file upload and recording types, otherwise blank -->
  <!--  TODO debug -->
  <itemMedia type="list">
<!--  <xsl:if test="$item-type='File Upload' or $item-type='Audio Recording'"> -->
    <xsl:call-template name="extract-media-id">
      <xsl:with-param name="raw-answer">
        <!-- <xsl:value-of select="$item-answer-text"/> -->
      </xsl:with-param>
    </xsl:call-template>
<!--  </xsl:if> -->
  </itemMedia>

  <!-- if other methods of determining type don't work, attempt to determine from structure-->
  <!-- NOT guaranteed to be accurate, this is a fallback if none in metadata, title -->
  <!-- DEPENDENCY WARNING: syncs with type strings in AuthoringConstantStrings.java -->
  <xsl:for-each select="//item">
    <xsl:variable name="labels"><xsl:for-each select=".//response_label"><xsl:value-of select="@ident"/></xsl:for-each></xsl:variable>
    <introSpect>
      <xsl:choose>
        <xsl:when test=".//render_choice and .//render_fib">matching</xsl:when>
        <!-- this is lame, but true false acts like a 2 answer MCSC with answers True, False -->
        <xsl:when test=".//render_choice and $labels='TF'">True False</xsl:when>
        <xsl:when test=".//render_choice">Multiple Choice</xsl:when>
        <xsl:when test=".//resprocessing">Fill In the Blank</xsl:when>
        <xsl:otherwise>Short Answers/Essay</xsl:otherwise>
      </xsl:choose>
    </introSpect>
  </xsl:for-each>

</itemData>



</xsl:template>

<xsl:template match="qtimetadatafield[fieldlabel='NUM_OF_ATTEMPTS']">
  This is Neon!
</xsl:template>



<!--
useful values:
"itemmetadata/qtimetadata/qtimetadatafield"
"resprocessing/respcondition">
"displayfeedback/@linkrefid='Correct'">
"resprocessing/respcondition">
"displayfeedback/@linkrefid='Correct'">
"presentation/flow/response_lid/render_choice/response_label">
-->



<!-- this template exists to strip the "id=" parameter off of a file upload type
the current contract is that this type will have an answer text value that uses
an id URL with the id of the media data record, if this is no longer the case,
this template will need to be revised.
-->

<xsl:template name="extract-media-id">
  <xsl:param name="raw-answer"/>
    <xsl:value-of
     select="substring-before(substring-after($raw-answer, '?id='), '&quot;')"/>
</xsl:template>

</xsl:stylesheet>
