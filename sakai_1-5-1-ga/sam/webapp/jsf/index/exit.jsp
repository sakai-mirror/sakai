<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: exit.jsp,v 1.2 2004/11/02 21:40:42 esmiley.stanford.edu Exp $ -->
  <f:view>
    <f:verbatim><!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    </f:verbatim>
    <f:loadBundle
      basename="org.sakaiproject.tool.assessment.bundle.MainIndexMessages"
      var="msg"/>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
      <title>Exit</title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      </head>
      <body>
  <!-- content... -->
  <h3 style="insColor insBak"><h:outputText  value="#{msg.button_exit}" /></h3>
  <h:outputText styleClass="validation" value="#{msg.you_have_left}" />
  <!-- end content -->
      </body>
    </html>
  </f:view>
