<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="distribution" scope="session" class="org.navigoproject.ui.web.form.edit.DistributionForm" />
<% int count=0; //this sets up the row counter %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title> Edit Grades Distribution </title>
<link href="../stylesheets/main.css" rel="stylesheet" type="text/css">
<%@include file="../includeJS/edit.js" %>
</head>

<logic:equal name="editorRole" value="templateEditor">
<body>
</logic:equal>
<logic:equal name="editorRole" value="assessmentEditor">
<body onLoad="disableCheckboxes('StudentViewable');">
</logic:equal>

<p class="pageTitle">Edit Grades Distribution</p>
<img src="../images/divider2.gif">
<p class="instructions">Specify who will be able to view student responses and
  grades and whether they will be sent to the gradebook. Then click the Submit
  button to save your input. No input will be saved if you click the Cancel button.</p>
<html:form action="editDistribution.do" method="post">
<table class="tblEdit">
  <tr>
    <td colspan="2" class="tdEditTop"><logic:equal name="editorRole" value="templateEditor"><span class="heading2"><em>Template
      Editor:</em>
      <jsp:getProperty name="description" property="templateName"/>
      </span> </logic:equal> <logic:equal name="editorRole" value="assessmentEditor"><span class="heading2"><em>Assessment
      Editor:</em>
      <jsp:getProperty name="description" property="name"/>
      </span></logic:equal></td>
    <logic:equal name="editorRole" value="templateEditor">
    <td class="tdEditEditView">Instructor Editable</td>
    </logic:equal>
    <td class="tdEditEditView">Student Viewable</td>
  </tr>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
          (distribution.getToAdmin_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide">
      <span class="heading2">
      <bean:message key="distribution.admin.description" bundle="option"/>
      </span>
    </td>
    <td><% if (session.getAttribute("editorRole").equals("templateEditor") ||
           distribution.getToAdmin_isInstructorEditable()) { %>
      <%-- note that options are out-of-order, this is deliberate --%>
      <bean:message key="distribution.admin" bundle="option"/><BR>
      <html:checkbox property="toAdmin2"/>
      <bean:message key="distribution.admin2" bundle="option"/><BR>
      <html:checkbox property="toAdmin1"/>
      <bean:message key="distribution.admin1" bundle="option"/><BR>
      <html:checkbox property="toAdmin3"/>
      <bean:message key="distribution.admin3" bundle="option"/><BR><BR><BR> <% } else { %>
      <bean:write name="distribution" property="toAdmin"/> <% } %> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td align="center"><html:checkbox property="toAdmin_isInstructorEditable"/></td>
    </logic:equal>
    <td align="center"><html:checkbox property="toAdmin_isStudentViewable"/></td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
          (distribution.getToTestee_isInstructorViewable() ||
           distribution.getToTestee_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide">
      <span class="heading2">
      <bean:message key="distribution.testee.description" bundle="option"/>
      </span>
    </td>
    <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           distribution.getToTestee_isInstructorEditable()) { %>
      <bean:message key="distribution.testee" bundle="option"/><BR>
      <html:checkbox property="toTestee1"/>
      <bean:message key="distribution.testee1" bundle="option"/><BR>
      <html:checkbox property="toTestee2"/>
      <bean:message key="distribution.testee2" bundle="option"/><BR>
      <html:checkbox property="toTestee3"/>
      <bean:message key="distribution.testee3" bundle="option"/><BR>
      <html:checkbox property="toTestee4"/>
      <bean:message key="distribution.testee4" bundle="option"/><BR><BR>
    <% } else { %> <bean:write name="distribution" property="toAdmin"/> <% } %> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td align="center"><html:checkbox property="toTestee_isInstructorEditable"/></td>
    </logic:equal>
    <td align="center"><html:checkbox property="toTestee_isStudentViewable"/></td>
  </tr>
  <% } %>
  <%
if (session.getAttribute("editorRole").equals("templateEditor") ||
      (distribution.getToGradebook_isInstructorViewable() ||
       distribution.getToGradebook_isInstructorEditable())) {
%>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide">
      <span class="heading2">
      <bean:message key="distribution.gradebook.description" bundle="option"/>
      </span>
    </td>
    <td> <%  if (session.getAttribute("editorRole").equals("templateEditor") ||
      distribution.getToGradebook_isInstructorEditable()) {
%>
      <bean:message key="distribution.gradebook" bundle="option"/><BR> <html:checkbox property="toGradebook"/>
      <bean:message key="distribution.gradebook1" bundle="option"/><BR> <%
} else { %> <bean:write name="distribution" property="toGradebook"/> <%    } %> <%-- if --%> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td align="center"><html:checkbox property="toGradebook_isInstructorEditable"/></td>
    </logic:equal>
    <td align="center"><html:checkbox property="toGradebook_isStudentViewable"/></td>
  </tr>
  <%
}
%>
  <%-- if --%>
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