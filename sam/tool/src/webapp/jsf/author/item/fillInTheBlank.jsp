<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--
* $Id$
<%--
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
**********************************************************************************/
--%>
-->
  <f:view>
    <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.AuthorMessages"
     var="msg"/>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head><%= request.getAttribute("html.head") %>
      <title><h:outputText value="#{msg.item_display_author}"/></title>
      <samigo:script path="/js/authoring.js"/>
      </head>
      <body onload="countNum();<%= request.getAttribute("html.body.onload") %>">

<div class="portletBody">
<%-- content... --%>
<%-- FORM --%>

<%-- HEADING --%>
<%@ include file="/jsf/author/item/itemHeadings.jsp" %>
<h:form id="itemForm">
<%-- QUESTION PROPERTIES --%>
<%-- 1 POINTS --%>
<div class="indnt2">
  <span id="num1" class="number"></span>
  <div class="shorttext"><h:outputLabel for="answerptr" value="#{msg.answer_point_value}" />
  <h:inputText id="answerptr" value="#{itemauthor.currentItem.itemScore}" >
    <f:validateDoubleRange />
  </h:inputText>
 <br/>  <h:message for="answerptr" styleClass="validate"/>
 </div>
<br/>
<%-- 2 QUESTION TEXT --%>
  <span id="num2" class="number"></span>
  <div class="longtext"> <h:outputLabel for="qtextarea" value="#{msg.q_text}" />
  <br/></div>
<div class="indnt2">
  <h:outputText value="#{msg.note_place_curly}" />
  <br/>
  <h:outputText value="#{msg.for_example_curly}" />
  <br/>
  <h:outputText value="#{msg.note_insert_pipe}" />
  <br/>
  <h:outputText value="#{msg.for_example_pipe}" />
<br/>
  <h:panelGrid>
   <samigo:wysiwyg
     rows="280" value="#{itemauthor.currentItem.itemText}" >
    <f:validateLength maximum="4000"/>
   </samigo:wysiwyg>
  </h:panelGrid>
 <br />
</div>
<%-- 3 PART --%>
<h:panelGrid columns="3" columnClasses="shorttext" rendered="#{itemauthor.target == 'assessment'}">
   <f:verbatim><span id="num3" class="number"></span></f:verbatim>
  <h:outputLabel value="#{msg.assign_to_p}" />
  <h:selectOneMenu id="assignToPart" value="#{itemauthor.currentItem.selectedSection}">
     <f:selectItems  value="#{itemauthor.sectionSelectList}" />
     <%-- use this in real  value="#{section.sectionNumberList}" --%>
  </h:selectOneMenu>
</h:panelGrid>
<%-- 5 POOL --%>
<h:panelGrid columns="3" columnClasses="shorttext" rendered="#{itemauthor.target == 'assessment'}">
 <f:verbatim><span id="num4" class="number"></span></f:verbatim>
  <h:outputLabel value="#{msg.assign_to_question_p}" />
  <h:selectOneMenu id="assignToPool" value="#{itemauthor.currentItem.selectedPool}">
     <f:selectItem itemValue="" itemLabel="#{msg.select_a_pool_name}" />
     <f:selectItems value="#{itemauthor.poolSelectList}" />
  </h:selectOneMenu>
</h:panelGrid>
<%-- FEEDBACK --%>
<span id="num5" class="number"></span>
<div class="longtext">
 <h:outputLabel value="#{msg.correct_incorrect_an}" />
 <div class="indnt2">
  <h:panelGrid>
   <h:outputText value="#{msg.correct_answer_opti}" />
   <samigo:wysiwyg rows="140" value="#{itemauthor.currentItem.corrFeedback}" >
     <f:validateLength maximum="4000"/>
   </samigo:wysiwyg>
   <h:outputText value="#{msg.incorrect_answer_op}" />
   <samigo:wysiwyg rows="140" value="#{itemauthor.currentItem.incorrFeedback}" >
     <f:validateLength maximum="4000"/>
   </samigo:wysiwyg>
  </h:panelGrid>
  </div>
</div>
 <%-- METADATA --%>
<h:panelGroup rendered="#{itemauthor.showMetadata == 'true'}" styleClass="longtext">
<f:verbatim><span id="num6" class="number"></span></f:verbatim>
<h:outputLabel value="Metadata"/><br/>
<f:verbatim><div class="indnt3"></f:verbatim>

<h:panelGrid columns="2" columnClasses="shorttext">
<h:outputText value="#{msg.objective}" />
  <h:inputText size="30" id="obj" value="#{itemauthor.currentItem.objective}" />
<h:outputText value="#{msg.keyword}" />
  <h:inputText size="30" id="keyword" value="#{itemauthor.currentItem.keyword}" />
<h:outputText value="#{msg.rubric_colon}" />
  <h:inputText size="30" id="rubric" value="#{itemauthor.currentItem.rubric}" />
</h:panelGrid>
 <f:verbatim></div></f:verbatim>
</h:panelGroup>
</div>
<%-- BUTTONS --%>
<p class="act">

 <h:commandButton rendered="#{itemauthor.target=='assessment'}" value="#{msg.button_save}" action="#{itemauthor.currentItem.getOutcome}" styleClass="active">
        <f:actionListener
           type="org.sakaiproject.tool.assessment.ui.listener.author.ItemAddListener" />
  </h:commandButton>
  <h:commandButton rendered="#{itemauthor.target=='questionpool'}" value="#{msg.button_save}" action="#{itemauthor.currentItem.getPoolOutcome}" styleClass="active">
        <f:actionListener
           type="org.sakaiproject.tool.assessment.ui.listener.author.ItemAddListener" />
  </h:commandButton>
  <h:commandButton rendered="#{itemauthor.target=='assessment'}" value="#{msg.button_cancel}" action="editAssessment" immediate="true"/>
  <h:commandButton rendered="#{itemauthor.target=='questionpool'}" value="#{msg.button_cancel}" action="editPool" immediate="true"/>
</p>
</h:form>
<%-- end content --%>
</div>
    </body>
  </html>
</f:view>
