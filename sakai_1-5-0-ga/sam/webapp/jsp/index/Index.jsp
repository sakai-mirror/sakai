<%--
/**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: Index.jsp,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
 */
--%>
<%!
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger("org.navigoproject.ui.web.index.Index.jsp");
%>
<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<html:html xhtml="true" locale="true">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>
Navigo: Index
</title>
<html:base/>
</head>
<body bgcolor="#ffffff">
<h1>
Navigo Index
</h1>
<p>
<html:errors/>
</p>
<p>
<br><html:link forward="LOGIN">/Login.do</html:link><br>
<br><html:link forward="SIMPLE_FORM_LOGIN">/login/SimpleFormLogin.do</html:link><br>
<br><html:link forward="BECOME">/login/Become.do</html:link><br>
</body>
</html:html>
