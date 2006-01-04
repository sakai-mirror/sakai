<%-- HTML JSF tag libary --%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%-- Core JSF tag library --%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%-- Sakai JSF tag library --%>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<f:view>
	<sakai:view_container title="Options">
	<sakai:view_content>
		<h:form id="options_form">

				<sakai:messages />

				<sakai:instruction_message value="Modify the tool placement configuration." />

				<sakai:group_box title="Options">

					<%-- the list of options --%>
					<sakai:flat_list value="#{OptionsTool.options}" var="option">
						<h:column>
							<f:facet name="header">
								<h:outputText value=""/>
							</f:facet>
							<h:outputText value="#{option.name}:"/>
						</h:column>
						<h:column>
							<f:facet name="header">
								<h:outputText value=""/>
							</f:facet>
							<h:inputText value="#{option.value}" />
						</h:column>
					</sakai:flat_list>

				</sakai:group_box>
	
					<sakai:button_bar>
						<sakai:button_bar_item
								action="#{OptionsTool.processActionSave}"
								value="Save" />
						<sakai:button_bar_item
								immediate="true"
								action="#{OptionsTool.processActionCancel}"
								value="Cancel" />
					</sakai:button_bar>

		 </h:form>
	</sakai:view_content>
	</sakai:view_container>
</f:view>
