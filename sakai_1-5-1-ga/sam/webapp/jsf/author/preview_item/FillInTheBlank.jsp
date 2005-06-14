<%-- $Id: FillInTheBlank.jsp,v 1.4 2004/12/17 21:52:57 daisyf.stanford.edu Exp $
include file for delivering fill in the blank questions
should be included in file importing DeliveryMessages
--%>
  <h:outputText escape="false" value="#{question.itemData.text}" />
  <h:dataTable value="#{question.itemData.itemTextArraySorted}" var="itemText">
    <h:column>
      <%-- question level feedback --%>
      <h:outputText escape="false" value="#{msg.q_level_feedb}:" />
      <f:verbatim><br/>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
      <h:outputText escape="false" value="#{msg.correct}:  #{question.itemData.correctItemFeedback}" />
      <f:verbatim><br/>&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
      <h:outputText escape="false" value="#{msg.incorrect}:  #{question.itemData.inCorrectItemFeedback}"/>
<%--
      <h:outputText escape="false" value="#{question.itemData.generalItemFeedback}" />
--%>
    </h:column>
  </h:dataTable>

