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
<sakai:view_container title="#{msgs.annc_delete_title}">
<h:form>

	<sakai:tool_bar_message value="#{msgs.annc_delete_toolbar_msg}" />

	<sakai:view_content>

		<sakai:messages />
	
		<sakai:instruction_message value="#{msgs.annc_delete_alert_msg}"/>
	
		<%-- the list of announcements selected for delete --%>
		<sakai:flat_list value="#{AnnouncementTool.announcementsSelected}" var="annc">
			<h:column>
				<f:facet name="header">
					<h:outputText value=""/>
				</f:facet>
				<h:outputText value="#{annc.draftIcon}"/>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msgs.annc_col_hdr_subject}" />
				</f:facet>
				<h:outputText value="#{annc.announcement.header.subject}"/>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msgs.annc_col_hdr_site}"/>
				</f:facet>
				<h:outputText value="#{annc.site}"/>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msgs.annc_col_hdr_from}"/>
				</f:facet>
				<h:outputText value="#{annc.announcement.header.from.displayName}"/>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="#{msgs.annc_col_hdr_date}"/>
				</f:facet>
				<%-- TODO: .toStringLocalFull --%>
				<h:outputText value="#{annc.announcement.header.date}"/>
			</h:column>
		</sakai:flat_list>
	
		<sakai:button_bar>
			<sakai:button_bar_item
					action="#{AnnouncementTool.processActionDeleteDelete}"
					value="#{msgs.annc_delete_delete}" />
			<sakai:button_bar_item
					action="#{AnnouncementTool.processActionDeleteCancel}"
					value="#{msgs.annc_delete_cancel}" />
		</sakai:button_bar>

	</sakai:view_content>

</h:form>
</sakai:view_container>
</f:view>
