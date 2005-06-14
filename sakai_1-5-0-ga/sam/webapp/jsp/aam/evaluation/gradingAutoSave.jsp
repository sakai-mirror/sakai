<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" import="java.sql.*" errorPage="" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<html>
<head>
<title>Evaluation</title>
</head>
<FRAMESET ROWS="100%,*">
   <html:frame frameName="mainFrame" href="totalScores.jsp" frameborder="0" noresize="true">
</html:frame>
   <html:frame frameName="dummyFrame"  forward="NAVIGATION" frameborder="0">
</html:frame>
</FRAMESET>
</html>
