<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ page errorPage="index_error.jsp" %>
<jsp:useBean id="subpoolTree" scope="session" class="org.navigoproject.business.entity.questionpool.model.QuestionPoolTreeImpl" />
<jsp:useBean id="questionpool" scope="session" class="org.navigoproject.ui.web.form.questionpool.QuestionPoolForm" />
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Remove Pool</title>
<link href="<%=request.getContextPath()%>/jsp/aam/stylesheets/main.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/css/main.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/jsp/aam/stylesheets/nav.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#ffffff" > 
<html:form action="removePool.do" method="post">
<div class="heading">Remove Pool Confirmation</div>
<br>

<div class="h2unit">&nbsp;&nbsp;&nbsp;&nbsp;Are you sure you want to remove the following pool(s) and ALL associated subpools and questions?
</div>

<br>
<h2>Pool Names</h2>  
<div class="h2unit">
<logic:iterate id="qpool" collection='<%=session.getAttribute("selectedpools")%>'>
<bean:write name="qpool" property="displayName" />
<br>
</logic:iterate>

<br>
<br>

  <center>
  <html:submit>
     <bean:message key="button.remove"/>
  </html:submit>
<input type="button" onclick="document.location='<%=request.getContextPath()%>/questionChooser.do'" value="Cancel"/>
  </center>
</div>
</html:form>
</body>
</html:html>
