<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: publishAssessment.jsp,v 1.9.2.1 2005/02/18 01:25:51 daisyf.stanford.edu Exp $ -->
  <f:view>
    <f:verbatim><!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    </f:verbatim>
    <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.AssessmentSettingsMessages"
     var="msg"/>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
      <title><h:outputText value="#{msg.publish_assessment_confirmation}" /></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      </head>
      <body>
 <!-- content... -->
 <h:form id="publishAssessmentForm">
   <h:inputHidden id="assessmentId" value="#{assessmentSettings.assessmentId}"/>
   <h3 style="insColor insBak"><h:outputText  value="#{msg.publish_assessment_confirmation}" /></h3>
     <f:verbatim> <div class="validation"></f:verbatim>
       <h:outputText value="#{msg.publish_confirm_message}" />
     <f:verbatim> </div></f:verbatim>
   <h:panelGrid columns="2" cellpadding="5" cellspacing="3">
     <h:outputText value="#{msg.assessment_title}" />
     <h:outputText value="#{assessmentSettings.title}" />

     <h:outputText value="#{msg.assessment_available_date}" />
     <h:panelGroup rendered="#{assessmentSettings.startDate != null}">
       <h:outputText value="#{assessmentSettings.startDate}" >
         <f:convertDateTime pattern="yyyy-MMM-dd hh:mm a" />
       </h:outputText>
     </h:panelGroup>
     <h:panelGroup rendered="#{assessmentSettings.startDate == null}">
       <h:outputText value="Immediate" />
     </h:panelGroup>

     <h:outputText value="#{msg.assessment_due_date}" />
     <h:outputText value="#{assessmentSettings.dueDate}" >
       <f:convertDateTime pattern="yyyy-MMM-dd hh:mm a" />
     </h:outputText>


     <h:outputText value="#{msg.assessment_retract_date}" />
     <h:outputText value="#{assessmentSettings.retractDate}" >
       <f:convertDateTime pattern="yyyy-MMM-dd hh:mm a" />
     </h:outputText>

     <h:outputText value="#{msg.time_limit}" />
     <h:outputText rendered="#{assessmentSettings.valueMap.hasTimeAssessment eq 'true'}"
        value="#{assessmentSettings.timedHours} hour,
        #{assessmentSettings.timedMinutes} minutes, #{assessmentSettings.timedSeconds} seconds" />
     <h:outputText rendered="#{assessmentSettings.valueMap.hasTimeAssessment ne 'true'}"
        value="No Time Limit" />

     <h:outputText value="#{msg.auto_submit}" />
     <h:panelGroup>
       <h:outputText value="On" rendered="#{assessmentSettings.autoSubmit}" />
       <h:outputText value="Off" rendered="#{!assessmentSettings.autoSubmit}" />
     </h:panelGroup>

     <h:outputText value="#{msg.submissions}" />
     <h:panelGroup>
       <h:outputText value="Unlimited" rendered="#{assessmentSettings.unlimitedSubmissions eq '1'}" />
       <h:outputText value="#{assessmentSettings.submissionsAllowed}"
         rendered="#{assessmentSettings.unlimitedSubmissions eq '0'}" />
     </h:panelGroup>

     <h:outputText value="#{msg.feedback_type}" />
     <h:panelGroup>
       <h:outputText value="Immediate" rendered="#{assessmentSettings.feedbackDelivery eq '1'}" />
       <h:outputText value="No Feedback" rendered="#{assessmentSettings.feedbackDelivery eq '3'}" />
       <h:outputText value="Available on #{assessmentSettings.feedbackDate}"
          rendered="#{assessmentSettings.feedbackDelivery eq '2'}" >
         <f:convertDateTime pattern="yyyy-MMM-dd hh:mm a" />
       </h:outputText>
     </h:panelGroup>

     <h:outputText value="#{msg.published_assessment_url}" />
     <h:outputText value="#{assessmentSettings.publishedUrl}" />

     <h:outputText value="#{msg.released_to_2}" />
     <h:outputText value="#{assessmentSettings.releaseTo}" />

     <h:panelGrid columns="2" cellpadding="3" cellspacing="3">
       <h:commandButton value="#{msg.button_save_and_publish}" type="submit"
         style="act" action="publishAssessment" >
          <f:actionListener
            type="org.sakaiproject.tool.assessment.ui.listener.author.PublishAssessmentListener" />
       </h:commandButton>
       <h:commandButton value="#{msg.button_cancel}" type="submit"
         style="act" action="author" />
     </h:panelGrid>
   </h:panelGrid>
 </h:form>
 <!-- end content -->
      </body>
    </html>
  </f:view>
