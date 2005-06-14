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

<f:loadBundle basename="org.sakaiproject.tool.annc.bundle.Messages" var="msgs"/>

<f:view>
<sakai:view_container title="#{msgs.annc_read_title}">
<h:form>

	<sakai:tool_bar>
		<sakai:tool_bar_item
			action="#{AnnouncementTool.processActionReadNew}"
			value="#{msgs.annc_read_new}" />
		<sakai:tool_bar_item
			action="#{AnnouncementTool.processActionReadDelete}"
			value="#{msgs.annc_read_delete}" />
		<sakai:tool_bar_item
			action="#{AnnouncementTool.processActionReadEdit}"
			value="#{msgs.annc_read_edit}" />
	</sakai:tool_bar>

	<sakai:view_content>

		<sakai:messages />

		<%-- the document display --%>
		<sakai:doc_properties>
 
			<%-- the selected announcement --%>
			<h:outputText value="#{msgs.annc_prop_hdr_subject}"/>
			<h:outputText value="#{AnnouncementTool.readAnnc.announcement.header.subject}"/>
		
			<h:outputText value="#{msgs.annc_prop_hdr_from}"/>
			<h:outputText value="#{AnnouncementTool.readAnnc.announcement.header.from.displayName}"/>
	
			<h:outputText value="#{msgs.annc_prop_hdr_date}"/>
			<%-- TODO: .toStringLocalFull --%>
			<h:outputText value="#{AnnouncementTool.readAnnc.announcement.header.date}"/>

		</sakai:doc_properties>

		<sakai:doc_section_title>
			<h:outputText value="#{msgs.annc_doc_title_message}"/>
		</sakai:doc_section_title>

		<sakai:doc_section>
			<h:outputText value="#{AnnouncementTool.readAnnc.announcement.body}" escape="true"/>
		</sakai:doc_section>

		<sakai:button_bar>
			<sakai:button_bar_item
					action="#{AnnouncementTool.processActionReadBack}"
					value="#{msgs.annc_read_back}" />
		</sakai:button_bar>

	</sakai:view_content>

</h:form>
</sakai:view_container>
</f:view>
