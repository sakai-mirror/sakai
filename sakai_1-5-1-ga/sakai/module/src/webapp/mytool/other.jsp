<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<%
		response.setContentType("text/html; charset=UTF-8");
		response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
		response.addDateHeader("Last-Modified", System.currentTimeMillis());
		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
		response.addHeader("Pragma", "no-cache");
%>

<f:loadBundle basename="org.sakaiproject.tool.mytool.bundle.Messages" var="msgs"/>

<f:view>
<sakai:view_container title="#{msgs.sample_title}">
<h:form>

	<sakai:tool_bar_message value="#{msgs.sample_other_toolbarmsg}" />

	<sakai:view_content>

		<sakai:messages />

		<sakai:doc_section_title>
			<h:outputText value="#{msgs.sample_other_heading_greeting}"/>
		</sakai:doc_section_title>

		<sakai:doc_section>
			<h:outputText value="#{MyTool.greeting}" />
			<h:outputText value="#{MyTool.userName}" />
			<sakai:date_output value="#{MyTool.date}" />
		</sakai:doc_section>

		<sakai:button_bar>
			<sakai:button_bar_item
					action="#{MyTool.processActionDoItAgain}"
					value="#{msgs.sample_other_btnbar}" />
		</sakai:button_bar>

	</sakai:view_content>

</h:form>
</sakai:view_container>
</f:view>
