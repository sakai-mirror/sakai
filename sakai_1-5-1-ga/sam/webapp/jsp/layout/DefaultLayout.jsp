<%--
/**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Pamela Song <casong@indiana.edu>
 * @version $Id: DefaultLayout.jsp,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
 */
--%>
<%!
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger("org.navigoproject.ui.web.item.jsp.layout.DefaultLayout.jsp");
%>

<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<HTML>
<HEAD>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<TITLE><tiles:getAsString name="title"/></TITLE>
</HEAD>
<BODY BGCOLOR="#FFFFFF" TOPMARGIN="0" LEFTMARGIN="0" MARGINWIDTH="0" MARGINHEIGHT="0">
<TABLE border="0" width="100%" cellspacing="0" cellspacing="0">
<TR>
<TD colspan="2"><tiles:insert attribute="header" /></TD>
</TR>
<TR>
<TD valign="top" align="left" bgcolor=ffffff>
<table width="100%" cellspacing="5" cellpadding="5"><tr><td>
<tiles:insert attribute='body' />
</td></tr></table>
</TD>
</TR>
</TABLE>
</BODY>
</HTML>