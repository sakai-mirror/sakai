<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://sakaiproject.org/jsf/syllabus" prefix="syllabus" %>
<% response.setContentType("text/html; charset=UTF-8"); %>
<f:view>
<f:loadBundle basename="org.sakaiproject.tool.syllabus.bundle.Messages" var="msgs"/>
	<sakai:view_container title="#{msgs.attachment}">
		<sakai:view_content>
			<h:form>
				<h3>
					<h:outputText value="#{msgs.bar_delete_items}" />
				</h3>	
				<div class="alertMessage">
					<h:outputText value="#{msgs.delAttConfAlert}" />
				</div>	
				<syllabus:syllabus_table value="#{SyllabusTool.prepareRemoveAttach}" var="eachAttach">
					<h:column>
						<f:facet name="header">
							<h:outputText value="Title" />
						</f:facet>
						<h:outputText value="#{eachAttach.name}"/>
						</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Size" />
						</f:facet>
						<h:outputText value="#{eachAttach.size}"/>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Type" />
						</f:facet>
							<h:outputText value="#{eachAttach.type}"/>
						</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Created by" />
						</f:facet>
						<h:outputText value="#{eachAttach.createdBy}"/>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Last modified by" />
						</f:facet>
							<h:outputText value="#{eachAttach.lastModifiedBy}"/>
					</h:column>
				</syllabus:syllabus_table>
				<div class="act">
					<h:commandButton 
					  value="#{msgs.bar_delete}" 
					  action="#{SyllabusTool.processRemoveAttach}"/>
					<h:commandButton 
					  value="#{msgs.bar_cancel}" 
					  action="#{SyllabusTool.processRemoveAttachCancel}"/>
				</div>	  
			</h:form>
		</sakai:view_content>
	</sakai:view_container>
</f:view>
				
