<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="access" scope="session" class="org.navigoproject.ui.web.form.edit.AccessForm" />
<jsp:useBean id="dateutil" scope="session" class="org.navigoproject.business.entity.DateHandlerWithNull" />
<% int count=0; //this sets up the row counter %>

<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Edit Access</title>
<link href="../stylesheets/main.css" rel="stylesheet" type="text/css">
<%@include file="../includeJS/edit.js" %>
</head>

<logic:equal name="editorRole" value="templateEditor">
<body>
</logic:equal>
<logic:equal name="editorRole" value="assessmentEditor">
<body onLoad="disableCheckboxes('StudentViewable');">
</logic:equal>

<p class="pageTitle">Edit Access to Assessment for <%=session.getAttribute("groupName") %></p>
<img src="../images/divider2.gif">
<p class="instructions">Specify the when the assessment is available for students
  to take and when it is due. Then click the Submit button to save your input.
No input will be saved if you click the Cancel button.</p>
<p>
<html:errors/>
</p>
<html:form action="editAccess.do" method="post">
<table border=0 cellpadding=4 cellspacing=2 width=100%>
  <tr>
    <td colspan="2" class="tdEditTop">
      <logic:equal name="editorRole" value="templateEditor"><span class="heading2"><em>Template Editor:</em>        <jsp:getProperty name="description" property="templateName"/>
      </span>      </logic:equal>
      <logic:equal name="editorRole" value="assessmentEditor"><span class="heading2"><em>Assessment Editor:</em>        <jsp:getProperty name="description" property="name"/>
      </span>      </logic:equal>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
    <th class="tdEditEditView">Instructor<br>
      Editable</th>
    <th class="tdEditEditView">Student Viewable</th>
    </logic:equal> </tr>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getReleaseType_isInstructorEditable())) { %>
  <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Release of Assessment</span> </td>
    <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           access.getReleaseType_isInstructorEditable()) { %>
      <html:radio name="access"
        property='<%= "groupArray[" + session.getAttribute("getGroup") + "].releaseType" %>' value="0"/>
      <bean:message key="release.type.0" bundle="option"/><br>
      <html:radio name="access"
        property='<%= "groupArray[" + session.getAttribute("getGroup") + "].releaseType" %>' value="1"/>
      <bean:message key="release.type.1" bundle="option"/><br>
          <logic:equal name="editorRole" value="templateEditor">
            <i>Assessment Author sets Date</i><br>
          </logic:equal>
          <logic:equal name="editorRole" value="assessmentEditor">
       Day<html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].releaseDay" %>'>
         <html:options name="dateutil" property="day"/>
       </html:select>
       Month<html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].releaseMonth" %>'>
         <html:options name="dateutil" property="month"/>
       </html:select>
       Year<html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].releaseYear" %>'>
         <html:options name="dateutil" property="year"/>
       </html:select>
       <br>Time:<html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].releaseHour" %>'>
          <html:options name="dateutil" property="hour"/>
       </html:select>
       <html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].releaseMinute" %>'>
          <html:options name="dateutil" property="min"/>
       </html:select>
       <html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].releaseAmPm" %>'>
          <html:options name="dateutil" property="amPm"/>
       </html:select><br />
       </logic:equal>
      <%--these options held out until v.1.x
      <html:radio name="access" property='<%= "groupArray[" + session.getAttribute("getGroup") + "].releaseType" %>' value="2"/>
Upon completion of <strong>[Assess. select here]</strong> <bean:message key="release.type.2" bundle="option"/><strong>[Textbox here]</strong>.<br>
      <strong> [This option is for Version 1, not for beta 2. So just ignore it for now.] </strong> <br>--%>


      <% } else { %> <bean:write name="access" property="getReleaseType"/> <% } %> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox name="access" property="releaseType_isInstructorEditable"/></td>
    </logic:equal>
    <td><html:checkbox name="access" property="releaseType_isStudentViewable"/></td>
 </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getRetractType_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>

    <td class="tdEditSide"><span class="heading2">Retraction of Assessment</span></td>
    <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           access.getRetractType_isInstructorEditable()) { %>
      <html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].retractType" %>' value="0"/>
      <bean:message key="retract.type.0" bundle="option"/><br>
      <!-- not for v1.0 html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].retractType" %>' value="1"/>
      <bean:message key="retract.type.1" bundle="option"/><br -->
      <html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].retractType" %>' value="2"/>
      <bean:message key="retract.type.2" bundle="option"/><br>
          <logic:equal name="editorRole" value="templateEditor">
            <i>Assessment Author sets Date</i><br>
          </logic:equal>
          <logic:equal name="editorRole" value="assessmentEditor">
       Day<html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].retractDay" %>'>
         <html:options name="dateutil" property="day"/>
       </html:select>
       Month<html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].retractMonth" %>'>
         <html:options name="dateutil" property="month"/>
       </html:select>
       Year<html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].retractYear" %>'>
         <html:options name="dateutil" property="year"/>
      </html:select>
       <br>Time:<html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].retractHour" %>'>
          <html:options name="dateutil" property="hour"/>
       </html:select>
       <html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].retractMinute" %>'>
          <html:options name="dateutil" property="min"/>
       </html:select>
       <html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].retractAmPm" %>'>
          <html:options name="dateutil" property="amPm"/>
       </html:select><br />
          </logic:equal>
      <!-- html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].retractType" %>' value="3"/>
      <bean:message key="retract.type.3" bundle="option"/><br -->
      <% } else { %> <bean:write name="access" property="getRetractType"/> <% } %> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox name="access" property="retractType_isInstructorEditable"/></td>
    </logic:equal>
    <td><html:checkbox name="access" property="retractType_isStudentViewable"/></td>
 </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getDueDate_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>

    <td class="tdEditSide"><span class="heading2">Due Date</span></td>
    <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           access.getDueDate_isInstructorEditable()) { %>
      <html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].dueDateModel" %>' value="0"/>
      <bean:message key="due.date.0" bundle="option"/><br>
      <html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].dueDateModel" %>' value="1"/>
      <bean:message key="due.date.1" bundle="option"/><br>
          <logic:equal name="editorRole" value="templateEditor">
            <i>Assessment Author sets Date</i><br>
          </logic:equal>
          <logic:equal name="editorRole" value="assessmentEditor">
       Day<html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].dueDay" %>'>
         <html:options name="dateutil" property="day"/>
       </html:select>
       Month<html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].dueMonth" %>'>
         <html:options name="dateutil" property="month"/>
       </html:select>
       Year<html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].dueYear" %>'>
         <html:options name="dateutil" property="year"/>
       </html:select>
       <br>Time:<html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].dueHour" %>'>
         <html:options name="dateutil" property="hour"/>
       </html:select>
       <html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].dueMinute" %>'>
         <html:options name="dateutil" property="min"/>
       </html:select>
       <html:select property='<%= "groupArray[" + session.getAttribute("getGroup") + "].dueAmPm" %>'>
         <html:options name="dateutil" property="amPm"/>
       </html:select><br />
          </logic:equal>
<!--	  allowed to modify date and time -->
      <% } else { %>
      printout Date time
      <% } %> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox name="access" property="dueDate_isInstructorEditable"/></td>
    </logic:equal>
    <td><html:checkbox name="access" property="dueDate_isStudentViewable"/></td>
 </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getRetryAllowed_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>

    <td class="tdEditSide"><span class="heading2">Due Date Extension Allowed</span></td>
    <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           access.getRetryAllowed_isInstructorEditable()) { %>
      <html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].retryAllowed" %>' value="true" />
      <bean:message key="retry.allowed.true" bundle="option"/><br>
      <html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].retryAllowed" %>' value="false"/>
      <bean:message key="retry.allowed.false" bundle="option"/><br>
        <% } else { %>
      <bean:write name="access" property="getRetryAllowed" />
        <% } %>
    </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox name="access" property="retryAllowed_isInstructorEditable"/></td>
    </logic:equal>
    <td><html:checkbox name="access" property="retryAllowed_isStudentViewable"/></td>
 </tr>
  <% } %>

<%-- these options 3 rows are left out until V.1 -mARC

  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getTimedAssessment_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>
    <td class="tdEditSide"><span class="heading2">Timed Assessment</span></td>
    <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           access.getTimedAssessment_isInstructorEditable()) { %> <html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].timedAssessment" %>' value="false" />
      Student can take as long as desired - Assessment is untimed<br> <html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].timedAssessment" %>' value="true" />
      Assessment is timed and students can no longer submit after <html:text property='<%= "groupArray[" + session.getAttribute("getGroup") + "].minutes" %>' />
      minutes.<br> <% } else { %> <bean:write name="access" property="getTimedAssessment" />
      <% } %> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox name="access" property="timedAssessment_isInstructorEditable"/></td>
    </logic:equal>
    <td><html:checkbox name="access" property="timedAssessment_isStudentViewable"/></td>
 </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getPasswordRequired_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>

    <td class="tdEditSide"><span class="heading2">Password Access</span></td>
    <td> <% if (session.getAttribute("editorRole").equals("templateEditor") ||
           access.getPasswordRequired_isInstructorEditable()) { %> <html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].passwordAccess" %>' value="false" />
      No secondary password is needed to take the assessment.<br> <html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].passwordAccess" %>' value="true" />
      A secondary password is needed. Enter Password: <html:password property='<%= "groupArray[" + session.getAttribute("getGroup") + "].password" %>' /><br>
      <% } else { %> <bean:write name="access" property="getPasswordRequired" />
      <% } %> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox name="access" property="passwordRequired_isInstructorEditable"/></td>
    </logic:equal>
    <td><html:checkbox name="access" property="passwordRequired_isStudentViewable"/></td>
 </tr>
  <% } %>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getIpAccessType_isInstructorEditable())) { %>
    <tr class='<%= (count++ % 2==0 ?"trOdd":"trEven") %>'>

    <td class="tdEditSide"><span class="heading2">IP Addresses</span></td>
    <td><% if (session.getAttribute("editorRole").equals("templateEditor") ||
           access.getIpAccessType_isInstructorEditable()) { %> <html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].ipAccess" %>' value="0" />
      Students at any IP Address can access the assessment.<br> <html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].ipAccess" %>' value="1" />
      Only students at these IP addresses and ranges can take the assessment:
      <a href="">Add New IP Address or Range</a><br> <html:radio property='<%= "groupArray[" + session.getAttribute("getGroup") + "].ipAccess" %>' value="2" />
      Students at these IP addresses and ranges CANNOT take the assessment: <a href="">Add
      New IP Address or Range</a><br> <% } else { %> <bean:write name="access" property="getIpAccessType" /> <% } %> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox name="access" property="ipAccessType_isInstructorEditable"/></td>
    </logic:equal>
    <td><html:checkbox name="access" property="ipAccessType_isStudentViewable"/></td>
 </tr>
  <% } %>
--%>
  <tr>
    <td>&nbsp;</td>
    <logic:equal name="editorRole" value="templateEditor">
      <td colspan="3">
    </logic:equal>
    <logic:equal name="editorRole" value="assessmentEditor">
      <td>
    </logic:equal>
      <html:submit value="Submit" property="Submit"/>
      <html:reset onclick="history.go(-1)" value="Cancel"/>
    </td></tr>
</table>
</html:form>
</body>
</html:html>
