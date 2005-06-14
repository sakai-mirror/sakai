<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" language="java" import="java.sql.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="display" scope="session" class="org.navigoproject.ui.web.form.edit.DisplayForm" />
<% int count=0; //this sets up the row counter %><head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Edit Display Features</title>
<link href="../stylesheets/main.css" rel="stylesheet" type="text/css">
<%@include file="../includeJS/edit.js" %>
</head>

<logic:equal name="editorRole" value="templateEditor">
<body>
</logic:equal>
<logic:equal name="editorRole" value="assessmentEditor">
<body onLoad="disableCheckboxes('StudentViewable');">
</logic:equal>

<p class="pageTitle">Edit Display Features</p>
<img src="../images/divider2.gif">
<p class="instructions">Specify the organization of the Assessment. Then click
  the Submit button to save your input. <br>
  No input will be saved if you click the Cancel button.<br>
  Note that:
<ul>
  <li> Assessments can have one or more Parts (each Part contains a header and
    a <br>
  set of questions).</li>
  <li> Students can move through the assessment in different ways (See Access
    to <br>
   Questions).</li>
  <li> An Assessment may be displayed on one or on several web pages.</li>
  <li> An Assessment may include a Table of Contents page listing Parts or Questions.</li>
  <li> Students can mark a question while taking an assessment for later review.<br>
  Marked questions will be listed on the Student's Review List page. </li>
</ul></p>
<html:form action="editDisplay.do" method="post">
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
    <td class="tdEditEditView">Instructor<br>
      Editable</td>
    </logic:equal>
    <td class="tdEditEditView">Student Viewable</td>
  </tr>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (display.getMultiPartAllowed_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide">
      <span class="heading2">
      <bean:message key="multipart.allowed" bundle="option"/>
      </span>
    </td>
    <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           display.getMultiPartAllowed_isInstructorEditable()) { %>
<html:radio property="multiPartAllowed" value="true" onclick="enable('disChunk1');"/>
<bean:message key="multipart.allowed.true" bundle="option"/><br />
<html:radio property="multiPartAllowed" value="false" onclick="disable('disChunk1');"/>
<bean:message key="multipart.allowed.false" bundle="option"/><BR>
      <% } else { %> <bean:write name="display" property="multiPartAllowed"/> <% } %> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td align="center"><html:checkbox property="multiPartAllowed_isInstructorEditable"/></td>
    </logic:equal>
    <td align="center"><html:checkbox property="multiPartAllowed_isStudentViewable"/></td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (display.getItemAccessType_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Access to Questions</span></td>
    <td><% if (session.getAttribute("editorRole").equals("templateEditor") ||
           display.getItemAccessType_isInstructorEditable()) { %>
      <html:radio property="itemAccessType" value="2" onclick="disable('bookmarksYes');" /> <bean:message key="item.access.2" bundle="option"/><BR>
<%--
      <html:radio property="itemAccessType" value="0"  onclick="enable('bookmarksYes');"/> <bean:message key="item.access.0" bundle="option"/><BR>
--%>
      <html:radio property="itemAccessType" value="1"  onclick="enable('bookmarksYes');"/> <bean:message key="item.access.1" bundle="option"/><BR>
    <% } else { %> <bean:write name="display" property="itemAccessType"/> <% } %> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td align="center"><html:checkbox property="itemAccessType_isInstructorEditable"/></td>
    </logic:equal>
    <td align="center"><html:checkbox property="itemAccessType_isStudentViewable"/></td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (display.getDisplayChunking_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Web Page Content</span></td>
    <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           display.getDisplayChunking_isInstructorEditable()) { %> <html:radio property="displayChunking" value="0"/> <bean:message key="display.chunking.0" bundle="option"/><BR>
      <html:radio styleId="disChunk1" property="displayChunking" value="1" /> <bean:message key="display.chunking.1" bundle="option"/><BR>
      <%-- left out for beta2 -mARC
      <html:radio property="displayChunking" value="2"/> <bean:message key="display.chunking.2" bundle="option"/><BR>
    --%>
    <html:radio property="displayChunking" value="3"/> <bean:message key="display.chunking.3" bundle="option"/><BR>
      <% } else { %> <bean:write name="display" property="displayChunking"/> <% } %> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td align="center"><html:checkbox property="displayChunking_isInstructorEditable"/></td>
    </logic:equal>
    <td align="center"><html:checkbox property="displayChunking_isStudentViewable"/></td>
  </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (display.getItemBookMarking_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Review List</span></td>
    <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           display.getItemBookMarking_isInstructorEditable()) { %>
        <html:radio styleId="bookmarksNo" property="itemBookMarking" value="true" />
        <bean:message key="item.bookmarking.true" bundle="option"/><BR>
        <html:radio styleId="bookmarksYes" property="itemBookMarking" value="false" />
        <bean:message key="item.bookmarking.false" bundle="option"/><BR>
      <% } else { %> <bean:write name="display" property="itemBookMarking"/> <% } %></td>
    <logic:equal name="editorRole" value="templateEditor">
    <td align="center"><html:checkbox property="itemBookMarking_isInstructorEditable"/></td>
    </logic:equal>
    <td align="center"><html:checkbox property="itemBookMarking_isStudentViewable"/></td>
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
