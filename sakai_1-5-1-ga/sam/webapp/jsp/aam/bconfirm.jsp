<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>confirm.jsp</title>
  </head>

  <body>
<html:form action="bdeleteAction.do" method="post">
<table width=100%>
<tr><td align="center" bgcolor="#99ccff">
<b>Delete Confirmation</b></td></tr></table>
<p>
<font color=red size=+1><center><b><bean:write name="delete.message"/></b>.</font><br><br>
<center>
<html:submit value="  OK  " property="submit" /><html:reset onclick="history.go(-1)" value ="Cancel"/>
</html:form>
  </body>
</html>
