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
<sakai:view_container title="#{msgs.pt_title_main}">
	<h:form>
		<sakai:tool_bar>
			<sakai:tool_bar_item
				action="#{PresentationTool.processActionRefreshPresentations}"
				value="#{msgs.pt_refresh_button}" />
			<sakai:tool_bar_item
				action="#{PresentationTool.processActionHelp}"
				value="#{msgs.pt_help_button}" />
		</sakai:tool_bar>
		<sakai:view_content>
            	<sakai:messages />
             <sakai:instruction_message value="#{PresentationTool.instructionMessage}"/>
			<%-- the list of presentations --%>
			<sakai:flat_list value="#{PresentationTool.presentations}" var="pres">
				<h:column>	<%--  View column.  --%>
					<f:facet name="header">
						<h:outputText value="#{msgs.pt_col_head_view}"/>
					</f:facet>
					<h:commandLink action="#{pres.processActionListView}">
						<h:outputText value="#{msgs.pt_link_view}"/>
					</h:commandLink>
				</h:column>
				<h:column rendered="#{PresentationTool.allowShow}">
					<f:facet name="header">
						<h:outputText value="#{msgs.pt_col_head_show}"/>
					</f:facet>
					<h:commandLink action="#{pres.processActionListShow}">
						<h:outputText value="#{msgs.pt_link_show}"/>
					</h:commandLink>
				</h:column>
				<h:column>	<%--  Join column.  --%>
					<f:facet name="header">
						<h:outputText value="#{msgs.pt_col_head_join}"/>
					</f:facet>
					<h:commandLink action="#{pres.processActionListJoin}">
						<h:outputText value="#{msgs.pt_link_join}" rendered="#{pres.showing}" />
					</h:commandLink>
				</h:column>
				<h:column>	<%--  The title column.  --%>
					<f:facet name="header">
						<h:outputText value="#{msgs.pt_col_head_title}" />
					</f:facet>
					<h:outputText value="#{pres.title}"/>
				</h:column>
				<h:column>	<%--  The title column.  --%>
					<f:facet name="header">
						<h:outputText value="#{pt_col_head_slides}" />
					</f:facet>
					<h:outputText value="#{pres.slideCount}"/>
				</h:column>
				<h:column>	<%--  Activity column.  --%>
					<f:facet name="header">
						<h:outputText value="#{pt_col_head_activity}"/>
					</f:facet>
					<h:outputText value="#{pres.modificationDate}"/>
				</h:column>
			</sakai:flat_list>
		</sakai:view_content>
	</h:form>
</sakai:view_container>
</f:view>
