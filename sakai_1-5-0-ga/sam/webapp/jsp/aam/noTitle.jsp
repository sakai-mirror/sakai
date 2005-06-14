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
<h2>Template Title Missing</h2>
  <div class="h2unit"> 
    A Template must have a title.  Please go back and enter a title for this template.<br>
		<div align="center">
        <html:form action="deleteAction.do" method="post">
          <html:reset onclick="history.go(-1)" value ="Back"/>
        </html:form>
  </div>
  </div>
</body>
</html>
