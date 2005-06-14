<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ page errorPage="index_error.jsp" %>
<jsp:useBean id="subpoolTree" scope="session" class="org.navigoproject.business.entity.questionpool.model.QuestionPoolTreeImpl" />
<jsp:useBean id="questionpool" scope="session" class="org.navigoproject.ui.web.form.questionpool.QuestionPoolForm" />
<html:html>
<head>
<title>Remove Question</title>
<link href="<%=request.getContextPath()%>/jsp/aam/stylesheets/main.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/css/main.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/jsp/aam/stylesheets/nav.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#ffffff" > 
<html:form action="removeQuestion.do" method="post">
<h1>Remove Question Confirmation</h1>
<br>
<div class="h1unit">
Are you sure you want to remove the following question(s)?
</div>

<br>
<h2>Question Text</h2>  
<br>

<div class="h2unit">
<logic:iterate id="qpool" collection='<%=session.getAttribute("selectedItems")%>'>
<bean:write name="qpool"  property="itemText" />
<br>
</logic:iterate>

<br>
<br>

  <center>
  <html:submit>
     <bean:message key="button.remove"/>
  </html:submit>

<html:reset onclick="history.go(-1)" value="Cancel"/>

  </center>
</div>
</html:form>
</body>
</html:html>
