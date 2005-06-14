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
<sakai:view_container title="#{msgs.annc_edit_title}">
<h:form>

	<sakai:tool_bar_message value="#{AnnouncementTool.editToolbarMsg}" />

	<sakai:view_content>

		<h:messages showSummary="false" showDetail="true" />
	
		<sakai:instruction_message value="#{AnnouncementTool.editInstructionMsg}" />
	
		<sakai:group_box title="#{msgs.annc_doc_title_announcement}">
			<sakai:panel_edit>
	 
				<%-- the selected announcement --%>
				<h:outputText value="#{msgs.annc_prop_hdr_subject}"/>
				<h:inputText value="#{AnnouncementTool.editAnnc.header.subject}" required="true" />
		
				<h:outputText value="#{msgs.annc_prop_hdr_message}"/>
				<h:inputTextarea value="#{AnnouncementTool.editAnnc.body}" required="true" />	
	
			</sakai:panel_edit>
		</sakai:group_box>
	
		<sakai:button_bar>
			<sakai:button_bar_item
					action="#{AnnouncementTool.processActionEditSave}"
					value="#{msgs.annc_edit_save}" />
			<sakai:button_bar_item
					action="#{AnnouncementTool.processActionEditPreview}"
					value="#{msgs.annc_edit_preview}" />
			<sakai:button_bar_item
					action="#{AnnouncementTool.processActionEditSaveDraft}"
					value="#{msgs.annc_edit_save_draft}" />
			<sakai:button_bar_item
					immediate="true"
					action="#{AnnouncementTool.processActionEditCancel}"
					value="#{msgs.annc_edit_cancel}" />
		</sakai:button_bar>

	</sakai:view_content>

</h:form>
</sakai:view_container>
</f:view>
