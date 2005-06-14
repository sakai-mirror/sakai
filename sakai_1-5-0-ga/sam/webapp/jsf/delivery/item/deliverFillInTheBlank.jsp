<%-- $Id: deliverFillInTheBlank.jsp,v 1.19 2005/02/14 17:08:14 rgollub.stanford.edu Exp $
include file for delivering matching questions
should be included in file importing DeliveryMessages
--%>

<samigo:dataLine value="#{question.fibArray}" var="answer"
  separator=" " first="0" rows="100">
  <h:column>
      <h:outputText value="#{answer.text} " escape="false" />
      <h:graphicImage id="image" 
        rendered="#{delivery.feedback eq 'true' &&
                    delivery.feedbackComponent.showCorrectResponse &&
                    answer.isCorrect && answer.hasInput}"
        alt="#{msg.correct}" url="/images/delivery/green.gif">
      </h:graphicImage>
      <h:inputText size="10" rendered="#{answer.hasInput}"
         disabled="#{delivery.previewMode eq 'true'}"
         value="#{answer.response}" onkeypress="return noenter()"/>
  </h:column>
</samigo:dataLine>

<f:verbatim><br /></f:verbatim>
<h:selectBooleanCheckbox value="#{question.review}" rendered="#{delivery.previewMode ne 'true' && delivery.navigation ne '1'}" id="mark_for_review" />
<h:outputLabel for="mark_for_review" value="#{msg.mark}" 
  rendered="#{delivery.previewMode ne 'true' && delivery.navigation ne '1'}"/>

<h:panelGroup rendered="#{delivery.feedback eq 'true'}">
  <f:verbatim><br /><br /></f:verbatim>
  <h:panelGroup rendered="#{delivery.feedbackComponent.showCorrectResponse}" >
    <f:verbatim><b></f:verbatim>
    <h:outputLabel for="answerKeyMC" value="#{msg.ans_key}: " />
    <h:outputText id="answerKeyMC"
       value="#{question.key}"/>
    <f:verbatim></b></f:verbatim>
  </h:panelGroup>
  <h:panelGroup rendered="#{delivery.feedbackComponent.showItemLevel && question.feedback ne ''}">
    <f:verbatim><br /><br /></f:verbatim>
    <f:verbatim><b></f:verbatim>
    <h:outputLabel for="feedSC" value="#{msg.feedback}: " />
    <f:verbatim></b></f:verbatim>
    <h:outputText id="feedSC" value="#{question.feedback}" escape="false" />
  </h:panelGroup>
  <h:panelGroup rendered="#{delivery.feedbackComponent.showGraderComment && question.gradingComment ne ''}">
    <f:verbatim><br /><br /></f:verbatim>
    <f:verbatim><b></f:verbatim>
    <h:outputLabel for="commentSC" value="#{msg.comment}: " />
    <f:verbatim></b></f:verbatim>
    <h:outputText id="commentSC" value="#{question.gradingComment}"
      escape="false" />
  </h:panelGroup>
</h:panelGroup>
