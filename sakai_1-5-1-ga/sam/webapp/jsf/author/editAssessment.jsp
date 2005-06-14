<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>

<!-- $Id: editAssessment.jsp,v 1.44 2005/02/04 19:26:34 lydial.stanford.edu Exp $ -->

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
      <title><h:outputText value="#{msg.create_modify_a}" /></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
<script language="javascript" style="text/JavaScript">
<!--
function resetSelectMenus(){
  var selectlist = document.getElementsByTagName("SELECT");

  for (var i = 0; i < selectlist.length; i++) {
        if ( selectlist[i].id.indexOf("changeQType") >=0){
          selectlist[i].value = "";
        }
  }
}

function clickInsertLink(field){
var insertlinkid= field.id.replace("changeQType", "hiddenlink");

var newindex = 0;
for (i=0; i<document.links.length; i++) {
  if(document.links[i].id == insertlinkid)
  {
    newindex = i;
    break;
  }  
}
 
document.links[newindex].onclick(); 
}
 
//-->
</script>
</head>
<body onload="document.forms[0].reset(); resetSelectMenus(); ">
<!-- content... -->
<!-- some back end stuff stubbed -->
<h:form id="assesssmentForm">
<h:messages/>
  <h:inputHidden id="assessmentId" value="#{assessmentBean.assessmentId}"/>
  <h:inputHidden id="showCompleteAssessment" value="#{author.showCompleteAssessment}"/>
  <h:inputHidden id="title" value="#{assessmentBean.title}" />
<%-- NOTE!
     add JavaScript to handle events that effect a part or question and
     set the value of these when a particular part or question is affected
     and the "current section" or "current part" needs to be changed
     other alternative maybe value changed listener
--%>
  <h:inputHidden id="SectionIdent" value="#{author.currentSection}"/>
  <h:inputHidden id="ItemIdent" value="#{author.currentItem}"/>

  <!-- HEADINGS -->
  <p class="navIntraTool">
    <h:commandLink action="author" immediate="true">
      <h:outputText value="#{msg.global_nav_assessmt}" />
    </h:commandLink>
    <h:outputText value=" | " />
    <h:commandLink action="template" immediate="true">
      <h:outputText value="#{msg.global_nav_template}" />
    </h:commandLink>
    <h:outputText value=" | " />
    <h:commandLink action="poolList" immediate="true">
      <h:outputText value="#{msg.global_nav_pools}" />
    </h:commandLink>
  </p>
  <div align="left">
    <h3>
       <h:outputText value="#{msg.qs}" />
       <h:outputText value=": " />
       <h:outputText value="#{assessmentBean.title}" />
    </h3>
  </div><div align="right">
    <h:outputText value="#{assessmentBean.questionSize} #{msg.existing_qs}" />
    <h:outputText value=" | " />
    <h:outputText value="#{assessmentBean.totalScore} #{msg.total_pt}" />
 </div>
  <p class="navModeAction">
    <h:commandLink id="insertPart" action="insertPart" immediate="true">
      <h:outputText value="#{msg.subnav_add_part}" />
      <f:param name="assessmentId" value="#{assessmentBean.assessmentId}"/>
      <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.AuthorPartListener" />
    </h:commandLink>
    <h:outputText value=" | " />
    <h:commandLink id="editAssessmentSettings" action="editAssessmentSettings" immediate="true">
      <h:outputText value="#{msg.subnav_settings}" />
      <f:param name="assessmentId" value="#{assessmentBean.assessmentId}"/>
      <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.AuthorSettingsListener" />
    </h:commandLink>
    <h:outputText value=" | " />
      <h:commandLink id="previewAssessmentQuestions" immediate="true" action="previewAssessment">
        <h:outputText value="#{msg.subnav_preview}"/>
        <f:param name="assessmentId" value="#{assessmentBean.assessmentId}"/>
        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.PreviewAssessmentListener" />
      </h:commandLink>

  </p>
   <br/>

  <h:outputText value="#{msg.add_q}"/>
<h:selectOneMenu onchange="clickInsertLink(this);"
  value="#{itemauthor.itemType}" id="changeQType">
<%--
  <f:valueChangeListener
           type="org.sakaiproject.tool.assessment.ui.listener.author.StartCreateItemListener" />
--%>

  <f:selectItem itemLabel="#{msg.select_qtype}" itemValue="" />
  <f:selectItem itemLabel="#{msg.multiple_choice_type}" itemValue="1"/>
  <f:selectItem itemLabel="#{msg.multiple_choice_surv}" itemValue="3"/>
  <f:selectItem itemLabel="#{msg.short_answer_essay}" itemValue="5"/>
  <f:selectItem itemLabel="#{msg.fill_in_the_blank}" itemValue="8"/>
  <f:selectItem itemLabel="#{msg.matching}" itemValue="9"/>
  <f:selectItem itemLabel="#{msg.true_false}" itemValue="4"/>
<%--
  <f:selectItem itemLabel="#{msg.audio_recording}" itemValue="7"/>
  <f:selectItem itemLabel="#{msg.file_upload}" itemValue="6"/>
--%>
  <f:selectItem itemLabel="#{msg.import_from_q}" itemValue="10"/>
</h:selectOneMenu>

<h:commandLink id="hiddenlink" action="#{itemauthor.doit}" value="">
  <f:actionListener
           type="org.sakaiproject.tool.assessment.ui.listener.author.StartCreateItemListener" />
</h:commandLink>

      <f:verbatim><br/></f:verbatim>
      <f:verbatim><br/></f:verbatim>
<h:dataTable border="1" id="parts" width="100%" headerClass="regHeading"
      value="#{assessmentBean.sections}" var="partBean">

 <%-- note that partBean is ui/delivery/SectionContentsBean not ui/author/SectionBean --%>
  <h:column>
    <h:panelGrid columns="2" width="100%">
      <h:panelGroup>
        <h:outputText styleClass="tier1" value="#{msg.p}" />
        <h:selectOneMenu id="number" value="#{partBean.number}" onchange="document.forms[0].submit();" >
          <f:selectItems value="#{assessmentBean.partNumbers}" />
          <f:valueChangeListener type="org.sakaiproject.tool.assessment.ui.listener.author.ReorderPartsListener" />
        </h:selectOneMenu>
        <h:outputText styleClass="tier1" value="#{partBean.title}" />
        <f:verbatim>&nbsp;- </f:verbatim>
        <h:outputText styleClass="tier1" value="#{partBean.questions}" />
        <h:outputText styleClass="tier1" value="#{msg.questions_lower_case}" />
      </h:panelGroup>
      <h:panelGroup>
<%--
        <h:commandLink id="removePart" immediate="true" action="removePart">
        <h:outputText value="#{msg.remove_part}" />
          <f:param name="sectionId" value="#{partBean.sectionId}"/>
          <f:param name="assessmentId" value="#{assessmentBean.assessmentId}"/>
          <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.RemovePartListener" />
        </h:commandLink>
--%>
        <h:commandLink action="confirmRemovePart" immediate="true">
          <h:outputText value="#{msg.remove_part}" />
          <!-- use this to set the sectionBean.sectionId in ConfirmRemovePartListener -->
          <f:param name="sectionId" value="#{partBean.sectionId}"/>
          <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.ConfirmRemovePartListener" />
        </h:commandLink>
        <f:verbatim>&nbsp;| </f:verbatim>
        <h:commandLink id="editPart" immediate="true" action="editPart">
          <h:outputText value="#{msg.edit_part}" />
          <f:param name="sectionId" value="#{partBean.sectionId}"/>
          <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.EditPartListener" />
        </h:commandLink>
      </h:panelGroup>
    </h:panelGrid>
  <h:dataTable border="1" id="parts" width="100%" headerClass="regHeading"
        value="#{partBean.itemContents}" var="question">

      <h:column>
        <h:panelGrid columns="2" border="1" width="100%">
          <h:panelGroup>
            <h:outputText styleClass="tier1" value="#{msg.q}" />
            <h:inputHidden id="currItemId" value="#{question.itemData.itemIdString}"/>
            <h:selectOneMenu id="number" onchange="document.forms[0].submit();" value="#{question.number}">
              <f:selectItems value="#{partBean.questionNumbers}" />
              <f:valueChangeListener type="org.sakaiproject.tool.assessment.ui.listener.author.ReorderQuestionsListener" />
            </h:selectOneMenu>
            <h:outputText styleClass="tier1" value="#{question.itemData.type.keyword}" />
            <h:outputText styleClass="tier1" value="#{question.itemData.score}" />
            <h:outputText styleClass="tier1" value="#{msg.points_lower_case}" />
          </h:panelGroup>
          <h:panelGroup>
            <h:commandLink styleClass="alignRight" immediate="true" id="deleteitem" action="#{itemauthor.confirmDeleteItem}">
              <h:outputText value="#{msg.button_remove}" />
              <f:param name="itemid" value="#{question.itemData.itemIdString}"/>
            </h:commandLink>
            <h:outputText value=" | " />
            <h:commandLink id="modify" action="#{itemauthor.doit}" immediate="true">
              <h:outputText value="#{msg.button_modify}" />
              <f:actionListener
                  type="org.sakaiproject.tool.assessment.ui.listener.author.ItemModifyListener" />
              <f:param name="itemid" value="#{question.itemData.itemIdString}"/>
              <f:param name="target" value="assessment"/>
            </h:commandLink>
          </h:panelGroup>
        </h:panelGrid>

        <h:panelGrid>
          <h:panelGroup rendered="#{question.itemData.typeId == 9}">
            <%@ include file="/jsf/author/preview_item/Matching.jsp" %>
          </h:panelGroup>

          <h:panelGroup rendered="#{question.itemData.typeId == 8}">
            <%@ include file="/jsf/author/preview_item/FillInTheBlank.jsp" %>
          </h:panelGroup>

          <h:panelGroup rendered="#{question.itemData.typeId == 7}">
            <%@ include file="/jsf/author/preview_item/AudioRecording.jsp" %>
          </h:panelGroup>

          <h:panelGroup rendered="#{question.itemData.typeId == 6}">
            <%@ include file="/jsf/author/preview_item/FileUpload.jsp" %>
          </h:panelGroup>

          <h:panelGroup rendered="#{question.itemData.typeId == 5}">
            <%@ include file="/jsf/author/preview_item/ShortAnswer.jsp" %>
          </h:panelGroup>

          <h:panelGroup rendered="#{question.itemData.typeId == 4}">
            <%@ include file="/jsf/author/preview_item/TrueFalse.jsp" %>
          </h:panelGroup>

          <!-- same as multiple choice single -->
          <h:panelGroup rendered="#{question.itemData.typeId == 3}">
            <%@ include file="/jsf/author/preview_item/MultipleChoiceSurvey.jsp" %>
          </h:panelGroup>

          <h:panelGroup rendered="#{question.itemData.typeId == 2}">
            <%@ include file="/jsf/author/preview_item/MultipleChoiceMultipleCorrect.jsp" %>
          </h:panelGroup>

          <h:panelGroup rendered="#{question.itemData.typeId == 1}">
            <%@ include file="/jsf/author/preview_item/MultipleChoiceSingleCorrect.jsp" %>
          </h:panelGroup>

          <h:outputText value="#{msg.ins_new_q}"/>
        </h:panelGrid>
<!-- each selectItem stores the itemtype, current sequence -->

<h:selectOneMenu id="changeQType" onchange="clickInsertLink(this);"  value="#{itemauthor.itemTypeString}" >

  <f:valueChangeListener type="org.sakaiproject.tool.assessment.ui.listener.author.StartInsertItemListener" />

  <f:selectItem itemLabel="#{msg.select_qtype}" itemValue=""/>
  <f:selectItem itemLabel="#{msg.multiple_choice_type}" itemValue="1,#{partBean.number},#{question.itemData.sequence}"/>
  <f:selectItem itemLabel="#{msg.multiple_choice_surv}" itemValue="3,#{partBean.number},#{question.itemData.sequence}"/>
  <f:selectItem itemLabel="#{msg.short_answer_essay}" itemValue="5,#{partBean.number},#{question.itemData.sequence}"/>
  <f:selectItem itemLabel="#{msg.fill_in_the_blank}" itemValue="8,#{partBean.number},#{question.itemData.sequence}"/>
  <f:selectItem itemLabel="#{msg.matching}" itemValue="9, #{partBean.number},#{question.itemData.sequence}"/>
  <f:selectItem itemLabel="#{msg.true_false}" itemValue="4,#{partBean.number},#{question.itemData.sequence}"/>
<%--
  <f:selectItem itemLabel="#{msg.audio_recording}" itemValue="7,#{partBean.number},#{question.itemData.sequence}"/>
  <f:selectItem itemLabel="#{msg.file_upload}" itemValue="6,#{partBean.number},#{question.itemData.sequence}"/>
--%>
  <f:selectItem itemLabel="#{msg.import_from_q}" itemValue="10,#{partBean.number},#{question.itemData.sequence}"/>

</h:selectOneMenu>

<h:commandLink id="hiddenlink" action="#{itemauthor.doit}" value="">
</h:commandLink>

</h:column>
</h:dataTable>
  </h:column>
</h:dataTable>



</h:form>
<!-- end content -->
      </body>
    </html>
  </f:view>

