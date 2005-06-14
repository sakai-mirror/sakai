<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="access" scope="session" class="org.navigoproject.ui.web.form.edit.AccessForm" />
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Edit Access Groups</title>
<link href="../stylesheets/main.css" rel="stylesheet" type="text/css">
<%@include file="../includeJS/edit.js" %>
</head>

<logic:equal name="editorRole" value="templateEditor">
<body>
</logic:equal>
<logic:equal name="editorRole" value="assessmentEditor">
<body onLoad="disableCheckboxes('StudentViewable');">
</logic:equal>

<p class="pageTitle">Edit Access Groups</p>
<img src="../images/divider2.gif">
<p class="instructions">[instructions here]</p>
<html:form action="editGroups.do" method="post">
<table width=100% border=0 cellpadding=4 cellspacing=2 class="tblEdit">
  <tr>
    <td colspan="2" width="100%" class="tdSideRedText">
    <!--
      <logic:equal name="editorRole" value="templateEditor">Template</logic:equal>
      <logic:equal name="editorRole" value="assessmentEditor">Assessment</logic:equal>
      -->
      <!--Authoring Mode-->
    </td>
    <logic:equal name="editorRole" value="templateEditor">
    <th>Instr View Only</th>
    <th>Instr View/Edit</th>
    <th>Student View</th>
    </logic:equal> </tr>
  <% if (session.getAttribute("editorRole").equals("templateEditor") ||
         (access.getGroups_isInstructorViewable() ||
          access.getGroups_isInstructorEditable())) { %>
  <tr>
    <td><B>Groups Taking Assessment</B></td>
    <td><% if (session.getAttribute("editorRole").equals("templateEditor") ||
           access.getGroups_isInstructorEditable()) { %> <logic:iterate name="access" id="groupItem" property="groups" type="org.navigoproject.business.entity.assessment.model.AccessGroup" indexId="ctr">
      <html:checkbox name="access" property='<%= "groupArray[" + ctr + "].isActive" %>' />
      <bean:write name="groupItem" property="name"/><br>
      </logic:iterate> <% } else { %> <bean:write name="access" property="groups"/> <% } %> </td>
    <logic:equal name="editorRole" value="templateEditor">
    <td><html:checkbox property="groups_isInstructorViewable"/></td>
    <td><html:checkbox property="groups_isInstructorEditable"/></td>
    <td><html:checkbox property="groups_isStudentViewable"/> </td>
    </logic:equal> </tr>
  <% } %>
  <tr><td>&nbsp;</td> <logic:equal name="editorRole" value="templateEditor">
    <td colspan="4"> </logic:equal> <logic:equal name="editorRole" value="assessmentEditor">
    <td> </logic:equal>
    <html:submit value="Submit" property="Submit"/>
    <html:reset onclick="history.go(-1)" value="Cancel"/>
    </td></tr>
</table>
</html:form>
</body>
</html:html>