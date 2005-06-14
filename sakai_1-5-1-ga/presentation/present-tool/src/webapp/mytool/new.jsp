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
<sakai:view_container title="#{msgs.pt_title_new}">
<h:form>

	<sakai:view_content>

		<h:messages showSummary="true" showDetail="true" />
	
		<sakai:instruction_message value="#{msgs.pt_create_instructions}" />
	
		<sakai:group_box title="#{msgs.pt_create_groupbox}">
			<sakai:panel_edit>
	 
				<h:outputText value="#{msgs.pt_create_title_prompt}"/>
				<h:inputText value="#{PresentationTool.title}" required="true" />

				<h:outputText value="#{msgs.pt_create_author_prompt}"/>
				<h:inputText value="#{PresentationTool.author}" required="true" />

			</sakai:panel_edit>
		</sakai:group_box>
		
		<sakai:button_bar>
			<sakai:tool_bar_item
				action="#{PresentationTool.processActionCreatePresentation}"
				value="#{msgs.pt_create_button}" />
			<sakai:tool_bar_item
				action="#{PresentationTool.processActionCancelCreate}"
				value="#{msgs.pt_cancel_button}" />
		</sakai:button_bar>

	</sakai:view_content>

</h:form>
</sakai:view_container>
</f:view>
