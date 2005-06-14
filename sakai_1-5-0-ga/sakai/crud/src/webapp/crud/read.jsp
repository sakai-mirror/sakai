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
<sakai:view_container title="#{msgs.title_read}">
<h:form>

	<sakai:tool_bar>
		<sakai:tool_bar_item
			action="#{CrudTool.processActionReadNew}"
			value="#{msgs.bar_new}" />
		<sakai:tool_bar_item
			action="#{CrudTool.processActionReadDelete}"
			value="#{msgs.bar_delete}" />
		<sakai:tool_bar_item
			action="#{CrudTool.processActionReadEdit}"
			value="#{msgs.bar_edit}" />
	</sakai:tool_bar>

	<sakai:view_content>

		<sakai:messages />

		<%-- the document display --%>
		<sakai:doc_properties>
 
			<%-- the selected thing --%>
			<h:outputText value="#{msgs.prop_hdr_name}"/>
			<h:outputText value="#{CrudTool.entry.entry.name}"/>
		
			<h:outputText value="#{msgs.prop_hdr_rank}"/>
			<h:outputText value="#{CrudTool.entry.entry.rank}"/>

			<h:outputText value="#{msgs.prop_hdr_serialNumber}"/>
			<h:outputText value="#{CrudTool.entry.entry.serialNumber}"/>
	
			<h:outputText value="#{msgs.prop_hdr_id}"/>
			<h:outputText value="#{CrudTool.entry.entry.id}"/>

			<h:outputText value="#{msgs.prop_hdr_version}"/>
			<h:outputText value="#{CrudTool.entry.entry.version}"/>

		</sakai:doc_properties>

		<sakai:button_bar>
			<sakai:button_bar_item
					action="#{CrudTool.processActionReadBack}"
					value="#{msgs.bar_back}" />
		</sakai:button_bar>

	</sakai:view_content>

</h:form>
</sakai:view_container>
</f:view>
