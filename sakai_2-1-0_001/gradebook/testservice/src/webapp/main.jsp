<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
<head>
	<title>Main: Gradebook Service Test</title>
	<%= request.getAttribute("sakai.html.head") %>
</head>
<body onload='<%= request.getAttribute("sakai.html.body.onload") %>'>
	<f:view>
		<h:form id="helloForm">
			<h:messages />

			<h:outputText value="Gradebook UID: " />
			<h:inputText id="gbUid" value="#{gradebookBean.uid}" required="false"/>

			<h:commandButton id="submit" actionListener="#{gradebookBean.search}" value="Search" /><br />

			<hr/>

			<h:outputText rendered="#{not empty gradebookBean.uid}" value="Found it = #{gradebookBean.uidFound}" />
		</h:form>
	</f:view>
</body>
</html>
