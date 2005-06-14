<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<%
		response.setContentType("text/html; charset=UTF-8");
		response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
		response.addDateHeader("Last-Modified", System.currentTimeMillis());
		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
		response.addHeader("Pragma", "no-cache");
%>

<f:loadBundle basename="org.sakaiproject.tool.mytool.bundle.Messages" var="msgs"/>

<f:view>
<sakai:view_container title="#{msgs.pt_title_show}">
	<h:form>
		<sakai:tool_bar>
			<sakai:tool_bar_item
				action="#{PresentationTool.processActionEnd}"
				value="#{msgs.pt_stop_button}" />
			<sakai:tool_bar_item
				action="#{PresentationTool.processActionPrevious}"
				value="#{msgs.pt_previous_button}" />
			<sakai:tool_bar_item
				action="#{PresentationTool.processActionNext}"
				value="#{msgs.pt_next_button}" />
			<sakai:tool_bar_spacer />
			<sakai:tool_bar_item
				disabled="true"
				value="Presenting slide: #{PresentationTool.showPosition}" 
			/>
		</sakai:tool_bar>

		<sakai:view_content>
            	<sakai:messages />
             <sakai:instruction_message value="#{PresentationTool.instructionMessage}"/>
			<h:graphicImage value="#{PresentationTool.slide.url}" />
		</sakai:view_content>
	</h:form>
</sakai:view_container>
</f:view>
