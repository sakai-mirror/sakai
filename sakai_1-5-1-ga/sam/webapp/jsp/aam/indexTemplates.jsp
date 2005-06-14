<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ page import="org.navigoproject.business.entity.assessment.model.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<!-- these are just temporary to get it rendering -->
<jsp:useBean id="templateEditorForm" scope="session" class="org.navigoproject.ui.web.form.IndexForm" />
<jsp:useBean id="description" scope="session" class="org.navigoproject.ui.web.form.edit.DescriptionForm" />

<html>
<head>
<title>Template Front Door</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<!-- temporary style location until a central one is decided upon -->
<style type="text/css">
<!--
td {
	text-align: left;
	vertical-align: top;
	font-size: 12px;
}
.tdDisplay {
	text-align: center;
	width: 180px;
}
body {
	font-family: Verdana, Arial, Helvetica, sans-serif;
}
.border {
	color: #FFFFFF;
	background-color: #666699;
	font-weight: bold;
}
.heading {
	font-size: 18px;
	font-weight: bold;
	color: #FFFFFF;
	background-color: #990000;
	height: 50px;
	vertical-align: middle;
	padding: 10px;
}
.tdIndent1 {
	padding-left: 25px;
}
.tdIndent2 {
	padding-left: 50px;
}
.navigo_border{
	FONT-WEIGHT: bold;
	COLOR:#FFFFFF;
	margin-top:0px;
	margin-bottom:10px;
	margin-left:20px;
	margin-right:20px;
	border-style:solid;
	border-width:1px;
	border-color:#000000;
	BACKGROUND-COLOR:#666699;
	font-size: 12px;
	padding-left: 5px;
}
.plain_border{
	margin-left:20px;
	margin-right:20px;
	margin-bottom: 10px;
	margin-top: 10px;
}
h3 {
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 12px;
	font-weight: bold;
}

-->
</style>
</head>

<body>
<div class="heading">Template Front Door</div>
<table width="90%" border="0" class="plain_border">
  <tr>
    <td><h3>Templates</h3></td>
    <td class="tdDisplay">
    <%--html:form name="navigationForm" action="<%=request.getContextPath()%>/Navigation.xml" method="post"--%>
      <html:button property='id' title="Return to Assessments Page" value='Author' />
    <%--/html:form--%></td>
  </tr>
</table>
    
  <div class="navigo_border" colspan="3">New Template </td> </div>
  <div class="plain_border"> 
  <html:form action="editDescription.do" method="post">
  <table width="100%" border="0">
    <tr> 
    <td class="tdIndent1">Create a new template<br>
      Title: 
      <%--html:text size="60" property="templateName" /--%>
      <html:submit value="Create" property="Submit" /></td>
    </tr>
	</table>
  </html:form>
    </div>
  <div class="navigo_border" colspan="3">Saved Templates</div>
  <div class="plain_border">
    <table width="100%" border="0">
      <tr>
        <td><h3>Title </h3></td>
        <td><h3>Last Modified </h3></td>
        <td>&nbsp;</td>
      </tr>
			  <logic:iterate name="index" id="templateItem" property="templateList" type="org.navigoproject.business.entity.assessment.model.AssessmentTemplateImpl" indexId="ctr">
  <tr class='<%= (ctr.intValue() % 2==0 ?"trEven":"trOdd") %>'>
      <td><html:link page='/editTemplate.do' paramId='id' paramName='templateItem' paramProperty='id'><bean:write name='templateItem' property='templateName' /></html:link></td>
      <td><%--bean:write name='templateItem' property='templateModified' /--%></td>
      <td><html:button property='id' value='Remove' /><%--html:link page='/confirmDelete.do' paramId='templateid' paramName='templateItem' paramProperty='id'>Remove</html:link--%></td>
    </tr>
  </logic:iterate>

    </table>
  </div>
  <p align="center">&nbsp;</p>
<p align="center">&nbsp;</p>
</html:form>
</body>
</html>
