<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: exportAssessment.jsp,v 1.6 2005/02/15 22:28:07 esmiley.stanford.edu Exp $ -->
  <f:view>
    <f:verbatim><!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    </f:verbatim>
    <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.AuthorImportExport"
     var="msg"/>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
      <title><h:outputText value="#{msg.export_a}" /></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      </head>
      <body>
 <!-- content... -->
<%--  <h:outputText  value="exportAssessmentId=#{param.exportAssessmentId}" /> --%>
 <h:form id="exportAssessmentForm">
    <h:outputText escape="false"
      value="<input type='hidden' name='assessmentId' value='#{param.exportAssessmentId}'" />
   <h3 style="insColor insBak"><h:outputText  value="#{msg.export_a}" /></h3>
   <h:panelGrid cellpadding="5" cellspacing="3">
     <f:verbatim> <div class="validation"></f:verbatim>
        <h:outputText value="#{msg.export_instructions}" escape="false" />
     <f:verbatim> </div><br /></f:verbatim>
       <%--
       Future:
       Choose version:
       VERSION_1_2 = 1;
       VERSION_2_0 = 2;
       set to true/false in resource bundle
       --%>
     <h:panelGrid columns="2" rendered="false">
       <h:outputText value="#{msg.im_ex_version_choose}"/>
       <h:selectOneRadio layout="lineDirection">
         <f:selectItem itemLabel="#{msg.im_ex_version_12}"
           itemValue="1.2"/>
         <f:selectItem itemLabel="#{msg.im_ex_version_20}"
           itemValue="2.0"/>
       </h:selectOneRadio>
     </h:panelGrid>
     <h:panelGrid columns="2" cellpadding="3" cellspacing="3">
      <h:commandButton value="#{msg.export_action}" type="submit" action="xmlDisplay"
          immediate="true" >
          <f:param name="assessmentId" value="lastModifiedDate"/>

        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.ExportAssessmentListener" />
      </h:commandButton>
       <h:commandButton value="#{msg.export_cancel_action}" type="reset"
         onclick="window.close()" style="act" action="author" />
     </h:panelGrid>
   </h:panelGrid>
 </h:form>

 <!-- end content -->
      </body>
    </html>
  </f:view>
