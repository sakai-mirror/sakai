<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: submitted.jsp,v 1.14 2005/02/08 05:42:37 daisyf.stanford.edu Exp $ -->
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
      <title><h:outputText value="#{msg.submission}" /></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      </head>
      <body>
 <!-- content... -->
<h3><h:outputText value="#{msg.submission}" /></h3>
<h4 class="tier1">
  <h:outputText value="#{delivery.assessmentTitle} " />
  <h:outputText value="#{msg.submission_info}" />
</h4>

<h:form id="submittedForm">
<font color="red"><h:messages /></font>
<div class="tier2">
  <h:panelGrid columns="1">
    <h:outputText value="#{msg.submission_confirmation_message_1}" />
    <h:outputText escape="false" value="#{delivery.submissionMessage}" />
    <h:panelGroup>
      <h:outputText value="#{msg.submission_confirmation_message_3} " rendered="#{delivery.url!=null && delivery.url!=''}" />
      <h:outputLink value="#{delivery.url}" rendered="#{delivery.url!=null && delivery.url!=''}">
        <h:outputText value="#{delivery.url}" />
      </h:outputLink>
      <h:outputText value="." rendered="#{delivery.url!=null && delivery.url!=''}" />
    </h:panelGroup>
  </h:panelGrid>
  <f:verbatim><p/></f:verbatim>
  <h:panelGrid columns="2">

    <h:outputText value="#{msg.course_name}" />
    <h:outputText value="#{delivery.courseName}" />

    <h:outputText value="#{msg.instructor}" />
    <h:outputText value="#{delivery.instructorName}" />

    <h:outputText value="#{msg.assessment_title}" />
    <h:outputText value="#{delivery.assessmentTitle}" />

    <h:outputText value="#{msg.number_of_sub_remain}" />
    <h:panelGroup>
      <h:outputText value="#{delivery.submissionsRemaining} out of #{delivery.settings.maxAttempts}"
          rendered="#{!delivery.settings.unlimitedAttempts}"/>
      <h:outputText value="#{msg.unlimited_}"
          rendered="#{delivery.settings.unlimitedAttempts}"/>
    </h:panelGroup>

    <h:outputText value="#{msg.submission_dttm}" />
    <h:outputText value="#{delivery.submissionDate}">
        <f:convertDateTime pattern="#{genMsg.output_date}" />
     </h:outputText>

    <h:outputText value="#{msg.conf_num}" />
    <h:outputText value="#{delivery.confirmation}" />

  </h:panelGrid>
</div>
<br /><br />
<div class="tier1">
  <h:panelGrid columns="2" cellpadding="3" cellspacing="3">
    <h:commandButton type="submit" value="#{msg.button_return}" action="select"/>
  </h:panelGrid>
</div>
</h:form>
  <!-- end content -->
      </body>
    </html>
  </f:view>

