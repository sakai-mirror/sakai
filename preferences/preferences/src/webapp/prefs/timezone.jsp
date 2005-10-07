<%-- HTML JSF tag libary --%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%-- Core JSF tag library --%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%-- Sakai JSF tag library --%>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<f:view>
	<sakai:view_container title="Preferences">
	<sakai:view_content>
		<h:form id="timezone_form">

				
				<%--h:outputText value="User ID: "/><h:inputText value="#{AdminPrefsTool.userId}" /--%>	
				<sakai:tool_bar>
			  <%--sakai:tool_bar_item action="#{UserPrefsTool.processActionRefreshFrmNoti}" value="Refresh" /--%>
 		    <sakai:tool_bar_item action="#{UserPrefsTool.processActionNotiFrmEdit}" value="Notifications" />
 		    <sakai:tool_bar_item action="#{UserPrefsTool.processActionEdit}" value="Customize Tabs" />
 		    <sakai:tool_bar_item value="Time Zone" />
   	  	</sakai:tool_bar>
				
				<br>

				<h:panelGroup rendered="#{UserPrefsTool.tzUpdated}">
					<jsp:include page="prefUpdatedMsg.jsp"/>	
				</h:panelGroup>
	
				<sakai:messages />
				
				<h3>Time Zone</h3>
				
				<p class="instruction">Please select your local time zone for selected Sakai tools, such as Schedule. You are currently in the <h:outputText value="#{UserPrefsTool.selectedTimeZone}"/> timezone</p>
				<br>
					
    			 <h:selectOneListbox 
                      value="#{UserPrefsTool.selectedTimeZone}"
                      size="20">
				    <f:selectItems value="#{UserPrefsTool.prefTimeZones}" />
				 </h:selectOneListbox>
			    <br><br>
			    <div>
			    <h:commandButton id="submit" style="active;" value="Update Preferences" action="#{UserPrefsTool.processActionTzSave}"></h:commandButton>
				<h:commandButton id="cancel" style="active;" value="Cancel" action="#{UserPrefsTool.processActionTzCancel}"></h:commandButton>
			    </div>
		 </h:form>
	</sakai:view_content>
	</sakai:view_container>
</f:view>
