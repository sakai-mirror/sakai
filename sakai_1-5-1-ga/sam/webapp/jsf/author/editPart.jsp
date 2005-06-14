<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: editPart.jsp,v 1.12 2005/02/11 00:01:05 lydial.stanford.edu Exp $ -->
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
      <title><h:outputText value="#{msg.create_modify_p}" /></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      </head>
      <body>
<!-- content... -->
<!-- some back end stuff stubbed -->
<h:form id="modifyPartForm">
  <h:inputHidden id="assessmentId" value="#{sectionBean.assessmentId}"/>
  <h:inputHidden id="sectionId" value="#{sectionBean.sectionId}"/>


  <h:panelGrid columns="3"  cellpadding="6" cellspacing="4">

   <h:panelGroup>
     <f:verbatim><h3 style="insColor insBak"></f:verbatim>
     <h:outputText value="#{msg.create_modify_p} - #{sectionBean.assessmentTitle}" />
     <f:verbatim></h3></f:verbatim>
   </h:panelGroup>
   <h:outputText value=""/>
   <h:outputText value=""/>

   <h:outputText value="#{msg.new_p}" />
   <h:outputText value=""/>

  </h:panelGrid>

<%--
  <h:panelGrid cellpadding="6" cellspacing="4">
    <h:outputText value="#{msg.note_p_headers_w}" styleClass="validation"/>
  </h:panelGrid>
--%>

  <h:panelGrid columns="3"  cellpadding="6" cellspacing="4">
   <h:outputText value="1" styleClass="number"/>
   <h:outputText value="#{msg.p_title}" />
   <h:inputText size="50" maxlength="250" value="#{sectionBean.sectionTitle}"/>

   <h:outputText value="2" styleClass="number"/>
   <h:outputText value="#{msg.p_information}" />
   <h:inputTextarea rows="10" value="#{sectionBean.sectionDescription}" cols="80" />

<%--
   <h:outputText value="3"  styleClass="number"/>
   <h:outputText value="#{msg.random_draw_from_que}" />
   <h:outputText value="" />

   <h:outputText value="" />
   <h:outputText value="#{msg.number_of_qs}" />
   <h:inputText value="#{section.numberSelected}" />

   <h:outputText value="" />
   <h:outputText value="#{msg.pool_name} " />
   <h:selectOneListbox id="pickpoolie"
     value="#{section.poolSelectItems}">
   </h:selectOneListbox>

   <h:outputText value="4"  styleClass="number"/>
   <h:outputText value="#{msg.q_ordering_n}" />
   <h:outputText value="" />

   <h:outputText value="" />
   <h:panelGroup>
     <h:selectOneRadio layout="pageDirection">
       <f:selectItem itemLabel="#{msg.as_listed_on_assessm}"
         itemValue="#{!section.random}"/>
       <f:selectItem itemLabel="#{msg.random_within_p}"
         itemValue="#{section.random}"/>
     </h:selectOneRadio>
   </h:panelGroup>
   <h:outputText value="" />
--%>
  </h:panelGrid>

   <h:panelGrid columns="2" cellpadding="3" cellspacing="3">
     <h:commandButton value="#{msg.button_save}" type="submit"
       style="act" action="editAssessment" >
        <f:actionListener
          type="org.sakaiproject.tool.assessment.ui.listener.author.SavePartListener" />
     </h:commandButton>
     <h:commandButton value="#{msg.button_cancel}" type="submit"
       style="act" action="editAssessment" />
   </h:panelGrid>

</h:form>
<!-- end content -->

      </body>
    </html>
  </f:view>

