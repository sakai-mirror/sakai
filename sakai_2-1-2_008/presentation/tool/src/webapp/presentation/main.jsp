<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<f:loadBundle basename="org.sakaiproject.tool.presentation.bundle.Messages" var="msgs"/>

<f:view>
<sakai:view_container title="#{msgs.pt_title_main}">
<sakai:view_content>
	<h:form>
		<sakai:tool_bar>
			<sakai:tool_bar_item
				action="#{PresentationTool.processActionRefreshPresentations}"
				value="#{msgs.pt_refresh_button}" />
			<sakai:tool_bar_item
				action="#{PresentationTool.processActionHelp}"
				value="#{msgs.pt_help_button}" />
		</sakai:tool_bar>
            	<sakai:messages />
             <sakai:instruction_message value="#{PresentationTool.instructionMessage}"/>
			<%-- the list of presentations --%>
			<sakai:flat_list value="#{PresentationTool.presentations}" var="pres">
				<h:column>	<%--  The title column.  --%>
					<f:facet name="header">
						<h:outputText value="#{msgs.pt_col_head_title}" />
					</f:facet>
					<sakai:doc_section>
						<h:outputText value="#{pres.title}"/>
					</sakai:doc_section>
					<sakai:doc_section>
						<h:commandLink action="#{pres.processActionListView}">
							<h:outputText value="#{msgs.pt_link_view}"/>
						</h:commandLink>
						<h:outputText value=" | " rendered="#{PresentationTool.allowShow}"/>
						<h:commandLink action="#{pres.processActionListShow}">
							<h:outputText value="#{msgs.pt_link_show}" rendered="#{PresentationTool.allowShow}"/>
						</h:commandLink>
						<h:outputText value=" | " rendered="#{pres.showing}"/>
						<h:commandLink action="#{pres.processActionListJoin}">
							<h:outputText value="#{msgs.pt_link_join}" rendered="#{pres.showing}" />
						</h:commandLink>
					</sakai:doc_section>
				</h:column>
				<h:column>	<%--  The title column.  --%>
					<f:facet name="header">
						<h:outputText value="#{msgs.pt_col_head_slides}" />
					</f:facet>
					<h:outputText value="#{pres.slideCount}"/>
				</h:column>
				<h:column>	<%--  Activity column.  --%>
					<f:facet name="header">
						<h:outputText value="#{msgs.pt_col_head_activity}"/>
					</f:facet>
					<h:outputText value="#{pres.modificationDate}"/>
				</h:column>
			</sakai:flat_list>
	</h:form>
</sakai:view_content>
</sakai:view_container>
</f:view>
