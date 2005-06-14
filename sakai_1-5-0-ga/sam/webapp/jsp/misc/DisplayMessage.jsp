<%--
/**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: DisplayMessage.jsp,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
 */
--%>
<%!
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger("/jsp/misc/DisplayMessage.jsp");
%>
<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ page language="java" import="org.navigoproject.ui.web.messaging.DisplayMessage"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%
DisplayMessage message = DisplayMessage.getInstance(request);
String url;
if(message.isTerminus())
{
  url = request.getContextPath()+"/Exit.do?forward=TERMINUS";
}
else
{
  url = request.getContextPath()+"/misc/DisplayMessage.do";
}
%>
<html:html xhtml="true" locale="true">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%
  if(message.getTimeout() > -1)
  {
  %>
    <META HTTP-EQUIV="REFRESH" CONTENT="<%=message.getTimeout()%>; URL=<%=url%>">
  <%
  }
%>
<title>
  <%=message.getTitle()%>
</title>
<html:base/>
</head>
<body>
  <html:errors />
  <p>&nbsp;</p>
  <%=message.getMessage()%>
  <p>&nbsp;</p>

  <html:form action="/misc/DisplayMessage" method="POST">
  <p align="center">
  <INPUT TYPE=SUBMIT NAME="submitDisplayMessage" VALUE="OK" style="width:120;height:40;font-size:16;font-weight:bold;color:black">
  </p>
  </html:form>
</body>
</html:html>
