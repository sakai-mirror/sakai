<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: removePart.jsp,v 1.9 2005/01/18 17:37:14 rgollub.stanford.edu Exp $ -->
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
      <title><h:outputText value="#{msg.remove_p_conf}" /></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      </head>
      <body>
 <!-- content... -->

 <h:form>
   <h3> <h:outputText  value="#{msg.remove_p_conf}" /></h3>
   <h:panelGrid cellpadding="6" cellspacing="4">
     <h:panelGroup>
      <f:verbatim><div class="validation"></f:verbatim>
     <h:outputText value="#{msg.choose_rem}" />
     <f:verbatim></div></f:verbatim>
     </h:panelGroup>
     <h:panelGrid columns="1">
       <h:selectOneRadio value="#{sectionBean.removeAllQuestions}" layout="pageDirection">
         <f:selectItem itemValue="1"
           itemLabel="#{msg.rem_p_all}" />
         <f:selectItem itemValue="0"
           itemLabel="#{msg.rem_p_only}" />
       </h:selectOneRadio>
       <h:panelGroup>
         <f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim> 
         <h:selectOneMenu id="sectionId" value="#{sectionBean.destSectionId}" >
           <f:selectItem itemValue="" itemLabel="select one ..."/>
           <f:selectItems value="#{assessmentBean.sectionList}" />
         </h:selectOneMenu>
       </h:panelGroup>
     </h:panelGrid>

     <h:panelGrid columns="2" cellpadding="3" cellspacing="3">
      <h:commandButton type="submit" value="#{msg.button_remove}" action="removePart">
        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.RemovePartListener" />
      </h:commandButton>
       <h:commandButton value="#{msg.button_cancel}" type="submit"
         style="act" action="editAssessment" />
     </h:panelGrid>
   </h:panelGrid>
 </h:form>
 <!-- end content -->
<!-- end content -->

      </body>
    </html>
  </f:view>
