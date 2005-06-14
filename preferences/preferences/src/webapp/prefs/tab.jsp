<%-- HTML JSF tag libary --%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%-- Core JSF tag library --%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%-- Sakai JSF tag library --%>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<f:view>
	<sakai:view_container title="Preferences">
	<sakai:view_content>
		<h:form id="prefs_form">
				
				<sakai:tool_bar>
			  <%--sakai:tool_bar_item action="#{UserPrefsTool.processActionRefreshFrmEdit}" value="Refresh" /--%>
 		    <sakai:tool_bar_item action="#{UserPrefsTool.processActionNotiFrmEdit}" value="Notifications" />
 		    <sakai:tool_bar_item value="Customize Tabs" />
   	  	</sakai:tool_bar>

				<br>
				

				<h:panelGroup rendered="#{UserPrefsTool.tabUpdated}">
					<jsp:include page="prefUpdatedMsg.jsp"/>	
				</h:panelGroup>
				
				<sakai:messages />
			
				<br><br>
				<h:outputText value="Customize Tabs" style="font-size: 12px;font-weight: bold;font-family: verdana, arial, helvetica, sans-serif;"/>
				<br>
				<h:outputText value="To add site or remove a site from Sites Visible in Tabs list box, select the site and use the right or left arrows to move the site. 
				To reorder sites in the Sites Visible in Tabs list box, select a site in the list and use the up and down arrows to change the order of the site." style="font-size: 12px;font-family: verdana, arial, helvetica, sans-serif;"/>

				<br><br>
				<h:panelGrid columns="4" cellspacing="0" border="0" width="90%">
					<h:outputText value="My Sites:" style="font-size: 11px;font-weight: bold;font-family: verdana, arial, helvetica, sans-serif;"/>
  		 		<f:verbatim> </f:verbatim>
  		 		<h:outputText value="Sites Visible in Tabs:" style="font-size: 11px;font-weight: bold;font-family: verdana, arial, helvetica, sans-serif;"/>
  		 		<f:verbatim> </f:verbatim>
							
					<%-- the list of preferences --%>					
					<h:selectManyListbox value="#{UserPrefsTool.selectedExcludeItems}" size="10" style="width: 100px;font-size: 12px;font-family: verdana, arial, helvetica, sans-serif;">
						<f:selectItems value="#{UserPrefsTool.prefExcludeItems}" />
					</h:selectManyListbox>

					<h:panelGrid columns="1" >
						<h:outputText value="Move Selected" style="font-size: 11px;font-family: verdana, arial, helvetica, sans-serif;"/>
						<h:commandButton id="add" value=">" action="#{UserPrefsTool.processActionAdd}"></h:commandButton>
						<h:commandButton id="remove" value="<" action="#{UserPrefsTool.processActionRemove}"></h:commandButton>
						
						<h:outputText value="Move All" style="font-size: 11px;font-family: verdana, arial, helvetica, sans-serif;"/>
						<h:commandButton id="addAll" value=">>" action="#{UserPrefsTool.processActionAddAll}"></h:commandButton>
						<h:commandButton id="removeAll" value="<<" action="#{UserPrefsTool.processActionRemoveAll}"></h:commandButton>
					</h:panelGrid>

					<%-- the list of preferences --%>					
					<h:selectManyListbox value="#{UserPrefsTool.selectedOrderItems}" size="10" style="width: 100px;font-size: 12px;font-family: verdana, arial, helvetica, sans-serif;">
						<f:selectItems value="#{UserPrefsTool.prefOrderItems}" />
					</h:selectManyListbox>						
					
					<h:panelGrid columns="1">	
					<h:commandLink action="#{UserPrefsTool.processActionMoveUp}"> <h:graphicImage value="prefs/Up-Arrow.gif"/> </h:commandLink>
					<h:commandLink action="#{UserPrefsTool.processActionMoveDown}"> <h:graphicImage value="prefs/Down-Arrow.gif"/> </h:commandLink>
							
					</h:panelGrid>

   			</h:panelGrid>
   			<br><br>
				<sakai:button_bar>
					<sakai:button_bar_item action="#{UserPrefsTool.processActionSave}" value="Update Preferences" />
					<sakai:button_bar_item immediate="true" action="#{UserPrefsTool.processActionCancel}" value="Cancel" />
				</sakai:button_bar>


		 </h:form>
		 <sakai:peer_refresh value="#{UserPrefsTool.refreshElement}" />
	</sakai:view_content>
	</sakai:view_container>
</f:view>
