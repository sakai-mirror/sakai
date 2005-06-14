<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: removeQuestion.jsp,v 1.9 2005/01/18 17:37:39 rgollub.stanford.edu Exp $ -->
  <f:view>
    <f:verbatim><!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    </f:verbatim>
    <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.AuthorMessages"
     var="msg"/>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
      <title><h:outputText value="#{msg.remove_q_conf}" /></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      </head>
      <body>
  <!-- content... -->
    
 <h:form>
  <h3><h:outputText  value="#{msg.remove_q_conf}" /></h3>
 <h:panelGrid cellpadding="6" cellspacing="4">
   <h:panelGroup>
       <f:verbatim> <div class="validation"></f:verbatim>
         <h:outputText value="#{msg.sure_rem_q}" />
        <f:verbatim>  </div></f:verbatim>
  </h:panelGroup>
     <h:panelGrid columns="2" cellpadding="3" cellspacing="3">
       <h:commandButton immediate="true" value="#{msg.button_remove}" type="submit" action="#{itemauthor.deleteItem}" >
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

