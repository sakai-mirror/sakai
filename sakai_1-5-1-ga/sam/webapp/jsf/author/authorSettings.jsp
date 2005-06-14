<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://www.sakaiproject.org/samigo" prefix="samigo" %>

<!-- $Id: authorSettings.jsp,v 1.84.2.2 2005/03/22 19:08:39 daisyf.stanford.edu Exp $ -->
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

<script language="javascript" style="text/JavaScript">
<!--
function validateUrl(){
  var list =document.getElementsByTagName("input");
  for (var i=0; i<list.length; i++){
    if (list[i].id.indexOf("finalPageUrl") >=0){
      var finalPageUrl = list[i].value;
      window.open(finalPageUrl,'validateUrl');
    }
  }
}

function validateUrl0(){
  alert("hu");
  var finalPageUrl = document.getElementsById("assessmentSettingsAction:finalPageUrl");
  alert("hello"+finalPageUrl.value);
  window.open(finalPageUrl.value,'validateUrl');
}
//-->
</script>



      </head>
    <body onload="hideUnhideAllDivsWithWysiwyg('none');">

<!-- content... -->
<h:form id="assessmentSettingsAction">
<f:verbatim><font color="red"></f:verbatim>
  <h:messages />
<f:verbatim></font></f:verbatim>

  <h:inputHidden id="assessmentId" value="#{assessmentSettings.assessmentId}"/>

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
     <h:outputText value="#{msg.settings}" />
     <h:outputText value=" - " />
     <h:outputText value="#{assessmentSettings.title}" />
    </h3>
  </div>

  <!-- *** GENERAL TEMPLATE INFORMATION *** -->
<h:panelGroup rendered="#{assessmentSettings.valueMap.templateInfo_isInstructorEditable==true
      and !assessmentSettings.noTemplate}" >
  <samigo:hideDivision id="div0" title="#{msg.heading_template_information}" >
    <h:panelGrid columns="2" >
        <h:outputText value="#{msg.template_title}"/>
        <h:outputText escape="false" value="#{assessmentSettings.templateTitle}" />
        <h:outputText value="#{msg.template_authors}"/>
        <h:outputText escape="false" value="#{assessmentSettings.templateAuthors}" />
        <h:outputText value="#{msg.template_description}"/>
        <h:outputText escape="false" value="#{assessmentSettings.templateDescription}" />
    </h:panelGrid>
  </samigo:hideDivision>
</h:panelGroup>

  <!-- *** ASSESSMENT INTRODUCTION *** -->
  <samigo:hideDivision id="div1" title="#{msg.heading_assessment_introduction}" >
    <h:panelGrid columns="2" columnClasses="indent1,tdDisplay" id="first"
      summary="#{summary_msg.enter_template_info_section}">
      <h:panelGroup>
        <h:outputText value="#{msg.assessment_title}"/>
      </h:panelGroup>
      <h:panelGroup>
        <h:inputText size="80" value="#{assessmentSettings.title}" />
      </h:panelGroup>
      <h:panelGroup>
        <h:outputText value="#{msg.assessment_creator}"/>
      </h:panelGroup>
      <h:panelGroup>
        <h:outputText value="#{assessmentSettings.creator}"
          rendered="#{assessmentSettings.valueMap.assessmentAuthor_isInstructorEditable==true}"/>
      </h:panelGroup>
      <h:panelGroup>
        <h:outputText value="#{msg.assessment_authors}"/>
      </h:panelGroup>
      <h:panelGroup>
        <h:inputText size="80" value="#{assessmentSettings.authors}"
          disabled="#{assessmentSettings.valueMap.assessmentAuthor_isInstructorEditable!=true}"/>
      </h:panelGroup>
      <h:panelGroup>
        <h:outputText value="#{msg.assessment_description}"/>
      </h:panelGroup>
      <h:panelGroup>
<%--
        <h:inputTextarea value="#{assessmentSettings.description}" cols="60"
          disabled="#{assessmentSettings.valueMap.description_isInstructorEditable!=true}"/>
--%>
<%-- TODO: DETERMINE IF WE CAN USE RENDERED --%>
       <samigo:wysiwyg rows="140" value="#{assessmentSettings.description}"  >
         <f:validateLength maximum="4000"/>
       </samigo:wysiwyg>

      </h:panelGroup>
    </h:panelGrid>
  </samigo:hideDivision>

  <!-- *** DELIVERY DATES *** -->
  <samigo:hideDivision id="div2" title="#{msg.heading_assessment_delivery_dates}">
    <h:panelGrid columns="3" columnClasses="indent1,tdDisplay"
      summary="#{summary_msg.delivery_dates_sec}">
      <h:selectBooleanCheckbox
        value="#{assessmentSettings.valueMap.hasAvailableDate}"/>
      <h:outputText value="#{msg.assessment_available_date}" />
      <samigo:datePicker value="#{assessmentSettings.startDateString}" size="25" id="startDate" >
        <f:convertDateTime pattern="MM-dd-yyyy hh:mm a" />
      </samigo:datePicker>

        <h:selectBooleanCheckbox
          rendered="#{assessmentSettings.valueMap.dueDate_isInstructorEditable==true}"
          value="#{assessmentSettings.valueMap.dueDate}"/>
        <h:outputText  rendered="#{assessmentSettings.valueMap.dueDate_isInstructorEditable==true}" value="#{msg.assessment_due_date}" />
        <h:panelGroup rendered="#{assessmentSettings.valueMap.dueDate_isInstructorEditable==true}">
          <samigo:datePicker value="#{assessmentSettings.dueDateString}" size="25" id="endDate">
            <f:convertDateTime pattern="MM-dd-yyyy hh:mm a" />
          </samigo:datePicker>
        </h:panelGroup>

        <h:selectBooleanCheckbox
          rendered="#{assessmentSettings.valueMap.retractDate_isInstructorEditable==true}"
          value="#{assessmentSettings.valueMap.hasRetractDate}"/>
        <h:outputText value="#{msg.assessment_retract_date}" rendered="#{assessmentSettings.valueMap.retractDate_isInstructorEditable==true}" />
        <h:panelGroup rendered="#{assessmentSettings.valueMap.retractDate_isInstructorEditable==true}">
          <samigo:datePicker value="#{assessmentSettings.retractDateString}" size="25" id="retractDate">
            <f:convertDateTime pattern="MM-dd-yyyy hh:mm a" />
          </samigo:datePicker>
        </h:panelGroup>
    </h:panelGrid>
  </samigo:hideDivision>

  <!-- *** RELEASED TO *** -->
<h:panelGroup rendered="#{assessmentSettings.valueMap.anonymousRelease_isInstructorEditable==true or assessmentSettings.valueMap.authenticatedRelease_isInstructorEditable==true}" >
  <samigo:hideDivision title="#{msg.heading_released_to}" id="div3">
    <h:panelGrid columnClasses="indent1,tdDisplay" summary="#{summary_msg.released_to_info_sec}">
      <h:selectManyListbox value="#{assessmentSettings.targetSelected}" size="5">
        <f:selectItems value="#{assessmentSettings.publishingTargets}" />
      </h:selectManyListbox>
    </h:panelGrid>
  </samigo:hideDivision>
</h:panelGroup>

  <!-- *** HIGH SECURITY *** -->
<h:panelGroup rendered="#{assessmentSettings.valueMap.ipAccessType_isInstructorEditable==true or assessmentSettings.valueMap.passwordRequired_isInstructorEditable==true}" >
  <samigo:hideDivision title="#{msg.heading_high_security}" id="div4">
    <h:panelGrid border="0" columns="3" columnClasses="indent1,tdDisplay"
        summary="#{summary_msg.high_security_sec}">
       <h:selectBooleanCheckbox
         rendered="#{assessmentSettings.valueMap.ipAccessType_isInstructorEditable==true}"
         value="#{assessmentSettings.valueMap.hasSpecificIP}"/>
      <h:outputText value="#{msg.high_security_allow_only_specified_ip}"
        rendered="#{assessmentSettings.valueMap.ipAccessType_isInstructorEditable==true}"/>
      <%-- no WYSIWYG for IP addresses --%>
      <h:inputTextarea value="#{assessmentSettings.ipAddresses}" cols="40" rows="5"
        rendered="#{assessmentSettings.valueMap.ipAccessType_isInstructorEditable==true}"/>
      <h:selectBooleanCheckbox
         rendered="#{assessmentSettings.valueMap.passwordRequired_isInstructorEditable==true}"
         value="#{assessmentSettings.valueMap.hasUsernamePassword}"/>
      <h:outputText value="#{msg.high_security_secondary_id_pw}"
        rendered="#{assessmentSettings.valueMap.passwordRequired_isInstructorEditable==true}"/>
      <h:panelGrid border="0" columns="2" columnClasses="indent1,tdDisplay"
        rendered="#{assessmentSettings.valueMap.passwordRequired_isInstructorEditable==true}">
        <h:outputText value="#{msg.high_security_username}"/>
        <h:inputText size="20" value="#{assessmentSettings.username}"/>

        <h:outputText value="#{msg.high_security_password}"/>
        <h:inputText size="20" value="#{assessmentSettings.password}"/>
      </h:panelGrid>
    </h:panelGrid>
  </samigo:hideDivision>
</h:panelGroup>

  <!-- *** TIMED *** -->
<h:panelGroup rendered="#{assessmentSettings.valueMap.ipAccessType_isInstructorEditable==true or assessmentSettings.valueMap.passwordRequired_isInstructorEditable==true}" >
  <samigo:hideDivision id="div5" title="#{msg.heading_timed_assessment}">
<%--DEBUGGING:
     Time Limit= <h:outputText value="#{assessmentSettings.timeLimit}" /> ;
     Hours= <h:outputText value="#{assessmentSettings.timedHours}" /> ;
     Min= <h:outputText value="#{assessmentSettings.timedMinutes}" /> ;
     hasQuestions?= <h:outputText value="#{not assessmentSettings.hasQuestions}" />
--%>
    <h:panelGrid columnClasses="indent1,tdDisplay"
        summary="#{summary_msg.timed_assmt_sec}">
      <h:panelGroup rendered="#{assessmentSettings.valueMap.timedAssessment_isInstructorEditable==true}">
        <h:selectBooleanCheckbox
         value="#{assessmentSettings.valueMap.hasTimeAssessment}"/>
        <h:outputText value="#{msg.timed_assessment}" />
        <h:selectOneMenu id="timedHours" value="#{assessmentSettings.timedHours}">
          <f:selectItems value="#{assessmentSettings.hours}" />
        </h:selectOneMenu>
        <h:outputText value="#{msg.timed_hours}." />
        <h:selectOneMenu id="timedMinutes" value="#{assessmentSettings.timedMinutes}">
          <f:selectItems value="#{assessmentSettings.mins}" />
        </h:selectOneMenu>
        <h:outputText value="#{msg.timed_minutes}." />
      </h:panelGroup>
    </h:panelGrid>
    <h:panelGrid columnClasses="indent1,tdDisplay">
      <h:panelGroup rendered="#{assessmentSettings.valueMap.timedAssessmentAutoSubmit_isInstructorEditable==true}">
       <h:selectBooleanCheckbox
         value="#{assessmentSettings.autoSubmit}"/>
        <h:outputText value="#{msg.auto_submit}" />
     </h:panelGroup>
    </h:panelGrid>
  </samigo:hideDivision>
</h:panelGroup>

  <!-- *** ASSESSMENT ORGANIZATION *** -->
<h:panelGroup rendered="#{assessmentSettings.valueMap.itemAccessType_isInstructorEditable==true or assessmentSettings.valueMap.displayChucking_isInstructorEditable==true or assessmentSettings.valueMap.displayNumbering_isInstructorEditable==true }" >
  <samigo:hideDivision id="div6" title="#{msg.heading_assessment_organization}" >
<%--     DEBUGGING:  Layout= <h:outputText value="#{assessmentSettings.assessmentFormat}" /> ;
     navigation= <h:outputText value="#{assessmentSettings.itemNavigation}" /> ;
     numbering= <h:outputText value="#{assessmentSettings.itemNumbering}" />
--%>
    <!-- NAVIGATION -->
    <h:panelGroup rendered="#{assessmentSettings.valueMap.itemAccessType_isInstructorEditable==true}">
      <h:outputText value="#{msg.navigation}" />
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="itemNavigation" value="#{assessmentSettings.itemNavigation}"  layout="pageDirection">
          <f:selectItem itemValue="1" itemLabel="#{msg.linear_access}"/>
          <f:selectItem itemValue="2" itemLabel="#{msg.random_access}"/>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>

    <!-- QUESTION LAYOUT -->
    <h:panelGroup rendered="#{assessmentSettings.valueMap.displayChunking_isInstructorEditable==true}">
      <h:outputText value="#{msg.question_layout}" />
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="assessmentFormat" value="#{assessmentSettings.assessmentFormat}"  layout="pageDirection">
          <f:selectItem itemValue="1" itemLabel="#{msg.layout_by_question}"/>
          <f:selectItem itemValue="2" itemLabel="#{msg.layout_by_part}"/>
          <f:selectItem itemValue="3" itemLabel="#{msg.layout_by_assessment}"/>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>

    <!-- NUMBERING -->
    <h:panelGroup rendered="#{assessmentSettings.valueMap.displayNumbering_isInstructorEditable==true}">
      <h:outputText value="#{msg.numbering}" />
       <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
         <h:selectOneRadio id="itemNumbering" value="#{assessmentSettings.itemNumbering}"  layout="pageDirection">
           <f:selectItem itemValue="1" itemLabel="#{msg.continous_numbering}"/>
           <f:selectItem itemValue="2" itemLabel="#{msg.part_numbering}"/>
         </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>
  </samigo:hideDivision>
</h:panelGroup>

  <!-- *** SUBMISSIONS *** -->
<h:panelGroup rendered="#{assessmentSettings.valueMap.submissionModel_isInstructorEditable==true or assessmentSettings.valueMap.lateHandling_isInstructorEditable==true or assessmentSettings.valueMap.autoSave_isInstructorEditable==true}" >
  <samigo:hideDivision id="div7" title="#{msg.heading_submissions}" >
<%--     DEBUGGING:
     Unlimited= <h:outputText value="#{assessmentSettings.unlimitedSubmissions}" /> ;
     Submissions= <h:outputText value="#{assessmentSettings.submissionsAllowed}" /> ;
     lateHandling= <h:outputText value="#{assessmentSettings.lateHandling}" />
--%>
    <!-- NUMBER OF SUBMISSIONS -->
    <h:panelGroup rendered="#{assessmentSettings.valueMap.submissionModel_isInstructorEditable==true}">
      <h:outputText value="#{msg.submissions}" />
      <f:verbatim><table><tr><td></f:verbatim>
        <h:selectOneRadio id="unlimitedSubmissions" value="#{assessmentSettings.unlimitedSubmissions}" layout="pageDirection">
          <f:selectItem itemValue="1" itemLabel="#{msg.unlimited_submission}"/>
          <f:selectItem itemValue="0" itemLabel="#{msg.only}" />
        </h:selectOneRadio>
      <f:verbatim></td><td valign="bottom"></f:verbatim>
        <h:panelGroup>
          <h:inputText size="5"  id="submissions_Allowed" value="#{assessmentSettings.submissionsAllowed}" />
          <h:outputLabel for="submissions_Allowed" value="#{msg.limited_submission}" />
        </h:panelGroup>
      <f:verbatim></td></tr></table></f:verbatim>
    </h:panelGroup>

    <!-- LATE HANDLING -->
    <h:panelGroup rendered="#{assessmentSettings.valueMap.lateHandling_isInstructorEditable==true}">
      <h:outputText value="#{msg.late_handling}" />
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="lateHandling" value="#{assessmentSettings.lateHandling}"  layout="pageDirection">
          <f:selectItem itemValue="2" itemLabel="#{msg.not_accept_latesubmission}"/>
          <f:selectItem itemValue="1" itemLabel="#{msg.accept_latesubmission}"/>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>

    <!-- AUTOSAVE -->
<%-- hide for 1.5 release SAM-148
    <h:panelGroup rendered="#{assessmentSettings.valueMap.autoSave_isInstructorEditable==true}">
      <h:outputText value="#{msg.auto_save}" />
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="autoSave" value="#{assessmentSettings.submissionsSaved}"  layout="pageDirection">
          <f:selectItem itemValue="1" itemLabel="#{msg.user_click_save}"/>
          <f:selectItem itemValue="2" itemLabel="#{msg.save_automatically}"/>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>
--%>
  </samigo:hideDivision>
</h:panelGroup>

  <!-- *** SUBMISSION MESSAGE *** -->
<h:panelGroup rendered="#{assessmentSettings.valueMap.submissionMessage_isInstructorEditable==true or assessmentSettings.valueMap.finalPageURL_isInstructorEditable==true}" >
  <samigo:hideDivision id="div8" title="#{msg.heading_submission_message}" >
    <h:panelGroup rendered="#{assessmentSettings.valueMap.submissionMessage_isInstructorEditable==true}">
      <h:outputText value="#{msg.submission_message}" />
      <f:verbatim><br/></f:verbatim>
<%--
      <h:inputTextarea cols="80" rows="5" value="#{assessmentSettings.submissionMessage}" />
--%>
<%-- TODO: DETERMINE IF WE CAN USE RENDERED --%>
       <samigo:wysiwyg rows="140" value="#{assessmentSettings.submissionMessage}"  >
         <f:validateLength maximum="4000"/>
       </samigo:wysiwyg>

    </h:panelGroup>
    <f:verbatim><br/></f:verbatim>
    <h:panelGroup rendered="#{assessmentSettings.valueMap.finalPageURL_isInstructorEditable==true}">
      <h:outputText value="#{msg.submission_final_page_url}" />
      <h:inputText size="80" id="finalPageUrl" value="#{assessmentSettings.finalPageUrl}" />
      <h:commandButton value="Validate URL" type="button" onclick="javascript:validateUrl();"/>
    </h:panelGroup>
  </samigo:hideDivision>
</h:panelGroup>

  <!-- *** FEEDBACK *** -->
<h:panelGroup rendered="#{assessmentSettings.valueMap.feedbackType_isInstructorEditable==true or assessmentSettings.valueMap.feedbackComponents_isInstructorEditable==true}" >
  <samigo:hideDivision id="div9" title="#{msg.heading_feedback}" >
    <h:outputText value="#{msg.feedback_delivery}" />
    <h:panelGroup rendered="#{assessmentSettings.valueMap.feedbackType_isInstructorEditable==true}">
      <h:panelGrid border="0" columns="1" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="feedbackDelivery" value="#{assessmentSettings.feedbackDelivery}"
           layout="pageDirection">
          <f:selectItem itemValue="1" itemLabel="#{msg.immediate_feedback}"/>
          <f:selectItem itemValue="3" itemLabel="#{msg.no_feedback}"/>
          <f:selectItem itemValue="2" itemLabel="#{msg.feedback_by_date}"/>
        </h:selectOneRadio>

      <h:panelGroup rendered="#{assessmentSettings.valueMap.feedbackType_isInstructorEditable==true}" >
        <samigo:datePicker value="#{assessmentSettings.feedbackDateString}" size="25" id="feedbackDate" >
          <f:convertDateTime pattern="MM-dd-yyyy hh:mm a" />
        </samigo:datePicker>
      </h:panelGroup>

      </h:panelGrid>
    </h:panelGroup>

    <h:panelGroup rendered="#{assessmentSettings.valueMap.feedbackComponents_isInstructorEditable==true}">
      <h:outputText value="#{msg.feedback_components}" />
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:panelGroup>
          <h:selectBooleanCheckbox value="#{assessmentSettings.showQuestionText}" disabled="true" />
          <h:outputText value="#{msg.question_text}" />
        </h:panelGroup>
        <h:panelGroup>
          <h:selectBooleanCheckbox value="#{assessmentSettings.showQuestionLevelFeedback}"/>
          <h:outputText value="#{msg.question_level_feedback}" />
        </h:panelGroup>
        <h:panelGroup>
          <h:selectBooleanCheckbox value="#{assessmentSettings.showStudentResponse}"/>
          <h:outputText value="#{msg.student_response}" />
        </h:panelGroup>
        <h:panelGroup>
          <h:selectBooleanCheckbox value="#{assessmentSettings.showSelectionLevelFeedback}"/>
          <h:outputText value="#{msg.selection_level_feedback}" />
        </h:panelGroup>
        <h:panelGroup>
          <h:selectBooleanCheckbox value="#{assessmentSettings.showCorrectResponse}"/>
          <h:outputText value="#{msg.correct_response}" />
        </h:panelGroup>
        <h:panelGroup>
          <h:selectBooleanCheckbox value="#{assessmentSettings.showStudentScore}"/>
          <h:outputText value="#{msg.student_score}" />
        </h:panelGroup>
        <h:panelGroup>
          <h:selectBooleanCheckbox value="#{assessmentSettings.showGraderComments}"/>
          <h:outputText value="#{msg.grader_comments}" />
        </h:panelGroup>
<%--
        <h:panelGroup>
          <h:selectBooleanCheckbox value="#{assessmentSettings.showStatistics}"/>
          <h:outputText value="#{msg.statistics_and_histogram}" />
        </h:panelGroup>
--%>
      </h:panelGrid>
    </h:panelGroup>
  </samigo:hideDivision>
</h:panelGroup>

  <!-- *** GRADING *** -->
<h:panelGroup rendered="#{assessmentSettings.valueMap.testeeIdentity_isInstructorEditable==true or assessmentSettings.valueMap.toGradebook_isInstructorEditable==true or assessmentSettings.valueMap.recordedScore_isInstructorEditable==true}" >
  <samigo:hideDivision id="div10" title="#{msg.heading_grading}" >
<%--     DEBUGGING:
     AnonymousGrading= <h:outputText value="#{assessmentSettings.anonymousGrading}" /> ;
--%>
    <h:outputText value="#{msg.student_identity}" />
    <h:panelGroup rendered="#{assessmentSettings.valueMap.testeeIdentity_isInstructorEditable==true}">
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="anonymousGrading" value="#{assessmentSettings.anonymousGrading}"  layout="pageDirection">
          <f:selectItem itemValue="2" itemLabel="#{msg.not_anonymous}"/>
          <f:selectItem itemValue="1" itemLabel="#{msg.anonymous}"/>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>

    <!-- GRADEBOOK OPTIONS -->
    <h:panelGroup rendered="#{assessmentSettings.valueMap.toGradebook_isInstructorEditable==true}">
      <h:outputText value="#{msg.gradebook_options}" />
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="toDefaultGradebook" value="#{assessmentSettings.toDefaultGradebook}"  layout="pageDirection">
          <f:selectItem itemValue="1" itemLabel="#{msg.to_default_gradebook}"/>
          <f:selectItem itemValue="2" itemLabel="#{msg.to_selected_gradebook}"/>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>

    <!-- RECORDED SCORE AND MULTIPLES -->
    <h:panelGroup rendered="#{assessmentSettings.valueMap.recordedScore_isInstructorEditable==true}">
      <h:outputText value="#{msg.recorded_score}" />
      <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
        <h:selectOneRadio id="scoringType" value="#{assessmentSettings.scoringType}"  layout="pageDirection">
          <f:selectItem itemValue="1" itemLabel="#{msg.highest_score}"/>
          <f:selectItem itemValue="2" itemLabel="#{msg.average_score}"/>
        </h:selectOneRadio>
      </h:panelGrid>
    </h:panelGroup>
  </samigo:hideDivision>
</h:panelGroup>

  <!-- *** COLORS AND GRAPHICS	*** -->
<h:panelGroup rendered="#{assessmentSettings.valueMap.bgColor_isInstructorEditable==true or assessmentSettings.valueMap.bgImage_isInstructorEditable==true}" >
  <samigo:hideDivision id="div11" title="#{msg.heading_graphics}" >
    <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
      <b><h:outputText value="#{msg.background_color}"
        rendered="#{assessmentSettings.valueMap.bgColor_isInstructorEditable==true}"/></b>
      <samigo:colorPicker value="#{assessmentSettings.bgColor}" size="10" id="pickColor"/>
      <b><h:outputText value="#{msg.background_image}"
        rendered="#{assessmentSettings.valueMap.bgImage_isInstructorEditable==true}"/></b>
      <h:inputText size="80" value="#{assessmentSettings.bgImage}"
        rendered="#{assessmentSettings.valueMap.bgImage_isInstructorEditable==true}"/>
    </h:panelGrid>
  </samigo:hideDivision>
</h:panelGroup>

  <!-- *** META *** -->
<h:panelGroup rendered="#{assessmentSettings.valueMap.metadataAssess_isInstructorEditable==true}">
  <samigo:hideDivision title="#{msg.heading_metadata}" id="div13">
    <h:outputText value="#{msg.assessment_metadata}" />
    <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
      <h:outputText value="#{msg.metadata_keywords}"/>
      <h:inputText size="80" value="#{assessmentSettings.keywords}"/>

      <h:outputText value="#{msg.metadata_objectives}"/>
      <h:inputText size="80" value="#{assessmentSettings.objectives}"/>

      <h:outputText value="#{msg.metadata_rubrics}"/>
      <h:inputText size="80" value="#{assessmentSettings.rubrics}"/>
    </h:panelGrid>
    <h:outputText value="#{msg.record_metadata}" />
    <h:panelGrid columns="2" columnClasses="indent1,tdDisplay">
<%-- see bug# SAM-117 -- no longer required in Samigo
     <h:selectBooleanCheckbox
       rendered="#{assessmentSettings.valueMap.metadataParts_isInstructorEditable==true}"
       value="#{assessmentSettings.valueMap.hasMetaDataForPart}"/>
     <h:outputText value="#{msg.metadata_parts}"
       rendered="#{assessmentSettings.valueMap.metadataParts_isInstructorEditable==true}"/>
--%>
     <h:selectBooleanCheckbox
       rendered="#{assessmentSettings.valueMap.metadataQuestions_isInstructorEditable==true}"
       value="#{assessmentSettings.valueMap.hasMetaDataForQuestions}"/>
     <h:outputText value="#{msg.metadata_questions}"
       rendered="#{assessmentSettings.valueMap.metadataQuestions_isInstructorEditable==true}" />
    </h:panelGrid>
  </samigo:hideDivision>
 </h:panelGroup>

  <f:verbatim><br/></f:verbatim>
  <h:commandButton  value="#{msg.button_save_and_publish}" type="submit"
      action="saveSettingsAndConfirmPublish" disabled="#{not assessmentSettings.hasQuestions}">
      <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.ConfirmPublishAssessmentListener" />
  </h:commandButton>
  <h:commandButton type="submit" value="#{msg.button_save_settings}" action="saveSettings">
      <f:actionListener type="org.sakaiproject.tool.assessment.ui.listener.author.SaveAssessmentSettingsListener" />
  </h:commandButton>
  <h:commandButton value="#{msg.button_cancel}" type="submit" action="author" />
</h:form>
<!-- end content -->
      </body>
    </html>
  </f:view>
