<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>
<!-- $Id: publishedSettings.jsp,v 1.21.2.4 2005/03/22 19:08:39 daisyf.stanford.edu Exp $ -->
  <f:view>
    <f:verbatim><!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    </f:verbatim>
    <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.AssessmentSettingsMessages"
     var="msg"/>
  <f:loadBundle
     basename="org.sakaiproject.tool.assessment.bundle.TemplateMessages"
     var="summary_msg"/>
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
      <title><h:outputText value="#{msg.sakai_assessment_manager} - #{msg.settings}" /></title>
      <samigo:stylesheet path="/css/samigo.css"/>
      <samigo:stylesheet path="/css/sam.css"/>
      <samigo:script path="/jsf/widget/hideDivision/hideDivision.js"/>
      <samigo:script path="/jsf/widget/datepicker/datepicker.js"/>
      <samigo:script path="/jsf/widget/colorpicker/colorpicker.js"/>
      </head>
    <body onload="hideUnhideAllDivsWithWysiwyg('none');">
<!-- content... -->
<h:form id="assessmentSettingsAction">
  <h:inputHidden id="assessmentId" value="#{publishedSettings.assessmentId}"/>

  <!-- HEADINGS -->

  <p class="navIntraTool">
    <h:commandLink  action="author">
      <h:outputText value="#{msg.global_nav_assessmt}" />
    </h:commandLink>
    <h:outputText value=" | " />
    <h:commandLink  action="template">
      <h:outputText value="#{msg.global_nav_template}" />
    </h:commandLink>
    <h:outputText value=" | " />
    <h:commandLink  action="poolList">
      <h:outputText value="#{msg.global_nav_pools}" />
    </h:commandLink>
  </p>

  <div class="heading">
    <h3>
     <h:outputText id="x1"
       value="#{msg.settings} - " />
     <h:outputText value="#{publishedSettings.title}" />
    </h3>
  </div>

  <!-- *** GENERAL TEMPLATE INFORMATION *** -->
<h:panelGroup >
  <samigo:hideDivision id="div1" title="#{msg.heading_assessment_introduction}" >
    <h:panelGrid columns="2" columnClasses="indent1,tdDisplay"
      summary="#{summary_msg.enter_template_info_section}">
      <h:panelGroup>
        <h:outputText value="#{msg.assessment_title}"/>
      </h:panelGroup>
      <h:panelGroup>
        <h:inputText size="80" value="#{publishedSettings.title}"  disabled="true" />
      </h:panelGroup>
      <h:panelGroup>
        <h:outputText value="#{msg.assessment_creator}"/>
      </h:panelGroup>
      <h:panelGroup>
        <h:outputText value="#{publishedSettings.creator}"/>
      </h:panelGroup>
      <h:panelGroup>
        <h:outputText value="#{msg.assessment_authors}"/>
      </h:panelGroup>
      <h:panelGroup>
       <%-- this disabled is so weird - daisyf --%>
        <h:inputText size="80" value="#{publishedSettings.authors}" disabled="true"/>
      </h:panelGroup>
      <h:panelGroup>
        <h:outputText value="#{msg.assessment_description}"/>
      </h:panelGroup>
      <h:panelGroup>
        <h:panelGrid width="600" border="1">
          <h:outputText value="#{publishedSettings.description}<br /><br /><br />"
            escape="false"/>
        </h:panelGrid>
<%--
        <h:inputTextarea value="#{publishedSettings.description}" cols="60"
           disabled="true"/>
--%>
      </h:panelGroup>
    </h:panelGrid>
  </samigo:hideDivision>
</h:panelGroup>

  <!-- *** DELIVERY DATES *** -->
<%-- these are pretty nearly done, ran into some issues in formatting, so
commenting out for now
--%>
  <samigo:hideDivision id="div2" title="#{msg.heading_assessment_delivery_dates}" >
    <h:panelGrid columns="3" columnClasses="indent1,tdDisplay"
      summary="#{summary_msg.delivery_dates_sec}">

      <h:selectBooleanCheckbox
        value="#{publishedSettings.valueMap.hasAvailableDate}"/>
      <h:outputText value="#{msg.assessment_available_date}" />
      <samigo:datePicker value="#{publishedSettings.startDateString}" size="25" id="startDate" >
        <f:convertDateTime pattern="MM-dd-yyyy hh:mm a" />
      </samigo:datePicker>

      <h:selectBooleanCheckbox
        value="#{publishedSettings.valueMap.dueDate}"/>
      <h:outputText value="#{msg.assessment_due_date}" />
      <samigo:datePicker value="#{publishedSettings.dueDateString}" size="25" id="endDate">
        <f:convertDateTime pattern="MM-dd-yyyy hh:mm a" />
      </samigo:datePicker>

      <h:selectBooleanCheckbox
        value="#{publishedSettings.valueMap.hasRetractDate}"/>
      <h:outputText value="#{msg.assessment_retract_date}" />
      <samigo:datePicker value="#{publishedSettings.retractDateString}" size="25" id="retractDate">
        <f:convertDateTime pattern="MM-dd-yyyy hh:mm a" />
      </samigo:datePicker>
    </h:panelGrid>
  </samigo:hideDivision>

  <!-- *** RELEASED TO *** -->

  <samigo:hideDivision title="#{msg.heading_released_to}" id="div3">
    <h:panelGrid columnClasses="indent1,tdDisplay" summary="#{summary_msg.released_to_info_sec}">
      <h:selectManyListbox disabled="true" value="#{publishedSettings.targetSelected}" size="5">
        <f:selectItems value="#{assessmentSettings.publishingTargets}" />
      </h:selectManyListbox>
    </h:panelGrid>
    <h:panelGrid columns="2">
      <h:outputText value="#{msg.published_assessment_url}: " />
      <h:outputLink value="#{publishedSettings.publishedUrl}" target="newWindow">
        <h:outputText value="#{publishedSettings.publishedUrl}" />
      </h:outputLink>
    </h:panelGrid>
  </samigo:hideDivision>

  <!-- *** HIGH SECURITY *** -->
  <samigo:hideDivision title="#{msg.heading_high_security}" id="div4">
    <h:panelGrid border="0" columns="3" columnClasses="indent1,tdDisplay"
        summary="#{summary_msg.high_security_sec}">
       <h:selectBooleanCheckbox  disabled="true"/>
      <h:outputText value="#{msg.high_security_allow_only_specified_ip}" />
      <h:inputTextarea value="#{publishedSettings.ipAddresses}" cols="40" rows="5"
        disabled="true"/>
      <h:selectBooleanCheckbox  disabled="true"
         value="#{publishedSettings.valueMap.hasUsernamePassword}"/>
      <h:outputText value="#{msg.high_security_secondary_id_pw}"/>
      <h:panelGrid border="0" columns="2" columnClasses="indent1,tdDisplay">
        <h:outputText value="#{msg.high_security_username}"/>
        <h:inputText size="20" value="#{publishedSettings.username}"
          disabled="true"/>

        <h:outputText value="#{msg.high_security_password}"/>
        <h:inputText size="20" value="#{publishedSettings.password}"
          disabled="true"/>
      </h:panelGrid>
    </h:panelGrid>
  </samigo:hideDivision>


  <!-- *** TIMED *** -->
  <samigo:hideDivision id="div5" title="#{msg.heading_timed_assessment}">
<%--DEBUGGING:
     Time Limit= <h:outputText value="#{publishedSettings.timeLimit}" /> ;
     Hours= <h:outputText value="#{publishedSettings.timedHours}" /> ;
     Min= <h:outputText value="#{publishedSettings.timedMinutes}" /> ;
     hasQuestions?= <h:outputText value="#{not publishedSettings.hasQuestions}" />
--%>
    <h:panelGrid columnClasses="indent1,tdDisplay"
        summary="#{summary_msg.timed_assmt_sec}">
      <h:panelGroup>
        <h:selectBooleanCheckbox  disabled="true"
         value="#{publishedSettings.valueMap.hasTimeAssessment}"/>
        <h:outputText value="#{msg.timed_assessment}" />
        <h:selectOneMenu id="timedHours" value="#{publishedSettings.timedHours}"
          disabled="true">
          <f:selectItems value="#{publishedSettings.hours}" />
        </h:selectOneMenu>
        <h:outputText value="#{msg.timed_hours}." />
        <h:selectOneMenu id="timedMinutes" value="#{publishedSettings.timedMinutes}"
           disabled="true">
          <f:selectItems value="#{publishedSettings.mins}" />
        </h:selectOneMenu>
        <h:outputText value="#{msg.timed_minutes}." />
      </h:panelGroup>
    </h:panelGrid>
    <h:panelGrid columnClasses="indent1,tdDisplay">
      <h:panelGroup>
       <h:selectBooleanCheckbox  disabled="true"
         value="#{publishedSettings.valueMap.hasAutosubmit}"/>
        <h:outputText value="#{msg.auto_submit}" />
     </h:panelGroup>
    </h:panelGrid>
  </samigo:hideDivision>

  <!-- *** ASSESSMENT ORGANIZATION *** -->
  <samigo:hideDivision id="div6" title="#{msg.heading_assessment_organization}" >
<%--     DEBUGGING:  Layout= <h:outputText value="#{publishedSettings.assessmentFormat}" /> ;
     navigation= <h:outputText value="#{publishedSettings.itemNavigation}" /> ;
     numbering= <h:outputText value="#{publishedSettings.itemNumbering}" />
--%>
    <!-- NAVIGATION -->
    <h4 class="plain"><h:outputText value="#{msg.navigation}" /></h4>
    <h:panelGroup>
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="itemNavigation"  disabled="true"
           value="#{publishedSettings.itemNavigation}"  layout="pageDirection">
          <f:selectItem itemValue="1" itemLabel="#{msg.linear_access}"/>
          <f:selectItem itemValue="2" itemLabel="#{msg.random_access}"/>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>

    <!-- QUESTION LAYOUT -->
    <h4 class="plain"><h:outputText value="#{msg.question_layout}" /></h4>
    <h:panelGroup>
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="assessmentFormat"  disabled="true"
            value="#{publishedSettings.assessmentFormat}"  layout="pageDirection">
          <f:selectItem itemValue="1" itemLabel="#{msg.layout_by_question}"/>
          <f:selectItem itemValue="2" itemLabel="#{msg.layout_by_part}"/>
          <f:selectItem itemValue="3" itemLabel="#{msg.layout_by_assessment}"/>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>

    <!-- NUMBERING -->
    <h4 class="plain"><h:outputText value="#{msg.numbering}" /></h4>
    <h:panelGroup>
       <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
         <h:selectOneRadio id="itemNumbering"  disabled="true"
             value="#{publishedSettings.itemNumbering}"  layout="pageDirection">
           <f:selectItem itemValue="1" itemLabel="#{msg.continous_numbering}"/>
           <f:selectItem itemValue="2" itemLabel="#{msg.part_numbering}"/>
         </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>
  </samigo:hideDivision>

  <!-- *** SUBMISSIONS *** -->
  <samigo:hideDivision id="div7" title="#{msg.heading_submissions}" >
<%--     DEBUGGING:
     Unlimited= <h:outputText value="#{publishedSettings.unlimitedSubmissions}" /> ;
     Submissions= <h:outputText value="#{publishedSettings.submissionsAllowed}" /> ;
     lateHandling= <h:outputText value="#{publishedSettings.lateHandling}" />
--%>
    <!-- NUMBER OF SUBMISSIONS -->
    <h4 class="plain"><h:outputText value="#{msg.submissions}" /></h4>
    <h:panelGroup>
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="unlimitedSubmissions"  disabled="true"
            value="#{publishedSettings.unlimitedSubmissions}" layout="pageDirection">
          <f:selectItem itemValue="1" itemLabel="#{msg.unlimited_submission}"/>
          <f:selectItem itemValue="0" itemLabel="#{msg.only}" />
            <h:panelGroup>
              <h:inputText size="5"  disabled="true"
                  value="#{publishedSettings.submissionsAllowed}" />
              <h:outputText value="#{msg.limited_submission}" />
            </h:panelGroup>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>

    <!-- LATE HANDLING -->
    <h4 class="plain"><h:outputText value="#{msg.late_handling}" /></h4>
    <h:panelGroup>
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="lateHandling"  disabled="true"
            value="#{publishedSettings.lateHandling}"  layout="pageDirection">
          <f:selectItem itemValue="2" itemLabel="#{msg.not_accept_latesubmission}"/>
          <f:selectItem itemValue="1" itemLabel="#{msg.accept_latesubmission}"/>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>

    <!-- AUTOSAVE -->
<%-- hide for 1.5 release SAM-148
    <h4 class="plain"><h:outputText value="#{msg.auto_save}" /></h4>
    <h:panelGroup>
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="autoSave"  disabled="true"
            value="#{publishedSettings.submissionsSaved}"  layout="pageDirection">
          <f:selectItem itemValue="1" itemLabel="#{msg.user_click_save}"/>
          <f:selectItem itemValue="2" itemLabel="#{msg.save_automatically}"/>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>
--%>
  </samigo:hideDivision>

  <!-- *** SUBMISSION MESSAGE *** -->
  <samigo:hideDivision id="div8" title="#{msg.heading_submission_message}" >
    <h:panelGroup>
      <h:outputText value="#{msg.submission_message}" />
      <f:verbatim><br/></f:verbatim>
      <h:panelGrid width="630" border="1">
        <h:outputText value="#{publishedSettings.submissionMessage}<br /><br /><br />"
          escape="false"/>
      </h:panelGrid>
<%--
      <h:inputTextarea cols="80" rows="5"  disabled="true"
          value="#{publishedSettings.submissionMessage}" />
--%>
    </h:panelGroup>
    <f:verbatim><br/></f:verbatim>
    <h:panelGroup>
      <h:outputText value="#{msg.submission_final_page_url}" />
      <h:inputText size="80"  disabled="true" value="#{publishedSettings.finalPageUrl}" />
    </h:panelGroup>
  </samigo:hideDivision>

  <!-- *** FEEDBACK *** -->
  <samigo:hideDivision id="div9" title="#{msg.heading_feedback}" >
    <h4 class="plain"><h:outputText value="#{msg.feedback_delivery}" /></h4>
    <h:panelGroup>
      <h:panelGrid columns="1" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="feedbackDelivery"  disabled="true"
             value="#{publishedSettings.feedbackDelivery}"
           layout="pageDirection">
          <f:selectItem itemValue="1" itemLabel="#{msg.immediate_feedback}"/>
          <f:selectItem itemValue="3" itemLabel="#{msg.no_feedback}"/>
          <f:selectItem itemValue="2" itemLabel="#{msg.feedback_by_date}"/>
        </h:selectOneRadio>
        <h:panelGroup>
        <f:verbatim>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</f:verbatim>
        <samigo:datePicker value="#{publishedSettings.feedbackDateString}" size="25" id="feedbackDate" >
          <f:convertDateTime pattern="MM-dd-yyyy hh:mm a" />
        </samigo:datePicker>
        </h:panelGroup>
      </h:panelGrid>
    </h:panelGroup>

    <h4 class="plain"><h:outputText value="#{msg.feedback_components}" /></h4>
    <h:panelGroup>
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:panelGroup>
          <h:selectBooleanCheckbox  disabled="true"
              value="#{publishedSettings.showQuestionText}"/>
          <h:outputText value="#{msg.question_text}" />
        </h:panelGroup>
        <h:panelGroup>
          <h:selectBooleanCheckbox  disabled="true"
              value="#{publishedSettings.showQuestionLevelFeedback}"/>
          <h:outputText value="#{msg.question_level_feedback}" />
        </h:panelGroup>
        <h:panelGroup>
          <h:selectBooleanCheckbox  disabled="true"
              value="#{publishedSettings.showStudentResponse}"/>
          <h:outputText value="#{msg.student_response}" />
        </h:panelGroup>
        <h:panelGroup>
          <h:selectBooleanCheckbox  disabled="true"
             value="#{publishedSettings.showSelectionLevelFeedback}"/>
          <h:outputText value="#{msg.selection_level_feedback}" />
        </h:panelGroup>
        <h:panelGroup>
          <h:selectBooleanCheckbox  disabled="true"
              value="#{publishedSettings.showCorrectResponse}"/>
          <h:outputText value="#{msg.correct_response}" />
        </h:panelGroup>
        <h:panelGroup>
          <h:selectBooleanCheckbox  disabled="true"
              value="#{publishedSettings.showStudentScore}"/>
          <h:outputText value="#{msg.student_score}" />
        </h:panelGroup>
        <h:panelGroup>
          <h:selectBooleanCheckbox  disabled="true"
              value="#{publishedSettings.showGraderComments}"/>
          <h:outputText value="#{msg.grader_comments}" />
        </h:panelGroup>
        <h:panelGroup>
          <h:selectBooleanCheckbox  disabled="true"
              value="#{publishedSettings.showStatistics}"/>
          <h:outputText value="#{msg.statistics_and_histogram}" />
        </h:panelGroup>
      </h:panelGrid>
    </h:panelGroup>
  </samigo:hideDivision>

  <!-- *** GRADING *** -->
  <samigo:hideDivision id="div10" title="#{msg.heading_grading}" >
<%--     DEBUGGING:
     AnonymousGrading= <h:outputText value="#{publishedSettings.anonymousGrading}" /> ;
--%>
    <h4 class="plain"><h:outputText value="#{msg.student_identity}" /></h4>
    <h:panelGroup>
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="anonymousGrading"  disabled="true"
            value="#{publishedSettings.anonymousGrading}"  layout="pageDirection">
          <f:selectItem itemValue="2" itemLabel="#{msg.not_anonymous}"/>
          <f:selectItem itemValue="1" itemLabel="#{msg.anonymous}"/>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>

    <!-- GRADEBOOK OPTIONS -->
    <h4 class="plain"><h:outputText value="#{msg.gradebook_options}" /></h4>
    <h:panelGroup>
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="toDefaultGradebook"  disabled="true"
            value="#{publishedSettings.toDefaultGradebook}"  layout="pageDirection">
          <f:selectItem itemValue="1" itemLabel="#{msg.to_default_gradebook}"/>
          <f:selectItem itemValue="2" itemLabel="#{msg.to_selected_gradebook}"/>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>

    <!-- RECORDED SCORE AND MULTIPLES -->
    <h4 class="plain"><h:outputText value="#{msg.recorded_score}" /></h4>
    <h:panelGroup>
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="scoringType"  disabled="true"
            value="#{publishedSettings.scoringType}"  layout="pageDirection">
          <f:selectItem itemValue="1" itemLabel="#{msg.highest_score}"/>
          <f:selectItem itemValue="2" itemLabel="#{msg.average_score}"/>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>
  </samigo:hideDivision>

  <!-- *** COLORS AND GRAPHICS	*** -->
  <samigo:hideDivision id="div11" title="#{msg.heading_graphics}" >
    <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
      <b><h:outputText value="#{msg.background_color}" /></b>
      <h:inputText size="80" value="#{publishedSettings.bgColor}"
          disabled="true" />

      <b><h:outputText value="#{msg.background_image}"/></b>
      <h:inputText size="80" value="#{publishedSettings.bgImage}"
         disabled="true" />
    </h:panelGrid>
  </samigo:hideDivision>

  <!-- *** META *** -->
<h:panelGroup>
  <samigo:hideDivision title="#{msg.heading_metadata}" id="div13">
    <h:outputText value="#{msg.assessment_metadata}" />
    <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
      <h:outputText value="#{msg.metadata_keywords}"/>
      <h:inputText size="80" value="#{publishedSettings.keywords}"  disabled="true"/>

      <h:outputText value="#{msg.metadata_objectives}"/>
      <h:inputText size="80" value="#{publishedSettings.objectives}"  disabled="true"/>

      <h:outputText value="#{msg.metadata_rubrics}"/>
      <h:inputText size="80" value="#{publishedSettings.rubrics}"  disabled="true"/>
    </h:panelGrid>
    <h:outputText value="#{msg.record_metadata}" />
    <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
<%-- see bug# SAM-117 -- no longer required in Samigo
     <h:selectBooleanCheckbox  disabled="true"
       value="#{publishedSettings.valueMap.hasMetaDataForPart}"/>
     <h:outputText value="#{msg.metadata_parts}"/>
--%>
     <h:selectBooleanCheckbox disabled="true"
       value="#{publishedSettings.valueMap.hasMetaDataForQuestions}"/>
     <h:outputText value="#{msg.metadata_questions}" />
    </h:panelGrid>
  </samigo:hideDivision>
 </h:panelGroup>

  <f:verbatim><br/></f:verbatim>
  <h:commandButton  type="submit" value="#{msg.button_save_settings}" action="saveSettings">
      <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.SavePublishedSettingsListener" />
  </h:commandButton>
  <h:commandButton  value="#{msg.button_cancel}" type="submit" action="author" />
</h:form>
<!-- end content -->
      </body>
    </html>
  </f:view>
