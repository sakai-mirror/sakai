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
<script language="javascript" style="text/JavaScript">
<!--
<%@ include file="/js/treeJavascript.js" %>
//-->
</script>
<title>Copy Pool</title>
<link href="<%=request.getContextPath()%>/css/main.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/jsp/aam/stylesheets/main.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/jsp/aam/stylesheets/nav.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#ffffff" onload="collapseAllRows();flagRows()">
<div class="heading">Copy Question</div>

<br>
<html:form action="copyQuestion.do" method="post">

<h2>Question Text:</h2>  
<br>
<logic:iterate id="qpool" collection='<%=session.getAttribute("selectedItems")%>'>
<bean:write name="qpool" property="itemText" />
<br>
</logic:iterate>

<br>
<table width="100%" border="0">
<tr>
<td>
<span class="number">1</span>
</td>
<td valign="top">
<span class="instructionsSteps">Copy the above question(s) to:
</span>
</td>
</tr>
<tr>
<td></td>
<td>
<%@ include file="/jsp/aam/questionpool/poolTree.jsp" %>

</td></tr>
<tr>
<td>
<span class="number">2</span>
</td>
<td>
<span class="instructionsSteps">Click "Copy" to continue or "Cancel" to return to previous page:</span>
</td></tr>
</table>

<br>
<br>
  <center>
  <html:submit>
     <bean:message key="button.copy"/>
  </html:submit>
<html:reset onclick="history.go(-1)" value="Cancel"/>

  </center>
</html:form>
</body>
</html:html>
