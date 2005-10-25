<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<f:loadBundle basename="org.sakaiproject.tool.su.bundle.Messages" var="msgs"/>

<f:view>
<sakai:view_container title="#{msgs.tool_title}">

	<sakai:title_bar value="#{msgs.tool_title}" helpDocId="su_main"/>

</sakai:view_container>
</f:view>
