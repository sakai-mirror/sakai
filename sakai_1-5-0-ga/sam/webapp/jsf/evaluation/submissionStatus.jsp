<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
  <f:view>
    <f:verbatim><!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    </f:verbatim>
    <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.EvaluationMessages"
     var="msg"/>
    <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.GeneralMessages"
     var="genMsg"/>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
      <title><h:outputText
        value="#{msg.title_sub_stat}" /></title>
      <samigo:stylesheet path="/css/main.css"/>
      </head>
      <body>
<!-- $Id:  -->
<!-- content... -->
<h:form id="submissionStatus">

<h:panelGrid>
  <h:outputText value="#{msg.settings}" />
  <h:outputText value="Submissions: " />
  <h:selectOneRadio layout="pageDirection" value ="questionScores.allSubmissions"
    onclick="document.forms[0].submit();">
    <f:selectItem itemValue="false" itemLabel="#{msg.last_sub}"  />
    <f:selectItem itemValue="true" itemLabel="#{msg.all_sub}" />
  </h:selectOneRadio>
</h:panelGrid>

  <h:panelGrid>
    <h:panelGroup>
      <h:outputText value="#{msg.all_sub}"
        rendered="#{submissionStatus.allSubmissions}" />
      <h:outputText value="#{msg.last_sub}"
        rendered="#{!submissionStatus.allSubmissions}" />
      <h:outputText value="#{msg.forall}" />
    </h:panelGroup>

    <h:panelGroup>
      <samigo:alphaIndex initials="#{totalScores.agentInitials}"/>
    </h:panelGroup>
  </h:panelGrid>

  <!-- STUDENT STATUS -->
  <!-- controller buttons for invisible pager control -->
  <samigo:pagerButtonControl controlId="status" formId="submissionStatus" />
  <h:dataTable id="statusTable" value="#{submissionStatus.agents}" rows="10"
    var="description" headerClass="altHeading2" rowClasses="trEven,trOdd">

    <!-- NAME/SUBMISSION ID -->
    <h:column>
     <f:facet name="header">
        <h:commandLink action="submissionStatus"  id="sortByAssessmentResultId"
          rendered="#{!totalScores.sortType=='assessmentResultID'}">
          <h:outputText value="#{msg.sub_id}" rendered="#{totalScores.anonymous}"/>
        </h:commandLink>
        <h:commandLink action="submissionStatus"  id="sortByLastName"
          rendered="#{!totalScores.sortType=='lastName'}">
          <h:outputText value="#{msg.name}" rendered="#{!totalScores.anonymous}"/>
        </h:commandLink>
     </f:facet>
     <h:panelGroup>
       <!-- not sure this will work, anchor??? -->
       <h:outputText value="<a name=" escape="false" rendered="#{!totalScores.anonymous}"/>
       <h:outputText value="##{description.lastInitial}" rendered="#{!totalScores.anonymous}" />
       <h:outputText value="/>" rendered="#{!totalScores.anonymous}"/>
       <h:commandLink action="studentGrade">
         <h:outputText value="#{description.lastName}" rendered="#{!totalScores.anonymous}"/>
       </h:commandLink>
       <h:outputText value="#{description.assessmentResultID}" rendered="#{totalScores.anonymous}"/>
     </h:panelGroup>
    </h:column>

   <!-- STUDENT ID -->
    <h:column rendered="#{!totalScores.anonymous}" >
     <f:facet name="header">
       <h:commandLink action="submissionStatus"  id="sortByIdString"
          rendered="#{totalScores.sortType!='idString'}">
          <h:outputText value="#{msg.uid}" />
        </h:commandLink>
     </f:facet>
        <h:outputText value="#{description.idString}" rendered="#{!totalScores.anonymous}"/>
    </h:column>

    <!-- DATE -->
    <h:column>
     <f:facet name="header">
        <h:commandLink action="submissionStatus"  id="sortBySubmissionDate"
          rendered="#{totalScores.sortType!='submissionDate'}">
          <h:outputText value="#{msg.date}" />
        </h:commandLink>
     </f:facet>
        <h:outputText value="#{description.submissionDate}">
         <f:convertDateTime pattern="#{genMsg.output_date}"/>
        </h:outputText>
    </h:column>

  </h:dataTable>
    <!-- invisible pager control -->
  <samigo:pager controlId="status" dataTableId="statusTable"
    showpages="999" showLinks="false" styleClass="rtEven"
    selectedStyleClass="rtOdd"/>

  <h:panelGrid>
     <h:commandButton value="#{msg.done}" action="author"/>
  </h:panelGrid>
</h:form>

<!-- end content -->
    </body>
  </html>
</f:view>
