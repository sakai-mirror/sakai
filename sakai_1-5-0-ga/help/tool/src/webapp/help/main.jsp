<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%-- Custom tag library just for this tool --%>
<%@ taglib uri="http://sakaiproject.org/jsf/help" prefix="help" %>
<f:view>
  <help:helpFrameSet 
    helpWindowTitle="Sakai Help" 
    searchToolUrl="/tunnel/sakai-help-tool/search/jsf.tool?pid=/tunnel/sakai-help-tool/search/jsf.tool" 
    tocToolUrl="/tunnel/sakai-help-tool/TOCDisplay/jsf.tool?pid=/tunnel/sakai-help-tool/TOCDisplay/jsf.tool" 
    helpDocUrl="#{HelpTool.resource.location}" 
    helpDocId="#{HelpTool.helpDocId}" />
</f:view>
