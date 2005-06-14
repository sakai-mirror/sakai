<html>
<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: confirmTempRemove.jsp,v 1.10 2005/01/25 08:59:32 rgollub.stanford.edu Exp $ -->
  <f:view>
    <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.TemplateMessages"
     var="msg"/>
    <f:verbatim><!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    </f:verbatim>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
      <title><h:outputText value="#{msg.remove_heading}"/></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css" />
      </head>
      <body>
  <!-- content... -->
  <h3><h:outputText value="#{msg.remove_conf}"/></h3>
 <h:form id="removeTemplateForm">
   <h:inputHidden id="templateId" value="#{template.idString}"/>
   <h:panelGrid cellpadding="5" cellspacing="3">
     <h:panelGroup>
       <f:verbatim><div class="validation"></f:verbatim>
       <h:outputText value="#{msg.remove_fer_sure}" />
       <h:outputText value=" &quot;" />
       <h:outputText value="#{template.templateName}"/>
       <h:outputText value="&quot;?" />
       
       <f:verbatim></div></f:verbatim>
     </h:panelGroup>
     <h:panelGrid columns="2" cellpadding="3" cellspacing="3">
       <p class="act">
       <h:commandButton value="#{msg.index_button_remove}" type="submit"
         style="act" action="template" >
          <f:actionListener
            type="org.sakaiproject.tool.assessment.ui.listener.author.DeleteTemplateListener" />
       </h:commandButton>
       <h:commandButton value="#{msg.cancel}" type="submit"
         style="act" action="template" />
       </p>
     </h:panelGrid>
   </h:panelGrid>
 </h:form>
  <!-- end content -->
      </body>
    </html>
  </f:view>
</html>
