<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ page errorPage="index_error.jsp" %>
<jsp:useBean id="subpoolTree" scope="session" class="org.navigoproject.business.entity.questionpool.model.QuestionPoolTreeImpl" />
<jsp:useBean id="questionpool" scope="session" class="org.navigoproject.ui.web.form.questionpool.QuestionPoolForm" />
<html:html>
<head>
<title>Import Pool</title>
<link href="<%=request.getContextPath()%>/css/main.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#ffffff" onload="collapseAllRows();flagRows()">
<div class="heading">Import Pool</div>  


<html:form action="importPool.do"  method="post" enctype="multipart/form-data">

<br/>
<table>
<tr>
<td>
<span class="number">1</span>
</td>
<td valign="top">
<span class="instructionsSteps">
Import from a file:
</td>
</tr>

<tr>
<td></td>
<td class="instructionsSteps">
<br/>
File: 
<html:file property="filename" />
<br/>
<br/>
</td>
</tr>
<tr>
<td>
<span class="number">2</span>
</td>
<td valign="top">
<span class="instructionsSteps">
Click "Import" to import pool(s) to Question Pool Manager or "Cancel" to return to the previous page:
</td>
</tr>
</table>
<br/>
<br/>
  <center>
<html:submit>
     <bean:message key="button.import"/>
  </html:submit>

<html:reset onclick="history.go(-1)" value="Cancel"/>
  </center>
</html:form>
</body>
</html:html>
