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
<title>Move Pool</title>
<link href="<%=request.getContextPath()%>/css/main.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/jsp/aam/stylesheets/nav.css" rel="stylesheet" type="text/css">
</head>
<body onload="collapseAllRows();flagRows()">
<div class="heading">Move Pool</div>  

<br>
<h2>Pool Name</h2>  
<html:form action="movePool.do" method="post">
<br>
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
<span class="instructionsSteps">
Move the above pool(s) to:
</span>
</td>
</tr>

<tr>
<td></td>
<td>
<table width="100%" border ="0">
 <tr>
   <td width="100%" > 
        <html:radio property="destPool" value="0"/>
<a class="doc"></a>
Question Pool Manager</td>
 </tr>
</table>

<%@ include file="/jsp/aam/questionpool/movePoolTree.jsp" %>
</td></tr>

<tr>
<td>
<span class="number">2</span>
</td>
<td valign="top">
<span class="instructionsSteps">
Click "Move" to continue or "Cancel" to return to the previous page:</span>  
</td></tr>
</table>

<br>
<br>
  <center>
	<html:submit>
                <bean:message key="button.move"/>
        </html:submit>
<input type="button" onclick="document.location='<%=request.getContextPath()%>/questionChooser.do'" value="Cancel"/>
  </center>
</html:form>
</body>
</html:html>
