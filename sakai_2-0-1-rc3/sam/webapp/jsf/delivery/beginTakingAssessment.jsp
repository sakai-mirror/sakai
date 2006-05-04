<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!--
* $Id$
<%--
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/
--%>
-->
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
      <head><%= request.getAttribute("html.head") %>
      <title><h:outputText value="#{msg.begin_assessment_}" /></title>
      </head>
      <body onload="<%= request.getAttribute("html.body.onload") %>">

<div class="portletBody">
 <!-- content... -->

<h:form id="takeAssessmentForm">

<h:panelGroup rendered="#{delivery.previewAssessment eq 'true' && delivery.notPublished ne 'true'}">
 <f:verbatim><div class="validation"></f:verbatim>
     <h:outputText value="#{msg.ass_preview}" />
     <h:commandButton value="#{msg.done}" action="editAssessment" type="submit"/>
 <f:verbatim></div></f:verbatim>
</h:panelGroup>

<h:panelGroup rendered="#{delivery.previewAssessment eq 'true' && delivery.notPublished eq 'true'}">
 <f:verbatim><div class="validation"></f:verbatim>
     <h:outputText value="#{msg.ass_preview}" />
     <h:commandButton value="#{msg.done}" action="editAssessment">
       <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.RemovePublishedAssessmentListener" />
     </h:commandButton>
 <f:verbatim></div></f:verbatim>
</h:panelGroup>


<h3><h:outputText value="#{msg.begin_assessment_}" /></h3>
<div class="indnt1">
 <h4> <h:outputText value="#{delivery.assessmentTitle} #{msg.info} " /></h4>
<div class="indnt2">
<font color="red"><h:messages/></font>
  <h:outputText value="#{delivery.instructorMessage}" escape="false"/>
<div class="shorttext">
    <h:outputLabel value="#{msg.course}" />
    <h:outputText value="#{delivery.courseName} " />
 </div>
<div class="shorttext">
    <h:outputLabel value="#{msg.creator}" />
    <h:outputText value="#{delivery.creatorName}" />
 </div>
<div class="shorttext">
    <h:outputLabel value="#{msg.assessment_title}" />
    <h:outputText value="#{delivery.assessmentTitle}" />
</div>

 <div class="shorttext">
    <h:outputLabel value="#{msg.time_limit}" />
    <h:panelGroup rendered="#{delivery.hasTimeLimit}">
       <h:outputText value="#{delivery.timeLimit_hour} " />
       <h:outputText value="#{msg.time_limit_hour} " />
       <h:outputText value="#{delivery.timeLimit_minute} " />
       <h:outputText value="#{msg.time_limit_minute}" />
    </h:panelGroup>
    <h:panelGroup rendered="#{!delivery.hasTimeLimit}">
       <h:outputText value="No Time Limit" />
    </h:panelGroup>
   </div>
   <div class="shorttext">
    <h:outputLabel value="#{msg.num_subs}" />
           <h:outputText value="#{delivery.settings.maxAttempts} (#{delivery.submissionsRemaining} #{msg.remaining})"
          rendered="#{!delivery.settings.unlimitedAttempts}"/>
        <h:outputText value="#{msg.unlimited_}"
          rendered="#{delivery.settings.unlimitedAttempts}"/>
   </div>

   <div class="shorttext">
    <h:outputLabel value="#{msg.auto_exp}" />
        <h:outputText value="#{msg.enabled_}"
          rendered="#{delivery.settings.autoSubmit}"/>
        <h:outputText value="#{msg.disabled}"
          rendered="#{!delivery.settings.autoSubmit}"/>
   </div>

   <div class="shorttext">
    <h:outputLabel value="#{msg.feedback}" />
        <h:outputText value="#{msg.immed}"
          rendered="#{delivery.feedbackComponent.showImmediate}"/>
        <h:outputText value="#{delivery.settings.feedbackDate}"
            rendered="#{delivery.feedbackComponent.showDateFeedback}">
          <f:convertDateTime pattern="#{genMsg.output_date_picker}"/>
        </h:outputText>
        <h:outputText value="#{msg.none}"
          rendered="#{delivery.feedbackComponent.showNoFeedback}"/>
   </div>
   <div class="shorttext">
    <h:outputLabel rendered="#{delivery.dueDate!=null}" value="#{msg.due_date}" />
      <h:outputText value="#{delivery.dueDate}" >
         <f:convertDateTime pattern="#{genMsg.output_date_picker}"/>
      </h:outputText>
   </div>

   <div class="shorttext">
    <h:outputLabel value="#{msg.username}"
      rendered="#{delivery.settings.username ne ''}" />
    <h:inputText value="#{delivery.username}"
      rendered="#{delivery.settings.username ne ''}" />
   </div>
   <div class="shorttext">
    <h:outputLabel value="#{msg.password}"
      rendered="#{delivery.settings.username ne ''}" />
    <h:inputSecret value="#{delivery.password}"
      rendered="#{delivery.settings.username ne ''}" />
   </div>

 </div></div>
<br/>
<p class="act">
  <h:commandButton value="#{msg.begin_assessment_}" action="#{delivery.validate}" type="submit" styleClass="active">
    <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.delivery.DeliveryActionListener" />
  </h:commandButton>
  <h:commandButton value="#{msg.button_cancel}"  action="select" type="submit"
     rendered="#{!delivery.accessViaUrl}" disabled="#{delivery.previewAssessment eq 'true'}">
    <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
  </h:commandButton>
  <h:commandButton value="#{msg.button_cancel}" type="button"
     style="act" onclick="javascript:window.open('/portal/','_top')"
      rendered="#{delivery.accessViaUrl}" disabled="#{delivery.previewAssessment eq 'true'}"/>
</p>

<h:panelGroup rendered="#{delivery.previewAssessment eq 'true' && delivery.notPublished ne 'true'}">
 <f:verbatim><div class="validation"></f:verbatim>
     <h:outputText value="#{msg.ass_preview}" />
     <h:commandButton value="#{msg.done}" action="editAssessment" type="submit"/>
 <f:verbatim></div></f:verbatim>
</h:panelGroup>

<h:panelGroup rendered="#{delivery.previewAssessment eq 'true' && delivery.notPublished eq 'true'}">
 <f:verbatim><div class="validation"></f:verbatim>
     <h:outputText value="#{msg.ass_preview}" />
     <h:commandButton value="#{msg.done}" action="editAssessment">
       <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.RemovePublishedAssessmentListener" />
     </h:commandButton>
 <f:verbatim></div></f:verbatim>
</h:panelGroup>

</h:form>
  <!-- end content -->
</div>
      </body>
    </html>
  </f:view>
