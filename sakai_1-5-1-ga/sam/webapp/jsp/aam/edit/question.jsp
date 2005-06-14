<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Edit Question Display Settings</title>
<link href="../stylesheets/main.css" rel="stylesheet" type="text/css">
<%@include file="../includeJS/edit.js" %>
</head>

<logic:equal name="editorRole" value="templateEditor">
<body>
</logic:equal>
<logic:equal name="editorRole" value="assessmentEditor">
<body onLoad="disableCheckboxes('StudentViewable');">
</logic:equal>

<p class="pageTitle">Edit Question Display Settings</p>
<img src="../images/divider2.gif">
<p class="instructions">[instructions here]</p>
<html:form action="editQuestion.do" method="post">
  <table class="tblEdit">
    <tr>
      <th>&nbsp;</th>
      <th>&nbsp;</th>
      <th>Instr Edit</th>	      
      <th>Student View</th>
     
    </tr>
    <tr>
      <td class="tdSideRedText">Question Name</td>
      <td>&nbsp;</td>
      <td><html:checkbox property="name_isInstructorEditable"/></td>
      <td><html:checkbox property="name_isStudentViewable"/></td>
    </tr>
    <tr>
      <td class="tdSideRedText">Question Text</td>
      <td>&nbsp;</td>
      <td><html:checkbox property="text_isInstructorEditable"/></td>
      <td><html:checkbox property="text_isStudentViewable"/></td>
    </tr>
    <tr>
      <td class="tdSideRedText">Hint</td>
      <td>&nbsp;</td>
      <td><html:checkbox property="hint_isInstructorEditable"/></td>
      <td><html:checkbox property="hint_isStudentViewable"/></td>
    </tr>
    <tr>
      <td class="tdSideRedText">Value</td>
      <td>&nbsp;</td>
      <td><html:checkbox property="value_isInstructorEditable"/></td>
      <td><html:checkbox property="value_isStudentViewable"/></td>
    </tr>
    <tr>
      <td class="tdSideRedText">Answer Feedback</td>
      <td>(for questions with multiple selection answers)</td>
      <td><html:checkbox property="afeedback_isInstructorEditable"/></td>
      <td><html:checkbox property="afeedback_isStudentViewable"/></td>
    </tr>
   <tr>
      <td class="tdSideRedText">Question Feedback</td>
      <td>&nbsp;</td>
      <td><html:checkbox property="feedback_isInstructorEditable"/></td>
       <td><html:checkbox property="feedback_isStudentViewable"/></td>
    </tr>
    <tr>
      <td class="tdSideRedText">Question Keywords</td>
      <td>&nbsp;</td>
      <td><html:checkbox property="keywords_isInstructorEditable"/></td>
      <td><html:checkbox property="keywords_isStudentViewable"/></td>
    </tr>
    <tr>
      <td class="tdSideRedText">Question Objectives</td>
      <td>&nbsp;</td>
      <td><html:checkbox property="objectives_isInstructorEditable"/></td>
      <td><html:checkbox property="objectives_isStudentViewable"/></td>
    </tr>
    <tr>
      <td class="tdSideRedText">Question Rubrics</td>
      <td>&nbsp;</td>
      <td><html:checkbox property="rubrics_isInstructorEditable"/></td>
      <td><html:checkbox property="rubrics_isStudentViewable"/> </td>
    </tr>
      <td>&nbsp;</td>
      <td>&nbsp;
        <html:submit value="Submit" property="Submit"/>
        <html:reset onclick="history.go(-1)" value="Cancel"/>
      </td>
      <td> </td>
    </tr>
  </table>
</html:form>
</body>
</html:html>
