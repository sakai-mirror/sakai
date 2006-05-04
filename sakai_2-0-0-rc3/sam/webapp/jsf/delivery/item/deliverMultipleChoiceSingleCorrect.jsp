<%-- $Id: deliverMultipleChoiceSingleCorrect.jsp,v 1.24 2005/05/26 21:42:15 zqingru.stanford.edu Exp $
include file for delivering multiple choice questions
should be included in file importing DeliveryMessages
--%>
<h:outputText value="<script>" escape="false" />
<h:outputText value="var selectedRadioButton#{question.itemData.itemId};" escape="false" />
<h:outputText value="function uncheckRadioButtons#{question.itemData.itemId}(radioButton) {" escape="false" />
<h:outputText value="if (selectedRadioButton#{question.itemData.itemId} != null) {" escape="false" />
<h:outputText value="selectedRadioButton#{question.itemData.itemId}.checked = false;" escape="false" />
<h:outputText value="}" escape="false" />
<h:outputText value="selectedRadioButton#{question.itemData.itemId} = radioButton;" escape="false" />
<h:outputText value="selectedRadioButton#{question.itemData.itemId}.checked = true;" escape="false" />
<h:outputText value="}" escape="false" />
<h:outputText value="</script>" escape="false" />
 
  <h:outputText value="#{question.text}"  escape="false"/>
  <h:dataTable value="#{question.selectionArray}" var="selection">
    <h:column rendered="#{delivery.feedback eq 'true' &&
       delivery.feedbackComponent.showCorrectResponse && !delivery.noFeedback=='true'}">
      <h:graphicImage id="image" 
        rendered="#{selection.answer.isCorrect eq 'true' && selection.response}"
        alt="#{msg.correct}" url="/images/checkmark.gif" >
      </h:graphicImage>
      <h:graphicImage id="image2" 
        rendered="#{selection.answer.isCorrect ne 'true' && selection.response}"
        width="16" height="16"
        alt="#{msg.not_correct}" url="/images/delivery/spacer.gif">
      </h:graphicImage>
    </h:column>
    <h:column>
     <h:selectOneRadio onfocus="if (this.defaultChecked) { uncheckRadioButtons#{question.itemData.itemId}(this) };" onclick="uncheckRadioButtons#{question.itemData.itemId}(this);" required="false" disabled="#{delivery.previewMode eq 'true'}"
       value="#{question.responseId}" layout="pageLayout">
       <f:selectItem itemValue="#{selection.answerId}" />
     </h:selectOneRadio>
    </h:column>
    <h:column>
     <h:outputText value=" #{selection.answer.label}" escape="false" />
     <h:outputText value="." rendered="#{selection.answer.label ne ''}" />
     <h:outputText value=" #{selection.answer.text}" escape="false" />
    </h:column>
    <h:column>
      <h:outputText value=" <b>Feedback:</b> #{selection.feedback}"
        rendered="#{selection.feedback ne ''}" escape="false" />
    </h:column>
  </h:dataTable>

  <h:panelGroup rendered="#{question.itemData.hasRationale}" >
    <f:verbatim><br /></f:verbatim>
    <h:outputLabel for="rationale" value="#{msg.rationale}" />
    <f:verbatim><br /></f:verbatim>
    <h:inputTextarea id="rationale" value="#{question.rationale}" rows="5" cols="40" disabled="#{delivery.previewMode eq 'true'}" />
  </h:panelGroup>

<f:verbatim><br /></f:verbatim>
<h:selectBooleanCheckbox value="#{question.review}" rendered="#{delivery.previewMode ne 'true' && delivery.navigation ne '1'}" id="mark_for_review" />
<h:outputLabel for="mark_for_review" value="#{msg.mark}"
  rendered="#{delivery.previewMode ne 'true' && delivery.navigation ne '1'}" />

<h:panelGroup rendered="#{delivery.feedback eq 'true'}">
  <f:verbatim><br /></f:verbatim>

  <h:panelGroup rendered="#{delivery.feedbackComponent.showCorrectResponse && !delivery.noFeedback=='true' && question.itemData.typeId != 3}" >
    <f:verbatim><b></f:verbatim>
    <h:outputLabel for="answerKeyMC" value="#{msg.ans_key}: " />
    <f:verbatim></b></f:verbatim>
    <h:outputText id="answerKeyMC"
       value="#{question.key}" escape="false" />
    
  </h:panelGroup>

  <h:panelGroup rendered="#{delivery.feedbackComponent.showItemLevel && question.feedback ne '' && question.feedback != null && !delivery.noFeedback=='true'}">
    <f:verbatim><br /></f:verbatim>
    <f:verbatim><b></f:verbatim>
    <h:outputLabel for="feedSC" value="#{msg.feedback}: " />
    <f:verbatim></b></f:verbatim>
    <h:outputText id="feedSC" value="#{question.feedback}" escape="false" />
  </h:panelGroup>
  <h:panelGroup rendered="#{delivery.feedbackComponent.showGraderComment && question.gradingComment ne '' && question.gradingComment != null && !delivery.noFeedback=='true'}">
    <f:verbatim><br /></f:verbatim>
    <f:verbatim><b></f:verbatim>
    <h:outputLabel for="commentSC" value="#{msg.comment}: " />
    <f:verbatim></b></f:verbatim>
    <h:outputText id="commentSC" value="#{question.gradingComment}" 
      escape="false" />
  </h:panelGroup>
</h:panelGroup>