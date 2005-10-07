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
 		    <sakai:tool_bar_item action="#{UserPrefsTool.processActionTZFrmEdit}" value="Time Zone" />
   	  	</sakai:tool_bar>

				<br />
				

				<h:panelGroup rendered="#{UserPrefsTool.tabUpdated}">
					<jsp:include page="prefUpdatedMsg.jsp"/>	

					<%--  (gsilver) there are 2 types of messages being rendered here with the same wrapper - errors and info (success) items 
	for the first the response should include a block element classed as "alertMessage" - if informational, "information", if success "success" --%>

	
					</h:panelGroup>
				
				<sakai:messages />
			
				<h3>Customize Tabs</h3>
			
				<p class="instruction">To add site or remove a site from Sites Visible in Tabs list box, select the site and use the right or left arrows to move the site.
				To reorder sites in the Sites Visible in Tabs list box, select a site in the list and use the up and down arrows to change the order of the site.</p>

				
	<%-- (gsilver) 2 issues 
	1.  if there are no sites to populate both selects a message should put in the response to the effect that there are no memberships, hence cannot move things onto tabs group or off it. The table and all its children should then be excluded  from the response.
		2. if a given select is empty (has no option children) the resultant xhtml is invalid - we may need to seed it if this is important. This is fairly standard practice and helps to provide a default width to an empty select item (ie: about 12 dashes)
--%>	

			   <table cellspacing="23" cellpadding="5%">
    			  <tr>
    			    <td>
    			      <b>Sites not visible in Tabs</b>
    			      <br/>
    			  	  <h:selectManyListbox value="#{UserPrefsTool.selectedExcludeItems}" size="10">
				   		<f:selectItems value="#{UserPrefsTool.prefExcludeItems}" />
				 	  </h:selectManyListbox>
				 	</td>
				 	
				 	<td style="text-align: center;">
				 	  <p class="instruction">Move selected</p>
<%-- (gsilver)  collapsed all next 3 lines - as jsf seems to be creating wspace that destroys alignment  --%>
				 	  <h:commandButton id="add" value=">" action="#{UserPrefsTool.processActionAdd}"></h:commandButton><br /><h:commandButton id="remove" value="<" action="#{UserPrefsTool.processActionRemove}"></h:commandButton>
		         	  <p class="instruction">
		         	  Move all
		         	  </p>
<%-- (gsilver)  collapsed all next 3 lines - as jsf seems to be creating wspace that destroys alignment  --%>					  
		         	  <h:commandButton id="addAll" value=">>" action="#{UserPrefsTool.processActionAddAll}"></h:commandButton><br /><h:commandButton id="removeAll" value="<<" action="#{UserPrefsTool.processActionRemoveAll}"></h:commandButton>
				 	</td>
				 	
				 	<td>
				 	  <b>Sites visible in Tabs</b>
    			      <br/>
				 	  <h:selectManyListbox value="#{UserPrefsTool.selectedOrderItems}" size="10">
				        <f:selectItems value="#{UserPrefsTool.prefOrderItems}" />
				      </h:selectManyListbox>
				 	</td>
				 	
<%-- (gsilver) I think I understand the use of commandLink below instead of commandButton,  need up and down arrows,  we could use a commandButton that has a background image, but Safari will display nothing then, so this is the best possible solution --%>					
					
				 	<td>
				 	  <h:commandLink action="#{UserPrefsTool.processActionMoveUp}"> <h:graphicImage value="prefs/Up-Arrow.gif"/> </h:commandLink>
		              <br />
		              <h:commandLink action="#{UserPrefsTool.processActionMoveDown}"> <h:graphicImage value="prefs/Down-Arrow.gif"/> </h:commandLink>
				 	</td>    			  
    			  </tr>
				</table>
			    <br /><br />
			    <div class="act">
			    <h:commandButton id="submit" style="active;" value="Update Preferences" action="#{UserPrefsTool.processActionSave}"></h:commandButton>
				 <h:commandButton id="cancel" style="active;" value="Cancel Changes" action="#{UserPrefsTool.processActionCancel}"></h:commandButton>
			    </div>

		 </h:form>
		 </div>
		 <sakai:peer_refresh value="#{UserPrefsTool.refreshElement}" />
	</sakai:view_content>
	</sakai:view_container>
</f:view>
