<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<html>
<head>
<title>Template Front Door</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="../../css/main.css" rel="stylesheet" type="text/css">
<link href="../../css/template.css" rel="stylesheet" type="text/css">
</head>

<body>
<div class="heading">Samigo</div>
<br>
<h2>Remove Template Confirmation</h2>
  
<div class="h2unit"> 
  <div class="h2unit">Are you certain you want to remove this Template?
  </div>
  <div align="center"> <html:form action="deleteAction.do" method="post"> 
    <html:submit value="Remove" property="submit" /><html:reset onclick="history.go(-1)" value ="Cancel"/> 
    </html:form> </div>
</div>
</body>
</html>
