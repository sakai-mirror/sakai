<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
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
      <title><h:outputText value="#{msg.invalid_assessment}"/></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      </head>
      <body>

<h:form id="redirectLoginForm">
  <h:outputText value="#{msg.anonymous_quit_warning}"/>
  <h3 style="insColor insBak"><h:outputText  value="#{msg.anonymous_quit_warning_message}" /></h3>
  <h:commandButton value="#{msg.button_return}" type="button"
     style="act" onclick="javascript:history.go(-1);" />

  <h:commandButton value="#{msg.button_quit}" type="button"
     style="act" onclick="javascript:window.open('login.faces','_top')" />

</h:form>

      </body>
    </html>
  </f:view>
