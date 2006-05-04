<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<f:view>
<f:loadBundle basename="org.sakaiproject.tool.syllabus.bundle.Messages" var="msgs"/>

	<sakai:view_container title="#{msgs.title_list}">
		<sakai:view_content>
			<h:form>

				<table width="100%" align="center">
					<tr>
					  <td width="100%" align="center">
						  <table width="100%" align="center">
							  <tr>
								  <td align="center" style="font-size: 12pt; color: #8B0000" width="100%">
								    Are you sure you want to delete the specified item?
								  </td>
								  <td/>
							  </tr>
						  </table>
						</td>
					</tr>
					<tr>
					  <td width="100%">
						  <table width="100%" align="center">
							  <tr>
							    <td width="50%" align="right">
		 							  <sakai:tool_bar_item
										  action="#{SyllabusTool.processConfirmMuliDelete}"
										  value="#{msgs.bar_ok}" />
								  </td>
								  <td width="50%" align="left">
									  <sakai:tool_bar_item
										  action="#{SyllabusTool.processCancelMultiDelete}"
										  value="#{msgs.bar_cancel}" />
								  </td>
							  </tr>
						  </table>
						</td>
					</tr>
				</table>

			</h:form>
		</sakai:view_content>
	</sakai:view_container>
</f:view>