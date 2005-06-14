
<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: fillInTheBlank.jsp,v 1.24 2005/01/19 20:49:09 lydial.stanford.edu Exp $ -->
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
<f:validateDoubleRange />
</h:inputText>
    <h:message for="answerptr" styleClass="validate"/><br/>
  <h:outputText value="  #{msg.zero_survey}" />
  </div>
  <!-- 2 TEXT -->
  <div class="longtext indnt1">
  <h:outputText styleClass="number" value="2" />
  <h:outputLabel for="qtextarea" value="#{msg.q_text}" />
  <h:outputText value="#{msg.note_place_curly}" />
  <f:verbatim><br/></f:verbatim>
  <h:outputText value="#{msg.for_example_curly}" />
  <br/>
    <!-- PLAIN TEXTAREA -->
    <h:inputTextarea id="qtextarea_nowysiwyg" cols="48" rows="9" value="#{itemauthor.currentItem.itemText}" required="true" />
<br />
    <h:message for="qtextarea_nowysiwyg" styleClass="validate"/><br/>
<%--
    <h:panelGrid columns="2">
      <h:inputTextarea id="qtextarea" cols="48" rows="9" value="#{itemauthor.currentItem.itemText}" />
      <h:outputLink id="sh_qtextarea" value="#"
        onclick="hideUnhide(this.id.substring(0,this.id.indexOf('sh_qtextarea'))+'qtextarea');">
        <h:outputText value="#{msg.show_hide}<br />#{msg.editor}" escape="false"/>
      </h:outputLink>
    </h:panelGrid>
    <h:inputText id="qtextarea" size="150" value="#{itemauthor.currentItem.itemText}" />
--%>
  </div>

  <!-- 4 PART -->
  <div class="shorttext indnt1">
  <h:panelGrid rendered="#{itemauthor.target == 'assessment'}">
  <h:panelGroup>

  <h:outputText styleClass="number" value="4" />
  <h:outputText value="#{msg.assign_to_p}" />
  <h:selectOneMenu id="assignToPart" value="#{itemauthor.currentItem.selectedSection}">
     <f:selectItems  value="#{itemauthor.sectionSelectList}" />
     <!-- use this in real  value="#{section.sectionNumberList}" -->
  </h:selectOneMenu>
  </h:panelGroup>
  </h:panelGrid>

  </div>

  <!-- 5 POOL -->
  <div class="shorttext indnt1">
  <h:panelGrid rendered="#{itemauthor.target == 'assessment'}">
  <h:panelGroup>

  <h:outputText styleClass="number" value="5" />
  <h:outputText value="#{msg.assign_to_question_p}" />
<%-- stub debug --%>
  <h:selectOneMenu id="assignToPool" value="#{itemauthor.currentItem.selectedPool}">
     <f:selectItem itemValue="" itemLabel="#{msg.select_a_pool_name}" />
     <f:selectItems value="#{itemauthor.poolSelectList}" />
  </h:selectOneMenu>
  </h:panelGroup>
  </h:panelGrid>

  </div>
 <!-- FEEDBACK -->
  <div class="longtext indnt1">

  <h:outputText styleClass="number" value="6" />
  <h:outputText value="#{msg.correct_incorrect_an}" />

  <h:panelGrid columns="2">
  <h:outputLabel for="corrfdbk"  value="#{msg.correct_answer_opti}" />
  <h:outputLabel for="incorrfdbk" value="#{msg.incorrect_answer_op}" />

   <!-- WYSIWYG  -->
   <samigo:wysiwyg rows="140" value="#{itemauthor.currentItem.corrFeedback}" >
     <f:validateLength maximum="4000"/>
   </samigo:wysiwyg>
   
<%--
   <h:panelGrid columns="2">
     <h:inputTextarea id="corrfdbk" cols="48" rows="9" value="#{itemauthor.currentItem.corrFeedback}" />
     <h:outputLink id="sh_corrfdbk" value="#"
       onclick="hideUnhide(this.id.substring(0,this.id.indexOf('sh_corrfdbk'))+'corrfdbk');">
       <h:outputText value="#{msg.show_hide}<br />#{msg.editor}" escape="false"/>
     </h:outputLink>
   </h:panelGrid>
--%>
   <!-- WYSIWYG  -->
   <samigo:wysiwyg rows="140" value="#{itemauthor.currentItem.incorrFeedback}" >
     <f:validateLength maximum="4000"/>
   </samigo:wysiwyg>

<%--
   <h:panelGrid columns="2">
     <h:inputTextarea id="incorrfdbk" cols="48" rows="9" value="#{itemauthor.currentItem.incorrFeedback}" />
     <h:outputLink id="sh_incorrfdbk" value="#"
       onclick="hideUnhide(this.id.substring(0,this.id.indexOf('sh_incorrfdbk'))+'incorrfdbk');">
       <h:outputText value="#{msg.show_hide}<br />#{msg.editor}" escape="false"/>
     </h:outputLink>
   </h:panelGrid>
--%>

  </h:panelGrid>
  </div>


 <!-- METADATA -->
<div class="longtext indnt1">
  <h:panelGrid columns="3" rendered="#{itemauthor.showMetadata == 'true'}">
  <h:outputText styleClass="number" value="7" />
  <h:outputLabel for="obj" value="#{msg.objective}" />
  <h:inputText id="obj" value="#{itemauthor.currentItem.objective}" />

  <h:outputText styleClass="number" value="8" />
  <h:outputLabel for="keyword" value="#{msg.keyword}" />
  <h:inputText id="keyword" value="#{itemauthor.currentItem.keyword}" />

  <h:outputText styleClass="number" value="9" />
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
