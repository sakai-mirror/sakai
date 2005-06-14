<%--
/**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: DebugSession.jsp,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
 */
--%>
<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>
Navigo - Debug Session
</title>
</head>
<body>
<h1>
Debug Session
</h1>
<html:form action="/Navigation" method="POST">
<html:submit value="<-- Back to Navigation" property="submit"/>
</html:form>
<h4>
<html:errors property="GLOBAL"/>
</h4>
<h2>
Session Attributes
</h2>
<%
java.util.Enumeration e = request.getSession().getAttributeNames();
int i=0;
while (e.hasMoreElements()) {
  String name = (String)e.nextElement();
  Object obj = request.getSession().getAttribute(name);
  i++;
  %>
  <%=i%>. <%=name%><br><pre><%=obj%></pre>
  <%
}
%>
<h2>
Request Attributes
</h2>
<%
e = request.getAttributeNames();
i=0;
while (e.hasMoreElements()) {
  String name = (String)e.nextElement();
  Object obj = request.getAttribute(name);
  i++;
  %>
  <%=i%>. <%=name%><br><pre><%=obj%></pre>
  <%
}
%>
<br/>
Request Encoding:<%=request.getCharacterEncoding()%><br/>
<br/>

<table border="1" width="100%" style="border-collapse: collapse">
<tr>
<td align="center"><font size="2">Version:</font> <code><bean:write name="BuildInfoBean" property="buildVersion" /></code></td>
<td align="center"><font size="2">Built:</font> <code><bean:write name="BuildInfoBean" property="buildTime" /></code></td>
<td align="center"><font size="2">Tag:</font> <code><bean:write name="BuildInfoBean" property="buildTag" /></code></td>
</tr>
</table>
<table border="1" width="100%" style="border-collapse: collapse">
<tr>
<td align="center"><font size="2">user-agent:</font> <code><%=request.getHeader("user-agent")%></code></td>
</tr>
</table>

</body>
</html:html>
