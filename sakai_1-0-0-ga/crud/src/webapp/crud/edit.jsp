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

<f:loadBundle basename="org.sakaiproject.tool.crud.bundle.Messages" var="msgs"/>

<f:view>
<sakai:view_container title="#{msgs.title_edit}">
<h:form>

	<sakai:tool_bar_message value="#{CrudTool.editToolbarMsg}" />

	<sakai:view_content>

		<h:messages showSummary="false" showDetail="true" />
	
		<sakai:instruction_message value="#{CrudTool.editInstructionMsg}" />
	
		<sakai:group_box title="#{msgs.title_entry}">
			<sakai:panel_edit>
	 
				<%-- the selected entry --%>
				<h:outputText value="#{msgs.prop_hdr_name}"/>
				<h:inputText value="#{CrudTool.entry.entry.name}" required="true" />
		
				<h:outputText value="#{msgs.prop_hdr_rank}"/>
				<h:inputText value="#{CrudTool.entry.entry.rank}" required="true" />
	
				<h:outputText value="#{msgs.prop_hdr_serialNumber}"/>
				<h:inputText value="#{CrudTool.entry.entry.serialNumber}" required="true" />

			</sakai:panel_edit>
		</sakai:group_box>

		<sakai:doc_properties>
 
			<h:outputText value="#{msgs.prop_hdr_id}"/>
			<h:outputText value="#{CrudTool.entry.entry.id}"/>

			<h:outputText value="#{msgs.prop_hdr_version}"/>
			<h:outputText value="#{CrudTool.entry.entry.version}"/>

		</sakai:doc_properties>

		<sakai:button_bar>
			<sakai:button_bar_item
					action="#{CrudTool.processActionEditSave}"
					value="#{msgs.bar_save}" />
			<sakai:button_bar_item
					immediate="true"
					action="#{CrudTool.processActionEditCancel}"
					value="#{msgs.bar_cancel}" />
		</sakai:button_bar>

	</sakai:view_content>

</h:form>
</sakai:view_container>
</f:view>
