<html>
<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: accessError.jsp,v 1.2 2005/01/25 18:00:12 rgollub.stanford.edu Exp $ -->
  <f:view>
    <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.DeliveryMessages"
     var="msg"/>
    <f:verbatim><!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    </f:verbatim>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
      <title><h:outputText value="#{msg.access_denied}"/></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css" />
      </head>
      <body>
  <!-- content... -->
  <h3><h:outputText value="#{msg.access_denied}"/></h3>
 <h:form id="removeTemplateForm">
   <h:panelGroup>
       <f:verbatim><div class="validation"></f:verbatim>
       <h:outputText value="#{msg.password_denied}" escape="false"
         rendered="#{delivery.settings.username ne ''}" />
       <h:outputText value="#{msg.ip_denied}" escape="false"
         rendered="#{delivery.settings.username eq ''}" />
       <f:verbatim></div></f:verbatim>
   </h:panelGroup>

   <f:verbatim><p class="act"></f:verbatim>
       <h:commandButton value="#{msg.button_return}" type="submit"
         style="act" action="select" >
          <f:actionListener
            type="org.sakaiproject.tool.assessment.ui.listener.select.SelectActionListener" />
       </h:commandButton>
   <f:verbatim></p></f:verbatim>
 </h:form>
  <!-- end content -->
      </body>
    </html>
  </f:view>
</html>
