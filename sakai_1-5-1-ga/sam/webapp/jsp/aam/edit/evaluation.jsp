<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="evaluation" scope="session" class="org.navigoproject.ui.web.form.edit.EvaluationForm" />
<% int count=0; //this sets up the row counter %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Edit Evaluation Settings</title>
<link href="<%=request.getContextPath()%>/stylesheets/main.css" rel="stylesheet" type="text/css">
<%@include file="../includeJS/edit.js" %>
</head>
<logic:equal name="editorRole" value="templateEditor">
<body>
</logic:equal>
<logic:equal name="editorRole" value="assessmentEditor">
  <body onLoad="disableCheckboxes('StudentViewable');">
</logic:equal>
<p class="pageTitle">Edit Grading</p>
<img src="<%=request.getContextPath()%>/images/divider2.gif">
<p class="instructions">Specify who will do the grading of the assessment and
  what type of grading and comments can be made. Then click the Submit button
  to save your input. No input will be saved if you click the Cancel button.</p>
<p class="errors">
  <html:errors/>
</p>
<html:form action="editEvaluation.do" method="post">
  <table class="tblEdit">
    <tr>
      <td colspan="2" class="tdEditTop"><logic:equal name="editorRole" value="templateEditor"><span class="heading2"><em>Template
              Editor:</em>
          <jsp:getProperty name="description" property="templateName"/>
          </span> </logic:equal>
        <logic:equal name="editorRole" value="assessmentEditor"><span class="heading2"><em>Assessment
              Editor:</em>
          <jsp:getProperty name="description" property="name"/>
          </span></logic:equal>
      </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td class="tdEditEditView">Instructor Editable</td>
      </logic:equal>
      <td class="tdEditEditView">Student Viewable</td>
    </tr>
    <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (evaluation.getEvaluationDistribution_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
      <td class="tdEditSide"> <span class="heading2"> <bean:message key="evaluation.distribution.to" bundle="option"/> </span> </td>
      <td><% if (session.getAttribute("editorRole").equals("templateEditor") ||
           evaluation.getEvaluationDistribution_isInstructorEditable()) { %>
        <html:checkbox property="evaluationDistribution_toInstructor" />
        <bean:message key="evaluation.distribution.to.instructor" bundle="option"/><br>
        <html:checkbox property="evaluationDistribution_toTAs" />
        <bean:message key="evaluation.distribution.to.tas" bundle="option"/><br>
        <html:checkbox property="evaluationDistribution_toSectionGrader"/>
        <bean:message key="evaluation.distribution.to.section.grader" bundle="option"/><br>
        <!-- the following are for v.1 -->
        <!--
        <html:checkbox property="evaluationDistribution_toReviewGroup"/>
        <bean:message key="evaluation.distribution.to.review.group" bundle="option"/><BR>
        <html:checkbox property="evaluationDistribution_toTestee" />
        <bean:message key="evaluation.distribution.to.testee" bundle="option"/><BR>
        -->
        <% } else { %>
        <bean:write name="evaluation" property="evaluationDistribution"/>
        <% } %>
      </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td align="center"><html:checkbox property="evaluationDistribution_isInstructorEditable"/>
        </td>
      </logic:equal>
      <td align="center"><html:checkbox property="evaluationDistribution_isStudentViewable"/>
      </td>
    </tr>
    <% } %>
    <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (evaluation.getTesteeIdentity_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
      <td class="tdEditSide"><span class="heading2"> <bean:message key="evaluation.testee.identity" bundle="option"/></span> </td>
      <td><% if (session.getAttribute("editorRole").equals("templateEditor") ||
           evaluation.getTesteeIdentity_isInstructorEditable()) { %>
        <html:radio property="testeeIdentity" value="true"/>
        <bean:message key="evaluation.testee.identity.true" bundle="option"/><BR>
        <html:radio property="testeeIdentity" value="false"/>
        <bean:message key="evaluation.testee.identity.false" bundle="option"/><BR>
        <% } else { %>
        <bean:write name="evaluation" property="testeeIdentity"/>
        <% } %>
      </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td align="center"><html:checkbox property="testeeIdentity_isInstructorEditable"/>
        </td>
      </logic:equal>
      <td align="center"><html:checkbox property="testeeIdentity_isStudentViewable"/>
      </td>
    </tr>
    <% } %>
    <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (evaluation.getEvaluationComponents_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
      <td class="tdEditSide"> <span class="heading2"> <bean:message key="evaluation.components" bundle="option"/> </span> </td>
      <td>
        <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           evaluation.getEvaluationComponents_isInstructorEditable()) { %>
        <html:checkbox property="evaluationComponents_scores"/>
        <bean:message key="evaluation.components.scores" bundle="option"/><br>
        <html:checkbox property="evaluationComponents_commentsForQuestions" />
        <bean:message key="evaluation.components.comments.for.questions"
          bundle="option"/><br>
        <html:checkbox property="evaluationComponents_commentsForParts" />
        <bean:message key="evaluation.components.comments.for.parts"
          bundle="option"/><br>
        <html:checkbox property="evaluationComponents_commentForAssess" />
        <bean:message key="evaluation.components.comment.for.assess"
          bundle="option"/><br>
        <% } else { %>
        <bean:write name="evaluation" property="evaluationComponents"/>
        <% } %>
      </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td align="center"><html:checkbox property="evaluationComponents_isInstructorEditable"/>
        </td>
      </logic:equal>
      <td align="center"><html:checkbox property="evaluationComponents_isStudentViewable"/>
      </td>
    </tr>
    <% } %>
<!-- Automatic Grading section-->
    <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (evaluation.getAutoScoring_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
      <td class="tdEditSide">
        <span class="heading2"><bean:message key="evaluation.auto.scoring" bundle="option"/></span> </td>
      <td>
        <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           evaluation.getAutoScoring_isInstructorEditable()) { %>
        <html:radio property="autoScoring" value="true"/>
        <bean:message key="evaluation.auto.scoring.true" bundle="option"/><BR>
        <html:radio property="autoScoring" value="false"/>
        <bean:message key="evaluation.auto.scoring.false" bundle="option"/><BR>
        <% } else { %>
        <bean:write name="evaluation" property="autoScoring"/>
        <% } %>
      </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td align="center"><html:checkbox property="autoScoring_isInstructorEditable"/>
        </td>
      </logic:equal>
      <td align="center"><html:checkbox property="autoScoring_isStudentViewable"/>
      </td>
    </tr>
    <% } %>

<!-- Grade Values section-->
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (evaluation.getScoringType_isInstructorEditable())) { %>

    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>

    <td class="tdEditSide"><span class="heading2"><bean:message key="evaluation.scoring.type" bundle="option"/> </span></td>
      <td>
<% if (session.getAttribute("editorRole").equals("templateEditor") ||
           evaluation.getScoringType_isInstructorEditable()) { %>
					 <!-- Numeric -->
					 <html:radio property="scoringType" value="0"/><bean:message key="evaluation.scoring.type.0" bundle="option"/><br />
					 <!-- beta2:alphanumeric v1: A,B,C,D,E,F,NP,I, with + (plus) and - (minus) -->
					 <html:radio property="scoringType" value="1"/><bean:message key="evaluation.scoring.type.1" bundle="option"/><br />
<%--deferred to v.1
					 <!-- (check), + (plus), - (minus) -->
					 <html:radio property="scoringType" value="2"/><bean:message key="evaluation.scoring.type.2" bundle="option"/><br />
					 <!-- Completed, Submitted but Incomplete, Not Submitted -->
					 <html:radio property="scoringType" value="3"/><bean:message key="evaluation.scoring.type.3" bundle="option"/><br />
--%>
<% } else { %>
        <bean:write name="evaluation" property="scoringType"/>
      <% } %>
        </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td align="center"><html:checkbox property="scoringType_isInstructorEditable"/></td>
</logic:equal>
      <td align="center"><html:checkbox property="scoringType_isStudentViewable"/></td>


    </tr>
    <% } %>

    <%-- Please note that the required field "defaultQuestionValue" specified in conf/validation.xml
  has been commented out in order to faciliate commenting out of the following paragraph without
  producing any error (bug# 350). When you are ready to uncomment this paragraph, you must also go to
  conf/validation.xml to uncomment the validation of the required field "defaultQuestionValue". -daisyf
	
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (evaluation.getNumericModel_isInstructorEditable())) { %>

  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>

    <td class="tdEditSide"><span class="heading2">Score Model for Assessment (Only
      applicable for Numeric Score Values)</span></td>
      <td><% if (session.getAttribute("editorRole").equals("templateEditor") ||
           evaluation.getNumericModel_isInstructorEditable()) { %>
        <html:radio property="numericModel" value="0"/>
        Sum of all questions' values with default question value of<BR>
         <html:text property="defaultQuestionValue"/><br>
         <html:radio property="numericModel" value="1"/>
        By weighted Parts (Each question in a part will have equal value)<BR>
         <html:radio property="numericModel" value="2"/>
        With fixed total score and equal-valued questions with total score of<BR>
          <html:text property="fixedTotalScore"/><br>
<% } else { %>
        <bean:write name="evaluation" property="numericModel"/>
      <% } %>
        </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td align="center"><html:checkbox property="numericModel_isInstructorEditable"/></td>
 </logic:equal>
      <td align="center"><html:checkbox property="numericModel_isStudentViewable"/></td>

    </tr>
    <% } %>--%>
    <tr>
      <td class="tdEditSide">&nbsp;</td>
      <td align="center">
        <html:submit value="Submit" property="Submit"/>
        <html:reset onclick="history.go(-1)" value="Cancel"/>
      </td>
      <logic:equal name="editorRole" value="templateEditor">
        <td align="center">&nbsp;</td>
        <td align="center">&nbsp;</td>
      </logic:equal>
    </tr>
  </table>
</html:form>
</body>
</html:html>
