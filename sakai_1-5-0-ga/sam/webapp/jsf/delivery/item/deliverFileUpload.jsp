<%-- $Id: deliverFileUpload.jsp,v 1.23 2005/02/14 17:08:14 rgollub.stanford.edu Exp $
include file for delivering file upload questions
should be included in file importing DeliveryMessages
--%>
<h:outputText value="#{question.text} "  escape="false"/>

<%-- use existing JSP for now until we have JSF file upload --%>
<h:outputLink rendered="#{delivery.previewMode ne 'true'}"
 value="#" onclick="javascript:window.open('/samigo/jsf/upload/uploadFile.faces','uploadWindow','toolbar=no,location=no,directories=no,status=no,menubar=yes,scrollbars=yes,resizable=yes,width=400,height=200');">
  <h:outputText value="#{msg.upload_file}." />
</h:outputLink>

<f:verbatim><br /></f:verbatim>
<!-- upload button -->
<h:outputText value="#{msg.upload_instruction}" />
<f:verbatim><br /></f:verbatim>
<h:panelGroup>
  <h:outputText value="#{msg.file}" />
  <!-- note that target represent the location where the upload medis will be temporarily stored -->
  <!-- For ItemGradingData, it is very important that target must be in this format: -->
  <!-- assessmentXXX/questionXXX/agentId -->
  <!-- please check the valueChangeListener to get the final destination -->
  <corejsf:upload 
    target="/jsf/upload_tmp/assessment#{delivery.assessmentId}/question#{question.itemData.itemId}/admin" 
    valueChangeListener="#{delivery.addMediaToItemGrading}" />
  <f:verbatim>&nbsp;&nbsp;</f:verbatim>
  <h:commandButton value="Upload" action="submit"/>
</h:panelGroup>
<f:verbatim><br /></f:verbatim>

      <%-- media list, note that question is ItemContentBean --%>

      <h:dataTable value="#{question.itemData.lastItemGradingDataByAgent.mediaArray}" var="media">
        <h:column>
          <f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
          <h:commandLink value="/samigo/servlet/ShowMedia?media=#{media.mediaId}" >
             <h:outputText escape="false" value="#{media.filename}" />
          </h:commandLink>
        </h:column>
      </h:dataTable>

<h:selectBooleanCheckbox value="#{question.review}" rendered="#{delivery.previewMode ne 'true' && delivery.navigation ne '1'}" id="mark_for_review" />
<h:outputLabel for="mark_for_review" value="#{msg.mark}" 
  rendered="#{delivery.previewMode ne 'true' && delivery.navigation ne '1'}" />

<h:panelGroup rendered="#{delivery.feedback eq 'true'}">
  <h:panelGroup rendered="#{delivery.feedbackComponent.showItemLevel}">
    <f:verbatim><br /><br /></f:verbatim>
    <f:verbatim><b></f:verbatim>
    <h:outputLabel for="feedSC" value="#{msg.feedback}: " />
    <f:verbatim></b></f:verbatim>
    <h:outputText id="feedSC" value="#{question.feedback}" escape="false" />
  </h:panelGroup>
  <h:panelGroup rendered="#{delivery.feedbackComponent.showGraderComment}">
    <f:verbatim><br /><br /></f:verbatim>
    <f:verbatim><b></f:verbatim>
    <h:outputLabel for="commentSC" value="#{msg.comment}: " />
    <f:verbatim></b></f:verbatim>
    <h:outputText id="commentSC" value="#{question.gradingComment}"
      escape="false" />
  </h:panelGroup>
</h:panelGroup>
