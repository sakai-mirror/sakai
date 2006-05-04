<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %> 
<%-- Custom tag library just for this tool --%>
<%@ taglib uri="http://sakaiproject.org/jsf/help" prefix="help" %>
 
<%
		response.setContentType("text/html; charset=UTF-8");
		response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
		response.addDateHeader("Last-Modified", System.currentTimeMillis());
		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
		response.addHeader("Pragma", "no-cache");
%>
<script type="text/javascript">
 function clearText(thefield) { if (thefield.defaultValue == thefield.value) thefield.value = ""; else thefield.value = thefield.defaultValue }

</script>

<f:loadBundle basename="org.sakaiproject.tool.help.bundle.Messages" var="msgs"/>
<f:view>
 
<div name="message" id="message" style="padding: 2px; background:#000000 none repeat scroll 0%; position: absolute; z-index: 3; 
-moz-background-clip: initial; -moz-background-origin: initial; -moz-background-inline-policy: initial; color: white; font-size: 
100%; top: 1px; left: 1px; display: none;">Searching.....please wait
</div>  <sakai:panel_edit></sakai:panel_edit>
<h:form>
      <h:commandButton value="#{msgs.back}" onclick="history.back()" />
      <h:commandButton value="#{msgs.forward}" onclick="history.forward()" />
      <sakai:panel_edit><br/></sakai:panel_edit>
      <h:commandButton value="#{msgs.closeHelp}" onclick="javascript:top.close();" />
	  <sakai:view_content>
     	 <sakai:group_box title="#{msgs.search}">
      		<sakai:panel_edit>
	 			<h:inputText value="#{SearchTool.searchString}" required="true" onclick="clearText(this)"/>
	  		</sakai:panel_edit>
	 		<h:commandButton action="#{SearchTool.processActionSearch}" value="#{msgs.search}"  onclick="document.getElementById('message').style.display = 'block';"> 
				 <help:defaultAction/>
	 		</h:commandButton>
		</sakai:group_box>
    </sakai:view_content>
</h:form>
<sakai:group_box title="#{msgs.search_result}">
    <h:outputText value="#{SearchTool.numberOfResult}" />
    <sakai:flat_list value="#{SearchTool.searchResults}" var="result">
	    <h:column>
			<h:outputLink value="#{result.location}" target="content">
			    <h:outputText value="#{result.name}" />
			</h:outputLink>
		</h:column>
		<h:column>
			<h:outputText value="#{result.formattedScore}" />
		</h:column>
    </sakai:flat_list>
</sakai:group_box>
<help:questionLink URL="/tunnel/sakai-help-tool/question/jsf.tool?pid=/tunnel/sakai-help-tool/question/jsf.tool"
    message="#{msgs.ask_more_help}"
    showLink="#{SearchTool.showLinkToQuestionTool}"/>
</f:view>