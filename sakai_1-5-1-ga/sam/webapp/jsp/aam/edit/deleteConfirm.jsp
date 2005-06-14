<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<jsp:useBean id="deleteConfirm" scope="session" class="org.navigoproject.ui.web.form.edit.DeleteConfirmForm" />
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Remove <bean:write name="deleteConfirm" property="name"/> </title>
<link href="../stylesheets/main.css" rel="stylesheet" type="text/css">
</head>
<body BGCOLOR=#FFFFFF leftmargin="0" topmargin="2" marginwidth="0" marginheight="0">

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<TR>
<TD BACKGROUND="../images/wizardtop_back.gif"><IMG SRC="../images/wizardtop.gif"></TD>
</tr>
<TR>
<TD ALIGN=LEFT WIDTH=100% VALIGN=BOTTOM>
<FONT FACE="arial, helvetica" SIZE=3 COLOR=#660000><B>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
Remove <bean:write name="deleteConfirm" property="name"/> </B>
</FONT>
&nbsp;
<FONT FACE="arial,helvetica" SIZE=1 COLOR=#660000>
</TD>
</TR>
<TR>
<TD VALIGN=TOP><img src="../images/divider2.gif" border="0"></td>
</tr>
</TABLE>
<p class="instructions">Click Cancel to exit with no changes. Click Remove to remove <bean:write name="deleteConfirm" property="name"/>. </p>

<html:form action="deleteConfirm.do" method="POST">
  <table class="tblEdit">
    
    <tr>
      <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
      <td>
        <html:submit value="Remove" property="Submit"/>
        <html:reset onclick="history.go(-1)" value="Cancel"/>
      </td>
    </tr>
  </table>
</html:form>
</body>
</html:html>
