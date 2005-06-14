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

	<sakai:tool_bar>
		<sakai:tool_bar_item
			action="#{MyTool.processActionDoIt}"
			value="#{msgs.sample_one_cmd_go}" />
	</sakai:tool_bar>

	<sakai:view_content>

		<h:messages showSummary="true" showDetail="true" />

		<sakai:instruction_message value="#{msgs.sample_one_instructions}" />
	
		<sakai:group_box title="#{msgs.sample_one_groupbox}">
			<sakai:panel_edit>
	 
				<h:outputText value="#{msgs.sample_one_hdr_name}"/>
				<h:inputText value="#{MyTool.userName}" required="true" />

				<h:outputText value="date"/>
				<sakai:date_input value="#{MyTool.date}" />

			</sakai:panel_edit>
		</sakai:group_box>

		<sakai:button_bar>
			<sakai:button_bar_item
					action="#{MyTool.processActionDoIt}"
					value="#{msgs.sample_one_cmd_go}" />
		</sakai:button_bar>

	</sakai:view_content>

</h:form>
</sakai:view_container>
</f:view>
