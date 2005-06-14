<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: reviewAssessment.jsp,v 1.1 2004/11/23 02:11:05 esmiley.stanford.edu Exp $ -->
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
      <title><h:outputText value="#{msg.review_assmt}" /></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      </head>
      <body>
<!-- content... -->
<!-- simplified somewhat on logic of displays, and no feedback yet -->
<h3><h:outputText value="#{msg.review_assmt}" /></h3>
<div class="tier1">
  <h3 style="insColor insBak">
   <h:outputText  value="#{msg.no_feedback_assessmt}"
     rendered="#{delivery.showNoFeedback}"/>
   <h:outputText  value="#{msg.feedback_avail_on}"
     rendered="#{delivery.showDateFeedback}"/>
   <h:outputText  value="This is immediate feedback."
     rendered="#{delivery.showImmediateFeedback}"/>
  </h3>
  <h:form id="reviewForm">
   <h:commandButton value="#{msg.button_return}" type="submit"
     style="act" action="select" />
  </h:form>
</div>
<!-- end content -->

      </body>
    </html>
  </f:view>

