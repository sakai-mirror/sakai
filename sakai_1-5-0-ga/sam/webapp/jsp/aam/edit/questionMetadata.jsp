<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>
Question Metadata
</title>
<link href="<%=request.getContextPath()%>/stylesheets/main.css" rel="stylesheet" type="text/css">
</head>
<body>
<p class="pageTitle">Edit Question Information</p>
<img src="<%=request.getContextPath()%>/images/divider2.gif">
<p class="instructions">[instructions here]</p>

<html:form action="/editQuestionMetadata.do" method="post">

<table class="tblEdit">
<tr>
<td class="tdSideRedText"><b>Name</b></td>
<td><html:textarea property="name"/></td></tr>
<td class="tdSideRedText"><b>Type</b></td>
<td><jsp:getProperty name="question" property="question"/></td></tr>
<tr>
<td class="tdSideRedText">Objectives</td>
<td><html:textarea property="objectives"/></td></tr>
<tr>
<td class="tdSideRedText">Metadata<br>(Keywords)</td>
<td><html:textarea property="keywords"/></td></tr>
<tr>
<td class="tdSideRedText">Rubrics <BR>(Grading Guidelines)</td>
<td><html:textarea property="rubrics"/></td></tr>
<tr><td colspan=2 align="center"><BR>

<html:submit value="Save" property="Submit"/>
<html:reset onclick="history.go(-1)" value="Cancel"/>
</td></tr>
</table>
</html:form>
</body>
</html:html>
