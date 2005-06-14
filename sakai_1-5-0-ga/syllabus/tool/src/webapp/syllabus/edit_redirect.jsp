<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<f:loadBundle basename="org.sakaiproject.tool.syllabus.bundle.Messages" var="msgs"/>

<f:view>
	<sakai:view_container title="#{msgs.title_edit}">
		<sakai:view_content>
			<h:form>

		  	<sakai:tool_bar_message value="#{msgs.add_sylla}" /> 
		  	        
<%--				<sakai:doc_section>
					<h:outputText value="#{msgs.newSyllabusForm1}"/>
					<h:outputText value="*" style="color: red"/>
					<h:outputText value=" means" />
				</sakai:doc_section>					
				<sakai:doc_section>
					<h:outputText value="#{msgs.newSyllabusForm2}"/>
				</sakai:doc_section>--%>

				<sakai:group_box title="Syllabus">
					
					<sakai:panel_edit>
						<sakai:doc_section>
							<h:outputText value="*" style="color: red"/>
							<h:outputText value="#{msgs.syllabus_url}"/>
						</sakai:doc_section>
						<h:inputText value="#{SyllabusTool.syllabusItem.redirectURL}" />
					</sakai:panel_edit>
					
				</sakai:group_box>


				<sakai:button_bar>
					<sakai:button_bar_item
						action="#{SyllabusTool.processEditSaveRedirect}"
						value="Save" />
					<sakai:button_bar_item
						action="#{SyllabusTool.processEditCancelRedirect}"
						value="#{msgs.cancel}" />
				</sakai:button_bar>

			</h:form>
		</sakai:view_content>
	</sakai:view_container>
</f:view>