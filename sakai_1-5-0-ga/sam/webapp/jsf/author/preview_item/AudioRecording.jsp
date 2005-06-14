<%-- $Id: AudioRecording.jsp,v 1.4 2004/12/14 19:05:58 esmiley.stanford.edu Exp $
include file for delivering audio questions
should be included in file importing DeliveryMessages
--%>
  <h:outputText escape="false" value="#{question.itemData.text}" />
  <f:verbatim><br/>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
  <h:outputLink
     value="javascript:window.open('/samigo/jsp/aam/applet/soundRecorder.jsp','ha_fullscreen','toolbar=no,location=no,directories=no,status=no,menubar=yes,'scrollbars=yes,resizable=yes,width=640,height=480');">
    <h:graphicImage id="image" alt="#{msg.audio_recording}."
       url="/images/recordresponse.gif" />
  </h:outputLink>

  <f:verbatim><br/>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
  <h:outputText escape="false" value="#{msg.time_allowed_seconds} #{question.itemData.duration}" />
  <f:verbatim><br/>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>

  <h:outputText escape="false" value="#{msg.number_of_tries}: " />
  <h:panelGroup rendered="#{question.itemData.triesAllowed > 10}">
    <h:outputText escape="false" value="Unlimited" />
  </h:panelGroup>
  <h:panelGroup rendered="#{question.itemData.triesAllowed <= 10}">
    <h:outputText escape="false" value="#{question.itemData.triesAllowed}" />
  </h:panelGroup>

  <h:dataTable value="#{question.itemData.itemTextArraySorted}" var="itemText">
    <h:column>
      <h:dataTable value="#{itemText.answerArray}" var="answer">
        <h:column>
          <h:outputText escape="false" value="#{msg.preview_model_short_answer}" />
          <h:outputText escape="false" value="#{answer.text}" />
        </h:column>
      </h:dataTable>

      <%-- question level feedback --%>
      <h:outputText escape="false" value="#{msg.q_level_feedb}:" />
      <f:verbatim><br/>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
      <h:outputText escape="false" value="#{question.itemData.generalItemFeedback}" />
    </h:column>
  </h:dataTable>

