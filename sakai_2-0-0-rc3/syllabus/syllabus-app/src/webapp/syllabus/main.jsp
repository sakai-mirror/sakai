<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://sakaiproject.org/jsf/syllabus" prefix="syllabus" %>

<f:view>
<f:loadBundle basename="org.sakaiproject.tool.syllabus.bundle.Messages" var="msgs"/>
	<sakai:view_container title="#{msgs.title_list}">
	<sakai:view_content>
		<h:form>
		  <sakai:tool_bar>
				<syllabus:syllabus_ifnot test="#{SyllabusTool.editAble}">
			  	<sakai:tool_bar_item
			    	action="#{SyllabusTool.processCreateAndEdit}"
						value="#{msgs.bar_create_edit}" />
				</syllabus:syllabus_ifnot>
   	  </sakai:tool_bar>

   	  <table width="100%">
  			<tr>
		  	  <td width="0%" />
   	  	  <td width="100%">
					</td>
				</tr>
			</table>

			<syllabus:syllabus_if test="#{SyllabusTool.syllabusItem.redirectURL}">
				<h:dataTable value="#{SyllabusTool.entries}" var="eachEntry">
					<h:column>
						<sakai:panel_edit>
							<sakai:doc_section>
								<h:outputText/>
							</sakai:doc_section>
							<h:outputText value="#{eachEntry.entry.title}" 
								style="font-size:14px;font-weight:bold"/>
						
							<sakai:doc_section>
								<h:outputText/>
							</sakai:doc_section>
							<syllabus:syllabus_htmlShowArea value="#{eachEntry.entry.asset}" />
						</sakai:panel_edit>
					</h:column>
				</h:dataTable>
				<h:outputText value="#{msgs.syllabus_noEntry}" style="font-size:10px;font-weight:bold" rendered="#{SyllabusTool.displayNoEntryMsg}"/>
			</syllabus:syllabus_if>
			<syllabus:syllabus_ifnot test="#{SyllabusTool.syllabusItem.redirectURL}">
  			<syllabus:syllabus_iframe redirectUrl="#{SyllabusTool.syllabusItem.redirectURL}" width="750" height="500" />
			</syllabus:syllabus_ifnot>

		</h:form>
	</sakai:view_content>
	</sakai:view_container>
</f:view>