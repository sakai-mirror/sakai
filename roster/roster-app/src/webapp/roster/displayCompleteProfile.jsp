<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%--TODO: Refactor Profile Code in order to use profile custom components--%>
<%@ taglib uri="http://sakaiproject.org/jsf/roster" prefix="roster" %> 
<% response.setContentType("text/html; charset=UTF-8"); %>
 <f:view>
 <h:form>
	<sakai:view_container title="#{msgs.profile_site_roster}">
		<sakai:view_content>
		    <sakai:view_title  value="#{msgs.profile_user_profile}"/>
			<h:panelGrid columns="1" border="0" >
			<%-- TODO: should the image sizes be predetermine for this view? --%>
					<h:graphicImage value="#{msgs.img_unavail}" height="75" width="75" rendered="#{RosterTool.participant.showCustomPhotoUnavailableForSelectedProfile}"/>
					<h:graphicImage value="#{RosterTool.participant.participant.profile.pictureUrl}" height="75" width="75" rendered="#{RosterTool.participant.showURLPhotoForSelectedProfile}"/>
					<h:graphicImage value="ParticipantImageServlet.prf?photo=#{RosterTool.participant.participant.id}"  width="75" rendered="#{RosterTool.participant.showCustomIdPhotoForSelectedProfile}"/>
			</h:panelGrid>
			<h4><h:outputText  value="#{msgs.profile_public_information}"/>	</h4>			 
			 	<p class="shorttext">
			 	<sakai:panel_edit >	
					<h:outputLabel style ="shorttext" value="#{msgs.profile_first_name}"/>
					<h:outputText value="#{RosterTool.participant.participant.profile.firstName}"/> 
					<h:outputLabel value="#{msgs.profile_last_name}"/>
					<h:outputText value="#{RosterTool.participant.participant.profile.lastName}"/>
					<h:outputLabel value="#{msgs.profile_nick_name}"/>				
					<h:outputText value="#{RosterTool.participant.participant.profile.nickName}"/>
					<h:outputLabel value="#{msgs.profile_position}"/> 				
					<h:outputText value="#{RosterTool.participant.participant.profile.position}"/> 
					<h:outputLabel value="#{msgs.profile_department}"/>
					<h:outputText value="#{RosterTool.participant.participant.profile.department}"/> 
					<h:outputLabel value="#{msgs.profile_school}"/>
					<h:outputText value="#{RosterTool.participant.participant.profile.school}"/>
					<h:outputLabel value="#{msgs.profile_room}"/>
					<h:outputText value="#{RosterTool.participant.participant.profile.room}"/> 
				</sakai:panel_edit>
				</p>
		 	<h4><h:outputText  value="#{msgs.profile_personal_information}" /></h4>
			 <p class="shorttext">
				<sakai:panel_edit>		 	
					<h:outputLabel style ="shorttext" value="#{msgs.profile_email}"/>
					<h:outputText value="#{RosterTool.participant.participant.profile.email} " />
					<h:outputLabel style ="shorttext" value="#{msgs.profile_homepage}"/>	
					<h:outputLink target="_blank" value="#{RosterTool.participant.participant.profile.homepage}" >
						<h:outputText value="#{RosterTool.participant.participant.profile.homepage}"/>
					</h:outputLink>	 
					<h:outputLabel style ="shorttext" value="#{msgs.profile_work_phone}"/>
					<h:outputText value="#{RosterTool.participant.participant.profile.workPhone}"/>
					<h:outputLabel value="#{msgs.profile_home_phone}"/>
					<h:outputText value="#{RosterTool.participant.participant.profile.homePhone}" />
					<h:outputLabel value="#{msgs.profile_other_information}"/>
					<roster:roster_display_HTML value="#{RosterTool.participant.participant.profile.otherInformation}"/>
			 </sakai:panel_edit> </p>
			 
		  	 <h:panelGrid>
			 	<h:commandButton  id="submit" value="#{msgs.back}" immediate="true" action="#{RosterTool.processCancel}" />
			 </h:panelGrid>	
  		</sakai:view_content>
	</sakai:view_container>
  </h:form>
</f:view>

	
