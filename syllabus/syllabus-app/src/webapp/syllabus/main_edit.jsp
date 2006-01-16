<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://sakaiproject.org/jsf/syllabus" prefix="syllabus" %>
<% response.setContentType("text/html; charset=UTF-8"); %>
<f:view>
<f:loadBundle basename="org.sakaiproject.tool.syllabus.bundle.Messages" var="msgs"/>
	<sakai:view_container title="#{msgs.title_list}">
	<sakai:view_content>
		<h:form>
		  <sakai:tool_bar>
		  <%-- (gsilver) cannot pass a needed title atribute to these next items --%>
			  <sakai:tool_bar_item
			    action="#{SyllabusTool.processListNew}"
					value="#{msgs.bar_new}" />
 		    <sakai:tool_bar_item
					action="#{SyllabusTool.processRedirect}"
					value="#{msgs.bar_redirect}" />
 		    <sakai:tool_bar_item
					action="#{SyllabusTool.processStudentView}"
					value="#{msgs.bar_student_view}" />
   	  </sakai:tool_bar>
			<syllabus:syllabus_if test="#{SyllabusTool.syllabusItem.redirectURL}">
		  	<sakai:tool_bar_message value="#{msgs.mainEditNotice}" />
				<syllabus:syllabus_table value="#{SyllabusTool.entries}" var="eachEntry">
<%--						<h:column rendered="#{!empty SyllabusTool.entries}">--%>
						<h:column rendered="#{! SyllabusTool.displayNoEntryMsg}">
							<f:facet name="header">
								<h:outputText value="#{msgs.mainEditHeaderItem}" />
							</f:facet>
							<h:commandLink action="#{eachEntry.processListRead}" title="#{msgs.goToItem} #{eachEntry.entry.title}">
								<h:outputText value="#{eachEntry.entry.title}"/>
							</h:commandLink>
						</h:column>
						<h:column rendered="#{! SyllabusTool.displayNoEntryMsg}">
							<f:facet name="header">
								<h:outputText value="" />
							</f:facet>
							<h:commandLink action="#{eachEntry.processUpMove}" style="text-decoration:none" title="#{msgs.mainEditLinkUpTitle}">
								<h:graphicImage url="/syllabus/moveup.gif" alt="#{msgs.mainEditLinkUpTitle}" />
<%--								<h:outputText value="Move Up"/> --%>
							</h:commandLink>
						</h:column>
						<h:column rendered="#{! SyllabusTool.displayNoEntryMsg}">
							<f:facet name="header">
								<h:outputText value="" />
							</f:facet>
							<h:commandLink action="#{eachEntry.processDownMove}"  style="text-decoration:none" title="#{msgs.mainEditLinkDownTitle}" styleClass="imageLink">
							<h:graphicImage url="/syllabus/movedown.gif" alt="#{msgs.mainEditLinkDownTitle}" />
<%--								<h:outputText value="Move Down"/> --%>
							</h:commandLink>
						</h:column>
						<h:column rendered="#{! SyllabusTool.displayNoEntryMsg}">
							<f:facet name="header">
								<h:outputText value="#{msgs.mainEditHeaderStatus}"/>
							</f:facet>
							<h:outputText value="#{eachEntry.entry.status}"/>
						</h:column>
						<h:column rendered="#{! SyllabusTool.displayNoEntryMsg}">
							<f:facet name="header">
  							<h:outputText value="#{msgs.mainEditHeaderRemove}"/>
							</f:facet>
							<h:selectBooleanCheckbox value="#{eachEntry.selected}" title="#{msgs.selectThisCheckBox}"/>
						</h:column>
					</syllabus:syllabus_table>
					<f:verbatim><p class="act"></f:verbatim>	
						<h:commandButton 
							  value="#{msgs.update}" 
							  action="#{SyllabusTool.processListDelete}"
							  title="#{msgs.update}"
							  rendered="#{! SyllabusTool.displayNoEntryMsg}"/>
					<f:verbatim></p></f:verbatim>		  
			</syllabus:syllabus_if>
			<syllabus:syllabus_ifnot test="#{SyllabusTool.syllabusItem.redirectURL}">
				<sakai:tool_bar_message value="#{msgs.redirect_sylla}" />
				<syllabus:syllabus_iframe redirectUrl="#{SyllabusTool.syllabusItem.redirectURL}" width="100%" height="500" />
			</syllabus:syllabus_ifnot>
		</h:form>
	</sakai:view_content>
	</sakai:view_container>
</f:view>
