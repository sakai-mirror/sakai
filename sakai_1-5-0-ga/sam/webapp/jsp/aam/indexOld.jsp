<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ page errorPage="index_error.jsp" %>
<jsp:useBean id="index" scope="request" class="org.navigoproject.ui.web.form.IndexForm" />
<%@ include file="params.jsp" %>
<%
  request.setAttribute("course", "" + session.getAttribute("course_name"));
%>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Admin Assignments</title>
<link href="stylesheets/main.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#ffffff">
<div class="courseTitle">
    <logic:present name="course">
    [<bean:write name="course"/>]
    </logic:present>
</div>
<br>
<img src="images/titleline.gif">

<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td><h2> Admin Assessments</h2>
    </td>
    <td align="right"><a href="realization/index.jsp">Student View</a><br>
    </td>
  </tr>
</table>

<p class="breadcrumb"><a href="#">My Courses</a> &gt; <a href="#">Course Homepage</a>
  &gt; <a href="#">Assessments</a> &gt; Admin Assessments</p>
<h3>1. Create Assessments</h3>
  <p>Select an existing template:
<html:form action="submitIndex.do" method="post">
<% index.setCourseId((String) session.getAttribute("course_id")); %>
    <html:select property="assessmentTypeChoice" name="index">
      <html:options name="index" property="templateIds" labelName="index" labelProperty="templateNames"/>
    </html:select>
    <html:submit value="Submit" />
</html:form>
  </p>
  <p>To modify an existing template or to create a new template, go to <a href="indexTemplates.jsp">Admin
    Templates</a>.</p>

<h3>2. Manage Assessments</h3>

<table class="tblMain">
  <tr class="tdindexTop">
    <td>Assessment Name</td>
    <td>Type</td>
    <td>Date Available</td>
    <td>Due Date</td>
    <td>Status</td>
    <td>Action</td>
  </tr>
  <logic:iterate name="index" id="assessmentItem" property="assessmentList" type="org.navigoproject.business.entity.assessment.model.AssessmentImpl" indexId="ctr">
  <tr class='<%= (ctr.intValue() % 2==0 ?"trEven":"trOdd") %>'>
    <td><bean:write name='assessmentItem' property='displayName' /></td>
    <td><bean:write name='assessmentItem' property='assessmentType' /></td>
    <td>&nbsp;</td>
    <td><html:link page='/editAssessment.do' paramId='id' paramName='assessmentItem' paramProperty='id'>
      Edit</html:link> | <html:link page='/totalScores.do' paramId='id' paramName='assessmentItem' paramProperty='id'>
      Grade</html:link> | <html:link page='/confirmDelete.do' paramId='assessmentid' paramName='assessmentItem' paramProperty='id'>Remove</html:link></td>
  </tr>
  </logic:iterate>
</table>

<h3><br>
</h3>
</body>
</html:html>
