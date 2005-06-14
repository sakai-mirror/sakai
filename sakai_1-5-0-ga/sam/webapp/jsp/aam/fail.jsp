<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<bean:parameter id="fail" name="failMessage" value=""/>
<html:html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<title>
A system error has occured.
</title>
</head>
<body bgcolor="#ffffff">
<h3>A system error has occured.</h3>
<!--
Holy Hades! This error scares the bezelzebub out of me!
-->
<pre>
A system error has occured in an internal Action.  Please note the action that
triggered this problem and the time and date that it occured and notify your
system administrator.
</pre>
  <font color="red">
    <bean:write name="fail" />
  </font>
</body>
</html:html>
