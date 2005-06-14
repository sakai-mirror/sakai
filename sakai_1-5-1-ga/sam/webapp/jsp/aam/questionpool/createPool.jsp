<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ page errorPage="index_error.jsp" %>
<jsp:useBean id="questionpool" scope="session" class="org.navigoproject.ui.web.form.questionpool.QuestionPoolForm" />
<jsp:useBean id="subpoolTree" scope="session" class="org.navigoproject.business.entity.questionpool.model.QuestionPoolTreeImpl" />
<jsp:useBean id="parentpools" scope="session" class="java.util.ArrayList" />
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script language="javascript" style="text/JavaScript">
<!--
<%@ include file="/js/treeJavascript.js" %>
//-->
</script>


<logic:equal name="createpooluse" value="create">
<title>Add Pool</title>
</logic:equal>
<logic:equal name="createpooluse" value="edit">
<title>Edit Pool</title>
</logic:equal>
<logic:equal name="createpooluse" value="createsub">
<title>Add Subpool</title>
</logic:equal>
<logic:equal name="createpooluse" value="editsub">
<title>Edit Subpool</title>
</logic:equal>

<link href="<%=request.getContextPath()%>/css/main.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/jsp/aam/stylesheets/nav.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#ffffff" onload="collapseRowsByLevel(<%=questionpool.getHtmlIdLevel()%>);flagRows()">
<div class="heading">Samigo</div>
<html:form action="editpool.do" method="post"> 
<logic:notEmpty name="questionpool" property="currentPool"> 
<bean:define name="questionpool" property="currentPool" id="thisPool" type="org.navigoproject.ui.web.form.questionpool.QuestionPoolBean" /> 
<bean:define name="questionpool" property="currentPool" id="headerPool" /> <bean:define name="thisPool" property="properties" id="props" /> 
</logic:notEmpty> 

<logic:match name="createpooluse" value="create"> 
<h1>Add Pool</h1>
</logic:match>

<logic:match name="createpooluse" value="edit">
<div align="right"><input type="button" value="Pool Manager"  onclick="document.location='<%=request.getContextPath()%>/questionChooser.do'">
</div>
<span class="h2">
<bean:write name="questionpool" property="currentPool.name" />
<br/>
<br/>
<logic:notEmpty name="parentpools">

<logic:iterate name="parentpools" id="parent" type="org.navigoproject.business.entity.questionpool.model.QuestionPool" indexId="ctr">
<html:link page="/startCreatePool.do?use=edit" paramName="parent" paramProperty="id" paramId="id">
<bean:write name="parent" property="displayName" /> 
</html:link>
>
</logic:iterate>
<span class="h2">
<bean:write name="questionpool" property="currentPool.name" />
</span>
</logic:notEmpty>

</div>
</logic:match>
<br/>


<logic:match name="createpooluse" value="edit">
<h2>General Information:</h2>

<table width="100%"class="tblMain">
  <tr>
    <td width="20%"> Pool Name:</td>
    <td> <html:text name="questionpool" property="currentPool.name" /></td>
  </tr>
  <tr>
    <td> Creator:</td>
    <td> 
	<bean:write name="questionpool" property="currentPool.properties.owner.displayName" />
    </td>
  <tr>
    <td colspan="2" height="10"></td>
  </tr>
  </tr>
<logic:empty name="parentpools">
  <tr>
    <td> Department/Group:</td>
    <td> <html:textarea name="questionpool" 
           property="currentPool.properties.organizationName" /></td>
  </tr>
</logic:empty>
  <tr>
    <td> Description:</td>
    <td> <html:textarea name="questionpool" 
           property="currentPool.description" /></td>
  </tr>
<logic:empty name="parentpools">
  <tr>
    <td> Objectives:</td>
    <td> <html:text name="questionpool" 
           property="currentPool.properties.objectives" /></td>
  </tr>
  <tr>
    <td> Keywords:</td>
    <td> <html:text name="questionpool" 
           property="currentPool.properties.keywords" /></td>
  </tr>
</logic:empty>
</table>
</logic:match>


<logic:match name="createpooluse" value="create">
<table width="100%" class="tblMain">
  <tr>
    <td width="20%"> Pool Name:</td>
    <td> <html:text name="questionpool" property="currentPool.name" /></td>
  </tr>
  <tr>
    <td> Creator:</td>
    <td>
        <bean:write name="questionpool" property="currentPool.properties.owner.displayName" />
    </td>
  </tr>
  <tr>
    <td colspan="2" height="10"></td>
  </tr>
  <tr>
    <td> Department/Group:<br/>(optional)</td>
    <td> <html:textarea name="questionpool"
           property="currentPool.properties.organizationName" /></td>
  </tr>
  <tr>
    <td> Description:<br/>(optional)</td>
    <td> <html:textarea name="questionpool"
           property="currentPool.description" /></td>
  </tr>
  <tr>
    <td> Objectives:<br/>(optional)</td>
    <td> <html:text name="questionpool"
           property="currentPool.properties.objectives" /></td>
  </tr>
  <tr>
    <td> Keywords:<br/>(optional)</td>
    <td> <html:text name="questionpool"
           property="currentPool.properties.keywords" /></td>
  </tr>
</table>
</logic:match>


</html:form>
<br>
<br>

<!-- BEGIN: if edit , need to dispaly questions, and subpools  -->
<logic:equal name="createpooluse" value="edit">

<br>
<%@ include file="/jsp/aam/questionpool/subpoolTreeTable.jsp" %>
<br>

<br>
<%@ include file="/jsp/aam/questionpool/questionTreeTable.jsp" %>
<br>


<br>
<br>
</logic:equal>

<!-- END: if edit , need to dispaly questions, and subpools  -->
<html:form action="editpool.do" method="post">
<center>
<input type="button" value="Save"  onclick="document.forms[0].submit();">
<input type="button" onclick="document.location='<%=request.getContextPath()%>/questionChooser.do'" value="Cancel"/>

</center>
</html:form>


</body>
</html:html>
