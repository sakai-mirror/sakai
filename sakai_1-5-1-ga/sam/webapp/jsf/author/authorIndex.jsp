<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: authorIndex.jsp,v 1.63.2.3 2005/03/29 01:03:30 esmiley.stanford.edu Exp $ -->
  <f:view>
    <f:verbatim><!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    </f:verbatim>
    <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.AuthorFrontDoorMessages"
     var="msg"/>
     <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.GeneralMessages"
     var="genMsg"/>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
      <title><h:outputText value="#{msg.auth_front_door}" /></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      </head>
      <body>
<!-- content... -->

<h:form id="authorIndexForm">
  <!-- HEADINGS -->
  <p class="navIntraTool">
      <h:outputText value="#{msg.global_nav_assessmt}"/>
    <h:outputText value=" | " />
    <h:commandLink action="template" immediate="true">
      <h:outputText value="#{msg.global_nav_template}" />
    </h:commandLink>
    <h:outputText value=" | " />
    <h:commandLink action="poolList" immediate="true">
      <h:outputText value="#{msg.global_nav_pools}" />
    </h:commandLink>
  </p>
  <h3>
    <h:outputText value="#{msg.assessments}"/>
  </h3>
  <h4><h:outputText value="#{msg.assessment_new}" /></h4>

  <h:panelGrid columns="3" cellpadding="3" cellspacing="3">
    <h:outputText value="#{msg.assessment_create}" styleClass="form_label" />
    <h:outputText value="" />
    <h:outputText value="" />

    <h:outputText value="#{msg.assessment_choose}" styleClass="form_label" />
    <h:panelGroup>
      <h:selectOneMenu id="assessmentTemplate"
        value="#{author.assessmentTemplateId}">
         <f:selectItem itemValue="" itemLabel="select one ..."/>
         <f:selectItems value="#{author.assessmentTemplateList}" />
      </h:selectOneMenu>

      <h:outputText value="#{msg.optional_paren}" styleClass="form_label" />
    </h:panelGroup>
    <h:outputText value="" />
    <h:panelGroup>
      <h:outputText value="#{msg.assessment_title}:" />

    </h:panelGroup>
    <h:panelGroup>
    <h:inputText id="title" value="#{author.assessTitle}" size="60" required="true">
    <!-- AuthorAssessmentListener.createAssessment() read param from AuthorBean to
      create the assessment  -->
    </h:inputText>
     <h:message for="title" styleClass="validate"/>
    </h:panelGroup>
    <h:commandButton type="submit" value="#{msg.button_create}" action="createAssessment">
      <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.AuthorAssessmentListener" />
    </h:commandButton>

    <h:outputText value="#{msg.assessment_import}" styleClass="form_label" />
    <h:outputText value="" styleClass="form_label" />
    <h:commandButton value="#{msg.button_import}" immediate="true" type="submit"

onclick=
"window.open( '../qti/_redirect_import.faces','_qti_import', 'toolbar=no,menubar=no,personalbar=no,width=650,height=375,scrollbars=no,resizable=yes');"
 />

  </h:panelGrid>

  <!-- CORE ASSESSMENTS-->
  <h4><h:outputText value="#{msg.assessment_core}" /></h4>
  <h:dataTable styleClass="listHier" id="coreAssessments" width="100%" headerClass="regHeading"
     value="#{author.assessments}" var="coreAssessment">
    <h:column>
      <f:facet name="header">
        <h:commandLink id="sortCoreByTitleAction" immediate="true" action="sort">
          <h:outputText value="#{msg.assessment_title}"/>
          <f:param name="coreOrderBy" value="title"/>
          <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.SortCoreAssessmentListener" />
        </h:commandLink>
      </f:facet>
      <h:commandLink id="editAssessment" immediate="true" action="editAssessment">
        <h:outputText id="assessmentTitle" value="#{coreAssessment.title}" />
        <f:param name="assessmentId" value="#{coreAssessment.assessmentBaseId}"/>
        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.EditAssessmentListener" />
      </h:commandLink>
      <f:verbatim><br /></f:verbatim>
      <!-- AuthorBean.editAssessmentSettings() prepare the edit page -->
      <h:commandLink id="editAssessmentSettings" immediate="true" action="editAssessmentSettings">
        <h:outputText id="linkSettings" value="#{msg.link_settings}"/>
        <f:param name="assessmentId" value="#{coreAssessment.assessmentBaseId}"/>
        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.AuthorSettingsListener" />
      </h:commandLink>
      <f:verbatim> | </f:verbatim>
      <h:commandLink id="authorAssessmentQuestions" immediate="true" action="editAssessment">
        <h:outputText id="linkQuestion" value="#{msg.link_question}"/>
        <f:param name="assessmentId" value="#{coreAssessment.assessmentBaseId}"/>
        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.EditAssessmentListener" />
      </h:commandLink>
      <f:verbatim> | </f:verbatim>
      <h:commandLink id="confirmRemoveAssessment" immediate="true" action="confirmRemoveAssessment">
        <h:outputText id="linkRemove" value="#{msg.link_remove}"/>
        <f:param name="assessmentId" value="#{coreAssessment.assessmentBaseId}"/>
        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.ConfirmRemoveAssessmentListener" />
      </h:commandLink>
      <f:verbatim> | </f:verbatim>
<%-- this version uses the same window --%>
<%--
      <h:commandLink id="exportAssessment" immediate="true" action="xmlDisplay" target="_qti_export">
        <h:outputText id="linkExport" value="#{msg.link_export}"/>
        <f:param name="assessmentId" value="#{coreAssessment.assessmentBaseId}"/>
        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.ExportAssessmentListener" />
      </h:commandLink>
--%>
<%-- this version uses a different window --%>
    <h:outputLink value="#"
      onclick=
      "window.open( '../qti/exportAssessment.faces?exportAssessmentId=#{coreAssessment.assessmentBaseId}','_qti_import', 'toolbar=no,menubar=yes,personalbar=no,width=650,height=375,scrollbars=no,resizable=no');"
       ><h:outputText id="linkExport" value="#{msg.link_export}"/>
      </h:outputLink>
    </h:column>
    <h:column>
      <f:facet name="header">
        <h:commandLink id="sortCoreByLastModifiedDateActionA" immediate="true" action="sort">
          <h:outputText value="#{msg.header_last_modified_date}" />
          <f:param name="coreOrderBy" value="lastModifiedDate"/>
          <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.SortCoreAssessmentListener" />
        </h:commandLink>
      </f:facet>
      <h:panelGrid columns="4">
        <h:outputText id="lastModifiedDate" value="#{coreAssessment.lastModifiedDate}">
         <f:convertDateTime pattern="#{genMsg.output_dateWoTime}"/>
        </h:outputText>
      </h:panelGrid>
    </h:column>
  </h:dataTable>

  <!-- PUBLISHED ASSESSMENTS-->
  <h4><h:outputText value="#{msg.assessment_pub}" /></h4>
  <!-- active -->
  <p>
<!--
  <span class="rightNav">
    <samigo:pagerButtons  formId="editTotalResults" dataTableId="myData"
      firstItem="1" lastItem="10" totalItems="50"
      prevText="Previous" nextText="Next" numItems="10" />
  </span>
-->
  </p>
  <h:outputText value="#{msg.assessment_active}" />
  <h:dataTable  styleClass="listHier" width="100%" headerClass="regHeading"
    value="#{author.publishedAssessments}" var="publishedAssessment">
    <h:column>
      <f:facet name="header">
        <h:commandLink id="sortPubByTitleAction" immediate="true" action="sort">
          <h:outputText value="#{msg.assessment_title}"/>
          <f:param name="publishedOrderBy" value="title"/>
          <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.SortPublishedAssessmentListener" />
        </h:commandLink>
      </f:facet>
      <h:outputText id="publishedAssessmentTitle" value="#{publishedAssessment.title}" />
      <f:verbatim><br /></f:verbatim>
      <h:commandLink id="editPublishedAssessmentSettings" immediate="true"
          action="editPublishedAssessmentSettings">
        <h:outputText  value="Settings" />
        <f:param name="publishedAssessmentId" value="#{publishedAssessment.publishedAssessmentId}"/>
        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.EditPublishedSettingsListener" />
      </h:commandLink>
<%-- This is a convenient link for Daisy, hide it for now
      <f:verbatim> | </f:verbatim>
      <h:commandLink id="removeAssessment" immediate="true" action="removeAssessment">
        <h:outputText id="linkRemove" value="#{msg.link_remove}"/>
        <f:param name="publishedAssessmentId" value="#{publishedAssessment.publishedAssessmentId}"/>
        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.RemovePublishedAssessmentListener" />
      </h:commandLink>
--%>
      <f:verbatim> | </f:verbatim>
      <h:commandLink action="totalScores" immediate="true" rendered="#{publishedAssessment.submissionSize >0}">
        <h:outputText value="Scores" />
        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.evaluation.TotalScoreListener" />
        <f:param name="publishedId" value="#{publishedAssessment.publishedAssessmentId}" />
      </h:commandLink>
    </h:column>

    <h:column>
      <f:facet name="header">
        <h:commandLink id="sortPubByreleaseToAction" immediate="true" action="sort">
          <h:outputText value="#{msg.assessment_release}" />
          <f:param name="publishedOrderBy" value="releaseTo"/>
          <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.SortPublishedAssessmentListener" />
        </h:commandLink>
      </f:facet>
      <h:outputText value="#{publishedAssessment.releaseTo}" >
           <f:convertDateTime pattern="#{genMsg.output_dateWoTime}"/>
      </h:outputText>
    </h:column>
    <h:column>
      <f:facet name="header">
        <h:commandLink id="sortPubByStartDateAction" immediate="true" action="sort">
          <h:outputText value="#{msg.assessment_date}" />
          <f:param name="publishedOrderBy" value="startDate"/>
          <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.SortPublishedAssessmentListener" />
        </h:commandLink>
      </f:facet>
      <h:outputText value="#{publishedAssessment.startDate}" >
          <f:convertDateTime pattern="#{genMsg.output_dateWoTime}"/>
        </h:outputText>
    </h:column>
    <h:column>
      <f:facet name="header">
        <h:commandLink id="sortPubByDueDateAction" immediate="true" action="sort">
          <h:outputText value="#{msg.assessment_due}" />
          <f:param name="publishedOrderBy" value="dueDate"/>
          <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.SortPublishedAssessmentListener" />
        </h:commandLink>
      </f:facet>
      <h:outputText value="#{publishedAssessment.dueDate}" >
          <f:convertDateTime pattern="#{genMsg.output_dateWoTime}"/>
      </h:outputText>
    </h:column>
  </h:dataTable>
  <!--inactive-->
  <p>
<!--
  <span class="rightNav">
    <samigo:pagerButtons  formId="editTotalResults" dataTableId="myData"
      firstItem="1" lastItem="10" totalItems="50"
      prevText="Previous" nextText="Next" numItems="10" />
  </span>
-->
  </p>
  <h:outputText value="#{msg.assessment_inactive}" />
  <h:dataTable  styleClass="listHier" width="100%" headerClass="regHeading"
    value="#{author.inactivePublishedAssessments}" var="inactivePublishedAssessment" id="inactivePublishedAssessments">
    <h:column>
      <f:facet name="header">
        <h:commandLink id="sortInactiveByTitleAction" immediate="true" action="sort">
          <h:outputText value="#{msg.assessment_title}"/>
          <f:param name="inactiveOrderBy" value="title"/>
          <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.SortInactivePublishedAssessmentListener" />
        </h:commandLink>
      </f:facet>
      <h:outputText id="inactivePublishedAssessmentTitle" value="#{inactivePublishedAssessment.title}" />
      <f:verbatim><br /></f:verbatim>
      <h:commandLink id="editPublishedAssessmentSettings" immediate="true"
          action="editPublishedAssessmentSettings">
        <h:outputText  value="Settings" />
        <f:param name="publishedAssessmentId" value="#{inactivePublishedAssessment.publishedAssessmentId}"/>
        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.EditPublishedSettingsListener" />
      </h:commandLink>
<%-- This is a convenient link for Daisy, hide it for now
      <f:verbatim> | </f:verbatim>
      <h:commandLink id="removeAssessment" immediate="true" action="removeAssessment">
        <h:outputText id="linkRemove" value="#{msg.link_remove}"/>
        <f:param name="publishedAssessmentId" value="#{inactivePublishedAssessment.publishedAssessmentId}"/>
        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.RemovePublishedAssessmentListener" />
      </h:commandLink>
--%>
      <f:verbatim> | </f:verbatim>
      <h:commandLink action="totalScores" immediate="true" rendered="#{inactivePublishedAssessment.submissionSize >0}">
        <h:outputText value="Scores" />
        <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.evaluation.TotalScoreListener" />
        <f:param name="publishedId" value="#{inactivePublishedAssessment.publishedAssessmentId}" />
      </h:commandLink>
    </h:column>
    <h:column>
      <f:facet name="header">
        <h:commandLink id="sortPubByreleaseToAction" immediate="true" action="sort">
          <h:outputText value="#{msg.assessment_release}" />
          <f:param name="inactiveOrderBy" value="releaseTo"/>
          <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.SortInactivePublishedAssessmentListener" />
        </h:commandLink>
      </f:facet>
      <h:outputText value="#{inactivePublishedAssessment.releaseTo}" >
          <f:convertDateTime pattern="#{genMsg.output_dateWoTime}"/>
      </h:outputText>
    </h:column>
    <h:column>
      <f:facet name="header">
        <h:commandLink id="sortInactivePubByStartDateAction" immediate="true" action="sort">
          <h:outputText value="#{msg.assessment_date}" />
          <f:param name="inactiveOrderBy" value="startDate"/>
          <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.SortInactivePublishedAssessmentListener" />
        </h:commandLink>
      </f:facet>
      <h:outputText value="#{inactivePublishedAssessment.startDate}" >
         <f:convertDateTime pattern="#{genMsg.output_dateWoTime}"/>
        </h:outputText>
    </h:column>
    <h:column>
      <f:facet name="header">
        <h:commandLink id="sortInactiveByDueDateAction" immediate="true" action="sort">
          <h:outputText value="#{msg.assessment_due}" />
          <f:param name="inactiveOrderBy" value="dueDate"/>
          <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.SortInactivePublishedAssessmentListener" />
        </h:commandLink>
      </f:facet>
      <h:outputText value="#{inactivePublishedAssessment.dueDate}" >
        <f:convertDateTime pattern="#{genMsg.output_dateWoTime}"/>
        </h:outputText>

    </h:column>
  </h:dataTable>

</h:form>
<!-- end content -->

      </body>
    </html>
  </f:view>
