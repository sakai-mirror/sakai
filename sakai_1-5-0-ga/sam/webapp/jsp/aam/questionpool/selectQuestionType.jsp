<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ page errorPage="index_error.jsp" %>
<jsp:useBean id="itemActionForm" scope="session" class="org.navigoproject.ui.web.asi.author.item.ItemActionForm" />
<html:html>
<head>
<title>Add A Question</title>
<link href="<%=request.getContextPath()%>/css/main.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#ffffff">
<div class="heading">Add Question to Pool</div>  
<html:form action="/asi/author/item/itemAction.do?target=questionpool" method="post">

<table>
<tr>
<td>
<span class="number">1</span>
</td>
<td valign="top">
<span class="instructionsSteps">
Select question type:</span>
</td>
</tr>
<tr>
<td></td>
<td>
<br/>
<span class="instructionsSteps">
Question Type:</span>
<html:select property="action" size="1">
      <html:option value="0">-- Select --</html:option>
      <html:option value="Multiple Choice">Multiple Choice (single correct)</html:option>
      <html:option value="Multiple Correct Answer">Multiple Choice (multiple correct)</html:option>
      <html:option value="Multiple Choice Survey">Multiple Choice Survey</html:option>
      <html:option value="Essay">Short Answer/Essay</html:option>
      <html:option value="Fill In the Blank">Fill in the Blank</html:option>
<%--      <html:option value="Matching">Matching</html:option> --%>
      <html:option value="True False">True / False Question</html:option>
<%--      <html:option value="Audio Recording">Audio Recording</html:option> --%>
      <html:option value="File Upload">File Upload</html:option>
      <html:option value="Import from Question Pool">Import from Question Pool</html:option>
    </html:select>

</span>
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
Click "Save" to continue or "Cancel" to return to the previous page:</span>  
</td>
</tr>
</table>
<br/>
<br/>

  <center>
	<html:submit>
                <bean:message key="button.save"/>
        </html:submit>

<html:reset onclick="history.go(-1)" value="Cancel"/>
  </center>
</html:form>
</body>
</html:html>
