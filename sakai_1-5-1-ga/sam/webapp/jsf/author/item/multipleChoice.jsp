<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: multipleChoice.jsp,v 1.24.2.2 2005/02/22 18:49:24 lydial.stanford.edu Exp $ -->
<%-- "checked in wysiwyg code but disabled, added in lydia's changes between 1.9 and 1.10" --%>
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
      <!-- AUTHORING -->
      <samigo:script path="/js/authoring.js"/>
      </head>
<body onload="resetInsertAnswerSelectMenus();">
<!-- content... -->
<!-- FORM -->
<!-- HEADING -->
<%@ include file="/jsf/author/item/itemHeadings.jsp" %>
<h:form id="itemForm" onsubmit="return false;">

  <!-- NOTE:  Had to call this.form.onsubmit(); when toggling between single  -->
  <!-- and multiple choice, or adding additional answer choices.  -->
  <!-- to invoke the onsubmit() function for htmlarea to save the htmlarea contents to bean -->
  <!-- otherwise, when toggleing or adding more choices, all contents in wywisyg editor are lost -->

  <!-- QUESTION PROPERTIES -->
  <!-- this is for creating multiple choice questions -->
  <!-- 1 POINTS -->
  <div class="shorttext indnt1">
  <h:outputText styleClass="number" value="1" />
    <h:outputLabel for="answerptr" value="#{msg.answer_point_value}" />
    <h:inputText id="answerptr" value="#{itemauthor.currentItem.itemScore}"required="true" >
<f:validateDoubleRange /></h:inputText>

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
   <samigo:wysiwyg rows="140" value="#{itemauthor.currentItem.itemText}" >
     <f:validateLength minimum="1" maximum="4000"/>
   </samigo:wysiwyg>
    
  </h:panelGrid>
  </div>

  <!-- 3 ANSWER -->
  <div class="longtext indnt1">
    <h:outputText styleClass="number" value="3" />
    <h:outputText value="#{msg.answer} " />

  <!-- need to add a listener, for the radio button below,to toggle between single and multiple correct-->

    <h:selectOneRadio layout="lineDirection"
		onclick="this.form.onsubmit();this.form.submit();"
           value="#{itemauthor.currentItem.multipleCorrectString}"
	valueChangeListener="#{itemauthor.currentItem.toggleChoiceTypes}" >
      <f:selectItem itemValue="1"
        itemLabel="#{msg.single}" />
      <f:selectItem itemValue="2"
        itemLabel="#{msg.multipl_mc}" />
    </h:selectOneRadio>


	<!-- dynamicaly generate rows of answers based on number of answers-->
    <h:dataTable id="mcchoices" value="#{itemauthor.currentItem.multipleChoiceAnswers}" var="answer" width="100%" headerClass="alignLeft">
      <h:column>
        <f:facet name="header">
          <h:outputText value="#{msg.correct_answer}"  />
        </f:facet>


	<!-- if multiple correct, use checkboxes -->
        <h:selectManyCheckbox value="#{itemauthor.currentItem.corrAnswers}" id="mccheckboxes"
	rendered="#{itemauthor.currentItem.multipleCorrect}">
	<f:selectItem itemValue="#{answer.label}" itemLabel="#{answer.label}"/>
        </h:selectManyCheckbox>

	<!-- if single correct, use radiobuttons -->



<h:selectOneRadio onclick="uncheckOthers(this);" id="mcradiobtn"
	layout="pageDirection"
	value="#{itemauthor.currentItem.corrAnswer}"
	rendered="#{!itemauthor.currentItem.multipleCorrect}">

	<f:selectItem itemValue="#{answer.label}" itemLabel="#{answer.label}"/>
</h:selectOneRadio>


<f:verbatim><br/></f:verbatim>
<h:commandLink id="removelink" onfocus="document.forms[1].onsubmit();" action="#{itemauthor.currentItem.removeChoices}">
  <h:outputText id="text" value="#{msg.button_remove}"/>
  <f:param name="answerid" value="#{answer.label}"/>
</h:commandLink>

        <!-- WYSIWYG -->
  <h:panelGrid width="50%">
   <samigo:wysiwyg rows="140" value="#{answer.text}" >
     <f:validateLength maximum="4000"/>
   </samigo:wysiwyg>
 
  </h:panelGrid>

<%--
        <h:inputTextarea value="#{answer.text}" cols="30" rows="5"/>
        <h:outputText value="#{msg.show_hide}<br />#{msg.editor}" escape="false"/>
--%>
      </h:column>

      <h:column>
        <f:facet name="header">
          <h:outputText value="#{msg.feedback_optional}" />
        </f:facet>
        <!-- WYSIWYG -->
<%--
        <h:inputTextarea value="#{answer.feedback}" cols="30" rows="5"/>
        <h:outputText value="#{msg.show_hide}<br />#{msg.editor}" escape="false"/>
--%>
        <f:verbatim><br/><br/><br/></f:verbatim>
        <h:panelGrid width="50%">
         <samigo:wysiwyg rows="140" value="#{answer.feedback}">
           <f:validateLength maximum="4000"/>
         </samigo:wysiwyg>

        </h:panelGrid>
      </h:column>
    </h:dataTable>

<h:inputHidden id="selectedRadioBtn" value="#{itemauthor.currentItem.corrAnswer}" />

  <h:outputText value="#{msg.insert_additional_a}" />
<h:selectOneMenu  id="insertAdditionalAnswerSelectMenu"  onchange="this.form.onsubmit(); clickAddChoiceLink();" value="#{itemauthor.currentItem.additionalChoices}" >
  <f:selectItem itemLabel="#{msg.select_menu}" itemValue="0"/>
  <f:selectItem itemLabel="1" itemValue="1"/>
  <f:selectItem itemLabel="2" itemValue="2"/>
  <f:selectItem itemLabel="3" itemValue="3"/>
  <f:selectItem itemLabel="4" itemValue="4"/>
  <f:selectItem itemLabel="5" itemValue="5"/>
  <f:selectItem itemLabel="6" itemValue="6"/>
</h:selectOneMenu>
<h:commandLink id="hiddenAddChoicelink" action="#{itemauthor.currentItem.addChoicesAction}" value="">
</h:commandLink>


  </div>

    <!-- 4 RANDOMIZE -->
  <div class="longtext indnt1">
    <h:outputText styleClass="number" value="4" />
    <h:outputText value="#{msg.randomize_answers}" />
    <h:selectOneRadio value="#{itemauthor.currentItem.randomized}" >
     <f:selectItem itemValue="true"
       itemLabel="#{msg.yes}" />
     <f:selectItem itemValue="false"
       itemLabel="#{msg.no}" />
    </h:selectOneRadio>
  </div>


    <!-- 5 RATIONALE -->
  <div class="longtext indnt1">
    <h:outputText styleClass="number" value="5" />
    <h:outputText value="#{msg.req_rationale}" />
    <h:selectOneRadio value="#{itemauthor.currentItem.rationale}" >
     <f:selectItem itemValue="true"
       itemLabel="#{msg.yes}" />
     <f:selectItem itemValue="false"
       itemLabel="#{msg.no}" />
    </h:selectOneRadio>
  </div>

    <!-- 6 PART -->
  <div class="longtext indnt1">
  <h:panelGrid rendered="#{itemauthor.target == 'assessment'}">
  <h:panelGroup>

  <h:outputText styleClass="number" value="6" />
  <h:outputText value="#{msg.assign_to_p}" />
  <h:selectOneMenu id="assignToPart" value="#{itemauthor.currentItem.selectedSection}">
     <f:selectItems  value="#{itemauthor.sectionSelectList}" />
  </h:selectOneMenu>
  </h:panelGroup>
  </h:panelGrid>

  </div>

    <!-- 7 POOL -->
<div class="longtext indnt1">
  <h:panelGrid rendered="#{itemauthor.target == 'assessment'}">
  <h:panelGroup>

  <h:outputText styleClass="number" value="7" />
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

  <h:outputText styleClass="number" value="8" />
  <h:outputText value="#{msg.correct_incorrect_an}" />

  <h:panelGrid columns="2">
  <h:outputLabel for="corrfdbk"  value="#{msg.correct_answer_opti}" />
  <h:outputLabel for="incorrfdbk" value="#{msg.incorrect_answer_op}" />

  <!-- WYSIWYG -->
    <%--
  <h:inputTextarea id="corrfdbk" value="#{itemauthor.currentItem.corrFeedback}" cols="30" rows="3"/>
  <h:outputText value="#{msg.show_hide}<br />#{msg.editor}" escape="false"/>
    --%>
   <samigo:wysiwyg rows="140" value="#{itemauthor.currentItem.corrFeedback}" >
     <f:validateLength maximum="4000"/>
   </samigo:wysiwyg>

  <!-- WYSIWYG -->
    <%--
  <h:inputTextarea id="incorrfdbk" value="#{itemauthor.currentItem.incorrFeedback}" cols="30" rows="3"/>
  <h:outputText value="#{msg.show_hide}<br />#{msg.editor}" escape="false"/>
    --%>
   <samigo:wysiwyg rows="140" value="#{itemauthor.currentItem.incorrFeedback}"  >
     <f:validateLength maximum="4000"/>
   </samigo:wysiwyg>


  </h:panelGrid>
  </div>

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

  <h:commandButton rendered="#{itemauthor.target=='assessment' && !itemauthor.currentItem.multipleCorrect}" value="#{msg.button_save}" action="#{itemauthor.currentItem.checkAnswer}" >
        <f:actionListener
           type="org.sakaiproject.tool.assessment.ui.listener.author.ItemAddListener" />
  </h:commandButton>
  <h:commandButton rendered="#{itemauthor.target=='assessment' && itemauthor.currentItem.multipleCorrect}" value="#{msg.button_save}" action="editAssessment">
        <f:actionListener
           type="org.sakaiproject.tool.assessment.ui.listener.author.ItemAddListener" />
  </h:commandButton>

  <h:commandButton rendered="#{itemauthor.target=='questionpool' && !itemauthor.currentItem.multipleCorrect}" value="#{msg.button_save}" action="#{itemauthor.currentItem.checkPoolAnswer}" >
        <f:actionListener
           type="org.sakaiproject.tool.assessment.ui.listener.author.ItemAddListener" />
  </h:commandButton>
  <h:commandButton rendered="#{itemauthor.target=='questionpool' && itemauthor.currentItem.multipleCorrect}" value="#{msg.button_save}" action="editPool">
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

