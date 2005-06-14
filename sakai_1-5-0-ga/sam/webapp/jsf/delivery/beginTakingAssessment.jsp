<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: beginTakingAssessment.jsp,v 1.24 2005/02/15 03:29:09 daisyf.stanford.edu Exp $ -->
  <f:view>
    <f:verbatim><!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    </f:verbatim>
    <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.DeliveryMessages"
     var="msg"/>
     <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.GeneralMessages"
     var="genMsg"/>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
      <title><h:outputText value="#{msg.begin_assessment_}" /></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      </head>
      <body>
 <!-- content... -->
<h3><h:outputText value="#{msg.begin_assessment_}" /></h3>
<h4 class="tier1">
  <h:outputText value="#{delivery.assessmentTitle} " />
  <h:outputText value="#{msg.info}" />
</h4>

<h:form id="takeAssessmentForm">
<font color="red"><h:messages/></font>
<div class="tier2">
  <h:outputText value="#{delivery.instructorMessage}" escape="false"/>
  <br />
  <h:panelGrid columns="2">
    <h:outputText value="#{msg.course}" />
    <h:outputText value="#{delivery.courseName}" />

    <h:outputText value="#{msg.inst}" />
    <h:outputText value="#{delivery.creatorName}" />

    <h:outputText value="#{msg.assessment_title}" />
    <h:outputText value="#{delivery.assessmentTitle}" />

    <h:outputText value="#{msg.time_limit}" />
    <h:panelGroup rendered="#{delivery.hasTimeLimit}">
       <h:outputText value="#{delivery.timeLimit_hour} " />
       <h:outputText value="#{msg.time_limit_hour} " />
       <h:outputText value="#{delivery.timeLimit_minute} " />
       <h:outputText value="#{msg.time_limit_minute}" />
    </h:panelGroup> 
    <h:panelGroup rendered="#{!delivery.hasTimeLimit}">
       <h:outputText value="No Time Limit" />
    </h:panelGroup> 

    <h:outputText value="#{msg.num_subs}" />
      <h:panelGroup>
        <h:outputText value="#{delivery.settings.maxAttempts} (#{delivery.submissionsRemaining} #{msg.remaining})"
          rendered="#{!delivery.settings.unlimitedAttempts}"/>
        <h:outputText value="#{msg.unlimited_}"
          rendered="#{delivery.settings.unlimitedAttempts}"/>
      </h:panelGroup>

    <h:outputText value="#{msg.auto_exp}" />
      <h:panelGroup>
        <h:outputText value="#{msg.enabled_}"
          rendered="#{delivery.settings.autoSubmit}"/>
        <h:outputText value="#{msg.disabled}"
          rendered="#{!delivery.settings.autoSubmit}"/>
      </h:panelGroup>

    <h:outputText value="#{msg.feedback}" />
      <h:panelGroup>
        <h:outputText value="#{msg.immed}"
          rendered="#{delivery.feedbackComponent.showImmediate}"/>
        <h:outputText value="#{msg.ondate}"
          rendered="#{delivery.feedbackComponent.showDateFeedback}"/>
        <h:outputText value="#{msg.none}"
          rendered="#{delivery.feedbackComponent.showNoFeedback}"/>
      </h:panelGroup>

    <h:outputText value="#{msg.due_date}" />
      <h:outputText value="#{delivery.dueDate}" >
         <f:convertDateTime pattern="#{genMsg.output_date}"/>
      </h:outputText>

    <h:outputText value="#{msg.username}"
      rendered="#{delivery.settings.username ne ''}" />
    <h:inputText value="#{delivery.username}"
      rendered="#{delivery.settings.username ne ''}" />

    <h:outputText value="#{msg.password}"
      rendered="#{delivery.settings.username ne ''}" />
    <h:inputSecret value="#{delivery.password}"
      rendered="#{delivery.settings.username ne ''}" />

  </h:panelGrid>
</div>
<h:panelGrid columns="2" cellpadding="3" cellspacing="3">
  <h:commandButton value="#{msg.begin_assessment_}" action="#{delivery.validate}" type="submit">
    <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.delivery.DeliveryActionListener" />
  </h:commandButton>
  <h:commandButton value="#{msg.button_cancel}"  action="select" type="submit">
    <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
  </h:commandButton>
</h:panelGrid>
</h:form>
  <!-- end content -->
      </body>
    </html>
  </f:view>

