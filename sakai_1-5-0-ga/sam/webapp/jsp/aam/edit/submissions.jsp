<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="submissions" scope="session" class="org.navigoproject.ui.web.form.edit.SubmissionsForm" />
<% int count=0; //this sets up the row counter %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>
Edit Submissions
</title>
<link href="../stylesheets/main.css" rel="stylesheet" type="text/css">
<%@include file="../includeJS/edit.js" %>
</head>

<logic:equal name="editorRole" value="templateEditor">
<body>
</logic:equal>
<logic:equal name="editorRole" value="assessmentEditor">
<body onLoad="disableCheckboxes('StudentViewable');">
</logic:equal>

<p class="pageTitle">Edit Submissions</p>
<img src="../images/divider2.gif">
<p class="instructions">Specify the how submissions by students will be handled.
Then click the Submit button to save your input. No input will be saved if you click the Cancel button.</p>
<html:form action="editSubmissions.do" method="post">
<table class="tblEdit">
  <tr>
    <td colspan="2" class="tdEditTop">
	<logic:equal name="editorRole" value="templateEditor"><span class="heading2"><em>Template 
      Editor:</em> 
      <jsp:getProperty name="description" property="templateName"/>
      </span> </logic:equal> <logic:equal name="editorRole" value="assessmentEditor"><span class="heading2"><em>Assessment 
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
         (submissions.getLateHandling_isInstructorEditable())) { %>

<tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"> <span class="heading2">Late Handling of Submissions</span> 
    </td>
<td>
<% if (session.getAttribute("editorRole").equals("templateEditor") ||
           submissions.getLateHandling_isInstructorEditable()) { %>
<html:radio property="lateHandling" value="0"/><bean:message key="latehandling.long.0" bundle="option"/><BR>
<html:radio property="lateHandling" value="1"/><bean:message key="latehandling.long.1" bundle="option"/><BR>
<html:radio property="lateHandling" value="2"/><bean:message key="latehandling.long.2" bundle="option"/><BR>
<% } else { %>
        <bean:write name="submissions" property="lateHandling"/>
      <% } %>
</td>
      <logic:equal name="editorRole" value="templateEditor">
<td align="center"><html:checkbox property="lateHandling_isInstructorEditable"/></td>
</logic:equal>
<td align="center"><html:checkbox property="lateHandling_isStudentViewable"/></td>

</tr>
<% } %>

  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (submissions.getSubmissionModel_isInstructorEditable())) { %>

<tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"> <span class="heading2">Number of Submissions Allowed</span> 
    </td>
<td><% if (session.getAttribute("editorRole").equals("templateEditor") ||
           submissions.getSubmissionModel_isInstructorEditable()) { %>
<html:radio property="submissionModel" value="0" onclick="enable('saveFirst');enable('saveAll');enable('saveLast');"/><bean:message key="number.submissions.long.0" bundle="option"/><BR>
<html:radio property="submissionModel" value="1" onclick="disable('saveFirst');disable('saveAll');check('saveLast');uncheck('saveFirst');uncheck('saveAll')"/><bean:message key="number.submissions.long.1" bundle="option"/><BR>
<html:radio property="submissionModel" value="2" onclick="enable('saveFirst');enable('saveAll');enable('saveLast');"/><html:text property="submissionNumber"/><bean:message key="number.submissions.long.2" bundle="option"/><BR>
<% } else { %>
        <bean:write name="submissions" property="submissionModel"/>
      <% } %>
</td>
      <logic:equal name="editorRole" value="templateEditor">
<td align="center"><html:checkbox property="submissionModel_isInstructorEditable"/></td>
<td align="center"><html:checkbox property="submissionModel_isStudentViewable"/></td>
</logic:equal>

</tr>
<% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (submissions.getSubmissionsSaved_isInstructorEditable())) { %>

<tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"> <span class="heading2">Submissions Saved</span></td>
<td>
<% if (session.getAttribute("editorRole").equals("templateEditor") ||
           submissions.getSubmissionsSaved_isInstructorEditable()) { %>
<html:checkbox property="submissionsSaved1" styleId="saveFirst" /> <bean:message key="submissions.saved.long.0" bundle="option"/><BR>
<html:checkbox property="submissionsSaved2" styleId="saveLast"/> <bean:message key="submissions.saved.long.1" bundle="option"/><BR>
<html:checkbox property="submissionsSaved3" styleId="saveAll" /> <bean:message key="submissions.saved.long.2" bundle="option"/><BR>
<% } else { %>
        <bean:write name="submissions" property="submissionsSaved"/>
      <% } %>
</td>
      <logic:equal name="editorRole" value="templateEditor">
<td align="center"><html:checkbox property="submissionsSaved_isInstructorEditable"/></td>
<td align="center"><html:checkbox property="submissionsSaved_isStudentViewable"/></td>
</logic:equal>

</tr>
<% } %>
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
