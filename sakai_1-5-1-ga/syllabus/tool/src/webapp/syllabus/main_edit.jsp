<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://sakaiproject.org/jsf/syllabus" prefix="syllabus" %>

<f:loadBundle basename="org.sakaiproject.tool.syllabus.bundle.Messages" var="msgs"/>

<%--
<%
  String tempS = request.getServerName();
  int tempP = request.getServerPort();
%>--%>

<f:view>
	<sakai:view_container title="#{msgs.title_list}">
		<h:form>
<%--
<%=tempS%>
<%=tempP%>--%>
		  <sakai:tool_bar>
			  <sakai:tool_bar_item
			    action="#{SyllabusTool.processListNew}"
					value="#{msgs.bar_new}" />
 		    <sakai:tool_bar_item
					action="#{SyllabusTool.processRedirect}"
					value="#{msgs.bar_redirect}" />
 		    <sakai:tool_bar_item
					action="#{SyllabusTool.processStudentView}"
					value="#{msgs.bar_student_view}" />
<%--   		 <sakai:tool_bar_item
					action="#{SyllabusTool.processStudentView}"
					value="#{msgs.bar_student_view}" /> --%>
   	  </sakai:tool_bar>

			<syllabus:syllabus_if test="#{SyllabusTool.syllabusItem.redirectURL}">
<%--				<sakai:view_content>--%>
<%--				<sakai:flat_list value="#{SyllabusTool.entries}" var="eachEntry">--%>
		   	  <table width="100%">
   	  			<tr>
   			  	  <td width="0%" />
		   	  	  <td width="100%">
<%--								Syllabus --%>
							</td>
						</tr>
					</table>
					<syllabus:syllabus_table value="#{SyllabusTool.entries}" var="eachEntry">
						<h:column rendered="#{!empty SyllabusTool.entries}">
							<f:facet name="header">
								<h:outputText value="Syllabus Item" />
							</f:facet>
							<h:commandLink action="#{eachEntry.processListRead}">
								<h:outputText value="#{eachEntry.entry.title}"/>
							</h:commandLink>
						</h:column>
						<h:column rendered="#{!empty SyllabusTool.entries}">
							<f:facet name="header">
								<h:outputText value="" />
							</f:facet>
							<h:commandLink action="#{eachEntry.processUpMove}">
								<h:outputText value="Move Up"/>
							</h:commandLink>
						</h:column>
						<h:column rendered="#{!empty SyllabusTool.entries}">
							<f:facet name="header">
								<h:outputText value="" />
							</f:facet>
							<h:commandLink action="#{eachEntry.processDownMove}">
								<h:outputText value="Move Down"/>
							</h:commandLink>
						</h:column>
						<h:column rendered="#{!empty SyllabusTool.entries}">
							<f:facet name="header">
								<h:outputText value="Status"/>
							</f:facet>
							<h:outputText value="#{eachEntry.entry.status}"/>
						</h:column>
						<h:column rendered="#{!empty SyllabusTool.entries}">
							<f:facet name="header">
  							<h:outputText value="Remove"/>
							</f:facet>
							<h:selectBooleanCheckbox value="#{eachEntry.selected}"/>
						</h:column>
<%--			</sakai:flat_list>--%>
					</syllabus:syllabus_table>
					<h:commandButton 
						  value="Update" 
						  action="#{SyllabusTool.processListDelete}" 
						  rendered="#{!empty SyllabusTool.entries}"/>
<%--				</sakai:view_content>--%>
			</syllabus:syllabus_if>
			<syllabus:syllabus_ifnot test="#{SyllabusTool.syllabusItem.redirectURL}">
  			<syllabus:syllabus_iframe redirectUrl="#{SyllabusTool.syllabusItem.redirectURL}" width="750" height="500" />
			</syllabus:syllabus_ifnot>

		</h:form>
	</sakai:view_container>
</f:view>