<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://jsftutorials.net/htmLib" prefix="htm" %> 

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%= request.getAttribute("sakai.html.head") %>
<body onload="<%= request.getAttribute("sakai.html.body.onload") %>">
<f:view>
		<f:loadBundle basename="org.sakaiproject.imslaunch.bundle.pageText" var="pageText"/>
		<head>
			<base href="<%=basePath%>">
			<title><h:outputText value="#{pageText.pagename_SiteGrabber}"/></title>
			<meta http-equiv="pragma" content="no-cache">
			<meta http-equiv="cache-control" content="no-cache">
			<meta http-equiv="expires" content="0">    
			<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
			<meta http-equiv="description" content="This is my page">
			<link rel="stylesheet" type="text/css" href="./style/default.css">		
		</head>
			  
		<body>
		<!-- Title -->
		<h:outputText value="#{pageText.pagename_SiteGrabber}"/>
		<p/>		
		<h:form>
			<h:panelGrid columns="3" cellspacing="0" cellpadding="2" frame="box" rowClasses="evenRows, oddRows">					
				<h:outputText value="#{pageText.form_SiteGrabber_site}"/>
				<h:selectOneMenu id="siteid" value="#{sitegrabber.siteId}">
					<f:selectItems value="#{sitegrabber.siteItems}"/>
				</h:selectOneMenu>	
			<h:commandButton  id="submit" action="#{sitegrabber.SiteURL}" value="#{pageText.form_SiteGrabber_submit}"/>				
			</h:panelGrid>
			<p/>		
			<h:outputText value="#{pageText.form_SiteGrabber_site}"/><h:outputText value="#{sitegrabber.site}"/>
			
			<!-- Error Messages -->
			<h:messages layout="table" showDetail="false" errorClass="errors" />
		</h:form>
		<h:form>	
			<!-- iframe -->
			<htm:iframe src="#{sitegrabber.site}" frameborder="1" height="600px" width="800px" marginheight="0" marginwidth="0" scrolling="yes"/>		
		</h:form>
	</body>
</f:view>
</body>
</html>
