<%-- HTML JSF tag libary --%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%-- Core JSF tag library --%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%-- Sakai JSF tag library --%>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<f:view>
	<sakai:view_container title="Preferences">
	<sakai:view_content>
		<h:form id="options_form">

				
				<%--h:outputText value="User ID: "/><h:inputText value="#{AdminPrefsTool.userId}" /--%>	
				<sakai:tool_bar>
			  <%--sakai:tool_bar_item action="#{UserPrefsTool.processActionRefreshFrmNoti}" value="Refresh" /--%>
 		    <sakai:tool_bar_item value="Notifications" />
 		    <sakai:tool_bar_item action="#{UserPrefsTool.processActionEdit}" value="Customize Tabs" />
   	  	</sakai:tool_bar>
				
				<br>

				<h:panelGroup rendered="#{UserPrefsTool.notiUpdated}">
					<jsp:include page="prefUpdatedMsg.jsp"/>	
				</h:panelGroup>
	
				<sakai:messages />
				
				<br><br>
				<h:outputText value="Notifications" style="font-size: 12px;font-weight: bold;font-family: verdana, arial, helvetica, sans-serif;"/>
				<br>
				<h:outputText value="You will receive all high priority notifications via email. Set low priority notifications below." style="font-size: 12px;font-family: verdana, arial, helvetica, sans-serif;"/>
				<br><br>
				<h:outputText value="Announcements" style="font-size: 12px;font-weight: bold;font-family: verdana, arial, helvetica, sans-serif;"/>
				<h:selectOneRadio value="#{UserPrefsTool.selectedAnnItem}" layout="pageDirection" style="font-size: 12px;font-family: verdana, arial, helvetica, sans-serif;">
    			<f:selectItem itemValue="3" itemLabel="Send me each notification separately"/><br>
    			<f:selectItem itemValue="2" itemLabel="Send me one email per day summarizing all low priority announcements"/><br>
    			<f:selectItem itemValue="1" itemLabel="Do not send me low priority announcements"/>
  			</h:selectOneRadio>
  			<br>
				<h:outputText value="Email Archive" style="font-size: 12px;font-weight: bold;font-family: verdana, arial, helvetica, sans-serif;"/>
				<h:selectOneRadio value="#{UserPrefsTool.selectedMailItem}" layout="pageDirection" style="font-size: 12px;font-family: verdana, arial, helvetica, sans-serif;">
    			<f:selectItem itemValue="3" itemLabel="Send me each mail sent to site separately"/><br>
    			<f:selectItem itemValue="2" itemLabel="Send me one email per day summarizing all emails"/><br>
    			<f:selectItem itemValue="1" itemLabel="Do not send me emails sent to the site"/>
  			</h:selectOneRadio>
  			<br>
				<h:outputText value="Resources" style="font-size: 12px;font-weight: bold;font-family: verdana, arial, helvetica, sans-serif;"/>
				<h:selectOneRadio value="#{UserPrefsTool.selectedRsrcItem}" layout="pageDirection" style="font-size: 12px;font-family: verdana, arial, helvetica, sans-serif;">
    			<f:selectItem itemValue="3" itemLabel="Send me each resource separately"/><br>
    			<f:selectItem itemValue="2" itemLabel="Send me one email per day summarizing all low priority resource notifications"/><br>
    			<f:selectItem itemValue="1" itemLabel="Do not send me low priority resource notifications"/>
  			</h:selectOneRadio>
  				
				<br><br>
				<sakai:button_bar>
					<sakai:button_bar_item action="#{UserPrefsTool.processActionNotiSave}" value="Update Preferences" />
					<sakai:button_bar_item action="#{UserPrefsTool.processActionNotiCancel}" value="Cancel" />
				</sakai:button_bar>

					
		 </h:form>
	</sakai:view_content>
	</sakai:view_container>
</f:view>
