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
 		    <sakai:tool_bar_item action="#{UserPrefsTool.processActionTZFrmEdit}" value="Time Zone" />
   	  	</sakai:tool_bar>
				
				<br />

				<h:panelGroup rendered="#{UserPrefsTool.notiUpdated}">
					<jsp:include page="prefUpdatedMsg.jsp"/>	
				</h:panelGroup>
	
				<sakai:messages />
				
				<h3>Notifications</h3>
<%--(gsilver)  the following radio elements have the following problems vis a vis xhtml validation and accessibility and style guide comformance
					1. the checked marker must be rendered as an attribute/value lowercased pair (ie checked="checked")
					2. the text in the itemLabel attribute should be output wrapped in a label element whose "for" attribute value  is the id of the associated input
						instead of	
							<label><input type="radio" checked name="options_form:_id8" value="3"> Send me each notification separately</input></label>
						it should be
							<input type="radio" checked name="options_form:_id8" value="3" /><label> Send me each notification separately</label>
					3. inputs should have an id because of the reason outlined in 2 above - the id should be unique for each input and be used as the value of the "for" attribute of the associated label
					4. I imagine that the wrapping table is a bit of jsf cruft. This layout does not need it. Neither needs the 2 line breaks before each one.
					5. The standard Style Guide way of doing this is:
					
						<p class="radio"><input id="$id"  /><label for "$id">Label</label></p>

				
--%>

				<p class="instruction">You will receive all high priority notifications via email. Set low priority notifications below. </p>
				<h4>Announcements</h4>
				<h:selectOneRadio value="#{UserPrefsTool.selectedAnnItem}" layout="pageDirection" style="margin:0 2em">
    			<f:selectItem itemValue="3" itemLabel="Send me each notification separately"/><br />
    			<f:selectItem itemValue="2" itemLabel="Send me one email per day summarizing all low priority announcements"/><br />
    			<f:selectItem itemValue="1" itemLabel="Do not send me low priority announcements"/>
  			</h:selectOneRadio>
  			<h4>Email Archive</h4>
				<h:selectOneRadio value="#{UserPrefsTool.selectedMailItem}" layout="pageDirection" style="margin:0 2em">
    			<f:selectItem itemValue="3" itemLabel="Send me each mail sent to site separately"/><br />
    			<f:selectItem itemValue="2" itemLabel="Send me one email per day summarizing all emails"/><br />
    			<f:selectItem itemValue="1" itemLabel="Do not send me emails sent to the site"/>
  			</h:selectOneRadio>
  			<h4>Resources</h4>
				<h:selectOneRadio value="#{UserPrefsTool.selectedRsrcItem}" layout="pageDirection" style="margin:0 2em">
    			<f:selectItem itemValue="3" itemLabel="Send me each resource separately"/><br />
    			<f:selectItem itemValue="2" itemLabel="Send me one email per day summarizing all low priority resource notifications"/><br />
    			<f:selectItem itemValue="1" itemLabel="Do not send me low priority resource notifications"/>
  			</h:selectOneRadio>
  			<h4>Syllabus</h4>
				<h:selectOneRadio value="#{UserPrefsTool.selectedSyllItem}" layout="pageDirection" style="margin:0 2em">
    			<f:selectItem itemValue="2" itemLabel="Send me each notification separately"/><br />
    			<f:selectItem itemValue="1" itemLabel="Do not send me low priority Syllabus items"/>
  			</h:selectOneRadio>  			
  				
				<p class="act">
				<h:commandButton id="submit" style="active;" value="Update Preferences" action="#{UserPrefsTool.processActionNotiSave}"></h:commandButton>
				<h:commandButton id="cancel" style="active;" value="Cancel Changes" action="#{UserPrefsTool.processActionNotiCancel}"></h:commandButton>
				</p>	
		 </h:form>
	</sakai:view_content>
	</sakai:view_container>
</f:view>
