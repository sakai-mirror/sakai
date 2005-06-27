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
			
				<h3>Customize Tabs</h3>
			
				<p class="instruction">To add site or remove a site from Sites Visible in Tabs list box, select the site and use the right or left arrows to move the site.
				To reorder sites in the Sites Visible in Tabs list box, select a site in the list and use the up and down arrows to change the order of the site.</p>
			   
			   <table cellspacing="23" cellpadding="5%">
  				  <tr>
    			    <th>My Sites.. </th>
    			    <th></th>
    			    <th>Sites visible in Tabs.. </th>
    			    <th></th>
    			  </tr>
    			  <tr>
    			    <td>
    			  	  <h:selectManyListbox value="#{UserPrefsTool.selectedExcludeItems}" size="10">
				   		<f:selectItems value="#{UserPrefsTool.prefExcludeItems}" />
				 	  </h:selectManyListbox>
				 	</td>
				 	
				 	<td>
				 	  Move selected
				 	  <br>
				 	  <h:commandButton id="add" value=">" action="#{UserPrefsTool.processActionAdd}"></h:commandButton>
		              <br>
		              <h:commandButton id="remove" value="<" action="#{UserPrefsTool.processActionRemove}"></h:commandButton>
		         	  <br>
		         	  Move all
		         	  <br>
		         	  <h:commandButton id="addAll" value=">>" action="#{UserPrefsTool.processActionAddAll}"></h:commandButton>
		              <br>
		              <h:commandButton id="removeAll" value="<<" action="#{UserPrefsTool.processActionRemoveAll}"></h:commandButton>
				 	</td>
				 	
				 	<td>
				 	  <h:selectManyListbox value="#{UserPrefsTool.selectedOrderItems}" size="10">
				        <f:selectItems value="#{UserPrefsTool.prefOrderItems}" />
				      </h:selectManyListbox>
				 	</td>
				 	
				 	<td>
				 	  <h:commandLink action="#{UserPrefsTool.processActionMoveUp}"> <h:graphicImage value="prefs/Up-Arrow.gif"/> </h:commandLink>
		              <br>
		              <h:commandLink action="#{UserPrefsTool.processActionMoveDown}"> <h:graphicImage value="prefs/Down-Arrow.gif"/> </h:commandLink>
				 	</td>    			  
    			  </tr>
				</table>
			    <br><br>
			    <div>
			    <h:commandButton id="submit" style="active;" value="Update Preferences" action="#{UserPrefsTool.processActionSave}"></h:commandButton>
				 <h:commandButton id="cancel" style="active;" value="Cancel" action="#{UserPrefsTool.processActionCancel}"></h:commandButton>
			    </div>

		 </h:form>
		 <sakai:peer_refresh value="#{UserPrefsTool.refreshElement}" />
	</sakai:view_content>
	</sakai:view_container>
</f:view>
