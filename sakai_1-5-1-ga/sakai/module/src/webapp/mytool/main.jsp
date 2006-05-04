<%-- HTML JSF tag libary --%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%-- Core JSF tag library --%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%-- Main Sakai tag library --%>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%-- Custom tag library just for this tool --%>
<%@ taglib uri="http://sakaiproject.org/jsf/module" prefix="module" %>
<%-- MyFaces tag library
<%@ taglib uri="http://myfaces.sourceforge.net/tld/myfaces_ext_0_9.tld" prefix="x"%>
 --%>

<%
		response.setContentType("text/html; charset=UTF-8");
		response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
		response.addDateHeader("Last-Modified", System.currentTimeMillis());
		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
		response.addHeader("Pragma", "no-cache");
%>

<f:loadBundle basename="org.sakaiproject.tool.mytool.bundle.Messages" var="msgs"/>

<f:view>
<sakai:view_container title="#{msgs.sample_title}">
<h:form enctype="multipart/form-data" >

	<sakai:tool_bar>
		<sakai:tool_bar_item
			action="#{MyTool.processActionDoIt}"
			value="#{msgs.sample_one_cmd_go}" />
		<sakai:tool_bar_item
			action="#{MyTool.processActionDoIt}"
			value="I'm not rendered"
			rendered="false" />
	</sakai:tool_bar>

	<sakai:view_content>

		<sakai:messages />

		<sakai:instruction_message value="#{msgs.sample_one_instructions}" />
	
		<sakai:group_box title="#{msgs.sample_one_groupbox}">
			<sakai:panel_edit>
	 
				<h:outputText value="#{msgs.sample_one_hdr_name}"/>
				<h:inputText value="#{MyTool.userName}" required="true" />

				<h:outputText value="date"/>
				<sakai:date_input value="#{MyTool.date}" />

				<h:outputText value="Something special" />
				<!-- Example custom tag used only in this tool -->
				<module:my_custom_tag />
				
				<h:outputText value="Page ID" />
				<h:outputText value="#{toolConfig.pageId}" />
				
				<h:outputText value="Page title" />
				<h:outputText value="#{toolConfig.containingPage.title}" />

				<h:outputText value="Site title" />
				<h:outputText value="#{toolConfig.containingPage.containingSite.title}" />

				<h:outputText value="Creator of this site" />
				<h:outputText value="#{toolConfig.containingPage.containingSite.createdBy.displayName}" />

				<h:outputText value="Site ID (POST 1.0.0)" />
				<h:outputText value="#{toolConfig.siteId}" />
				
				<h:outputText value="Site ID (1.0.0)" />
				<h:outputText value="#{MyTool.currentSiteId}" />				
				
				<h:outputText value="Current user" />
				<h:outputText value="#{MyTool.currentUser.displayName}" />

				<h:outputText value="Can current user modify the structure of this site?" />
				<h:outputText value="#{MyTool.canCurrentUserModifySite}" />
			
				<h:outputText value="WYSIWYG editor tag (rows, columns, and 2 rows of buttons)" />
				<sakai:rich_text_area value="#{MyTool.text1}" rows="10" columns="79" toolbarButtonRows="2" />

				<h:outputText value="WYSIWYG editor tag (default)" />
				<sakai:rich_text_area value="#{MyTool.text2}" />

				<h:outputText value="WYSIWYG editor tag (width and height)" />
				<sakai:rich_text_area value="#{MyTool.text3}" width="500" height="200"/>

				<h:outputText value="WYSIWYG editor tag (width, height, and no buttons)" />
				<sakai:rich_text_area value="#{MyTool.text4}" width="640" height="480" toolbarButtonRows="0" />

				<h:outputText value="WYSIWYG editor tag (width, height, and 3 rows of buttons)" />
				<sakai:rich_text_area value="#{MyTool.text5}" width="640" height="480" toolbarButtonRows="3" />

				<%--
				<h:outputText value="Sakai custom file upload tag" />
				<sakai:input_file_upload target="sakai-module/uploads/" />
				--%>

				<%--
				<h:outputText value="MyFaces file upload tag" />
                <x:inputFileUpload id="fileupload"
                                       accept="image/*"
                                       value="#{MyTool.upFile}"
                                       storage="file"
                                       styleClass="fileUploadInput"
                                       required="true"/>
                --%>

				
			</sakai:panel_edit>
		</sakai:group_box>

		<sakai:button_bar>
			<sakai:button_bar_item
					action="#{MyTool.processActionDoIt}"
					value="I am not rendered" 
					rendered="false" />
			<sakai:button_bar_item
					action="#{MyTool.processActionDoIt}"
					value="#{msgs.sample_one_cmd_go}" />
			
		</sakai:button_bar>

	</sakai:view_content>

</h:form>
</sakai:view_container>
</f:view>