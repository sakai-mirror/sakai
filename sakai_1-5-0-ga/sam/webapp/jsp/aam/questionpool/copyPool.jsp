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
<script language="javascript" style="text/JavaScript">
<!--
<%@ include file="/js/treeJavascript.js" %>
//-->
</script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Copy Pool</title>
<link href="<%=request.getContextPath()%>/css/main.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/jsp/aam/stylesheets/nav.css" rel="stylesheet" type="text/css">
</head>
<body onload="collapseAllRows();flagRows()">
<div class="heading">Copy Pool</div>

<html:form action="copyPool.do" method="post">

<br>
<h2>Pool Name</h2>  
<logic:iterate id="qpool" collection='<%=session.getAttribute("selectedpools")%>'>
<bean:write name="qpool" property="displayName" />
<br>
</logic:iterate>

<br>

<table width="100%" border="0"> 
<tr>
<td>
<span class="number">1</span>
</td>
<td valign="top">
<span class="instructionsSteps">Copy the above pool(s) to:
</span>
</td>
</tr>
<tr>
<td></td>
<td>
<table width="100%">
 <tr>
   <td width="100%">
        <html:checkbox property="rootPoolSelected"/>
<a class="doc"></a>
Question Pool Manager</td>
 </tr>

</table>

<%@ include file="/jsp/aam/questionpool/poolTree.jsp" %>

</td></tr>
<tr>
<td>
<span class="number">2</span>
</td>
<td valign="top">
<span class="instructionsSteps">Click "Copy" to continue or "Cancel" to return to previous page:</span>
</td></tr>
</table>

<br>
<br>
  <center>
  <html:submit>
     <bean:message key="button.copy"/>
  </html:submit>

<input type="button" onclick="document.location='<%=request.getContextPath()%>/questionChooser.do'" value="Cancel"/>

</center>
</html:form>
</body>
</html:html>
