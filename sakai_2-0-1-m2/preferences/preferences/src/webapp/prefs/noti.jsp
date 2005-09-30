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
				
				<h3>Notifications</h3>
				
				<p class="instruction">You will receive all high priority notifications via email. Set low priority notifications below. </p>
				<br>
				<h4>Announcements</h4>
				<h:selectOneRadio value="#{UserPrefsTool.selectedAnnItem}" layout="pageDirection" style="font-size: 12px;font-family: verdana, arial, helvetica, sans-serif;">
    			<f:selectItem itemValue="3" itemLabel="Send me each notification separately"/><br>
    			<f:selectItem itemValue="2" itemLabel="Send me one email per day summarizing all low priority announcements"/><br>
    			<f:selectItem itemValue="1" itemLabel="Do not send me low priority announcements"/>
  			</h:selectOneRadio>
  			<br>
  			<h4>Email Archive</h4>
				<h:selectOneRadio value="#{UserPrefsTool.selectedMailItem}" layout="pageDirection" style="font-size: 12px;font-family: verdana, arial, helvetica, sans-serif;">
    			<f:selectItem itemValue="3" itemLabel="Send me each mail sent to site separately"/><br>
    			<f:selectItem itemValue="2" itemLabel="Send me one email per day summarizing all emails"/><br>
    			<f:selectItem itemValue="1" itemLabel="Do not send me emails sent to the site"/>
  			</h:selectOneRadio>
  			<br>
  			<h4>Resources</h4>
				<h:selectOneRadio value="#{UserPrefsTool.selectedRsrcItem}" layout="pageDirection" style="font-size: 12px;font-family: verdana, arial, helvetica, sans-serif;">
    			<f:selectItem itemValue="3" itemLabel="Send me each resource separately"/><br>
    			<f:selectItem itemValue="2" itemLabel="Send me one email per day summarizing all low priority resource notifications"/><br>
    			<f:selectItem itemValue="1" itemLabel="Do not send me low priority resource notifications"/>
  			</h:selectOneRadio>
  				
				<br><br>
				<h:commandButton id="submit" style="active;" value="Update Preferences" action="#{UserPrefsTool.processActionNotiSave}"></h:commandButton>
				<h:commandButton id="cancel" style="active;" value="Cancel" action="#{UserPrefsTool.processActionNotiCancel}"></h:commandButton>
					
		 </h:form>
	</sakai:view_content>
	</sakai:view_container>
</f:view>
