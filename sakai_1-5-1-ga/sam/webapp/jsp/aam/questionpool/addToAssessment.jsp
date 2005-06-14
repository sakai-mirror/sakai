
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
<title>Add to Assessment</title>
<link href="<%=request.getContextPath()%>/css/main.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#ffffff" >
<div class="heading">Add Question</div>  

<html:form action="addToAssessment.do" method="post">

<br>
<h2>Question Text</h2>  
<br>
<logic:iterate id="qpool" collection='<%=session.getAttribute("selectedItems")%>'>
<bean:write name="qpool" property="itemText" filter="false" />
<br>
</logic:iterate>

<br>
<table width="100%" border="0">

<tr>
<td>
<span class="number">1</span>
</td>
<td valign="top">
<span class="instructionsSteps">Add the above question(s) to the following assessment:
</span>
</td>
</tr>

<tr>
<td></td>
<td valign="top">
<span class="instructionsSteps">Assessment Title:</span>
<html:select property="assessmentID" size="1" >
      <html:options collection="allAssets"  property="key" labelProperty="value" />
</html:select>
</span>

<br>
<br>

</td></tr>

<tr>
<td>
<span class="number">2</span>
</td>
<td valign="top">
<span class="instructionsSteps">Click "Save" to continue or "Cancel" to return to the previous page:</span
>
</td></tr>
</table>

<br>
<br>
  <center>
	<html:submit>
                <bean:message key="button.save"/>
        </html:submit>

<html:reset onclick="history.go(-1)" value="Cancel"/>

  </center>
</html:form>
</body>
</html:html>
