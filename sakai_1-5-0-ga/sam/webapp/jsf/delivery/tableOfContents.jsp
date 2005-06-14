<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: tableOfContents.jsp,v 1.15 2005/02/05 08:04:41 rgollub.stanford.edu Exp $ -->
<f:view>
  <f:verbatim><!DOCTYPE html
   PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
  </f:verbatim>
  <f:loadBundle
   basename="org.sakaiproject.tool.assessment.bundle.DeliveryMessages"
   var="msg"/>
  <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    <title><h:outputText value="#{msg.table_of_contents}" /></title>
    <samigo:stylesheet path="/css/samigo.css"/>
    <samigo:stylesheet path="/css/sam.css"/>
    <samigo:script path="/jsf/widget/hideDivision/hideDivision.js" />
    </head>
    <body onload="hideUnhideAllDivs('none');">
<!-- content... -->
<h3><h:outputText value="#{delivery.assessmentTitle} " /></h3>
<h4 class="tier1">
  <h:outputText value="#{msg.table_of_contents} " />
  <h:outputText styleClass="tier10" value="#{msg.tot_score} " />
  <!-- h:outputText value="#{delivery.tableOfContents.currentScore}/" / -->
  <h:outputText value="#{delivery.tableOfContents.maxScore} " />
  <h:outputText value="#{msg.pt}" />
</h4>

<div class="tier2">
<h:panelGrid columns="2" width="325">
  <h:outputLabel for="keyprompt">
    <h:outputText id="keyprompt"
      value="#{msg.key}"/>
  </h:outputLabel>
  <h:outputText value=""/>
  <h:graphicImage
   alt="#{msg.unans_q}"
   url="/images/tree/blank.gif" />
  <h:outputText value="#{msg.unans_q}" />
  <h:graphicImage
   alt="#{msg.q_marked}"
   url="/images/tree/marked.gif" />
  <h:outputText value="#{msg.q_marked}" />
</h:panelGrid>
</div>

<h:form id="tableOfContentsForm">
<font color="red"><h:messages/></font>
<div class="tier2">
  <h:dataTable value="#{delivery.tableOfContents.partsContents}" var="part">
  <h:column>
    <samigo:hideDivision id="hidePartDiv" title = "#{msg.p} #{part.number} - #{part.text}  -
       #{part.questions-part.unansweredQuestions}/#{part.questions} #{msg.ans_q}, #{part.maxPoints} #{msg.pt}" >
    
      <h:dataTable value="#{part.itemContents}" var="question">
      <h:column>
      <f:verbatim><h4 class="tier2"></f:verbatim>
        <h:panelGroup>
          <h:graphicImage
            alt="#{msg.unans_q}"
            url="/images/tree/blank.gif" rendered="#{question.unanswered}"/>
          <h:graphicImage
            alt="#{msg.q_marked}"
            url="/images/tree/marked.gif"  rendered="#{question.review}"/>
          <h:commandLink immediate="true" action="takeAssessment">
            <h:outputText value="#{question.sequence}. #{question.strippedText} #{question.maxPoints} #{msg.pt} " escape="false" />
            <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.delivery.DeliveryActionListener" />
            <f:param name="partnumber" value="#{part.number}" />
            <f:param name="questionnumber" value="#{question.number}" />
          </h:commandLink>
        </h:panelGroup>
       </h:column>
      </h:dataTable>
    </samigo:hideDivision>
   </h:column> 
  </h:dataTable>
</div>

<h:panelGrid columns="2" cellpadding="3" cellspacing="3">
  <h:commandButton type="submit" value="#{msg.button_submit_grading}"
    action="#{delivery.submitForGrade}">
  </h:commandButton>
  <h:commandButton type="submit" value="#{msg.button_save_x}" 
    action="#{delivery.saveAndExit}">
  </h:commandButton>
</h:panelGrid>

</h:form>
<!-- end content -->
    </body>
  </html>
</f:view>

