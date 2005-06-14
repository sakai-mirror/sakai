<%--
/**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version $Id: SimpleFormLogin_content.jsp,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
 */
--%>
<%!
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger("org.navigoproject.ui.web.item.login.SimpleFormLogin.jsp");
%>
<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>
Navigo: Simple Login
</title>
</head>
<html:base/>
<body bgcolor="#ffffff">
<h1>
Simple Login
</h1>

<logic:messagesPresent>
   <bean:message key="errors.header"/>
   <ul>
   <html:messages id="error">
      <li><bean:write name="error"/></li>
   </html:messages>
   </ul><hr>
   <bean:message key="errors.footer" />
</logic:messagesPresent>

<html:form action="/login/SimpleFormLogin" method="POST">
Username<br>
<html:text property="username"/>
<br><br>
Password<br>
<html:password property="password"/>
<br><br>
<html:submit property="submit" value="Submit"/>&nbsp;
<html:reset value ="Reset"/>
</html:form>

</body>
</html:html>