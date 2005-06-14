
<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: audioRecording.jsp,v 1.27 2005/01/19 20:49:09 lydial.stanford.edu Exp $ -->
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
      <title><h:outputText value="#{msg.item_display_author}"/></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      </head>
<body>
<!-- content... -->
<!-- FORM -->



<!-- HEADING -->
<%@ include file="/jsf/author/item/itemHeadings.jsp" %>
<h:form id="itemForm">
<!-- QUESTION PROPERTIES -->
  <!-- 1 POINTS -->
  <div class="shorttext indnt1">
  <h:outputText styleClass="number" value="1" />
    <h:outputLabel for="answerptr" value="#{msg.answer_point_value}" />
    <h:inputText id="answerptr" value="#{itemauthor.currentItem.itemScore}" >
<f:validateDoubleRange/>
</h:inputText>
    <h:message for="answerptr" styleClass="validate"/><br/>
  <h:outputText value="  #{msg.zero_survey}" />
  </div>
  <!-- 2 TEXT -->
  <div class="longtext indnt1">
  <h:outputText styleClass="number" value="2" />
  <h:outputLabel for="qtextarea" value="#{msg.q_text}" />
  <br/>
  <!-- WYSIWYG -->
    <%--
  <h:panelGrid columns="2">
  <h:inputTextarea id="qtextarea" value="#{itemauthor.currentItem.itemText}" cols="48" rows="8"/>
  <h:outputText value="#{msg.show_hide}<br />#{msg.editor}" escape="false"/>
  </h:panelGrid>
    --%>
  <h:panelGrid width="50%">
   <samigo:wysiwyg rows="140" value="#{itemauthor.currentItem.itemText}"  >
     <f:validateLength maximum="4000"/>
   </samigo:wysiwyg>
  </h:panelGrid>
  </div>

  <!-- 3 TIME allowed -->
  <div class="longtext indnt1">
  <h:outputText styleClass="number" value="3" />
  <h:outputText value="#{msg.time_allowed_seconds}" />
  <h:outputText value="#{msg.time_allowed_seconds_indic}" />
  <h:inputText id="timeallowed" value="#{itemauthor.currentItem.timeAllowed}" required="true">
<f:validateDoubleRange/>
</h:inputText>
 <h:message for="timeallowed" styleClass="validate"/><br/>
  </div>
  <!-- 4 attempts -->
  <div class="longtext indnt1">
  <h:outputText styleClass="number" value="4" />
  <h:outputText value="#{msg.number_of_attempts}" />
  <h:outputText value="#{msg.number_of_attempts_indic}" />
  <h:selectOneMenu id="noattempts" value="#{itemauthor.currentItem.numAttempts}" required="true">
  <f:selectItem itemLabel="#{msg.select}" itemValue=""/>
  <f:selectItem itemLabel="1" itemValue="1"/>
  <f:selectItem itemLabel="2" itemValue="2"/>
  <f:selectItem itemLabel="3" itemValue="3"/>
  <f:selectItem itemLabel="4" itemValue="4"/>
  <f:selectItem itemLabel="5" itemValue="5"/>
  <f:selectItem itemLabel="6" itemValue="6"/>

  </h:selectOneMenu>
 <h:message for="noattempts" styleClass="validate"/><br/>
  </div>

  <!-- 5 PART -->
  <div class="longtext indnt1">

  <h:panelGrid rendered="#{itemauthor.target == 'assessment'}">
  <h:panelGroup>

  <h:outputText styleClass="number" value="5" />
  <h:outputText value="#{msg.assign_to_p}" />
  <h:selectOneMenu id="assignToPart" value="#{itemauthor.currentItem.selectedSection}">
     <f:selectItems  value="#{itemauthor.sectionSelectList}" />
     <!-- use this in real  value="#{section.sectionNumberList}" -->
  </h:selectOneMenu>
  </h:panelGroup>
  </h:panelGrid>

  </div>

  <!-- 6 POOL -->
  <div class="longtext indnt1">

  <h:panelGrid rendered="#{itemauthor.target == 'assessment'}">
  <h:panelGroup>

  <h:outputText styleClass="number" value="6" />
  <h:outputText value="#{msg.assign_to_question_p}" />
  <h:selectOneMenu id="assignToPool" value="#{itemauthor.currentItem.selectedPool}">
     <f:selectItem itemValue="" itemLabel="#{msg.select_a_pool_name}" />
     <f:selectItems value="#{itemauthor.poolSelectList}" />
  </h:selectOneMenu>

  </h:panelGroup>
  </h:panelGrid>

  </div>
 <!-- FEEDBACK -->
  <div class="longtext indnt1">
   <h:outputText styleClass="number" value="7" />
   <h:outputLabel for="fdbk" value="#{msg.feedback_optional}<br />" />

  <!-- WYSIWYG -->
    <%--
  <h:panelGrid columns="2">
  <h:inputTextarea id="fdbk" value="#{itemauthor.currentItem.generalFeedback}" cols="30" rows="3"/>
  <h:outputText value="#{msg.show_hide}<br />#{msg.editor}" escape="false"/>
  </h:panelGrid>
    --%>
  <h:panelGrid width="50%">
   <samigo:wysiwyg rows="140" value="#{itemauthor.currentItem.generalFeedback}"  >
     <f:validateLength maximum="4000"/>
   </samigo:wysiwyg>
  </h:panelGrid>
  </div>

 <!-- METADATA -->
<!-- METADATA -->
<div class="longtext indnt1">
  <h:panelGrid columns="3" rendered="#{itemauthor.showMetadata == 'true'}">
  <h:outputText styleClass="number" value="8" />
  <h:outputLabel for="obj" value="#{msg.objective}" />
  <h:inputText id="obj" value="#{itemauthor.currentItem.objective}" />

  <h:outputText styleClass="number" value="9" />
  <h:outputLabel for="keyword" value="#{msg.keyword}" />
  <h:inputText id="keyword" value="#{itemauthor.currentItem.keyword}" />

  <h:outputText styleClass="number" value="10" />
  <h:outputLabel for="rubric" value="#{msg.rubric_colon}" />
  <h:inputText id="rubric" value="#{itemauthor.currentItem.rubric}" />
  </h:panelGrid>

  </div>

<div class="indnt1">
<h:panelGrid columns="2" cellpadding="3" cellspacing="3">
  <h:commandButton rendered="#{itemauthor.target=='assessment'}" value="#{msg.button_save}" action="editAssessment">
        <f:actionListener
           type="org.sakaiproject.tool.assessment.ui.listener.author.ItemAddListener" />
  </h:commandButton>
  <h:commandButton rendered="#{itemauthor.target=='questionpool'}" value="#{msg.button_save}" action="editPool">
        <f:actionListener
           type="org.sakaiproject.tool.assessment.ui.listener.author.ItemAddListener" />
  </h:commandButton>


  <h:commandButton rendered="#{itemauthor.target=='assessment'}" style="act" value="#{msg.button_cancel}" action="editAssessment" immediate="true"/>
 <h:commandButton rendered="#{itemauthor.target=='questionpool'}" style="act" value="#{msg.button_cancel}" action="editPool" immediate="true"/>

</h:panelGrid>

  </div>
</h:form>


<!-- end content -->
    </body>
  </html>
</f:view>
