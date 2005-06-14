<%@ page import="org.navigoproject.business.entity.assessment.model.*" %>
<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ page errorPage="index_error.jsp" %>
<jsp:useBean id="index" scope="request" class="org.navigoproject.ui.web.form.IndexForm" />
<%@ include file="params.jsp" %>
<%
  request.setAttribute("course", "" + session.getAttribute("course_name"));
%>

<!-- these are just temporary to get it rendering -->
<jsp:useBean id="description" scope="session" class="org.navigoproject.ui.web.form.edit.DescriptionForm" />

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<title>Template Front Door</title>
<link href="../../css/main.css" rel="stylesheet" type="text/css">
</head>

<body>
<div class="heading">Sakai Assessment Manager</div>
<h1>
  <table width="90%" border="0">
    <tr>
      <td class="h1text">My Templates</td>
    <td class="tdDisplay">
    <html:form action="/Navigation" method="post">
    <html:hidden property="navigationSubmit"  value="Author"/> 
    <html:submit property="Submit" value="My Assessments" /><br>
    </html:form></td>
  </tr>
</table></h1>
    
  
<h2>New Template </td> </h2>
  
<div class="h2unit"> <html:form action="newTemplate.do" method="post"> Create 
  a new template<br>
      Title: 
      <html:text size="60" property="templateName" />
      <html:submit value="Create" property="Submit" />
  </html:form>
    </div>
  
<h2>Saved Templates</h2>
  
<div class="h2unit"> 
  <table width="100%" border="0">
    <tr> 
      <th class="altHeading2">Title </th>
      <th class="altHeading2">Last Modified </th>
      <td>&nbsp;</td>
    </tr>
    <logic:iterate name="index" id="templateItem" property="templateList" type="org.navigoproject.business.entity.assessment.model.AssessmentTemplateImpl" indexId="ctr"> 
    <tr class='<%= (ctr.intValue() % 2==0 ?"trEven":"trOdd") %>'> 
      <td><html:link page='/editTemplate.do' paramId='id' paramName='templateItem' paramProperty='id'><bean:write name='templateItem' property='templateName' /></html:link></td>
      <td><bean:write name='templateItem' property='data.lastModified' /></td>
      <td><html:form action="confirmDelete.do" method="post"> 
        <input type="hidden" name="templateid" value="<bean:write name='templateItem' property='id' />"/>
        <html:submit property='submitDelete' value='Remove' /></html:form></td>
    </tr>
    </logic:iterate> 
  </table>
  </div>
  <p align="center">&nbsp;</p>
<p align="center">&nbsp;</p>
</body>
</html>
