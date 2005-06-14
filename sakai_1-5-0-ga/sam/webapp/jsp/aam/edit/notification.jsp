<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="notification" scope="session" class="org.navigoproject.ui.web.form.edit.NotificationForm" />
<% int count=0; //this sets up the row counter %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Edit Notification </title>
<link href="../stylesheets/main.css" rel="stylesheet" type="text/css">
<%@include file="../includeJS/edit.js" %>
</head>

<logic:equal name="editorRole" value="templateEditor">
<body>
</logic:equal>
<logic:equal name="editorRole" value="assessmentEditor">
<body onLoad="disableCheckboxes('StudentViewable');">
</logic:equal>

<p class="pageTitle">Edit Notification</p>
<img src="../images/divider2.gif">
<p class="instructions">Specify when notification email messages are sent automatically
  to students and instructional staff. Then click the Submit button to save your
  input. No input will be saved if you click the Cancel button.</p>
<html:form action="editNotification.do" method="post">
<table class="tblEdit">
  <tr>
    <td colspan="2" class="tdEditTop"> <logic:equal name="editorRole" value="templateEditor"><span class="heading2"><em>Template
      Editor:</em>
      <jsp:getProperty name="description" property="templateName"/>
      </span> </logic:equal> <logic:equal name="editorRole" value="assessmentEditor"><span class="heading2"><em>Assessment
      Editor:</em>
      <jsp:getProperty name="description" property="name"/>
      </span></logic:equal> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td class="tdEditEditView">Instructor Editable</td>
    </logic:equal>
    <td class="tdEditEditView">Student Viewable</td>
  </tr>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (notification.getTesteeNotification_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Student Notification</span></td>
    <td><% if (session.getAttribute("editorRole").equals("templateEditor") ||
           notification.getTesteeNotification_isInstructorEditable()) { %>
      Notification emails are sent to students who can take/are taking 
      the assessment when following events occur:
      <BR> <html:checkbox property="testeeNotification1"/>
      When an assessment is made available to students.<BR> <html:checkbox property="testeeNotification2"/>
      On day before an assessment is due.<BR> <html:checkbox property="testeeNotification3"/>
      When a question is modified after assessment is available to students.<BR>
      <html:checkbox property="testeeNotification4"/> When feedback and scores
      are available.<BR> <% } else { %> <bean:write name="notification" property="testeeNotification"/>
      <% } %> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td align="center"><html:checkbox property="testeeNotification_isInstructorEditable"/></td>
    </logic:equal>
    <td align="center"><html:checkbox property="testeeNotification_isStudentViewable"/></td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (notification.getInstructorNotification_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Instructional Staff Notification
      (Instructor, TA, Graders)</span></td>
    <td><% if (session.getAttribute("editorRole").equals("templateEditor") ||
           notification.getInstructorNotification_isInstructorEditable()) { %>
      Notification emails are automatically sent to instructional staff
      when the following events occur.<BR>
      <html:checkbox property="instructorNotification1"/> When an assessment
      is made available to students.<BR> <html:checkbox property="instructorNotification2"/>
      On day before an assessment is due.<BR> <html:checkbox property="instructorNotification3"/>
      When a question is modified after assessment is available to students.<BR>
      <html:checkbox property="instructorNotification4"/> When feedback and scores
      are available.<BR> <% } else { %> <bean:write name="notification" property="instructorNotification"/>
      <% } %> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td align="center"><html:checkbox property="instructorNotification_isInstructorEditable"/></td>
    </logic:equal>
    <td align="center"><html:checkbox property="instructorNotification_isStudentViewable"/></td>
  </tr>
  <% } %>
  <tr>
    <td class="tdEditSide">&nbsp;</td>
    <td align="center"> <html:submit value="Submit" property="Submit"/> <html:reset onclick="history.go(-1)" value="Cancel"/>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td align="center">&nbsp;</td>
    <td align="center">&nbsp;</td>
    </logic:equal> </tr>
</table>
</html:form>
</body>
</html:html>
