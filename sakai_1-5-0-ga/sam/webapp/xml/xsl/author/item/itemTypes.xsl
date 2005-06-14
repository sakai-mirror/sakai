<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template name="itemTypes">
<!--chen update -->
    <xsl:variable name="disableMultipleChoiceSingle" select="/stxx/request/attribute[@name='disableMultipleChoiceSingle']/disableMultipleChoiceSingle"/>
    <xsl:variable name="disableMultipleCorrectAnswer" select="/stxx/request/attribute[@name='disableMultipleChoiceMultiple']/disableMultipleChoiceMultiple"/>
    <xsl:variable name="disableMultipleChoiceSurvey" select="/stxx/request/attribute[@name='disableMultipleChoiceSurvey']/disableMultipleChoiceSurvey"/>
    <xsl:variable name="disableEssay" select="/stxx/request/attribute[@name='disableShortAnswerEssay']/disableShortAnswerEssay"/>
    <xsl:variable name="disableFillInBlank" select="/stxx/request/attribute[@name='disableFillInTheBlank']/disableFillInTheBlank"/>
    <xsl:variable name="disableMatching" select="/stxx/request/attribute[@name='disableMatching']/disableMatching"/>
    <xsl:variable name="disableTrueFalse" select="/stxx/request/attribute[@name='disableTrueFalse']/disableTrueFalse"/>
    <xsl:variable name="disableAudioRecording" select="/stxx/request/attribute[@name='disableAudioRecording']/disableAudioRecording"/>
    <xsl:variable name="disableFileUpload" select="/stxx/request/attribute[@name='disableFileUpload']/disableFileUpload"/>

    <select name="action">

      <option value="default"></option>

<!--      <xsl:choose>
        <xsl:when test="$disableMultipleChoiceSingle='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="Multiple Choice">Multiple Choice (single correct)</option>
        </xsl:otherwise>
      </xsl:choose>-->

      <xsl:choose>
        <xsl:when test="$disableMultipleCorrectAnswer='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="Multiple Correct Answer">Multiple Choice (multiple correct)</option>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:choose>
        <xsl:when test="$disableMultipleChoiceSurvey='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="Multiple Choice Survey">Multiple Choice Survey</option>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:choose>
        <xsl:when test="$disableEssay='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="Essay">Short Answer/Essay</option>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:choose>
        <xsl:when test="$disableFillInBlank='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="Fill In the Blank">Fill in the Blank</option>
        </xsl:otherwise>
      </xsl:choose>
  
      <xsl:choose>
        <xsl:when test="$disableMatching='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="Matching">Matching</option>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:choose>
        <xsl:when test="$disableTrueFalse='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="True False">True/False Question</option>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:choose>
        <xsl:when test="$disableAudioRecording='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="Audio Recording">Audio Recording</option>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:choose>
        <xsl:when test="$disableFileUpload='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="File Upload">File Upload</option>
        </xsl:otherwise>
      </xsl:choose>


      <option value="Import from Question Pool">Import from Question Pool</option>
    </select>
  </xsl:template>
  <xsl:template name="resolveItemTypes">
    <xsl:param name="itemType"/>
    <xsl:if test="$itemType='Multiple Choice'">Multiple Choice (single correct) </xsl:if>
    <xsl:if test="$itemType='Multiple Correct Answer'">Multiple Choice (multiple correct)</xsl:if>
    <xsl:if test="$itemType='Multiple Choice Survey'">Multiple Choice Survey</xsl:if>
    <xsl:if test="$itemType='Essay'">Short Answer/Essay</xsl:if>
    <xsl:if test="$itemType='Fill In the Blank'">Fill in the Blank</xsl:if>
    <xsl:if test="$itemType='Matching'">Matching</xsl:if>
    <xsl:if test="$itemType='True False'">True/False</xsl:if>
    <xsl:if test="$itemType='Audio Recording'">Audio Recording</xsl:if>
    <xsl:if test="$itemType='File Upload'">File Upload</xsl:if>
  </xsl:template>

<!--testing for auto submit item type drop box -->
  <xsl:template name="itemTypesAuto">
    <xsl:param name="formName"/>

    <xsl:variable name="disableMultipleChoiceSingle" select="/stxx/request/attribute[@name='disableMultipleChoiceSingle']/disableMultipleChoiceSingle"/>
    <xsl:variable name="disableMultipleCorrectAnswer" select="/stxx/request/attribute[@name='disableMultipleChoiceMultiple']/disableMultipleChoiceMultiple"/>
    <xsl:variable name="disableMultipleChoiceSurvey" select="/stxx/request/attribute[@name='disableMultipleChoiceSurvey']/disableMultipleChoiceSurvey"/>
    <xsl:variable name="disableEssay" select="/stxx/request/attribute[@name='disableShortAnswerEssay']/disableShortAnswerEssay"/>
    <xsl:variable name="disableFillInBlank" select="/stxx/request/attribute[@name='disableFillInTheBlank']/disableFillInTheBlank"/>
    <xsl:variable name="disableMatching" select="/stxx/request/attribute[@name='disableMatching']/disableMatching"/>
    <xsl:variable name="disableTrueFalse" select="/stxx/request/attribute[@name='disableTrueFalse']/disableTrueFalse"/>
    <xsl:variable name="disableAudioRecording" select="/stxx/request/attribute[@name='disableAudioRecording']/disableAudioRecording"/>
    <xsl:variable name="disableFileUpload" select="/stxx/request/attribute[@name='disableFileUpload']/disableFileUpload"/>

    <select name="action" onchange="javascript:form.submit()" >


      <option value="default"></option>

<!--      <xsl:choose>
        <xsl:when test="$disableMultipleChoiceSingle='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="Multiple Choice">Multiple Choice (single correct)</option>
        </xsl:otherwise>
      </xsl:choose>-->

      <xsl:choose>
        <xsl:when test="$disableMultipleCorrectAnswer='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="Multiple Correct Answer">Multiple Choice (multiple correct)</option>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:choose>
        <xsl:when test="$disableMultipleChoiceSurvey='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="Multiple Choice Survey">Multiple Choice Survey</option>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:choose>
        <xsl:when test="$disableEssay='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="Essay">Short Answer/Essay</option>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:choose>
        <xsl:when test="$disableFillInBlank='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="Fill In the Blank">Fill in the Blank</option>
        </xsl:otherwise>
      </xsl:choose>
  
      <xsl:choose>
        <xsl:when test="$disableMatching='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="Matching">Matching</option>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:choose>
        <xsl:when test="$disableTrueFalse='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="True False">True/False Question</option>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:choose>
        <xsl:when test="$disableAudioRecording='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="Audio Recording">Audio Recording</option>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:choose>
        <xsl:when test="$disableFileUpload='true'">
        </xsl:when>
        <xsl:otherwise>
          <option value="File Upload">File Upload</option>
        </xsl:otherwise>
      </xsl:choose>


      <option value="Import from Question Pool">Import from Question Pool</option>
    </select>
  </xsl:template>

</xsl:stylesheet>
