<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="fileUpload" scope="session" class="org.navigoproject.ui.web.form.edit.FileUploadForm" />
<jsp:useBean id="description" scope="session" class="org.navigoproject.ui.web.form.edit.DescriptionForm" />
<% int count=0; //this sets up the row counter %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title> <bean:write name="fileUpload" property="title"/> </title>
<link href="<%=request.getContextPath()%>/stylesheets/main.css" rel="stylesheet" type="text/css">

<script language="javascript"><!--
function hasHTTP(s) {
  if (!s) return false;
  if (s.indexOf("http")==0) return true;
  return false;
}

// if a "numeric" field contains non-numeric values set to 0
function fixMyURL(o){
  s=o.value;
  if (!hasHTTP(s)){
    o.value="http://" + s;
  }
}
//--></script>

</head>
<body bgcolor="#ffffff" topmargin=0 marginheight=0 leftmargin=0 marginwidth=0>
<html:form action="fileUpload.do" method="POST" enctype="multipart/form-data">
<p class="pageTitle"> <bean:write name="fileUpload" property="title"/> </p>
<img src="<%=request.getContextPath()%>/images/divider2.gif">
<logic:equal name="editorRole" value="templateEditor">
<p class="instructions">Specify the inline media to be displayed and give descriptive Information. Then click the Submit button to save your input. No input will be saved if you click the Cancel button. </P>
<span class="heading2"><em>Template Editor:</em>
      <jsp:getProperty name="description" property="templateName"/>
</span>
</logic:equal>
<logic:equal name="editorRole" value="assessmentEditor">
<br>
<span class="heading2"><em>Assessment Editor:</em>
      <jsp:getProperty name="description" property="name"/>
</span>
</logic:equal>
  <table WIDTH=100% CELLPADDING=6 CELLSPACING=2 BORDER=1>
    <logic:equal name="fileUpload" property="isEdit" value="false">
      <tr  bgcolor=<%= (count++ % 2 ==0?"#E1E1E1":"#FFFFFF") %> >
        <td class="tdSideRedText">Source</td>
        <td>
          <html:hidden property="isHtmlInline"/>
          <html:radio property="source" value="0"/>
          Upload a file from your computer:
          <html:file property="newfile"/>
          <br>
          <html:radio property="source" value="1"/>
          Link to a URL:
          <html:text property="link" onchange="fixMyURL(this)"/>
          <br>
        </td>
      </tr>
    </logic:equal>
    <logic:equal name="fileUpload" property="isEdit" value="true">
      <tr  bgcolor=<%= (count++ % 2 ==0?"#E1E1E1":"#FFFFFF") %> >
        <td class="tdSideRedText">Source</td>
        <td>
          <logic:equal name="fileUpload" property="link" value="">
            <bean:write name="fileUpload" property="fileName" />
          </logic:equal>
          <logic:notEqual name="fileUpload" property="link" value="">
            <%--bean:write name="fileUpload" property="link" /--%>
            Link to a URL:
            <html:text property="link" onchange="fixMyURL(this)" />
          </logic:notEqual>
        </td>
      </tr>
    </logic:equal>
    <tr  bgcolor=<%= (count++ % 2 ==0?"#E1E1E1":"#FFFFFF")%> >
      <td class="tdSideRedText">Title</td>
      <td>
        <html:text  property="name" />
        <br>
      </td>
    </tr>
    <tr  bgcolor=<%= (count++ % 2 ==0?"#E1E1E1":"#FFFFFF") %> >
      <td class="tdSideRedText">Description <br> (Optional)</td>
      <td>
        <html:textarea cols="50" rows="4" property="description" />
        <br>
      </td>
    </tr>
    <tr  bgcolor=<%= (count++ % 2 ==0?"#E1E1E1":"#FFFFFF") %> >
      <td class="tdSideRedText">Author/Citation <br> (Optional)</td>
      <td>
        <html:text property="author" />
        <br>
      </td>
    </tr>
    <tr  bgcolor=<%= (count++ % 2 ==0?"#E1E1E1":"#FFFFFF") %> >
      <td>&nbsp;</td>
      <td>
        <html:submit value="Submit" property="Submit"/>
        <html:reset onclick="history.go(-1)" value="Cancel"/>
      </td>
    </tr>
  </table>

</html:form>
</body>
</html:html>
