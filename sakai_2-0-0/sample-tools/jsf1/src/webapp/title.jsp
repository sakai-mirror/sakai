<%-- HTML JSF tag libary --%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%-- Core JSF tag library --%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page import="java.util.List, java.util.Iterator" %>
<f:loadBundle basename="sample.messages" var="msg"/>
<HTML>
	<HEAD> <title>Main: JSF Test Tool Title</title> 
		<%= request.getAttribute("sakai.html.head") %>
	</HEAD>
	<body onload="<%= request.getAttribute("sakai.html.body.onload") %>">
 		<f:view>
			<h:outputText value="#{msg.title}" />
		</f:view>
	</body>
</HTML>  
