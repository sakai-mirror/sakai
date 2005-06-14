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
<sakai:view_container title="#{msgs.annc_list_title}">
<h:form id="mainForm">

	<sakai:tool_bar>
		<sakai:tool_bar_item
			action="#{AnnouncementTool.processActionListNew}"
			value="#{msgs.annc_list_new}" />
		<sakai:tool_bar_item
			action="#{AnnouncementTool.processActionListDelete}"
			value="#{msgs.annc_list_delete}" />
		<sakai:tool_bar_item
			action="#{AnnouncementTool.processActionListEdit}"
			value="#{msgs.annc_list_edit}" />
		<sakai:tool_bar_spacer />
		<sakai:tool_bar_item
			action=""
			value="#{msgs.annc_list_merge}"
			disabled="true" />
		<sakai:tool_bar_item
			action=""
			value="#{msgs.annc_list_options}"
			disabled="true" />
		<sakai:tool_bar_item
			action=""
			value="#{msgs.annc_list_permissions}"
			disabled="true" />
	</sakai:tool_bar>

	<sakai:view_content>

	<%-- Alerts --%>
	<sakai:messages />

	<sakai:pager_buttons formId="mainForm" dataTableId="XXXdataTableId" 
		firstItem="#{AnnouncementTool.pagerFirstItem}" lastItem="#{AnnouncementTool.pagerLastItem}" 
		prevText="#{msgs.annc_list_prev}" nextText="#{msgs.annc_list_next}" 
		numItems="#{AnnouncementTool.pagerNumItems}" 
		totalItems="#{AnnouncementTool.pagerTotalItems}" />
	
		<%-- the list of announcements --%>
		<sakai:flat_list value="#{AnnouncementTool.announcements}" var="annc">
			<h:column>
				<f:facet name="header">
					<h:outputText value=""/>
				</f:facet>
				<h:selectBooleanCheckbox value="#{annc.selected}"/>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value=""/>
				</f:facet>
				<h:outputText value="#{annc.draftIcon}"/>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:commandLink action="#{AnnouncementTool.processActionListSortSubject}">
						<h:outputText value="#{msgs.annc_col_hdr_subject}" />
						<h:graphicImage url="#{AnnouncementTool.sortSubjectIcon}" />
					</h:commandLink>
				</f:facet>
				<h:commandLink
						action="#{annc.processActionListRead}">
					<h:outputText value="#{annc.announcement.header.subject}"/>
				</h:commandLink>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:commandLink action="#{AnnouncementTool.processActionListSortSite}">
						<h:outputText value="#{msgs.annc_col_hdr_site}"/>
						<h:graphicImage url="#{AnnouncementTool.sortSiteIcon}" />
					</h:commandLink>
				</f:facet>
				<h:outputText value="#{annc.site}"/>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:commandLink action="#{AnnouncementTool.processActionListSortFrom}">
						<h:outputText value="#{msgs.annc_col_hdr_from}"/>
						<h:graphicImage url="#{AnnouncementTool.sortFromIcon}" />
					</h:commandLink>
				</f:facet>
				<h:outputText value="#{annc.announcement.header.from.displayName}"/>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:commandLink action="#{AnnouncementTool.processActionListSortDate}">
						<h:outputText value="#{msgs.annc_col_hdr_date}"/>
						<h:graphicImage url="#{AnnouncementTool.sortDateIcon}" />
					</h:commandLink>
				</f:facet>
				<%-- TODO: .toStringLocalFull --%>
				<h:outputText value="#{annc.announcement.header.date}"/>
			</h:column>
		</sakai:flat_list>

	</sakai:view_content>

</h:form>
</sakai:view_container>
</f:view>
